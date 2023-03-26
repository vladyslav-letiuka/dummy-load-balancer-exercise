package vladyslav.letiuka.dlb.loadbalancer;

import vladyslav.letiuka.dlb.exception.provider.ProviderException;
import vladyslav.letiuka.dlb.exception.provider.ProviderExcludedException;
import vladyslav.letiuka.dlb.provider.Provider;

import java.util.concurrent.atomic.AtomicInteger;

public class RegisteredProvider {

    private static final int SUCCESSFUL_CHECK_STREAK_THRESHOLD = 2;

    private final Provider delegate;
    private final String name;
    private volatile boolean manuallyExcluded;
    private final AtomicInteger successfulCheckStreak;

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
        successfulCheckStreak = new AtomicInteger(SUCCESSFUL_CHECK_STREAK_THRESHOLD);
    }

    public String get() throws ProviderException {
        if (manuallyExcluded || isAutoExcluded()) {
            throw new ProviderExcludedException();
        }
        return delegate.get();
    }

    public void ping() {
        if (delegate.check()) {
            successfulCheckStreak.incrementAndGet();
        } else {
            successfulCheckStreak.set(0);
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

    private boolean isAutoExcluded() {
        return successfulCheckStreak.get() < SUCCESSFUL_CHECK_STREAK_THRESHOLD;
    }
}
