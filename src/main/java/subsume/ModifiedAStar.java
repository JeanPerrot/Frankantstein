package subsume;

import ants.Aim;
import ants.Ants;
import ants.Tile;
import map.CostMap;

import java.util.*;


/**
 * given a cost map, will use any tile with cost zero as having converged. ie the first path that hits a tile with
 * cost zero will be returned.
 */
public class ModifiedAStar {
    private static int counts = 0;

    private CostMap map;
    private Ants ants;
    private HeuristicCost heuristicCost;
    private EndCondition endCondition;


    private SortedMap<Integer, Collection<Path>> frontier = new TreeMap<Integer, Collection<Path>>();
    private Map<Tile, Path> frontierNoCost = new HashMap<Tile, Path>();


    private Map<Tile, Tile> cameFrom = new HashMap<Tile, Tile>();
    private Set<Tile> visited = new HashSet<Tile>();

    private int costLimit = 35;

    public ModifiedAStar(CostMap costMap, Ants ants, int costLimit) {
        this.map = costMap;
        this.ants = ants;
        this.costLimit = costLimit;
        heuristicCost = getZeroHeuristics();
        endCondition = new EndCondition() {
            @Override
            public boolean goalReached(Tile tile) {
                return map.getCost(tile) == 0;
            }
        };
    }

    /**
     * returns an AppraisedPath with goalReached=false if not successful
     */
    public AppraisedPath findPath(Tile start) {
//        Print.println("AStar used " + counts++);
        clear();
        addToFrontier(new Path(start, 0, heuristicCost(start), 0));

        while (!frontier.isEmpty()) {
            Path lowestScore = getLowestScoreFrontier();
            if (lowestScore.computationCost > costLimit) {
                List<Tile> path = reconstructPath(lowestScore.tile);
                if (path.size() == 1) {
                    return null;
                }
                return new AppraisedPath(toAim(path), lowestScore.fScore(), false);
            }
            if (endCondition.goalReached(lowestScore.tile)) {
                List<Tile> path = reconstructPath(lowestScore.tile);
                if (path.size() == 1) {
                    return null;
                }
                return new AppraisedPath(toAim(path), lowestScore.fScore(), true);
            }
            removeFromFrontier(lowestScore);
            visited.add(lowestScore.tile);

            List<Tile> neighbors = getNeighbors(lowestScore.tile);
            for (Tile tile : neighbors) {
                if (visited.contains(tile)) {
                    continue;
                }
                boolean improved = true;
                int knownCost = score(tile) + lowestScore.knownCost;
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
                    cameFrom.put(tile, lowestScore.tile);
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
        frontierNoCost.put(path.tile, path);
    }

    private void removeFromFrontier(Path path) {
        Collection<Path> paths = frontier.get(path.fScore());
        paths.remove(path);
        if (paths.isEmpty()) {
            frontier.remove(path.fScore());
        }
        frontierNoCost.remove(path.tile);
    }

    private Path getInFrontier(Tile tile) {
        return frontierNoCost.get(tile);
    }

    private int score(Tile tile) {
        return map.getCost(tile);
    }

    private List<Tile> getNeighbors(Tile tile) {
        List<Tile> retValue = new ArrayList<Tile>();
        for (Aim aim : Aim.values()) {
            retValue.add(ants.getTile(tile, aim));
        }
        return retValue;

    }

    private List<Aim> toAim(List<Tile> path) {
        List<Aim> retValue = new ArrayList<Aim>();
        int index = 0;
        do {
            Tile begin = path.get(index++);
            Tile end = path.get(index);
            retValue.add(toAim(begin, end));
        } while (index < path.size() - 2);
        return retValue;
    }

    private Aim toAim(Tile begin, Tile end) {
        List<Aim> aims = ants.getDirections(begin, end);
        assert (aims.size() == 1);
        return aims.get(0);
    }

    private List<Tile> reconstructPath(Tile tile) {
        List<Tile> path = new ArrayList<Tile>();
        while (tile != null) {
            path.add(tile);
            tile = cameFrom.get(tile);
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
            public int cost(Tile current) {
                return 0;
            }
        };
    }


    private int heuristicCost(Tile current) {
        return heuristicCost.cost(current);
    }

    static interface HeuristicCost {
        int cost(Tile current);
    }

    public EndCondition getGoalEndCondition(final Tile goal) {
        return new EndCondition() {
            @Override
            public boolean goalReached(Tile tile) {
                return tile.equals(goal);
            }
        };
    }


    static interface EndCondition {
        boolean goalReached(Tile tile);
    }

    private class Path implements Comparable<Path> {
        Tile tile;
        int knownCost;
        int heuristicCost;
        int computationCost;

        private Path(Tile tile, int cost, int heuristicCost, int computationCost) {
            this.tile = tile;
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
            return tile + " (" + fScore() + ")";
        }
    }

    public static class AppraisedPath {
        List<Aim> path;
        int cost;
        boolean goalReached;

        public AppraisedPath(List<Aim> path, int cost, boolean goalReached) {
            this.path = path;
            this.cost = cost;
            this.goalReached = goalReached;
        }


    }


}