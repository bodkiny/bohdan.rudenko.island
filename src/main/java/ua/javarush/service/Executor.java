package ua.javarush.service;

import ua.javarush.island.Island;
import ua.javarush.util.factories.LivingBeingFactory;
import ua.javarush.util.loaders.ApplicationLoader;
import ua.javarush.util.statistic.StatisticsDisplayer;

import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Executor {
    public static final int MAX_DAYS_OF_SIMULATION = 1000;
    private final TaskHandler taskHandler = new TaskHandler();
    private final ExecutorService executorService = Executors.newWorkStealingPool();
    private final LivingBeingFactory livingBeingFactory = new LivingBeingFactory();
    private final StatisticsDisplayer statisticsDisplayer = new StatisticsDisplayer();

    public void execute() {
        System.out.println("--- Initialize the island ---");
        int daysOfSimulation = getNumberOfDays();

        Island island = new ApplicationLoader(livingBeingFactory).load();

        System.out.println("\n\n++++ Beginning of existence ++++\n");
        statisticsDisplayer.getIslandStatistics(island);


        IntStream.rangeClosed(1, daysOfSimulation)
                .forEach(day -> {
                    try {
                        simulateDay(island, day);
                    } catch (InterruptedException e) {
                        throw new RuntimeException("Error while simulating day#" + day, e);
                    }
                });

        System.out.println("\n\n++++ The end of the island lifetime ++++\n");
        statisticsDisplayer.getIslandStatistics(island);
        executorService.shutdown();
    }

    private void simulateDay(Island island, int day) throws InterruptedException {
        System.out.println("\n<<<<<day#" + day + ">>>>>");
        taskHandler.processFeedingOnIsland(island, executorService);
        taskHandler.processReproductionOnIsland(island, executorService);
        taskHandler.processMovementOnIsland(island, executorService);
        statisticsDisplayer.getIslandStatistics(island);
    }

    private int getNumberOfDays() {
        System.out.print("How many days you want the island to live? (range 1 - " + MAX_DAYS_OF_SIMULATION + "): ");
        String inputLine = new Scanner(System.in).nextLine();

        return Optional.ofNullable(inputLine)
                .map(Integer::parseInt)
                .filter(size -> size > 0 && size <= MAX_DAYS_OF_SIMULATION)
                .orElseThrow(() -> new IllegalArgumentException("Invalid input value"));
    }
}
