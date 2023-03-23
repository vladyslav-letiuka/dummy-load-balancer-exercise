package vladyslav.letiuka.dlb.util;

import vladyslav.letiuka.dlb.loadbalancer.LoadBalancer;
import vladyslav.letiuka.dlb.loadbalancer.RegisteredProvider;
import vladyslav.letiuka.dlb.loadbalancer.RoundRobinLoadBalancer;
import vladyslav.letiuka.dlb.provider.ConstantDelayProviderDecorator;
import vladyslav.letiuka.dlb.provider.StaticToggledProvider;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class TestLoadBalancerFactory {

    private TestLoadBalancerFactory() {

    }

    public static LoadBalancer buildRoundRobinBalancer(
            Supplier<RegisteredProvider> providerSupplier,
            int providerCount) {

        List<RegisteredProvider> providers = IntStream.range(0, providerCount)
                .mapToObj($ -> providerSupplier.get())
                .collect(Collectors.toList());

        return new RoundRobinLoadBalancer(providers, true);
    }

    public static List<RegisteredProvider> buildFastProviders(int count) {
        return IntStream.range(0, count)
                .mapToObj(id -> new RegisteredProvider(
                        new StaticToggledProvider("output" + id), "name" + id))
                .collect(Collectors.toList());
    }

    public static List<RegisteredProvider> buildSlowProviders(int count) {
        return IntStream.range(0, count)
                .mapToObj(id -> new RegisteredProvider(
                        new ConstantDelayProviderDecorator(
                                new StaticToggledProvider("output" + id), 500),
                        "name" + id))
                .collect(Collectors.toList());
    }
}
