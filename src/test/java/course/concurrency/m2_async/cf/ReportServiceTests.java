package course.concurrency.m2_async.cf;

import course.concurrency.m2_async.cf.report.ReportServiceCF;
import course.concurrency.m2_async.cf.report.ReportServiceExecutors;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ReportServiceTests {

//    private ReportServiceExecutors reportService;
    private ReportServiceCF reportService;

    @ParameterizedTest
    @MethodSource("initReportService")
    public void testMultipleTasks(ExecutorService executorService, String type, int threads) throws InterruptedException {
        reportService = new ReportServiceCF(executorService);

        int poolSize = Runtime.getRuntime().availableProcessors()*3;
        int iterations = 5;

        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        for (int i = 0; i < poolSize; i++) {
            executor.submit(() -> {
                try {
                    latch.await();
                } catch (InterruptedException ignored) {}
                for (int it = 0; it < iterations; it++) {
                    reportService.getReport();
                }
            });
        }

        long start = System.currentTimeMillis();
        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);
        long end = System.currentTimeMillis();

        System.out.printf("(%s;%s) Rough execution time: %s\n", type, threads, (end - start));
    }

    public static Stream<Arguments> initReportService() {
        return Stream.of(
            Arguments.of(ForkJoinPool.commonPool(), "FORKJOIN", 0),
            Arguments.of(new ForkJoinPool(2, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) -> {}, true), "FORKJOIN", 2),
            Arguments.of(new ForkJoinPool(4, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) -> {}, true), "FORKJOIN", 4),
            Arguments.of(new ForkJoinPool(8, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) -> {}, true), "FORKJOIN", 8),
            Arguments.of(new ForkJoinPool(16, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) -> {}, true), "FORKJOIN", 16),
            Arguments.of(new ForkJoinPool(32, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) -> {}, true), "FORKJOIN", 32),
            Arguments.of(new ForkJoinPool(2, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) -> {}, false), "FORKJOIN", 2),
            Arguments.of(new ForkJoinPool(4, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) -> {}, false), "FORKJOIN", 4),
            Arguments.of(new ForkJoinPool(8, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) -> {}, false), "FORKJOIN", 8),
            Arguments.of(new ForkJoinPool(16, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) -> {}, false), "FORKJOIN", 16),
            Arguments.of(new ForkJoinPool(32, ForkJoinPool.defaultForkJoinWorkerThreadFactory, (t, e) -> {}, false), "FORKJOIN", 32),
            Arguments.of(Executors.newCachedThreadPool(), "CACHED", 0),
            Arguments.of(Executors.newSingleThreadExecutor(), "SINGLE", 0),
            Arguments.of(Executors.newFixedThreadPool(1), "FIXED", 1),
            Arguments.of(Executors.newFixedThreadPool(2), "FIXED", 2),
            Arguments.of(Executors.newFixedThreadPool(4), "FIXED", 4),
            Arguments.of(Executors.newFixedThreadPool(8), "FIXED", 8),
            Arguments.of(Executors.newFixedThreadPool(16), "FIXED", 16),
            Arguments.of(Executors.newFixedThreadPool(32), "FIXED", 32),
            Arguments.of(Executors.newFixedThreadPool(64), "FIXED", 64),
            Arguments.of(Executors.newFixedThreadPool(128), "FIXED", 128)
        );
    }
}
