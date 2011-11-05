package map;

import ants.Aim;
import ants.Tile;

import java.util.*;

public class TrueWalk {

    private WorldMap map;

    public TrueWalk(WorldMap map) {
        this.map = map;
    }

    public void nearWalk(Tile tile, int initialCost, Action action) {
        Set<Tile> visited = new HashSet<Tile>();
        Set<Tile> nonCostedFrontier = new HashSet<Tile>();
        SortedSet<CostedTile> frontier = new TreeSet<CostedTile>();
        frontier.add(new CostedTile(tile, initialCost));
        nonCostedFrontier.add(tile);

        while (!frontier.isEmpty()) {
            CostedTile toVisit = frontier.last();
            frontier.remove(toVisit);
            nonCostedFrontier.remove(tile);
            visited.add(toVisit);
            if (!map.getIlk(toVisit.getRow(), toVisit.getCol()).isPassable()) {
                continue;
            }
            action.perform(toVisit,toVisit.cost);
            if (toVisit.cost == 1) continue;

            for (Tile neighbor : getNeighbors(toVisit)) {
                if (visited.contains(neighbor)) continue;
                //this should not ever be better than what is in the frontier?
                if (nonCostedFrontier.contains(neighbor)) continue;

                frontier.add(new CostedTile(neighbor, toVisit.cost - 1));
                nonCostedFrontier.add(neighbor);
            }
        }
    }

    private static class CostedTile extends Tile {
        static int classDiscriminant = 0;
        int cost;
        int discriminant;

        public CostedTile(Tile tile, int cost) {
            super(tile.getRow(), tile.getCol());
            this.cost = cost;
            this.discriminant = classDiscriminant++;
        }

        @Override
        public int compareTo(Tile o) {
            if (o instanceof CostedTile) {
                CostedTile cast = (CostedTile) o;
                if (cast.cost == cost) {
                    return discriminant - cast.discriminant;
                }
                return cost - cast.cost;
            }
            return super.compareTo(o);
        }

        public String toString() {
            return super.toString() + " - " + cost;
        }
    }


    private List<Tile> getNeighbors(Tile tile) {
        List<Tile> retValue = new ArrayList<Tile>();
        for (Aim aim : Aim.values()) {
            retValue.add(map.getTile(tile.getRow() + aim.getRowDelta(), tile.getCol() + aim.getColDelta()));
        }
        return retValue;
    }

    public static interface Action{
        void perform(Tile tile, int cost);
    }



}