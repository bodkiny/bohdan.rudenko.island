package ua.javarush.util.statistic;

import ua.javarush.island.Island;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsDisplayer {
    public void getIslandStatistics(Island island){
        Map<String, Long> statistics = Arrays.stream(island.getLocations())
                .flatMap(Arrays::stream)
                .flatMap(location -> location.getLivingBeings().entrySet().stream()
                        .map(entry -> Map.entry(entry.getKey().getSimpleName(), entry.getValue().size())))
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingLong(Map.Entry::getValue)));

        System.out.println("--- Island statistics ---");
        statistics.forEach((name, count) ->
                System.out.println(name + ": " + count));
    }
}
