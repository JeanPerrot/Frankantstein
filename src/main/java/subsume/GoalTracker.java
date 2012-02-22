package subsume;

import ants.Tile;

import java.util.*;


//track food gathering
public class GoalTracker {

    private static GoalTracker foodTracker = new GoalTracker();
    private static GoalTracker exploreTrack = new GoalTracker();
    private static GoalTracker borderTracker = new GoalTracker();

    public static GoalTracker getFoodTracker() {
        return foodTracker;
    }

    public static GoalTracker getExploreTrack() {
        return exploreTrack;
    }

    public static GoalTracker getBorderTracker() {
        return borderTracker;
    }

    private Map<Tile, Set<Ant>> assignments = new HashMap<Tile, Set<Ant>>();

    private Set<Ant> getAssignees(Tile tile) {
        clean(tile);
        return assignments.get(tile);
    }

    public void assign(Tile tile, Ant ant) {
        Set<Ant> assignees = getAssignees(tile);
        if (assignees == null) {
            assignees = new HashSet<Ant>();
            assignments.put(tile, assignees);
        }
        assignees.add(ant);
    }

    public boolean isAssigned(Tile tile) {
        Set<Ant> assignees = getAssignees(tile);
        return assignees != null && !assignees.isEmpty();
    }

    public void unassign(Tile tile, Ant ant) {
        Set<Ant> assignees = getAssignees(tile);
        if (assignees == null) {
            return;
        }
        assignees.remove(ant);
    }

    private void clean(Tile tile) {
        Set<Ant> ants = assignments.get(tile);
        if (ants == null) {
            return;
        }
        for (Iterator<Ant> it = ants.iterator(); it.hasNext(); ) {
            if (!it.next().isAlive()) {
                it.remove();
            }
        }
        if (ants.isEmpty()) {
            assignments.remove(tile);
        }
    }

    public Collection<Tile> getAssignedTiles() {
        Set<Tile> retValue = new HashSet<Tile>();
        for (Tile tile : new HashSet<Tile>(assignments.keySet())) {
            if (isAssigned(tile)) {
                retValue.add(tile);
            }
        }
        return retValue;

    }

    public int getAssignedCount(Tile tile) {
        Set<Ant> assignees = getAssignees(tile);
        if (assignees == null) {
            return 0;
        }
        return assignees.size();

    }
}