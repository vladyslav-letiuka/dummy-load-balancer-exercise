package vladyslav.letiuka.dlb.provider;

import vladyslav.letiuka.dlb.exception.provider.ProviderAtCapacityException;
import vladyslav.letiuka.dlb.exception.provider.ProviderException;

import java.util.concurrent.Semaphore;

public class LimitedCapacityProviderDecorator implements Provider {

    private final Provider delegate;
    private final Semaphore semaphore;

    public LimitedCapacityProviderDecorator(Provider delegate, int capacity) {
        this.delegate = delegate;
        semaphore = new Semaphore(capacity);
    }

    @Override
    public String get() throws ProviderException {
        boolean acquired = false;
        try {
            acquired = semaphore.tryAcquire();
            if (!acquired) {
                throw new ProviderAtCapacityException();
            }
            return delegate.get();
        } finally {
            if (acquired) {
                try {
                    semaphore.release();
                }
                catch (Exception e) {
                    // no-op: exists so that exception in finally block does not override real exception

                    // insert logging of exception stacktrace here
                }
            }
        }
    }

    @Override
    public boolean check() {
        return delegate.check();
    }
}
