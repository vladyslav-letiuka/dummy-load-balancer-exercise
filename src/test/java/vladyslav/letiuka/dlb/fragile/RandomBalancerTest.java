package vladyslav.letiuka.dlb.fragile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import vladyslav.letiuka.dlb.loadbalancer.LoadBalancer;
import vladyslav.letiuka.dlb.loadbalancer.RandomLoadBalancer;
import vladyslav.letiuka.dlb.loadbalancer.RegisteredProvider;
import vladyslav.letiuka.dlb.provider.StaticToggledProvider;
import vladyslav.letiuka.dlb.util.TestLoadBalancerFactory;
import vladyslav.letiuka.dlb.util.TrafficBaseTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomBalancerTest extends TrafficBaseTest {

    @Test
    void distributionLooksReasonable() throws Exception {
        List<RegisteredProvider> providers = TestLoadBalancerFactory.buildFastProviders(5);

        StaticToggledProvider deadProvider = new StaticToggledProvider("unreachable-output");
        deadProvider.setAlive(false);

        RegisteredProvider registeredDeadProvider = new RegisteredProvider(deadProvider, "dead");

        // make sure dead provider in the middle does not change distribution
        providers.add(2, registeredDeadProvider);

        LoadBalancer randomLoadBalancer = new RandomLoadBalancer(providers);

        Map<String, Integer> aggregatedOutputs = new HashMap<>();

        List<Future<String>> requests =
                IntStream.range(0, 1000).mapToObj($ -> prepareRequest(randomLoadBalancer)).collect(Collectors.toList());

        String lastOutput = null;
        int repeats = 0;
        for (Future<String> request : requests) {
            String output = request.get();
            aggregatedOutputs.put(output, aggregatedOutputs.getOrDefault(output, 0) + 1);
            if (output.equals(lastOutput)) {
                ++repeats;
            }
            lastOutput = output;
        }

        Assertions.assertNull(aggregatedOutputs.get("unreachable-output"));
        Assertions.assertEquals(5, aggregatedOutputs.size(), "Should count only alive providers");
        Assertions.assertTrue(repeats >= 50 && repeats <= 500, "Outputs should not cluster too much," +
                " but should also repeat occasionally (expected around 200 repeats)");
        for (Map.Entry<String, Integer> entry : aggregatedOutputs.entrySet()) {
            int occurred = entry.getValue();
            Assertions.assertTrue(occurred >= 50 && occurred <= 500,
                    "Output {" + entry.getKey() + "} expected to occur roughly 1000 / 5 = 200 times," +
                            " but actually occurred " + occurred + " times");
        }
    }
}
