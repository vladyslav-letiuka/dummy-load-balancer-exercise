package vladyslav.letiuka.dlb.loadbalancer;

import vladyslav.letiuka.dlb.exception.balancer.LoadBalancerException;

public interface LoadBalancer {
    String get() throws LoadBalancerException;
}
