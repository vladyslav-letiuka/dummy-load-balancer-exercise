package vladyslav.letiuka.dlb.util;

import vladyslav.letiuka.dlb.loadbalancer.LoadBalancer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class TrafficGenerator {

    public Future<String> prepareNewRequest(LoadBalancer loadBalancer, ExecutorService executorService) {
        return executorService.submit(loadBalancer::get);
    }
}
