package subsume;

import ants.Tile;

import java.util.*;

public class SeekFood extends GoalSeek {

    public SeekFood(Ant ant) {
        super(ant);
    }

    @Override
    protected void assign(Ant ant, Tile goal) {
        GoalTracker.getFoodTracker().assign(goal, ant);
    }


    @Override
    protected void pickGoals() {
        //reevaluate goals every time.
        GoalTracker foodTracker = GoalTracker.getFoodTracker();
        Tile newGoal = getBestGoal(foodTracker);

        Tile oldGoal=goal;
        goal = newGoal;
        potentialGoals = Arrays.asList(goal);
        foodTracker.assign(oldGoal,null);
        foodTracker.assign(goal,ant);
    }

    private Tile getBestGoal(GoalTracker foodTracker) {
        List<Tile> availableFoodTiles = getCloseAvailableFoodTiles(foodTracker);
        SortedMap<Integer,Tile> sortedGoals=trueDistanceFoodTiles(availableFoodTiles);
        if (sortedGoals.isEmpty()){
            return null;
        }
        if (sortedGoals.firstKey()>10){
            return null;
        }
        Tile bestGoal=sortedGoals.values().iterator().next();
        return bestGoal;
    }

    SortedMap<Integer, Tile> trueDistanceFoodTiles(Collection<Tile> tiles) {
        SortedMap<Integer, Tile> retValue = new TreeMap<Integer, Tile>();
        for (Tile tile : tiles) {
            AStar.AppraisedPath path = new AStar(ant.getWorldMap(), ant.ants).findPath(ant.tile, tile);
            retValue.put(path.cost, tile);
        }
        return retValue;
    }

    private List<Tile> getCloseAvailableFoodTiles(GoalTracker foodTracker) {
        List<Tile> foodTiles = ant.getCloseFoodTiles();
        List<Tile> availableFoodTiles = new ArrayList<Tile>();
        for (Tile tile : foodTiles) {
            if (!foodTracker.isAssigned(tile) || (goal != null && tile.equals(goal))) {
                availableFoodTiles.add(tile);
            }
        }
        return availableFoodTiles;
    }
}