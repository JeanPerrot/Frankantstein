package map;

import ants.Ants;
import ants.Tile;

import java.util.Collection;
import java.util.Collections;

public class PheromoneMap extends CostMap {

    public static final int PHEROMONE_RADIUS = 10;
    private static PheromoneMap pheromoneMap;

    public static void clearTurn() {
        if (pheromoneMap != null) {
            pheromoneMap.decay();
        }
    }

    public static CostMap getMyPheromoneMap(Ants ants, Tile myTile) {
        CostMap myMap = new DistanceCostMap(ants.getMap(), Collections.singleton(myTile), PHEROMONE_RADIUS, true);
        AddMap addMap = new AddMap();
        PassableMap passable = new PassableMap(ants, ants.getMap(), 0);
        addMap.setMaps(getPheromoneMap(ants), passable);
        return addMap;
    }

    public static PheromoneMap getPheromoneMap(Ants ants) {
        if (pheromoneMap == null) {
            pheromoneMap = new PheromoneMap(ants.getMap());
        }
        return pheromoneMap;
    }

    private int decayTime = 30;
    private int slopeFactor=3;
    private int radius = PHEROMONE_RADIUS;
    private float[][] data;
    private WorldMap map;

    protected PheromoneMap(WorldMap map) {
        super(map.rows, map.cols);
        this.map = map;
        this.data = new float[rows][cols];
    }

    public void decay() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                float f = data[i][j];
                data[i][j] = Math.max(0, f - slopeFactor*(float)radius / decayTime);
            }
        }
    }

    public void markTerritory(Collection<Tile> tiles) {
        for (Tile tile : tiles) {
            markTerritory(tile);
        }
    }


    public void markTerritory(Tile tile) {
        TrueWalk.Action action = new TrueWalk.Action() {
            @Override
            public void perform(Tile tile, int cost) {
                data[tile.getRow()][tile.getCol()] = Math.max(slopeFactor*cost,data[tile.getRow()][tile.getCol()]);
            }
        };
        new TrueWalk(map).nearWalk(tile, radius, action);
    }


    @Override
    public int getCost(Tile tile) {
        float inMap = data[tile.getRow()][tile.getCol()];
        return Math.round(inMap);
    }
}