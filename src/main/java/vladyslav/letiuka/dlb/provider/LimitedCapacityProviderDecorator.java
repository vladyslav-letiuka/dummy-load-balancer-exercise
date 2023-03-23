package vladyslav.letiuka.dlb.provider;

import vladyslav.letiuka.dlb.exception.ProviderAtCapacityException;
import vladyslav.letiuka.dlb.exception.ProviderException;

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
            System.out.println("Trying to acquire semaphore lock...");
            acquired = semaphore.tryAcquire();
            if (!acquired) {
                System.out.println("Failed to acquire semaphore lock...");
                throw new ProviderAtCapacityException();
            }
            return delegate.get();
        } finally {
            if (acquired) {
                semaphore.release();
            }
        }
    }

    @Override
    public boolean check() {
        return delegate.check();
    }
}
