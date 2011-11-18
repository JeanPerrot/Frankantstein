package map;

import ants.Ilk;
import ants.Tile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

//accumulate static belief about the world
public class WorldMap {

    public final int rows;
    public final int cols;
    private Ilk[][] world;
    private boolean[][] explored;
    private int totalExplored = 0;
    private Set<Tile> enemyHills = new HashSet<Tile>();

    public WorldMap(int rows, int cols) {
        this.cols = cols;
        this.rows = rows;
        explored = new boolean[rows][cols];
        for (boolean[] row : explored) {
            Arrays.fill(row, false);
        }
        world = new Ilk[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                world[i][j] = Ilk.LAND;
            }
        }
    }

    public void addKnowledge(Tile tile, Ilk ilk) {
        markExplored(tile.getRow(), tile.getCol());
        world[tile.getRow()][tile.getCol()] = ilk;
    }

    public void markExplored(int row, int col) {
        boolean previous = explored[row][col];
        explored[row][col] = true;
        if (!previous) {
            totalExplored++;
        }
    }

    public Tile getTile(int row, int col) {
        if (row < 0) row += rows;
        row = row % rows;
        if (col < 0) col += cols;
        col = col % cols;
        return new Tile(row, col);
    }

    public Ilk getIlk(int row, int col) {
        row = (row + rows) % rows;
        col = (col + cols) % cols;
        return world[row][col];
    }

    public boolean isExplored(int row, int col) {
        row = row % rows;
        col = col % cols;
        return explored[row][col];
    }

    public boolean allExplored() {
        return totalExplored == rows * cols;
    }

    public void addEnemyHill(Tile tile) {
        enemyHills.add(tile);
    }

    public void removeEnemyHill(Tile tile) {
        enemyHills.remove(tile);
    }

    public Set<Tile> getEnemyHills() {
        return enemyHills;
    }
}