package map;

import ants.Tile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WayPoint {
    private Tile center;
    private Set<Tile> reachable = new HashSet<Tile>();

    private Map<WayPoint, Integer> connections = new HashMap<WayPoint, Integer>();

    public WayPoint(Tile center, Set<Tile> reachable) {
        this.center = center;
        this.reachable = reachable;
    }

    public void connect(WayPoint other, int distance) {
        connections.put(other, distance);
    }

    public boolean isReachable(Tile tile) {
        return reachable.contains(tile);
    }

    public Tile getCenter() {
        return center;
    }

    public Set<Tile> getReachable() {
        return reachable;
    }

    public int distanceTo(WayPoint other) {
        return connections.get(other);
    }

    public Set<WayPoint> getConnections() {
        return connections.keySet();
    }
    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WayPoint wayPoint = (WayPoint) o;

        if (center != null ? !center.equals(wayPoint.center) : wayPoint.center != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return center != null ? center.hashCode() : 0;
    }
}
