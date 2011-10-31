package subsume;

import ants.Tile;
import map.WorldMap;

import java.util.ArrayList;
import java.util.List;

//split this into two behaviors?
public class WanderAim extends GoalSeek {
    private static int numGoals = 9;

    public WanderAim(Ant ant) {
        super(ant);
    }

    @Override
    protected void pickGoals() {
        if (goal == null || ant.ants.getMap().isExplored(goal.getRow(), goal.getCol())) {
            potentialGoals=getCloseUnexploredTiles();
        }
    }

    //look for unexplored tile near the centers
    private List<Tile> getCloseUnexploredTiles() {
        List<Tile> retValue = new ArrayList<Tile>();

        WorldMap map = ant.getWorldMap();
        List<Tile> centers = ant.getColonyCenters();

        if (map.allExplored()) {
            return null;
        }
        if (centers.isEmpty()) {
            centers.add(new Tile(0, 0));
        }
        outer:
        for (Tile center : centers) {
            for (int distance = 9; distance < 100; distance += 5) {
                for (int i = -distance; i < distance+1; i += distance) {
                    for (int j = -distance; j < distance+1; j += distance) {
                        int row = center.getRow() + i % map.rows;
                        int col = center.getCol() + j % map.cols;
                        if (row < 0) row += map.rows;
                        if (col < 0) col += map.cols;
                        if (!map.isExplored(row, col)) {
                            retValue.add(new Tile(row, col));
                            if (retValue.size() > numGoals) {
                                break outer;
                            }
                        }
                    }
                }
            }
        }
        return retValue;
    }


}