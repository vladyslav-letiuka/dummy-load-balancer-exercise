package vladyslav.letiuka.dlb.provider;

import vladyslav.letiuka.dlb.exception.provider.ProviderException;

public class ConstantDelayProviderDecorator implements Provider {
    private final Provider delegate;
    private final int delayMillis;

    public ConstantDelayProviderDecorator(Provider delegate, int delayMillis) {
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
