package ua.javarush.service;

import ua.javarush.interfaces.Feeding;
import ua.javarush.interfaces.Moving;
import ua.javarush.island.Island;
import ua.javarush.living_beings.LivingBeing;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class TaskHandler {
    public void processFeedingOnIsland(Island island, ExecutorService executorService) throws InterruptedException {
        List<Callable<Void>> tasks = Arrays.stream(island.getLocations())
                .flatMap(Arrays::stream)
                .map(location -> (Callable<Void>) () -> {
                    location.getLivingBeings().values().stream()
                            .flatMap(List::stream)
                            .filter(Feeding.class::isInstance)
                            .map(Feeding.class::cast)
                            .forEach(feedingLivingBeing -> feedingLivingBeing.eat(location));

                    return null;
                })
                .toList();

        executorService.invokeAll(tasks);
    }

    public void processReproductionOnIsland(Island island, ExecutorService executorService) throws InterruptedException {
        List<Callable<Void>> tasks = Arrays.stream(island.getLocations())
                .flatMap(Arrays::stream)
                .map(location -> (Callable<Void>) () -> {
                    location.getLivingBeings().values().forEach(livingBeingsList ->
                                    livingBeingsList.forEach(livingBeing -> {
                                        LivingBeing newborn = livingBeing.populate(location);
                                        if(newborn != null){
                                            livingBeingsList.add(newborn);
                                        }
                                    })
                    );

                    makeAllLivingBeingsReadyForReproduction(location.getLivingBeings());
                    return null;
                })
                .toList();

        executorService.invokeAll(tasks);
    }

    private void makeAllLivingBeingsReadyForReproduction(Map<Class<? extends LivingBeing>, List<LivingBeing>> livingBeings) {
        livingBeings.values().stream()
                .filter(list -> !list.isEmpty())
                .flatMap(List::stream)
                .forEach(livingBeing -> livingBeing.setHasAlreadyReproduced(false));
    }

    public void processMovementOnIsland(Island island, ExecutorService executorService) throws InterruptedException {
        List<Callable<Void>> tasks = Arrays.stream(island.getLocations())
                .flatMap(Arrays::stream)
                .map(location -> (Callable<Void>) () -> {
                    location.getLivingBeings().values().stream()
                            .flatMap(List::stream)
                            .filter(Moving.class::isInstance)
                            .map(Moving.class::cast)
                            .forEach(movingLivingBeing -> movingLivingBeing.move(island, location));

                    return null;
                })
                .toList();

        executorService.invokeAll(tasks);
    }
}
