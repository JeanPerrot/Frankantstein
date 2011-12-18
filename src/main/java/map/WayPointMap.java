package map;

import ants.Ants;
import ants.Tile;
import subsume.algo.AStar;
import subsume.algo.TrueWalk;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WayPointMap {
    private int radius = 7;
    private WorldMap map;
    private Ants ants;
    private Set<WayPoint> waypoints = new HashSet<WayPoint>();
    private Map<Tile, Set<WayPoint>> wayPointMap = new HashMap<Tile, Set<WayPoint>>();

    public WayPointMap(WorldMap map, Ants ants) {
        this.map = map;
        this.ants = ants;
    }

    public void visit(Tile tile) {
        if (wayPointMap.containsKey(tile)) {
            return;
        }
        //not part of a waypoint... use.
        //by definition the whole radius is visible from the ant

        Set<Tile> reachable = getReachableFrom(tile);
        WayPoint newWayPoint = new WayPoint(tile, reachable);


        //connect to other waypoints
        Set<WayPoint> connected = new HashSet<WayPoint>();
        for (Tile canReach : reachable) {
            for (WayPoint canReachPoint : wayPointMap.get(canReach)) {
                if (connected.contains(canReachPoint)) {
                    continue;
                }
                connected.add(canReachPoint);
                int distance = getDistanceBetween(newWayPoint, canReachPoint);
                newWayPoint.connect(canReachPoint, distance);
            }
        }
        updateMap(newWayPoint);
    }

    private void updateMap(WayPoint newWayPoint) {
        for (Tile tile : newWayPoint.getReachable()) {
            Set<WayPoint> wayPoints = wayPointMap.get(tile);
            if (wayPoints == null) {
                wayPoints = new HashSet<WayPoint>();
                wayPointMap.put(tile, wayPoints);
            }
            wayPoints.add(newWayPoint);
        }
    }

    private int getDistanceBetween(WayPoint newWayPoint, WayPoint canReachPoint) {
        AStar aStar = new AStar(map, ants);
        return aStar.findPath(newWayPoint.getCenter(), canReachPoint.getCenter()).cost;
    }

    private Set<Tile> getReachableFrom(Tile tile) {
        final Set<Tile> retValue = new HashSet<Tile>();
        TrueWalk trueWalk = new TrueWalk(map);
        TrueWalk.Action action = new TrueWalk.Action() {
            @Override
            public void perform(Tile tile, int cost) {
                retValue.add(tile);
            }
        };
        trueWalk.nearWalk(tile, radius+1, action);
        return retValue;
    }


}
