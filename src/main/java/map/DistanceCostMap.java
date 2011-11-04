package map;

import ants.Tile;

import java.util.Set;

class DistanceCostMap extends CostMap {
    private int[][] data;
    private WorldMap map;

    DistanceCostMap(WorldMap map, Set<Tile> costItems, int dimension, boolean substract) {
        this.map = map;
        int multiplier = substract ? -1 : 1;
        data = new int[map.rows][map.cols];
        int[][] cost = new int[map.rows][map.cols];
        for (Tile costItem : costItems) {
            paintCosts(costItem, dimension, cost);
        }
        for (int i = 0; i < map.rows; i++) {
            for (int j = 0; j < map.cols; j++) {
                data[i][j] += multiplier * cost[i][j];
            }
        }
    }

    private void paintCosts(Tile costItem, int dimension, final int[][] costs) {
        TrueDistance.Action action = new TrueDistance.Action() {
            @Override
            public void perform(Tile tile, int cost) {
                costs[tile.getRow()][tile.getCol()] += cost;
            }
        };
        new TrueDistance(map).nearWalk(costItem, dimension, action);
    }


    @Override
    public int getCost(Tile tile) {
        tile = map.getTile(tile.getRow(), tile.getCol());
        return data[tile.getRow()][tile.getCol()];
    }

    @Override
    public int getRows() {
        return map.rows;
    }

    @Override
    public int getCols() {
        return map.cols;
    }
}