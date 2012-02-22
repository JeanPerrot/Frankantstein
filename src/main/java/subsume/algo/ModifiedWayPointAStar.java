package subsume.algo;

import ants.Aim;
import ants.Ants;
import ants.Tile;
import map.WayPoint;
import map.WayPointMap;

import java.util.*;


/**
 * given a cost map, will use any waypoint with cost zero as having converged. ie the first path that hits a waypoint with
 * cost zero will be returned.
 */
public class ModifiedWayPointAStar {

    private WayPointMap map;
    private Ants ants;
    private HeuristicCost heuristicCost;
    private EndCondition endCondition;


    private SortedMap<Integer, Collection<Path>> frontier = new TreeMap<Integer, Collection<Path>>();
    private Map<WayPoint, Path> frontierNoCost = new HashMap<WayPoint, Path>();


    private Map<WayPoint, WayPoint> cameFrom = new HashMap<WayPoint, WayPoint>();
    private Set<WayPoint> visited = new HashSet<WayPoint>();

    private int costLimit = 35;

    public ModifiedWayPointAStar(WayPointMap map, Ants ants, int costLimit, EndCondition endCondition) {
        this.map = map;
        this.ants = ants;
        this.costLimit = costLimit;
        this.endCondition = endCondition;
        heuristicCost = getZeroHeuristics();
    }

    /**
     * returns an AppraisedPath with goalReached=false if not successful
     */
    public List<WayPoint> findPath(WayPoint start) {
//        Print.println("AStar used " + counts++);
        clear();
        addToFrontier(new Path(start, 0, heuristicCost(start), 0));

        while (!frontier.isEmpty()) {
            Path lowestScore = getLowestScoreFrontier();
            if (lowestScore.computationCost > costLimit) {
                List<WayPoint> path = reconstructPath(lowestScore.waypoint);
                if (path.size() == 1) {
                    return null;
                }
                return path;
            }
            if (endCondition.goalReached(lowestScore.waypoint)) {
                List<WayPoint> path = reconstructPath(lowestScore.waypoint);
                if (path.size() == 1) {
                    return null;
                }
                return path;
            }
            removeFromFrontier(lowestScore);
            visited.add(lowestScore.waypoint);

            List<WayPoint> neighbors = getNeighbors(lowestScore.waypoint);
            for (WayPoint tile : neighbors) {
                if (visited.contains(tile)) {
                    continue;
                }
                boolean improved = true;
                int knownCost = score(lowestScore.waypoint, tile) + lowestScore.knownCost;
                int heuristicCost = heuristicCost(tile);
                int tentativeCost = heuristicCost + knownCost;
                int computationCost = lowestScore.computationCost + 1;

                Path alreadyInFrontier = getInFrontier(tile);
                if (alreadyInFrontier == null) {
                    int size = frontierSize();
                    addToFrontier(new Path(tile, knownCost, heuristicCost, computationCost));
                    if (frontierSize() < size + 1) throw new RuntimeException();
                } else {
                    if (alreadyInFrontier.fScore() < tentativeCost) {
                        improved = false;
                    }
                }
                if (improved) {
                    cameFrom.put(tile, lowestScore.waypoint);
                }
            }
        }
        return null;
    }

    private int frontierSize() {
        return frontierNoCost.size();
    }

    private void addToFrontier(Path path) {
        Collection<Path> paths = frontier.get(path.fScore());
        if (paths == null) {
            paths = new ArrayList<Path>();
        }
        paths.add(path);
        frontier.put(path.fScore(), paths);
        frontierNoCost.put(path.waypoint, path);
    }

    private void removeFromFrontier(Path path) {
        Collection<Path> paths = frontier.get(path.fScore());
        paths.remove(path);
        if (paths.isEmpty()) {
            frontier.remove(path.fScore());
        }
        frontierNoCost.remove(path.waypoint);
    }

    private Path getInFrontier(WayPoint tile) {
        return frontierNoCost.get(tile);
    }

    private int score(WayPoint from, WayPoint to) {
        return from.distanceTo(to);
    }

    private List<WayPoint> getNeighbors(WayPoint waypoint) {
        return new ArrayList<WayPoint>(waypoint.getConnections());
    }

    private List<WayPoint> reconstructPath(WayPoint wayPoint) {
        List<WayPoint> path = new ArrayList<WayPoint>();
        while (wayPoint != null) {
            path.add(wayPoint);
            wayPoint = cameFrom.get(wayPoint);
        }
        Collections.reverse(path);
        return path;

    }

    private Path getLowestScoreFrontier() {
        Collection<Path> paths = frontier.get(frontier.firstKey());
        Path retValue = null;
        int maxCost = Integer.MAX_VALUE;
        for (Path path : paths) {
            if (path.fScore() < maxCost) {
                retValue = path;
                maxCost = path.fScore();
            }
        }
        return retValue;
    }

    private void clear() {
        frontier.clear();
        cameFrom.clear();
        visited.clear();
    }

    //0 is perfectly acceptable. This will boil down to a breadth-first search.
    public HeuristicCost getZeroHeuristics() {
        return new HeuristicCost() {
            public int cost(WayPoint current) {
                return 0;
            }
        };
    }


    private int heuristicCost(WayPoint current) {
        return heuristicCost.cost(current);
    }

    static interface HeuristicCost {
        int cost(WayPoint current);
    }

    public EndCondition getGoalEndCondition(final WayPoint goal) {
        return new EndCondition() {
            @Override
            public boolean goalReached(WayPoint waypoint) {
                return waypoint.equals(goal);
            }
        };
    }


    public static interface EndCondition {
        boolean goalReached(WayPoint waypoint);
    }

    private class Path implements Comparable<Path> {
        WayPoint waypoint;
        int knownCost;
        int heuristicCost;
        int computationCost;

        private Path(WayPoint wayPoint, int cost, int heuristicCost, int computationCost) {
            this.waypoint = wayPoint;
            this.knownCost = cost;
            this.heuristicCost = heuristicCost;
            this.computationCost = computationCost;
        }

        public int fScore() {
            return knownCost + heuristicCost;
        }

        @Override
        public int compareTo(Path path) {
            return fScore() - path.fScore();
        }

        public String toString() {
            return waypoint + " (" + fScore() + ")";
        }
    }

    public static class AppraisedPath {
        public List<Aim> path;
        public int cost;
        public boolean goalReached;

        public AppraisedPath(List<Aim> path, int cost, boolean goalReached) {
            this.path = path;
            this.cost = cost;
            this.goalReached = goalReached;
        }


    }


}