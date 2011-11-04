package map;

import ants.Aim;
import ants.Tile;

import java.util.*;

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

    private void paintCosts(Tile tile, int initialCost, int[][] costs) {
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
            costs[toVisit.getRow()][toVisit.getCol()] += toVisit.cost;
            if (toVisit.cost == 1) continue;

            for (Tile neighbor : getNeighbors(toVisit)) {
                if (visited.contains(neighbor)) continue;
                //this should not ever be better than what is in the frontier?
                if (nonCostedFrontier.contains(neighbor)) continue;

                frontier.add(new CostedTile(tile, toVisit.cost - 1));
                nonCostedFrontier.add(neighbor);
            }
        }

    }

    private static class CostedTile extends Tile {
        static int discrimant = 0;
        int cost;
        int discriminant;

        public CostedTile(Tile tile, int cost) {
            super(tile.getRow(), tile.getCol());
            this.cost = cost;
            this.discriminant = discrimant++;
        }

        @Override
        public int compareTo(Tile o) {
            if (o instanceof CostedTile) {
                CostedTile cast = (CostedTile) o;
                if (cast.cost == cost) {
                    return discriminant - cast.discriminant;
                }
                return cost = cast.cost;
            }
            return super.compareTo(o);
        }


    }


    private List<Tile> getNeighbors(Tile tile) {
        List<Tile> retValue = new ArrayList<Tile>();
        for (Aim aim : Aim.values()) {
            retValue.add(map.getTile(tile.getRow() + aim.getRowDelta(), tile.getCol() + aim.getColDelta()));
        }
        return retValue;

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