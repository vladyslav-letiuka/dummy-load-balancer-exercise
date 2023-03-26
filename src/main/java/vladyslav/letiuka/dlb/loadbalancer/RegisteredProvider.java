package vladyslav.letiuka.dlb.loadbalancer;

import vladyslav.letiuka.dlb.exception.provider.ProviderException;
import vladyslav.letiuka.dlb.exception.provider.ProviderExcludedException;
import vladyslav.letiuka.dlb.provider.Provider;

public class RegisteredProvider {

    private static final int SUCCESSFUL_CHECK_STREAK_THRESHOLD = 2;

    private final Provider delegate;
    private final String name;
    private volatile boolean manuallyExcluded;
    private volatile boolean autoExcluded;
    private volatile int successfulCheckStreak;

    public RegisteredProvider(Provider delegate, String name) {
        if (delegate == null) {
            throw new IllegalArgumentException();
        }
        if (name == null) {
            throw new IllegalArgumentException();
        }
        this.delegate = delegate;
        this.name = name;
        manuallyExcluded = false;
        autoExcluded = false;
        successfulCheckStreak = SUCCESSFUL_CHECK_STREAK_THRESHOLD;
    }

    public String get() throws ProviderException {
        if (manuallyExcluded || successfulCheckStreak < SUCCESSFUL_CHECK_STREAK_THRESHOLD) {
            throw new ProviderExcludedException();
        }
        return delegate.get();
    }

    public synchronized boolean ping() {
        boolean isHealthy = delegate.check();

        successfulCheckStreak = isHealthy ? successfulCheckStreak + 1 : 0;

        return isHealthy;
    }

    public String getName() {
        return name;
    }

    public void include() {
        manuallyExcluded = false;
    }

    public void exclude() {
        manuallyExcluded = true;
    }
}
