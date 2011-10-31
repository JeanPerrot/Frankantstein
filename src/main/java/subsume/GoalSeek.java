package subsume;

import ants.Aim;
import ants.Tile;

import java.util.ArrayList;
import java.util.List;

public abstract class GoalSeek extends Layer {

    List<Tile> potentialGoals = new ArrayList<Tile>();
    Tile goal;

    public GoalSeek(Ant ant) {
        super(ant);
    }

    @Override
    public Aim output() {
        pickGoals();
//        if (goal==null){
        goal = evaluateAllGoals();
//        }
        if (goal != null) {
            //is there a way to get to it?
            Aim direction = aimAt(goal);
            if (aimAt(goal) != null) {
                return direction;
            }
        }
        return null;
    }

    protected abstract void pickGoals();

    protected Tile evaluateAllGoals() {
        int minCost = Integer.MAX_VALUE;
        Tile bestGoal = null;
        for (Tile goal : potentialGoals) {
            AStar.AppraisedPath path = new AStar(ant.ants.getMap(), ant.ants).findPath(ant.tile, goal);
            if (path == null) continue;
            if (path.cost < minCost) {
                bestGoal = goal;
                minCost = path.cost;
            }
        }
        return bestGoal;
    }

    private Aim aimAt(Tile goal) {
        AStar.AppraisedPath path = new AStar(ant.ants.getMap(), ant.ants).findPath(ant.tile, goal);
        if (path != null) {
            return path.path.get(0);
        }
        return null;
    }
}