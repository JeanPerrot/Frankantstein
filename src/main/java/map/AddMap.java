package map;

import ants.Tile;

public class AddMap extends CostMap {
    CostMap[] maps = new CostMap[0];

    public AddMap() {
        super(0, 0);
    }

    public void setMaps(CostMap... maps) {
        this.maps = maps;
        super.rows = maps[0].rows;
        super.cols = maps[0].cols;
    }

    @Override
    public int getCost(Tile tile) {
        int cost = 0;
        for (CostMap map : maps) {
            cost += map.getCost(tile);
        }
        return Math.max(0,cost);
    }

    @Override
    public int getRows() {
        return maps[0].getRows();
    }

    @Override
    public int getCols() {
        return maps[0].getCols();
    }
}