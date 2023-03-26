package vladyslav.letiuka.dlb.util;

import org.junit.jupiter.api.BeforeAll;
import vladyslav.letiuka.dlb.ProviderFactory;
import vladyslav.letiuka.dlb.loadbalancer.LoadBalancer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class TrafficBaseTest {
    protected static ExecutorService executorService;
    protected static ProviderFactory factory;

    @BeforeAll
    static void prepareAllBase() {
        factory = new ProviderFactory();
        executorService = Executors.newFixedThreadPool(20);
    }

    protected Future<String> prepareRequest(LoadBalancer balancer) {
        return executorService.submit(balancer::get);
    }
}
