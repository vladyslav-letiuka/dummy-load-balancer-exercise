package vladyslav.letiuka.dlb.provider;

import vladyslav.letiuka.dlb.exception.ProviderException;
import vladyslav.letiuka.dlb.provider.Provider;

public class ConstantDelayProviderDecorator implements Provider {
    private final Provider delegate;
    private final long delayMillis;

    public ConstantDelayProviderDecorator(Provider delegate, long delayMillis) {
        this.delegate = delegate;
        this.delayMillis = delayMillis;
    }

    @Override
    public String get() throws ProviderException {
        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            // no-op
        }

        return delegate.get();
    }

    @Override
    public boolean check() {
        return delegate.check();
    }
}
