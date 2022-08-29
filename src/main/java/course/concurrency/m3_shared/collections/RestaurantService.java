package course.concurrency.m3_shared.collections;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class RestaurantService {

    private ConcurrentMap<String, Integer> stat = new ConcurrentHashMap<>();
    private Restaurant mockRestaurant = new Restaurant("A");

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return mockRestaurant;
    }

    public void addToStat(String restaurantName) {
        // ваш код
        stat.merge(restaurantName, 1, Integer::sum);
    }

    public Set<String> printStat() {
        return stat.entrySet().stream()
            .map(kv -> kv.getKey() + " - " + kv.getValue())
            .collect(Collectors.toSet());
    }
}
