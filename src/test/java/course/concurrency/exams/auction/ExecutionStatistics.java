package course.concurrency.exams.auction;

import static java.util.stream.Collectors.summarizingLong;

import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.concurrent.ConcurrentHashMap;

public class ExecutionStatistics {

    private ConcurrentHashMap<String, List<Long>> stat = new ConcurrentHashMap<>();

    public void addData(String method, Long duration) {
        stat.putIfAbsent(method, new ArrayList<>());
        stat.get(method).add(duration);
    }

    public void printStatistics() {
        stat.forEach((k,v) -> System.out.println(k + ": " + listToStat(v)));
    }

    private String listToStat(List<Long> values) {
        LongSummaryStatistics stat = values.stream().collect(summarizingLong(Long::valueOf));
        return String.format("%.0f (%d-%d)", stat.getAverage(), stat.getMin(), stat.getMax());
    }
}
