package vladyslav.letiuka.dlb.exception.balancer;

import vladyslav.letiuka.dlb.exception.balancer.LoadBalancerException;

public class RequestRejectedException extends LoadBalancerException {
    public RequestRejectedException() {
        super();
    }

    public RequestRejectedException(String message) {
        super(message);
    }

    public RequestRejectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestRejectedException(Throwable cause) {
        super(cause);
    }
}
