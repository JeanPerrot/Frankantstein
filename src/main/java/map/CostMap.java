package map;

import ants.Ants;
import ants.Tile;
import subsume.GoalTracker;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public abstract class CostMap {
    public static final int OCCUPY_RADIUS = 5;

    public abstract int getCost(Tile tile);

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
            addmap.setMaps(new PassableMap(ants.getMap()), new DistanceCostMap(ants.getMap(), ants.getMyAnts(), OCCUPY_RADIUS, false));
        }
        return occupy;
    }

    private static class DistanceCostMap extends CostMap {
        private int[][] data;

        private DistanceCostMap(WorldMap map, Set<Tile> costItems, int dimension, boolean substract) {
            int multiplier = substract ? -1 : 1;
            data = new int[map.cols][map.rows];
            for (Tile tile : costItems) {
                for (int i = -dimension; i < dimension; i++) {
                    for (int j = -dimension; j < dimension; j++) {
                        Tile wrapped = map.getTile(tile.getRow() + i, tile.getCol() + j);
                        data[wrapped.getCol()][wrapped.getRow()] = 1 * multiplier;
                    }
                }
            }
        }

        @Override
        public int getCost(Tile tile) {
            return data[tile.getRow()][tile.getCol()];
        }
    }


    public static void clearTurn() {
        explore = null;
        occupy = null;
    }

    public static class AddMap extends CostMap {
        CostMap[] maps = new CostMap[0];

        public void setMaps(CostMap... maps) {
            this.maps = maps;
        }

        @Override
        public int getCost(Tile tile) {
            int cost = 0;
            for (CostMap map : maps) {
                cost += map.getCost(tile);
            }
            return cost;
        }
    }


    public static class ExploreMap extends AddMap {

        private UnExploredMap unexplored;

        public ExploreMap(Ants ants, GoalTracker tracker) {
            super();
            unexplored = new UnExploredMap(ants, tracker);
            PassableMap passable = new PassableMap(ants.getMap());
            setMaps(unexplored, passable);
        }

        public void assign(Tile assigned) {
            unexplored.assign(assigned);
        }

        public void unassign(Tile goal) {
            unexplored.unassign(goal);
        }
    }


    public static class UnExploredMap extends CostMap {

        private int[][] costs;
        private WorldMap map;
        private Ants ants;
        private PassableMap passableMap;


        private UnExploredMap(Ants ants, GoalTracker tracker) {

            this.ants = ants;
            this.map = ants.getMap();
            this.passableMap = new PassableMap(map);
            costs = new int[map.rows][map.cols];
            for (int[] row : costs) {
                Arrays.fill(row, 0);
            }
            for (Tile assigned : tracker.getAssignedTiles()) {
                assign(assigned);
            }
        }

        @Override
        public int getCost(Tile tile) {
            return costs[tile.getRow()][tile.getCol()] + passableMap.getCost(tile);
        }

        public void assign(Tile assigned) {
            for (Tile offset : ants.getVisionOffsets()) {
                Tile willBeVisible = ants.getTile(assigned, offset);
                costs[willBeVisible.getRow()][willBeVisible.getCol()] = 1;
            }
        }

        public void unassign(Tile goal) {
            for (Tile offset : ants.getVisionOffsets()) {
                Tile visibleFromGoal = ants.getTile(goal, offset);
                costs[visibleFromGoal.getRow()][visibleFromGoal.getCol()] = 0;
            }
        }
    }

    private static class PassableMap extends CostMap {

        private WorldMap map;

        private PassableMap(WorldMap map) {
            this.map = map;
        }

        @Override
        public int getCost(Tile tile) {
            return getMapCost(tile.getRow(), tile.getCol());
        }

        private int getMapCost(int row, int col) {
            if (!map.isExplored(row, col)) {
                return 0;
            }
            if (map.getIlk(row, col).isPassable()) {
                return 1;
            }
            return 10000;
        }


    }
}