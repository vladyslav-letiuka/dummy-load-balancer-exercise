package vladyslav.letiuka.dlb.provider;

import vladyslav.letiuka.dlb.exception.ProviderException;

public class StaticToggledProvider implements Provider {
    private final String output;
    private volatile boolean alive;

    public StaticToggledProvider(String output) {
        this.output = output;
        this.alive = true;
    }

    @Override
    public String get() throws ProviderException {
        if(!alive) {
            throw new ProviderException("Not alive");
        }
        return output;
    }

    public boolean check() {
        return alive;
    }

    public void setAlive(boolean newValue) {
        this.alive = newValue;
    }
}
