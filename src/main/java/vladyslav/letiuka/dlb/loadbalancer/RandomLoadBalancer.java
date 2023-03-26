package vladyslav.letiuka.dlb.loadbalancer;

import vladyslav.letiuka.dlb.exception.balancer.LoadBalancerException;
import vladyslav.letiuka.dlb.exception.balancer.RequestRejectedException;
import vladyslav.letiuka.dlb.exception.provider.ProviderException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RandomLoadBalancer extends ExclusionLoadBalancer {

    public RandomLoadBalancer(Collection<RegisteredProvider> providers) {
        this(providers, 1000);
    }

    public RandomLoadBalancer(Collection<RegisteredProvider> providers, int millisBetweenPings) {
        super(providers, millisBetweenPings);
    }

    @Override
    public String get() throws LoadBalancerException {
        List<RegisteredProvider> prioritizedProviders = new ArrayList<>(providers);
        Collections.shuffle(prioritizedProviders);

        for (RegisteredProvider provider : prioritizedProviders) {
            try {
                return provider.get();
            } catch (ProviderException e) {
                // no-op
            }
        }

        throw new RequestRejectedException("Could not find a provider");
    }
}
