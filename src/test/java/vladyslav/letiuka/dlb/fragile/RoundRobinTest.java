package vladyslav.letiuka.dlb.fragile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vladyslav.letiuka.dlb.loadbalancer.RegisteredProvider;
import vladyslav.letiuka.dlb.loadbalancer.RoundRobinLoadBalancer;
import vladyslav.letiuka.dlb.provider.Provider;
import vladyslav.letiuka.dlb.util.TrafficBaseTest;

import java.util.Arrays;
import java.util.concurrent.Future;

public class RoundRobinTest extends TrafficBaseTest {

    @Test
    void shouldDistributeFairly() throws Exception {

        Provider provider3 = factory.buildProvider("#3", null, null);
        Provider provider2 = factory.buildProvider("#2", null, null);
        Provider provider1 = factory.buildProvider("#1", null, null);

        RegisteredProvider wrapper3 = factory.registerProvider(provider3);
        RegisteredProvider wrapper2 = factory.registerProvider(provider2, "second");
        RegisteredProvider wrapper1 = factory.registerProvider(provider1);

        RoundRobinLoadBalancer loadBalancer = new RoundRobinLoadBalancer(Arrays.asList(wrapper1, wrapper2, wrapper3),
                1000, true);

        Future<String> f1 = executorService.submit(loadBalancer::get);
        Thread.sleep(10);
        Future<String> f2 = executorService.submit(loadBalancer::get);
        Thread.sleep(10);
        Future<String> f3 = executorService.submit(loadBalancer::get);
        Thread.sleep(10);
        loadBalancer.excludeProviders("second");
        Thread.sleep(10);
        Future<String> f4 = executorService.submit(loadBalancer::get);
        Thread.sleep(10);
        Future<String> f5 = executorService.submit(loadBalancer::get);
        Thread.sleep(10);
        loadBalancer.includeProviders("second");
        Thread.sleep(10);
        Future<String> f6 = executorService.submit(loadBalancer::get);
        Thread.sleep(10);
        Future<String> f7 = executorService.submit(loadBalancer::get);

        Assertions.assertEquals("#1", f1.get());
        Assertions.assertEquals("#2", f2.get());
        Assertions.assertEquals("#3", f3.get());
        Assertions.assertEquals("#1", f4.get());
        Assertions.assertEquals("#3", f5.get(), "Should skip excluded #2");
        Assertions.assertEquals("#1", f6.get());
        Assertions.assertEquals("#2", f7.get(), "At this point #2 should be included again");
    }
}
