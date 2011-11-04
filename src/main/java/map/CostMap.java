package map;

import ants.Ants;
import ants.Tile;
import subsume.GoalTracker;

import java.util.*;

public abstract class CostMap {
    public static final int OCCUPY_RADIUS = 15;

    public abstract int getCost(Tile tile);

    public int getCost(int row, int col) {
        return getCost(new Tile(row, col));
    }

    private static ExploreMap explore = null;
    private static CostMap occupy = null;

    public static ExploreMap getExploreMap(Ants ants, GoalTracker exploreTrack) {
        if (explore == null) {
            explore = new ExploreMap(ants, exploreTrack);
        }
        return explore;
    }

    public static CostMap getMyOccupyMap(Ants ants, Tile myTile) {
        CostMap myMap = new DistanceCostMap(ants.getMap(), Collections.singleton(myTile), OCCUPY_RADIUS, true);
        AddMap addMap = new AddMap();
        addMap.setMaps(myMap, getOccupyMap(ants));
        return addMap;

    }

    public static CostMap getOccupyMap(Ants ants) {
        if (occupy == null) {
            AddMap addmap = new AddMap();
            addmap.setMaps(new PassableMap(ants, ants.getMap(), 0), new DistanceCostMap(ants.getMap(), ants.getMyAnts(), OCCUPY_RADIUS, false));
            occupy = addmap;
        }
        return occupy;
    }


    public static void clearTurn() {
        explore = null;
        occupy = null;
    }


    public abstract int getRows();

    public abstract int getCols();

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int row = 0; row < getRows(); row++) {
            for (int col = 0; col < getCols(); col++) {
                builder.append(getCost(row, col));
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}