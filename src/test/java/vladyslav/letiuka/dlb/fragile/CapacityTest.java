package vladyslav.letiuka.dlb.fragile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vladyslav.letiuka.dlb.exception.balancer.LoadBalancerException;
import vladyslav.letiuka.dlb.exception.balancer.RequestRejectedException;
import vladyslav.letiuka.dlb.loadbalancer.LoadBalancer;
import vladyslav.letiuka.dlb.loadbalancer.RegisteredProvider;
import vladyslav.letiuka.dlb.loadbalancer.RoundRobinLoadBalancer;
import vladyslav.letiuka.dlb.provider.Provider;
import vladyslav.letiuka.dlb.util.TrafficBaseTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CapacityTest extends TrafficBaseTest {

    @Test
    void shouldFailAtZeroCapacity() throws Throwable {

        Provider provider = factory.buildProvider("single", null, 0);
        RegisteredProvider wrapper = factory.registerProvider(provider);

        LoadBalancer loadBalancer = new RoundRobinLoadBalancer(Collections.singletonList(wrapper),
                1000, true);

        try {
            executorService.submit(loadBalancer::get).get();
            Assertions.fail();
        } catch (ExecutionException e) {
            Assertions.assertInstanceOf(LoadBalancerException.class, e.getCause());
        }
    }

    @Test
    void shouldLetThroughUpToCapacity() throws Exception {

        Provider provider = factory.buildProvider("single", 100, 3);
        RegisteredProvider wrapper = factory.registerProvider(provider);

        RoundRobinLoadBalancer loadBalancer = new RoundRobinLoadBalancer(Collections.singletonList(wrapper),
                1000, true);

        Future<String> f1 = executorService.submit(loadBalancer::get);
        Thread.sleep(10);
        Future<String> f2 = executorService.submit(loadBalancer::get);
        Thread.sleep(10);
        Future<String> f3 = executorService.submit(loadBalancer::get);
        Thread.sleep(10);
        Future<String> f4 = executorService.submit(loadBalancer::get);
        Thread.sleep(150);
        Future<String> f5 = executorService.submit(loadBalancer::get);

        Assertions.assertEquals("single", f1.get());
        Assertions.assertEquals("single", f2.get());
        Assertions.assertEquals("single", f3.get());
        try {
            f4.get();
            Assertions.fail();
        } catch (ExecutionException e) {
            Assertions.assertInstanceOf(RequestRejectedException.class, e.getCause());
        }
        Assertions.assertEquals("single", f5.get());
    }


    @Test
    void shouldLetThroughUpToCapacitySum() throws Exception {

        Provider provider1 = factory.buildProvider("first", 100, 1);
        Provider provider2 = factory.buildProvider("second", 100, 2);
        RegisteredProvider wrapper1 = factory.registerProvider(provider1);
        RegisteredProvider wrapper2 = factory.registerProvider(provider2);

        RoundRobinLoadBalancer loadBalancer = new RoundRobinLoadBalancer(Arrays.asList(wrapper1, wrapper2),
                1000, true);

        Future<String> f1 = executorService.submit(loadBalancer::get);
        Thread.sleep(10);
        Future<String> f2 = executorService.submit(loadBalancer::get);
        Thread.sleep(10);
        Future<String> f3 = executorService.submit(loadBalancer::get);
        Thread.sleep(10);
        Future<String> f4 = executorService.submit(loadBalancer::get);
        Thread.sleep(150);
        Future<String> f5 = executorService.submit(loadBalancer::get);

        Assertions.assertEquals("first", f1.get());
        Assertions.assertEquals("second", f2.get());
        Assertions.assertEquals("second", f3.get());
        try {
            f4.get();
            Assertions.fail();
        } catch (ExecutionException e) {
            Assertions.assertInstanceOf(RequestRejectedException.class, e.getCause());
        }
        Assertions.assertEquals("first", f5.get());
    }


}
