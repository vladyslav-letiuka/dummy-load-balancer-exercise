package vladyslav.letiuka.dlb.slow;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vladyslav.letiuka.dlb.ProviderFactory;
import vladyslav.letiuka.dlb.exception.LoadBalancerException;
import vladyslav.letiuka.dlb.exception.ProviderAtCapacityException;
import vladyslav.letiuka.dlb.loadbalancer.LoadBalancer;
import vladyslav.letiuka.dlb.loadbalancer.RandomLoadBalancer;
import vladyslav.letiuka.dlb.loadbalancer.RegisteredProviderWrapper;
import vladyslav.letiuka.dlb.provider.Provider;

import java.util.Collections;
import java.util.concurrent.*;

public class CapacityTest {

    @Test
    void shouldFailAtZeroCapacity() throws Throwable {
        ProviderFactory factory = new ProviderFactory();

        Provider provider = factory.buildProvider("hi", null, 0);
        RegisteredProviderWrapper wrapper = factory.registerProvider(provider, "hi-man");

        LoadBalancer loadBalancer = new RandomLoadBalancer(Collections.singletonList(wrapper));

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            executorService.submit(loadBalancer::get).get();
            Assertions.fail();
        } catch (ExecutionException e) {
            Assertions.assertInstanceOf(LoadBalancerException.class, e.getCause());
        } catch (Throwable e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }
}
