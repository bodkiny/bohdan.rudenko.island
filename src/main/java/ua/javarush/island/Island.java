package ua.javarush.island;

import lombok.Getter;

@Getter
public class Island {
    private final Location[][] locations;

    public Island(int x, int y) {
        locations = new Location[x][y];
    }
}
