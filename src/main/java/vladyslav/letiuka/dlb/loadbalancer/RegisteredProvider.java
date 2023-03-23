package vladyslav.letiuka.dlb.loadbalancer;

import vladyslav.letiuka.dlb.exception.ProviderException;
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
        if (manuallyExcluded || autoExcluded) {
            throw new ProviderException("Excluded");
        }
        return delegate.get();
    }

    public synchronized boolean ping() {
        boolean isHealthy = delegate.check();

        successfulCheckStreak = isHealthy ? successfulCheckStreak + 1 : 0;
        autoUpdateStatus();

        return isHealthy;
    }

    private synchronized void autoUpdateStatus() {
        if (successfulCheckStreak == 0) {
            autoExcluded = true;
        } else if (successfulCheckStreak >= SUCCESSFUL_CHECK_STREAK_THRESHOLD) {
            autoExcluded = false;
        }
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
