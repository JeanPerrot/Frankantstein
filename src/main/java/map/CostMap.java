package map;

import ants.Ants;
import ants.Tile;
import subsume.GoalTracker;
import subsume.Print;

import java.util.Collections;

public abstract class CostMap {
    public static final int OCCUPY_RADIUS = 25;

    int rows;
    int cols;

    protected CostMap(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

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
        PheromoneMap.clearTurn();
    }


    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public void print(int minr, int minc, int dim) {
        for (int row = minr; row < minr + dim; row++) {
            StringBuilder builder = new StringBuilder();
            for (int col = minc; col < minc + dim; col++) {
                int cost = getCost(row, col);
                if (cost == 0) {
                    builder.append(" ");
                } else if (cost < 10) {
                    builder.append(cost);
                } else if (cost < 1000) {
                    builder.append("M");
                } else {
                    builder.append("X");
                }

            }
            Print.println("row " + row + ": " + builder.toString());
        }
    }
}