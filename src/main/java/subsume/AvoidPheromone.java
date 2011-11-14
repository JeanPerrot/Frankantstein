package subsume;

import map.CostMap;
import map.PheromoneMap;
import subsume.algo.ModifiedAStar;

public class AvoidPheromone extends Layer {

    private int costLimit = 50;

    public AvoidPheromone(Ant ant) {
        super(ant);
    }

    @Override
    protected Decision output() {
        CostMap occupyMap = PheromoneMap.getMyPheromoneMap(ant.ants, ant.tile);
        ModifiedAStar.AppraisedPath path = new ModifiedAStar(occupyMap, ant.ants, costLimit).findPath(ant.tile);
        if (path == null || path.path.isEmpty()) {
            return Decision.DONTKNOW;
        }
        return Decision.move(path.path.get(0));
    }

}