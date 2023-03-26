package vladyslav.letiuka.dlb.util;

import vladyslav.letiuka.dlb.loadbalancer.RegisteredProvider;
import vladyslav.letiuka.dlb.provider.ConstantDelayProviderDecorator;
import vladyslav.letiuka.dlb.provider.StaticToggledProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class TestLoadBalancerFactory {

    private TestLoadBalancerFactory() {

    }

    public static List<RegisteredProvider> buildFastProviders(int count) {
        return IntStream.range(0, count)
                .mapToObj(id -> new RegisteredProvider(
                        new StaticToggledProvider("#" + id), "name" + id))
                .collect(Collectors.toList());
    }

    public static List<RegisteredProvider> buildSlowProviders(int count) {
        return IntStream.range(0, count)
                .mapToObj(id -> new RegisteredProvider(
                        new ConstantDelayProviderDecorator(
                                new StaticToggledProvider("#" + id), 500),
                        "name" + id))
                .collect(Collectors.toList());
    }
}
