package vladyslav.letiuka.dlb.provider;

import vladyslav.letiuka.dlb.exception.ProviderException;

public interface Provider {
    String get() throws ProviderException;

    boolean check();
}
