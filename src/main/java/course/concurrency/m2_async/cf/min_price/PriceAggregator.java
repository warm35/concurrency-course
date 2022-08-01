package course.concurrency.m2_async.cf.min_price;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        var threadPoolExecutor = Executors.newFixedThreadPool(shopIds.size());

        var prices = shopIds.stream()
            .map(shopId -> supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), threadPoolExecutor)
                .exceptionally(err -> Double.MAX_VALUE)
                .completeOnTimeout(null, 2500, TimeUnit.MILLISECONDS))
            .reduce(PriceAggregator::min);

        return prices.map(CompletableFuture::join).orElse(Double.NaN);
    }

    private static CompletableFuture<Double> min(CompletableFuture<Double> a, CompletableFuture<Double> b) {
        return a.thenCombineAsync(b, (aValue, bValue) -> {
            if (aValue == null) {
                return bValue;
            } else if (bValue == null) {
                return aValue;
            }

            return Double.min(aValue, bValue);
        });
    }
}
