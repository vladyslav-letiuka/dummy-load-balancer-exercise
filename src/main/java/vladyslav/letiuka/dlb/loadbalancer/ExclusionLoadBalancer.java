package vladyslav.letiuka.dlb.loadbalancer;

import java.lang.ref.Cleaner;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class ExclusionLoadBalancer implements LoadBalancer {

    private static final int MAX_PROVIDERS_SUPPORTED = 10;
    private static final Cleaner cleaner = Cleaner.create();

    protected final List<RegisteredProvider> providers;

    public ExclusionLoadBalancer(Collection<RegisteredProvider> providers) {
        this(providers, 1000);
    }

    public ExclusionLoadBalancer(Collection<RegisteredProvider> providers, int pingIntervalMillis) {
        if (providers.size() > MAX_PROVIDERS_SUPPORTED) {
            throw new IllegalArgumentException();
        }
        this.providers = List.copyOf(providers);

        WeakReference<ExclusionLoadBalancer> weakThis = new WeakReference<>(this);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(
                () -> healthPingFromWeakRef(weakThis),
                pingIntervalMillis, pingIntervalMillis, TimeUnit.MILLISECONDS);
        cleaner.register(this, scheduledExecutorService::shutdown); // shutdown scheduled pings when GCed
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

    private void setProviderStatus(RegisteredProvider provider, boolean included) {
        if (included) {
            provider.include();
        } else {
            provider.exclude();
        }
    }

    private void healthPing() {
        providers.forEach(RegisteredProvider::ping);
    }

    private static void healthPingFromWeakRef(WeakReference<ExclusionLoadBalancer> weakRef) {
        ExclusionLoadBalancer balancer = weakRef.get();
        if (balancer != null) {
            balancer.healthPing();
        }
    }
}
