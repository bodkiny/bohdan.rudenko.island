package ua.javarush.living_beings.animals;

import lombok.Getter;
import lombok.Setter;
import ua.javarush.interfaces.Feeding;
import ua.javarush.interfaces.Moving;
import ua.javarush.island.Direction;
import ua.javarush.island.Island;
import ua.javarush.island.Location;
import ua.javarush.living_beings.LivingBeing;
import ua.javarush.util.factories.LivingBeingFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public abstract class Animal extends LivingBeing implements Feeding, Moving {
    private double initialWeight;
    private double currentWeight;
    private double satingWeight;
    private int maxTravelDistance;
    private Map<String, Integer> preferredFood;
    private int maxConsumeChance = 100;
    private double minWeightBeforeDeathPercentage = 0.5;

    @Override
    public void move(Island island, Location currentLocation) {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        Location[][] locations = island.getLocations();
        Direction[] directions = Direction.values();

        int x = currentLocation.getX();
        int y = currentLocation.getY();
        for (int i = 0; i < current.nextInt(maxTravelDistance); i++) {
            int newX = getRandomX(current, directions, x);
            int newY = getRandomY(current, directions, y);

            if(locationIsValid(newX, newY, locations)){
                x = newX;
                y = newY;
                break;
            }
        }

        currentLocation.removeLivingBeing(this);
        locations[x][y].addLivingBeing(this);
    }

    private boolean locationIsValid(int x, int y, Location[][] locations) {
        return x >= 0 && y >= 0 && x < locations.length && y < locations[0].length &&
                (locations[x][y].getLivingBeings().get(this.getClass()) == null ||
                 locations[x][y].getLivingBeings().get(this.getClass()).size() < getMaxPopulation());
    }

    @Override
    public void eat(Location location) {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        List<String> potentialFoodTypes = getPotentialFood();

        if(!potentialFoodTypes.isEmpty()){
            String selectedFood = potentialFoodTypes.get(current.nextInt(potentialFoodTypes.size()));
            int chanceToEat = preferredFood.get(selectedFood);

            if(chanceToEat >= current.nextInt(maxConsumeChance)){
                tryToEat(selectedFood, location);
            }

            currentWeight -= satingWeight;
            if(minimumPossibleWeightThresholdHasBeenExceed()){
                location.removeLivingBeing(this);
            }
        }
    }

    private static int getRandomY(ThreadLocalRandom current, Direction[] directions, int y) {
        return y + directions[current.nextInt(directions.length)].getY();
    }

    private static int getRandomX(ThreadLocalRandom current, Direction[] directions, int x) {
        return x + directions[current.nextInt(directions.length)].getX();
    }

    private boolean minimumPossibleWeightThresholdHasBeenExceed() {
        return currentWeight / ((Animal) LivingBeingFactory.getLivingBeingByClass(getClass())).getInitialWeight() < minWeightBeforeDeathPercentage;
    }

    private List<String> getPotentialFood() {
        return preferredFood.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .map(Map.Entry::getKey)
                .filter(element -> LivingBeingFactory.getLivingBeingClassByName(element) != null)
                .toList();
    }

    private void tryToEat(String selectedFood, Location location) {
       location.getLivingBeings().computeIfPresent(LivingBeingFactory.getLivingBeingClassByName(selectedFood), (food, livingBeingList) -> {
           if(!livingBeingList.isEmpty()){
               LivingBeing livingBeing = livingBeingList.get(0);
               double saturation = (livingBeing instanceof Animal) ? ((Animal) livingBeing).getCurrentWeight() : satingWeight;
               currentWeight += saturation;
               livingBeingList.remove(0);
           }
           return livingBeingList;
       });
    }
}
