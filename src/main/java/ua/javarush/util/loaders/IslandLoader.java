package ua.javarush.util.loaders;

import ua.javarush.island.Island;
import ua.javarush.island.Location;
import ua.javarush.living_beings.LivingBeing;
import ua.javarush.util.factories.LivingBeingFactory;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class IslandLoader {
    private final Scanner scanner;
    private final int maxX = 20;
    private final int maxY = 20;

    public IslandLoader(Scanner scanner) {
        this.scanner = scanner;
    }

    public Island loadIsland() {
        int x = getIslandDimension("X", maxX);
        int y = getIslandDimension("Y", maxY);
        Island island = new Island(x, y);
        fillIsland(island.getLocations());
        return island;
    }

    private void fillIsland(Location[][] locations) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < locations.length; i++) {
            for (int j = 0; j < locations[i].length; j++) {
                Map<Class<? extends LivingBeing>, LivingBeing> livingBeings = LivingBeingFactory.getLivingBeings();
                locations[i][j] = new Location(i, j);

                for (var entry : livingBeings.entrySet()) {
                    int maxPopulation = entry.getValue().getMaxPopulation();
                    int randomPopulation = random.nextInt(maxPopulation);

                    for (int k = 0; k < randomPopulation; k++) {
                        locations[i][j].addLivingBeing(LivingBeingFactory.getLivingBeingByClass(entry.getKey()));
                    }
                }
            }
        }
    }

    private int getIslandDimension(String dimension, int maxSizeDimension) {
        while (true) {
            try {
                System.out.print("Please, enter Island " + dimension + " size (range 1 - " + maxSizeDimension + "): ");
                int size = Integer.parseInt(scanner.nextLine());
                if (size > 0 && size <= maxSizeDimension) {
                    return size;
                }
                throw new IllegalArgumentException("Invalid input size. Check input data.");
            } catch (NumberFormatException e) {
                System.out.println("Input is not a number. Check input data.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
