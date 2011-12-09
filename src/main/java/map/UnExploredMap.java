package map;

import ants.Ants;
import ants.Tile;
import subsume.GoalTracker;
import subsume.algo.TrueWalk;

import java.util.Arrays;

public class UnExploredMap extends CostMap {

    private int[][] costs;
    private WorldMap map;
    private Ants ants;

    UnExploredMap(Ants ants, GoalTracker tracker) {
        super(ants.getMap().rows, ants.getMap().cols);

        this.ants = ants;
        this.map = ants.getMap();
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
        return costs[tile.getRow()][tile.getCol()];
    }

    private void paintCosts(Tile costItem, int dimension, final int newCost) {
        TrueWalk.Action action = new TrueWalk.Action() {
            @Override
            public void perform(Tile tile, int cost) {
                costs[tile.getRow()][tile.getCol()] = newCost;
            }
        };
        new TrueWalk(map).nearWalk(costItem, dimension, action);
    }

    public void assign(Tile assigned) {
        int viewRadius=7;
        paintCosts(assigned,viewRadius,1);
    }

    public void unassign(Tile goal) {
        int viewRadius=7;
        paintCosts(goal,viewRadius,0);
    }

    @Override
    public int getRows() {
        return ants.getRows();
    }

    @Override
    public int getCols() {
        return ants.getCols();
    }
}