package subsume.algo;

import ants.Ants;
import map.WayPoint;

import java.util.*;

public class WayPointAStar {

    private int rows;
    private int cols;
    private HeuristicCost heuristicCost;
    private EndCondition endCondition;

    private SortedMap<Integer, Collection<Path>> frontier = new TreeMap<Integer, Collection<Path>>();
    private Map<WayPoint, Path> frontierNoCost = new HashMap<WayPoint, Path>();
    private Map<WayPoint, WayPoint> cameFrom = new HashMap<WayPoint, WayPoint>();
    private Set<WayPoint> visited = new HashSet<WayPoint>();


    public WayPointAStar(Ants ants) {
        this.rows = ants.getRows();
        this.cols = ants.getCols();
    }

    public List<WayPoint> findPath(WayPoint start, WayPoint goal) {
        heuristicCost = getGoalHeuristics(goal);
        endCondition = getGoalEndCondition(goal);
        return findPath(start);
    }

    private List<WayPoint> findPath(WayPoint start) {
        clear();
        addToFrontier(new Path(start, 0, heuristicCost(start)));

        while (!frontier.isEmpty()) {
            Path lowestScore = getLowestScoreFrontier();
            if (endCondition.goalReached(lowestScore.wayPoint)) {
                List<WayPoint> path = reconstructPath(lowestScore.wayPoint);
                return path;
            }
            removeFromFrontier(lowestScore);
            visited.add(lowestScore.wayPoint);

            List<WayPoint> neighbors = getNeighbors(lowestScore.wayPoint);
            for (WayPoint waypoint : neighbors) {
                if (visited.contains(waypoint)) {
                    continue;
                }
                boolean improved = true;
                int knownCost = score(lowestScore.wayPoint, waypoint) + lowestScore.knownCost;
                int heuristicCost = heuristicCost(waypoint);
                int tentativeCost = heuristicCost + knownCost;

                Path alreadyInFrontier = getInFrontier(waypoint);
                if (alreadyInFrontier == null) {
                    int size = frontierSize();
                    addToFrontier(new Path(waypoint, knownCost, heuristicCost));
                    if (frontierSize() < size + 1) throw new RuntimeException();
                } else {
                    if (alreadyInFrontier.fScore() < tentativeCost) {
                        improved = false;
                    }
                }
                if (improved) {
                    cameFrom.put(waypoint, lowestScore.wayPoint);
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
        frontierNoCost.put(path.wayPoint, path);
    }

    private void removeFromFrontier(Path path) {
        Collection<Path> paths = frontier.get(path.fScore());
        paths.remove(path);
        if (paths.isEmpty()) {
            frontier.remove(path.fScore());
        }
        frontierNoCost.remove(path.wayPoint);
    }

    private Path getInFrontier(WayPoint WayPoint) {
        return frontierNoCost.get(WayPoint);
    }

    private int score(WayPoint from, WayPoint to) {
        return from.distanceTo(to);
    }

    private List<WayPoint> getNeighbors(WayPoint waypoint) {
        return new ArrayList<WayPoint>(waypoint.getConnections());
    }


    private List<WayPoint> reconstructPath(WayPoint WayPoint) {
        List<WayPoint> path = new ArrayList<WayPoint>();
        while (WayPoint != null) {
            path.add(WayPoint);
            WayPoint = cameFrom.get(WayPoint);
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

    private int horizontalDistance(WayPoint current, WayPoint goal) {
        int diff = Math.abs(goal.getCenter().getCol() - current.getCenter().getCol());
        assert (diff < cols);
        return Math.min(diff, cols - diff);
    }

    private int verticalDistance(WayPoint current, WayPoint goal) {
        int diff = Math.abs(goal.getCenter().getRow() - current.getCenter().getRow());
        return Math.min(diff, rows - diff);
    }

    public HeuristicCost getGoalHeuristics(final WayPoint goal) {
        return new HeuristicCost() {
            public int cost(WayPoint current) {
                return verticalDistance(current, goal) + horizontalDistance(current, goal);
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
            public boolean goalReached(WayPoint WayPoint) {
                return WayPoint.equals(goal);
            }
        };
    }


    static interface EndCondition {
        boolean goalReached(WayPoint WayPoint);
    }

    private class Path implements Comparable<Path> {
        WayPoint wayPoint;
        int knownCost;
        int heuristicCost;

        private Path(WayPoint WayPoint, int cost, int heuristicCost) {
            this.wayPoint = WayPoint;
            this.knownCost = cost;
            this.heuristicCost = heuristicCost;
        }

        public int fScore() {
            return knownCost + heuristicCost;
        }

        @Override
        public int compareTo(Path path) {
            return fScore() - path.fScore();
        }

        public String toString() {
            return wayPoint + " (" + fScore() + ")";
        }
    }

}