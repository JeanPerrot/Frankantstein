package subsume;

import ants.Tile;
import subsume.algo.AStar;

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
        SortedMap<Integer,List<Tile>> sortedGoals=trueDistanceFoodTiles(availableFoodTiles);
        if (sortedGoals.isEmpty()){
            return null;
        }
        if (sortedGoals.firstKey()>15){
            return null;
        }
        List<Tile> best=sortedGoals.values().iterator().next();
        if (best.contains(goal)){
            return goal;
        }
        return best.iterator().next();
    }

    SortedMap<Integer, List<Tile>> trueDistanceFoodTiles(Collection<Tile> tiles) {
        SortedMap<Integer, List<Tile>> retValue = new TreeMap<Integer, List<Tile>>();
        for (Tile tile : tiles) {
            AStar.AppraisedPath path = new AStar(ant.getWorldMap(), ant.ants).findPath(ant.tile, tile);
            List<Tile>costed=retValue.get(path.cost);
            if (costed==null){
                costed=new ArrayList<Tile>();
            }
            costed.add(tile);
            retValue.put(path.cost, costed);
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