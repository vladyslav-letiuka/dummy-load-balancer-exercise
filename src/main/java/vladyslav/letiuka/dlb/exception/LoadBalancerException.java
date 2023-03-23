package vladyslav.letiuka.dlb.exception;

public class LoadBalancerException extends Exception {
    public LoadBalancerException() {
        super();
    }

    public LoadBalancerException(String message) {
        super(message);
    }

    public LoadBalancerException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadBalancerException(Throwable cause) {
        super(cause);
    }
}
