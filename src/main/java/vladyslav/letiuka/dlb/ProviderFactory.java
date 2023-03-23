package vladyslav.letiuka.dlb;

import vladyslav.letiuka.dlb.loadbalancer.RegisteredProvider;
import vladyslav.letiuka.dlb.provider.ConstantDelayProviderDecorator;
import vladyslav.letiuka.dlb.provider.LimitedCapacityProviderDecorator;
import vladyslav.letiuka.dlb.provider.Provider;
import vladyslav.letiuka.dlb.provider.StaticToggledProvider;

public class ProviderFactory {

    public Provider buildProvider(String output, Integer millisDelay, Integer capacity) {
        Provider provider = new StaticToggledProvider(output);
        if(millisDelay != null) {
            provider = new ConstantDelayProviderDecorator(provider, millisDelay);
        }
        if(capacity != null) {
            provider = new LimitedCapacityProviderDecorator(provider, capacity);
        }
        return provider;
    }

    public RegisteredProvider registerProvider(Provider provider) {
        return new RegisteredProvider(provider, "anonymous");
    }

    public RegisteredProvider registerProvider(Provider provider, String name) {
        return new RegisteredProvider(provider, name);
    }

}
