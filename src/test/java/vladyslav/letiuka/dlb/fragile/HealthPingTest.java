package vladyslav.letiuka.dlb.fragile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vladyslav.letiuka.dlb.loadbalancer.RegisteredProvider;
import vladyslav.letiuka.dlb.loadbalancer.RoundRobinLoadBalancer;
import vladyslav.letiuka.dlb.provider.ConstantDelayProviderDecorator;
import vladyslav.letiuka.dlb.provider.Provider;
import vladyslav.letiuka.dlb.provider.StaticToggledProvider;
import vladyslav.letiuka.dlb.util.TrafficBaseTest;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HealthPingTest extends TrafficBaseTest {

    @Test
    void shouldExcludeAndIncludeProvidersAutomatically() throws Exception {

        StaticToggledProvider toggledProvider = new StaticToggledProvider("#1");
        Provider provider = new ConstantDelayProviderDecorator(toggledProvider, 100);

        RegisteredProvider wrapper1 = factory.registerProvider(provider);

        RoundRobinLoadBalancer loadBalancer = new RoundRobinLoadBalancer(List.of(wrapper1),
                200, true);

        Future<String> f1 = executorService.submit(loadBalancer::get);
        Thread.sleep(10); // t=10

        // not yet done, see returned result assertion at the bottom of the test
        Assertions.assertFalse(f1.isDone(), "Request should still be in progress");

        Thread.sleep(240); // t=250


        Future<String> f2 = executorService.submit(loadBalancer::get);
        Thread.sleep(10); // t=260


        // not yet done, see returned result assertion at the bottom of the test
        Assertions.assertFalse(f2.isDone(), "Request should still be in progress");

        toggledProvider.setAlive(false); // service went down during request processing, should still affect request

        Thread.sleep(200); // t=460

        // ping happened at t=400, now provider excluded

        Future<String> f3 = executorService.submit(loadBalancer::get);
        Thread.sleep(10); // t=470;
        Assertions.assertThrowsExactly(ExecutionException.class, f3::get,
                "Fails instantly because excluded");

        toggledProvider.setAlive(true);

        Thread.sleep(200); // t=670;

        // ping happened at t=600, but provider still excluded because we need 2 successful pings

        Future<String> f4 = executorService.submit(loadBalancer::get);
        Thread.sleep(10); // t=680;
        Assertions.assertTrue(f4.isDone());
        Assertions.assertThrowsExactly(ExecutionException.class, f4::get, "Still excluded");

        Thread.sleep(200); // t=880;

        // ping at 800, second success, included again

        Future<String> f5 = executorService.submit(loadBalancer::get);
        Thread.sleep(10); // t=890;
        Assertions.assertFalse(f5.isDone());


        Thread.sleep(300); // t=1090;
        // -----------------------------------------------------------------------------
        // old requests check-up below: giving them more time to finish makes the test less fragile

        Assertions.assertEquals("#1", f1.get());
        Assertions.assertThrowsExactly(ExecutionException.class, f2::get,
                "Should fail because alive = false by the end of request execution");
        Assertions.assertEquals("#1", f5.get());


    }
}
