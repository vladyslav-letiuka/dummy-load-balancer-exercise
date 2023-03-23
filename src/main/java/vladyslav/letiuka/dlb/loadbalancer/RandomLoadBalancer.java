package vladyslav.letiuka.dlb.loadbalancer;

import vladyslav.letiuka.dlb.exception.LoadBalancerException;
import vladyslav.letiuka.dlb.exception.ProviderException;
import vladyslav.letiuka.dlb.exception.RequestRejectedException;
import vladyslav.letiuka.dlb.provider.Provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RandomLoadBalancer extends ExclusionLoadBalancer {

    public RandomLoadBalancer(Collection<RegisteredProviderWrapper> providers) {
        super(providers);
    }

    @Override
    public String get() throws LoadBalancerException {
        List<RegisteredProviderWrapper> prioritizedProviders = new ArrayList<>(providers);
        Collections.shuffle(prioritizedProviders);

        for (RegisteredProviderWrapper provider : prioritizedProviders) {
            try {
                return provider.get();
            } catch (ProviderException e) {
                // no-op
            }
        }

        throw new RequestRejectedException("Could not find a provider");
    }
}
