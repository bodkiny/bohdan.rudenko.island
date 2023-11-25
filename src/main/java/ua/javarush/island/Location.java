package ua.javarush.island;

import lombok.Getter;
import ua.javarush.living_beings.LivingBeing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Location {
    private final int x;
    private final int y;
    private final Map<Class<? extends LivingBeing>, List<LivingBeing>> livingBeings;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
        this.livingBeings = new ConcurrentHashMap<>();
    }

    public void addLivingBeing(LivingBeing livingBeing){
        livingBeings
                .computeIfAbsent(livingBeing.getClass(), key -> new ArrayList<>())
                .add(livingBeing);
    }

    public void removeLivingBeing(LivingBeing livingBeing){
        Class<? extends LivingBeing> entityClass = livingBeing.getClass();
        livingBeings.computeIfPresent(entityClass, (k, v) -> {
            v.remove(livingBeing);
            return v;
        });
    }
}
