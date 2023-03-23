package vladyslav.letiuka.dlb.loadbalancer;

import vladyslav.letiuka.dlb.exception.LoadBalancerException;
import vladyslav.letiuka.dlb.exception.ProviderException;
import vladyslav.letiuka.dlb.exception.RequestRejectedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer extends ExclusionLoadBalancer {

    // used to avoid too many costly index reset operations
    private static final int RESET_THRESHOLD = Integer.MAX_VALUE / 2;

    private static final int ROUND_ROBIN_RETRIES = 100;

    private final AtomicInteger indexCounter;
    private final boolean deterministic;

    public RoundRobinLoadBalancer(Collection<RegisteredProviderWrapper> providers) {
        this(providers, false);
    }

    public RoundRobinLoadBalancer(Collection<RegisteredProviderWrapper> providers, boolean deterministic) {
        super(providers);
        indexCounter = new AtomicInteger(0);
        this.deterministic = deterministic;
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

    private String getRoundRobin() throws LoadBalancerException {

        for (int i = 0; i < ROUND_ROBIN_RETRIES; ++i) {
            RegisteredProviderWrapper provider = getNextProvider();
            try {
                return provider.get();
            } catch (ProviderException e) {
                // no-op
            }
        }

        throw new RequestRejectedException("No alive providers");
    }

    private String getRandom() throws LoadBalancerException {

        List<RegisteredProviderWrapper> prioritizedProviders;
        if (deterministic) {
            prioritizedProviders = providers;
        } else {
            prioritizedProviders = new ArrayList<>(providers);
            Collections.shuffle(prioritizedProviders);
        }

        for (RegisteredProviderWrapper provider : prioritizedProviders) {
            try {
                return provider.get();
            } catch (ProviderException e) {
                // no-op
            }
        }

        throw new RequestRejectedException("Could not find a provider");
    }


    private RegisteredProviderWrapper getNextProvider() {
        int counterValue = indexCounter.getAndIncrement();
        if (counterValue >= RESET_THRESHOLD) {
            resetIndexCounter();
        }
        return providers.get(counterValue % providers.size());
    }

    /**
     * Prevents overflow into negative values in order to preserve the correct
     * {@code %} operand behavior.
     */
    private void resetIndexCounter() {
        indexCounter.updateAndGet(counter -> counter % providers.size());
    }
}
