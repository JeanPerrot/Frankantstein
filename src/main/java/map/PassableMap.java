package map;

import ants.Ants;
import ants.Tile;

class PassableMap extends CostMap {

    private WorldMap map;
    private int passableCost;
    private int impassableCost = 1000000;
    private int hillCost = 1000;
    private Ants ants;

    PassableMap(Ants ants, WorldMap map, int passableCost) {
        this.ants = ants;
        this.map = map;
        this.passableCost = passableCost;

    }

    @Override
    public int getCost(Tile tile) {
        return getMapCost(tile.getRow(), tile.getCol());
    }

    private int getMapCost(int row, int col) {
        if (ants.getMyHills().contains(new Tile(row, col))) {
            return hillCost;
        }
        if (map.getIlk(row, col).isPassable()) {
            return passableCost;
        }
        return impassableCost;
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