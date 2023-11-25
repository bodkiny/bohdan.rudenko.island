package ua.javarush.util.factories;

import lombok.Getter;
import ua.javarush.living_beings.LivingBeing;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class LivingBeingFactory {
    @Getter
    private static final Map<Class<? extends LivingBeing>, LivingBeing> livingBeings = new ConcurrentHashMap<>();

    public void addLivingBeing(LivingBeing livingBeing){
        livingBeings.put(livingBeing.getClass(), livingBeing);
    }

    public static LivingBeing getLivingBeingByClass(Class<? extends LivingBeing> clazz){
        return getLivingBeingCopy(livingBeings.get(clazz));
    }

    public static Class<? extends LivingBeing> getLivingBeingClassByName(String className){
        Objects.requireNonNull(className, "className must not be null");
        return livingBeings.entrySet().stream()
                .filter(entry -> entry.getValue().getName().equals(className))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private static <T> T getLivingBeingCopy(T livingBeing) {
        try {
            return generateNewLivingBeing(livingBeing);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e){
            throw new RuntimeException(e);
        }
    }

    private static <T> T generateNewLivingBeing(T livingBeing) throws InstantiationException,
                                                                      IllegalAccessException,
                                                                      InvocationTargetException,
                                                                      NoSuchMethodException {
        Class<?> livingBeingClass = livingBeing.getClass();
        T newLivingBeing = (T) livingBeingClass.getConstructor().newInstance();
        while (livingBeingClass != null){
            Field[] declaredFields = livingBeingClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                declaredField.set(newLivingBeing, declaredField.get(livingBeing));
            }
            livingBeingClass = livingBeingClass.getSuperclass();
        }
        return newLivingBeing;
    }
}
