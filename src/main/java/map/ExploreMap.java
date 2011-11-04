package map;

import ants.Ants;
import ants.Tile;
import subsume.GoalTracker;

public class ExploreMap extends AddMap {

    private UnExploredMap unexplored;
    private Ants ants;

    public ExploreMap(final Ants ants, GoalTracker tracker) {
        super();
        this.ants = ants;
        final WorldMap map = ants.getMap();
        unexplored = new UnExploredMap(ants, tracker);
        PassableMap passable = new PassableMap(ants, map, 0);
        CostMap notVisited = new CostMap() {
            @Override
            public int getCost(Tile tile) {
                return map.isExplored(tile.getRow(), tile.getCol()) ? 1 : 0;
            }

            @Override
            public int getRows() {
                return ants.getRows();
            }

            @Override
            public int getCols() {
                return ants.getCols();
            }
        };
        setMaps(unexplored, passable, notVisited);
    }

    public void assign(Tile assigned) {
        unexplored.assign(assigned);
    }

    public void unassign(Tile goal) {
        unexplored.unassign(goal);
    }


}