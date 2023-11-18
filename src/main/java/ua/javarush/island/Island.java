package ua.javarush.island;

public class Island {
    private final Location[][] locations;

    public Island(int x, int y) {
        locations = new Location[x][y];
    }
}
