package subsume;

import ants.Aim;
import ants.Tile;
import map.CostMap;
import map.WayPoint;
import map.WayPointMap;
import map.WorldMap;
import subsume.algo.*;
import util.Print;

import java.util.*;

public class NewExplore extends Layer {

    private WayPoint borderGoal;
    private Tile tileGoal;
    private GoalTracker borderTracker = GoalTracker.getBorderTracker();

    public NewExplore(Ant ant) {
        super(ant);
    }

    @Override
    protected Decision output() {
        if (everythingExplored()) {
            return Decision.DONTKNOW;
        }

        if (atBorder()) {
            unassignGoal();
            return exploreFromBorder();
        }

        if (isGoalInvalid()) {
            tileGoal=null;
            if (borderGoal != null) {
                unassignGoal();
            }
            borderGoal = chooseNewGoal();
            if (borderGoal != null) {
                assignGoal();
            }
        }
        if (borderGoal == null) {
            return Decision.DONTKNOW;
        }
        return aimAtGoal(borderGoal);
    }

    private boolean everythingExplored() {
        return ant.getWorldMap().allExplored();
    }

    private Decision exploreFromBorder() {
        if (tileGoal==null){
            tileGoal = withinSight();
        }
        if (tileGoal == null) {
            Print.println("mmmh");
            return Decision.DONTKNOW;
        }
        return aimAtGoal(tileGoal);
    }

    private boolean atBorder() {
        WayPointMap waypointMap = ant.ants.getWaypointMap();
        return waypointMap.isAtBorder(ant.getTile());
    }

    private void assignGoal() {
        Print.println("assigning explore borderGoal " + borderGoal);
        borderTracker.assign(borderGoal.getCenter(), ant);
        CostMap.getExploreMap(ant.ants, borderTracker).assign(borderGoal.getCenter());
    }

    private void unassignGoal() {
        if (borderGoal == null) {
            return;
        }
        borderTracker.unassign(borderGoal.getCenter(), ant);
        CostMap.getExploreMap(ant.ants, borderTracker).unassign(borderGoal.getCenter());
    }

    private Decision aimAtGoal(Tile goal) {
        Aim aim = AStar.firstStep(ant.ants, ant.tile, goal);
        if (aim == null) {
            return Decision.DONTKNOW;
        }
        return Decision.move(aim);
    }

    private WayPoint chooseNewGoal() {
        WayPointMap waypointMap = ant.ants.getWaypointMap();
        final Set<WayPoint> border = waypointMap.getBorder();
        if (border.isEmpty()) {
            return null;
        }

        //get the closest border element.
        List<WayPoint> path = new ModifiedWayPointAStar(ant.ants.getWaypointMap(), ant.ants, 500, new ModifiedWayPointAStar.EndCondition() {
            @Override
            public boolean goalReached(WayPoint waypoint) {
                return border.contains(waypoint);
            }
        }).findPath(getClosest(waypointMap.getWayPoints(ant.tile)));

        if (path == null || path.isEmpty()) {
            return null;
        }
        return path.get(path.size() - 1);
    }


    private Decision aimAtGoal(WayPoint goal) {
        final WayPoint next = nextWayPoint(goal);
        if (next == null) {
            return Decision.DONTKNOW;
        }
        final List<Tile> goals = new ArrayList<Tile>();
        final TrueWalk trueWalk = new TrueWalk(ant.getWorldMap());
        trueWalk.nearWalk(ant.getTile(), 17, new TrueWalk.Action() {
            @Override
            public void perform(Tile tile, int cost) {
                if (next.isReachable(tile)) {
                    trueWalk.stop();
                    goals.add(tile);
                }
            }
        });
        Tile tileGoal = goals.get(0);
        return aimAtGoal(tileGoal);
    }


    /**
     * final destination is 'to'.
     * Which wayPoint should I go to first?
     */
    private WayPoint nextWayPoint(WayPoint to) {
        WayPointMap waypointMap = ant.ants.getWaypointMap();
        Set<WayPoint> waypoints = waypointMap.getWayPoints(ant.tile);
        List<List<WayPoint>> paths = new ArrayList<List<WayPoint>>();

        //TODO could be more efficient
        for (WayPoint waypoint : waypoints) {
            List<WayPoint> path = new WayPointAStar(ant.ants).findPath(waypoint, to);
            if (path == null) continue;
            paths.add(path);
        }
        if (paths.isEmpty()) {
            return null;
        }
        Collections.sort(paths, new Comparator<List<WayPoint>>() {
            @Override
            public int compare(List<WayPoint> wayPoints, List<WayPoint> wayPoints1) {
                return wayPoints.size() - wayPoints1.size();
            }
        });

        List<WayPoint> shortest = paths.get(0);
        if (shortest.isEmpty()) {
            return null;
        }
        //these may include current waypoints. don't use.
        for (WayPoint wayPoint : shortest) {
            if (!waypoints.contains(wayPoint)) {
                return wayPoint;
            }
        }
        return null;
    }

    private Tile withinSight() {
        final WorldMap worldMap = ant.getWorldMap();
        final TrueWalk trueWalk = new TrueWalk(worldMap);
        final List<Tile> retValue = new ArrayList<Tile>();
        //instead, do A*?
        TrueWalk.Action action = new TrueWalk.Action() {
            boolean found = false;

            @Override
            public void perform(Tile tile, int cost) {
                if (found) return;
                if (!worldMap.isExplored(tile.getRow(), tile.getCol())) {
                    retValue.add(tile);
                    found = true;
                    trueWalk.stop();
                }
            }
        };
        trueWalk.nearWalk(ant.getTile(), 22, action);
        if (retValue.isEmpty()) {
            return null;
        }
        return retValue.get(0);
    }

    private WayPoint getClosest(Set<WayPoint> waypoints) {
        //TODO could do a TrueWalk
        Map<Integer, WayPoint> costs = new TreeMap<Integer, WayPoint>();
        for (WayPoint wayPoint : waypoints) {
            Tile center = wayPoint.getCenter();
            costs.put(ant.ants.getDistance(ant.tile, center), wayPoint);
        }
        return costs.values().iterator().next();

    }


    private Tile followPath(Tile tile, List<Aim> path) {
        Tile retValue = tile;
        for (Aim aim : path) {
            retValue = ant.ants.getTile(retValue, aim);
        }
        return retValue;
    }

    private boolean isGoalInvalid() {
        return borderGoal == null || !ant.ants.getWaypointMap().getBorder().contains(borderGoal);
    }

    @Override
    public String explain() {
        return super.explain() + (borderGoal != null ? " for waypoint (" + borderGoal + ")" : " from border waypoint");
    }
}