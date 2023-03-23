package vladyslav.letiuka.dlb.loadbalancer;

import vladyslav.letiuka.dlb.exception.LoadBalancerException;

public interface LoadBalancer {
    String get() throws LoadBalancerException;
}
