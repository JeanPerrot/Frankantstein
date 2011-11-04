package map;

import ants.Ants;
import ants.Tile;
import subsume.GoalTracker;

import java.util.Arrays;

public class UnExploredMap extends CostMap {

    private int[][] costs;
    private WorldMap map;
    private Ants ants;

    UnExploredMap(Ants ants, GoalTracker tracker) {

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

    @Override
    public int getRows() {
        return ants.getRows();
    }

    @Override
    public int getCols() {
        return ants.getCols();
    }
}