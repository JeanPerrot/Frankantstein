package subsume;

import map.CostMap;
import subsume.algo.ModifiedAStar;

public class OccupyTerritory extends Layer {

    private int costLimit=30;

    public OccupyTerritory(Ant ant) {
        super(ant);
    }

    @Override
    protected Decision output() {
        CostMap occupyMap = CostMap.getMyOccupyMap(ant.ants, ant.tile);
        ModifiedAStar.AppraisedPath path = new ModifiedAStar(occupyMap, ant.ants,costLimit).findPath(ant.tile);
        if (path == null || path.path.isEmpty()) {
            return Decision.DONTKNOW;
        }
        return Decision.move(path.path.get(0));
    }
}