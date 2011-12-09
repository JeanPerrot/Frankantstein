package subsume;

import ants.Aim;
import ants.Tile;
import map.CostMap;
import subsume.algo.AStar;
import subsume.algo.ModifiedAStar;
import util.Print;

import java.util.List;

public class Explore extends Layer {

    private Tile goal;
    private int costLimit=50;
    private GoalTracker exploreTrack = GoalTracker.getExploreTrack();

    public Explore(Ant ant) {
        super(ant);
    }

    @Override
    protected Decision output() {
        if (isGoalInvalid()) {
            if (goal != null) {
                unassignGoal();
            }
            goal = choseNewGoal();
            if (goal != null) {
                assignGoal();
            }
        }
        if (goal == null) {
            return Decision.DONTKNOW;
        }
        return aimAtGoal();
    }

    private void assignGoal() {
        Print.println("assigning explore goal "+goal);
        exploreTrack.assign(goal, ant);
        CostMap.getExploreMap(ant.ants, exploreTrack).assign(goal);
    }

    private void unassignGoal() {
        exploreTrack.assign(goal, null);
        CostMap.getExploreMap(ant.ants, exploreTrack).unassign(goal);
    }

    private Decision aimAtGoal() {
        Aim aim = AStar.firstStep(ant.ants, ant.tile, goal);
        if (aim == null) {
            return Decision.DONTKNOW;
        }
        return Decision.move(aim);
    }

    private Tile choseNewGoal() {
        if (ant.getWorldMap().allExplored()) {
            Print.println("EVERYTHING WAS EXPLORED!");
            return null;
        }
        //cost map is going to be given by the WorldMap.
        CostMap map = CostMap.getExploreMap(ant.ants, GoalTracker.getExploreTrack());
        ModifiedAStar.AppraisedPath path = new ModifiedAStar(map, ant.ants,costLimit).findPath(ant.tile);
        if (path == null) {
            return null;
        }
        if (!path.goalReached) {
            return null;
        }
        if (path.path.isEmpty()) {
            return null;
        }
        return followPath(ant.tile, path.path);

    }

    private Tile followPath(Tile tile, List<Aim> path) {
        Tile retValue = tile;
        for (Aim aim : path) {
            retValue = ant.ants.getTile(retValue, aim);
        }
        return retValue;
    }

    private boolean isGoalInvalid() {
        return goal == null || ant.getWorldMap().isExplored(goal.getRow(), goal.getCol());
    }

    @Override
    public String explain() {
        return super.explain() + " for goal (" + goal + ")";
    }
}