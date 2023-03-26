package vladyslav.letiuka.dlb.loadbalancer;

import vladyslav.letiuka.dlb.exception.balancer.LoadBalancerException;
import vladyslav.letiuka.dlb.exception.balancer.RequestRejectedException;
import vladyslav.letiuka.dlb.exception.provider.ProviderException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer extends ExclusionLoadBalancer {

    // used to avoid too many costly index reset operations
    private static final int RESET_THRESHOLD = Integer.MAX_VALUE / 2;

    private final AtomicInteger indexCounter;
    private final boolean deterministic;
    private volatile short retries;

    public RoundRobinLoadBalancer(Collection<RegisteredProvider> providers) {
        this(providers, 1000);
    }

    public RoundRobinLoadBalancer(Collection<RegisteredProvider> providers, int millisBetweenPings) {
        this(providers, millisBetweenPings, false);
    }

    public RoundRobinLoadBalancer(Collection<RegisteredProvider> providers, int millisBetweenPings, boolean deterministic) {
        super(providers, millisBetweenPings);
        indexCounter = new AtomicInteger(0);
        this.deterministic = deterministic;
        retries = 20;
    }

    @Override
    public String get() throws LoadBalancerException {
        try {
            return getRoundRobin(); // not guaranteed to hit every possible provider after any X retries
        } catch (RequestRejectedException e) {
            // no-op
        }
        return getRandom(); // fallback strategy
    }

    public void setRetries(short retries) {
        this.retries = retries;
    }

    private String getRoundRobin() throws LoadBalancerException {

        for (int i = 0; i < retries; ++i) {
            RegisteredProvider provider = getNextProvider();
            try {
                return provider.get();
            } catch (ProviderException e) {
                // no-op
            }
        }

        throw new RequestRejectedException("No alive providers");
    }

    private String getRandom() throws LoadBalancerException {

        List<RegisteredProvider> prioritizedProviders;
        if (deterministic) {
            prioritizedProviders = providers;
        } else {
            prioritizedProviders = new ArrayList<>(providers);
            Collections.shuffle(prioritizedProviders);
        }

        for (RegisteredProvider provider : prioritizedProviders) {
            try {
                return provider.get();
            } catch (ProviderException e) {
                // no-op
            }
        }

        throw new RequestRejectedException("Could not find a provider");
    }


    private RegisteredProvider getNextProvider() {
        int counterValue = indexCounter.getAndIncrement();
        if (counterValue >= RESET_THRESHOLD) {
            resetIndexCounter();
        }
        return providers.get(counterValue % providers.size());
    }

    /**
     * Prevents overflow into negative values in order to preserve the correct
     * {@code %} operator behavior.
     */
    private void resetIndexCounter() {
        indexCounter.updateAndGet(counter -> counter % providers.size());
    }
}
