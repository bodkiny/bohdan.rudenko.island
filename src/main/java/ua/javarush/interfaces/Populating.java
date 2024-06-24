package ua.javarush.interfaces;

import ua.javarush.island.Location;
import ua.javarush.living_beings.LivingBeing;

public interface Populating {
    <T extends LivingBeing> T populate(Location location);
}
