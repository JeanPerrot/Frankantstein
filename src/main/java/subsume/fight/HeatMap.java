package subsume.fight;


import ants.Ants;
import ants.Tile;

import java.util.*;

/**
 * for each tile, how many enemy ants is the tile exposed to (for a given ant owner)
 */
public class HeatMap {

    private int[][] heatMap;
    private int rows;
    private int cols;

    private static Map<Integer, HeatMap> heatMaps = new HashMap<Integer, HeatMap>();
    private static HeatMap myHeatMap;

    public static HeatMap getMyHeat(Ants ants) {
        if (myHeatMap == null) {
            myHeatMap = new HeatMap(ants.getMyAnts(), ants.getAttackOffsets(), ants.getRows(), ants.getCols());
        }
        return myHeatMap;
    }

    public static HeatMap getHeat(Ants ants, int me) {
        HeatMap retValue = heatMaps.get(me);
        if (retValue == null) {
            retValue = new HeatMap(ants, me);
            heatMaps.put(me, retValue);
        }
        return retValue;
    }

    public static void clearTurn() {
        heatMaps.clear();
        myHeatMap = null;
    }


    public HeatMap(Ants ants, int owner) {
        Set<Tile> allEnemies = new HashSet<Tile>();
        Set<Integer> allOwnersButMe = new HashSet<Integer>(ants.getVisibleEnemies());
        allOwnersButMe.remove(owner);

        for (Integer anOwner : allOwnersButMe) {
            allEnemies.addAll(ants.getEnemyAnts(anOwner));
        }
        if (owner != 0) {
            //my ants are  your enemies!
            allEnemies.addAll(ants.getMyAnts());
        }

        init(allEnemies, ants.getAttackOffsets(), ants.getRows(), ants.getCols());
    }


    public HeatMap(Set<Tile> enemyAnts, Set<Tile> attackOffsets, int rows, int cols) {
        init(enemyAnts, attackOffsets, rows, cols);
    }

    private void init(Set<Tile> enemyAnts, Set<Tile> attackOffsets, int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        heatMap = new int[rows][cols];
        for (int[] row : heatMap) {
            Arrays.fill(row, 0);
        }

        for (Tile tile : enemyAnts) {
            for (Tile offset : attackOffsets) {
                int row = tile.getRow() + offset.getRow();
                int col = tile.getRow() + offset.getRow();
                row = (row + rows) % rows;
                col = (col + cols) % cols;
                heatMap[row][col] = heatMap[row][col] + 1;
            }
        }
    }


    public int exposure(int row, int col) {
        row = (row + rows) % rows;
        col = (col + cols) % cols;
        return heatMap[row][col];
    }

}