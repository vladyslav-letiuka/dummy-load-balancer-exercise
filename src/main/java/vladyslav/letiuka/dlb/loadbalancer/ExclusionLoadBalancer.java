package vladyslav.letiuka.dlb.loadbalancer;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ExclusionLoadBalancer implements LoadBalancer {

    private static final int MAX_PROVIDERS_SUPPORTED = 10;

    protected final List<RegisteredProviderWrapper> providers;

    public ExclusionLoadBalancer(Collection<RegisteredProviderWrapper> providers) {
        if (providers.size() > MAX_PROVIDERS_SUPPORTED) {
            throw new IllegalArgumentException();
        }
        this.providers = List.copyOf(providers);

        WeakReference<ExclusionLoadBalancer> weakThis = new WeakReference<>(this);

        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> healthPingFromWeakRef(weakThis, scheduledExecutorService),
                2000, 2000, TimeUnit.MILLISECONDS);
    }

    public void includeProviders(String name) {
        setProviderStatus(name, true);
    }

    public void excludeProviders(String name) {
        setProviderStatus(name, false);
    }

    private void setProviderStatus(String name, boolean included) {
        providers.stream()
                .filter(provider -> provider.getName().equalsIgnoreCase(name))
                .forEach(provider -> setProviderStatus(provider, included));
    }

    private void setProviderStatus(RegisteredProviderWrapper provider, boolean included) {
        if (included) {
            provider.include();
        } else {
            provider.exclude();
        }
    }

    private void healthPing() {
        providers.forEach(RegisteredProviderWrapper::ping);
    }

    private static void healthPingFromWeakRef(WeakReference<ExclusionLoadBalancer> weakRef, ExecutorService executor) {
        ExclusionLoadBalancer balancer = weakRef.get();
        if (balancer == null) {
            executor.shutdown();
        } else {
            balancer.healthPing();
        }
    }
}
