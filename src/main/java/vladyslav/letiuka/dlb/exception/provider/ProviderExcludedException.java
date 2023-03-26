package vladyslav.letiuka.dlb.exception.provider;

public class ProviderExcludedException extends ProviderException {
    public ProviderExcludedException() {
        super();
    }

    public ProviderExcludedException(String message) {
        super(message);
    }

    public ProviderExcludedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProviderExcludedException(Throwable cause) {
        super(cause);
    }
}
