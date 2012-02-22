package map;

import ants.Ants;
import ants.Tile;
import subsume.algo.AStar;
import subsume.algo.TrueWalk;

import java.util.*;

public class WayPointMap {
    private int radius = 8;
    private WorldMap map;
    private Ants ants;
    private Set<WayPoint> waypoints = new HashSet<WayPoint>();
    private Map<Tile, Set<WayPoint>> wayPointMap = new HashMap<Tile, Set<WayPoint>>();
    private Set<WayPoint> nonBorder = new HashSet<WayPoint>();
    private Set<WayPoint> uncheckedBorder = new HashSet<WayPoint>();

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
            Set<WayPoint> wayPoints = wayPointMap.get(canReach);
            if (wayPoints == null) {
                continue;
            }
            for (WayPoint canReachPoint : wayPoints) {
                if (connected.contains(canReachPoint)) {
                    continue;
                }
                connected.add(canReachPoint);
                int distance = getDistanceBetween(newWayPoint, canReachPoint);
                newWayPoint.connect(canReachPoint, distance);
                canReachPoint.connect(newWayPoint, distance);
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
        this.waypoints.add(newWayPoint);
        uncheckedBorder = new HashSet<WayPoint>(waypoints);
        uncheckedBorder.removeAll(nonBorder);

    }

    private int getDistanceBetween(WayPoint newWayPoint, WayPoint canReachPoint) {
        AStar aStar = new AStar(map, ants);
        return aStar.findPath(newWayPoint.getCenter(), canReachPoint.getCenter()).cost;
    }

    private Set<Tile> getReachableFrom(Tile tile) {
        final Set<Tile> retValue = new HashSet<Tile>();
        Set<Tile> offsets = ants.getVisionOffsets();
        final Set<Tile> visible = new HashSet<Tile>();
        for (Tile offset : offsets) {
            visible.add(ants.getTile(tile, offset));
        }
        TrueWalk trueWalk = new TrueWalk(map);
        TrueWalk.Action action = new TrueWalk.Action() {
            @Override
            public void perform(Tile tile, int cost) {
                if (visible.contains(tile))
                    retValue.add(tile);
            }
        };
        trueWalk.nearWalk(tile, radius + 1, action);
        return retValue;
    }

    private boolean isBorder(WayPoint wayPoint) {
        if (nonBorder.contains(wayPoint)) {
            return false;
        }
        if (!uncheckedBorder.contains(wayPoint)) {
            return true;
        }
        boolean retValue = checkIsBorder(wayPoint);
        if (!retValue) {
            nonBorder.add(wayPoint);
        }
        uncheckedBorder.remove(wayPoint);
        return retValue;
    }

    private boolean checkIsBorder(WayPoint wayPoint) {
        final List<Tile> unexplore = new ArrayList<Tile>();
        final TrueWalk trueWalk = new TrueWalk(map);
        trueWalk.nearWalk(wayPoint.getCenter(), radius * 2, new TrueWalk.Action() {
            @Override
            public void perform(Tile tile, int cost) {
                if (!map.isExplored(tile.getRow(), tile.getCol())) {
                    unexplore.add(tile);
                    trueWalk.stop();
                }
            }
        });
        return !unexplore.isEmpty();
    }


    public Set<WayPoint> getBorder() {
        //brute force
        Set<WayPoint> retValue = new HashSet<WayPoint>();
        for (WayPoint wayPoint : waypoints) {
            if (isBorder(wayPoint)) {
                retValue.add(wayPoint);
            }
        }
        return retValue;
    }

    public Set<WayPoint> getWayPoints(Tile tile) {
        return wayPointMap.get(tile);
    }

    public boolean isAtBorder(Tile tile) {
        for (WayPoint wayPoint : getWayPoints(tile)) {
            if (isBorder(wayPoint)) {
                return true;
            }
        }
        return false;
    }

}
