package subsume;

import map.CostMap;

public class OccupyTerritory extends Layer {

    public OccupyTerritory(Ant ant) {
        super(ant);
    }

    @Override
    protected Decision output() {
        CostMap occupyMap = CostMap.getMyOccupyMap(ant.ants, ant.tile);
        ModifiedAStar.AppraisedPath path = new ModifiedAStar(occupyMap, ant.ants).findPath(ant.tile);
        if (path == null || path.path.isEmpty()) {
            return Decision.DONTKNOW;
        }
        return Decision.move(path.path.get(0));
    }
}