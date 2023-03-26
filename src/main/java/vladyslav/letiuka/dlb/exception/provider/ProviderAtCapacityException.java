package vladyslav.letiuka.dlb.exception.provider;

public class ProviderAtCapacityException extends ProviderException {
    public ProviderAtCapacityException() {
        super();
    }

    public ProviderAtCapacityException(String message) {
        super(message);
    }

    public ProviderAtCapacityException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProviderAtCapacityException(Throwable cause) {
        super(cause);
    }
}
