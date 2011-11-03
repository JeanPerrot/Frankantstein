package subsume;

import ants.Tile;

import java.util.*;


//track food gathering
public class GoalTracker {

    private static GoalTracker foodTracker = new GoalTracker();
    private static GoalTracker exploreTrack=new GoalTracker();

    public static GoalTracker getFoodTracker() {
        return foodTracker;
    }

    public static GoalTracker getExploreTrack() {
        return exploreTrack;
    }

    private Map<Tile, Ant> assignments = new HashMap<Tile, Ant>();

    public Ant getAssignee(Tile tile) {
        clean(tile);
        return assignments.get(tile);
    }

    public Ant assign(Tile tile, Ant ant) {
        return assignments.put(tile, ant);
    }

    public boolean isAssigned(Tile tile) {
        return getAssignee(tile) != null;
    }

    private void clean(Tile tile) {
        Ant ant = assignments.get(tile);
        if (ant == null) {
            return;
        }
        if (!ant.isAlive()) {
            assignments.remove(tile);
        }
    }

    public Collection<Tile>getAssignedTiles(){
        Set<Tile>retValue=new HashSet<Tile>();
        for (Tile tile:new HashSet<Tile>(assignments.keySet())){
              if (isAssigned(tile)){
                  retValue.add(tile);
              }
        }
        return retValue;

    }

}