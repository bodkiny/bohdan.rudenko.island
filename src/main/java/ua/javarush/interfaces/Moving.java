package ua.javarush.interfaces;

import ua.javarush.island.Island;
import ua.javarush.island.Location;

public interface Moving {
    void move(Island island, Location currentLocation);
}
