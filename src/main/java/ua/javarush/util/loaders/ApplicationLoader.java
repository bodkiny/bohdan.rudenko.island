package ua.javarush.util.loaders;

import org.reflections.Reflections;
import ua.javarush.annotation.Configuration;
import ua.javarush.island.Island;
import ua.javarush.living_beings.LivingBeing;
import ua.javarush.util.factories.LivingBeingFactory;

import java.util.Scanner;
import java.util.Set;

public class ApplicationLoader {
    private final String classPackage = "ua.javarush.living_beings";
    private final IslandLoader islandLoader = new IslandLoader(new Scanner(System.in));
    private final LivingBeingFactory livingBeingFactory;
    private final JsonConfigLoader jsonConfigLoader = new JsonConfigLoader();

    public ApplicationLoader(LivingBeingFactory livingBeingFactory) {
        this.livingBeingFactory = livingBeingFactory;
    }

    public Island load() {
        loadLivingBeings();
        return loadIsland();
    }

    private Island loadIsland() {
        return islandLoader.loadIsland();
    }

    private void loadLivingBeings() {
        Reflections reflections = new Reflections(classPackage);
        Set<Class<? extends LivingBeing>> subTypes = reflections.getSubTypesOf(LivingBeing.class);

        try {
            subTypes.stream()
                    .filter(clazz -> clazz.isAnnotationPresent(Configuration.class))
                    .map(jsonConfigLoader::getObject)
                    .forEach(livingBeingFactory::addLivingBeing);
        } catch (Exception e) {
            throw new RuntimeException("Error loading living beings", e);
        }
    }
}
