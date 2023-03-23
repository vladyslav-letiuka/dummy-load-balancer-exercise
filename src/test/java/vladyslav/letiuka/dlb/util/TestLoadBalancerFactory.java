package vladyslav.letiuka.dlb.util;

import vladyslav.letiuka.dlb.loadbalancer.LoadBalancer;
import vladyslav.letiuka.dlb.loadbalancer.RegisteredProviderWrapper;
import vladyslav.letiuka.dlb.loadbalancer.RoundRobinLoadBalancer;
import vladyslav.letiuka.dlb.provider.ConstantDelayProviderDecorator;
import vladyslav.letiuka.dlb.provider.StaticToggledProvider;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestLoadBalancerFactory {

    public static LoadBalancer buildRoundRobinBalancer(
            Supplier<RegisteredProviderWrapper> providerSupplier,
            int providerCount) {

        List<RegisteredProviderWrapper> providers = IntStream.range(0, providerCount)
                .mapToObj($ -> providerSupplier.get())
                .collect(Collectors.toList());

        return new RoundRobinLoadBalancer(providers, true);
    }

    public static List<RegisteredProviderWrapper> buildFastProviders(int count) {
        return IntStream.range(0, count)
                .mapToObj(id -> new RegisteredProviderWrapper(
                        new StaticToggledProvider("output" + id), "name" + id))
                .collect(Collectors.toList());
    }

    public static List<RegisteredProviderWrapper> buildSlowProviders(int count) {
        return IntStream.range(0, count)
                .mapToObj(id -> new RegisteredProviderWrapper(
                        new ConstantDelayProviderDecorator(
                                new StaticToggledProvider("output" + id), 500),
                        "name" + id))
                .collect(Collectors.toList());
    }
}
