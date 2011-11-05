package subsume;

import ants.Aim;
import ants.Ants;
import ants.Ilk;
import ants.Tile;
import map.AntMap;
import map.WorldMap;

import java.util.*;

public class Ant {
    private static int ID = 0;

    int id = ID++;
    Ants ants;
    Tile tile;
    Decision currentDecision;
    AntMap nextTurn;

    Layer defendHill = new DefendHill(this);
    Layer attackHill = new AttackCloseHill(this);
    Layer seekFood = new SeekFood(this);
    Layer avoidPheromone = new AvoidPheromone(this);
    Layer occupyTerritory = new OccupyTerritory(this);
    Layer wanderAim = new WanderAim(this);
    Layer explore = new Explore(this);
    Layer attackDistantHill = new AttackDistantHill(this);
    Layer randomWalk = new RandomWalk(this);
    Layer avoidObstacles = new AvoidObstacles(this);

    List<Layer> layers = Arrays.asList(defendHill,
            attackHill, seekFood, avoidPheromone, occupyTerritory, explore, attackDistantHill, wanderAim, randomWalk, avoidObstacles);


    public void setAnts(Ants ants) {
        this.ants = ants;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public void setNextTurn(AntMap nextTurn) {
        this.nextTurn = nextTurn;
    }

    public Aim resolve() {
        //help debugging
        if (TurnCount.count == TurnCount.turnStop) {
                if (TurnCount.tilestop != null && tile.equals(TurnCount.tilestop)) {
                Print.println("reached break condition");
            }
        }

        cleanDecisions();
        currentDecision = defendHill.decide();
        if (currentDecision.dontKnow()) {
            currentDecision = attackHill.decide();
        }
        if (currentDecision.dontKnow()) {
            currentDecision = seekFood.decide();
        }
        if (currentDecision.dontKnow()) {
            currentDecision = explore.decide();
        }
//        if (currentDecision.dontKnow()) {
//            currentDecision = attackDistantHill.decide();
//        }

        if (currentDecision.dontKnow()) {
            currentDecision = avoidPheromone.decide();
        }
//        if (currentDecision.dontKnow()) {
//            currentDecision = occupyTerritory.decide();
//        }
//        if (currentDecision.dontKnow()) {
//            currentDecision = wanderAim.decide();
//        }

//        if (currentDecision.dontKnow()) {
//            currentDecision = randomWalk.decide();
//        }
        currentDecision = avoidObstacles.decide();
        markNextLocation();
        explain();
        return currentDecision.aim;
    }

    private void cleanDecisions() {
        currentDecision = Decision.DONTKNOW;
        for (Layer layer : layers) {
            layer.lastDecision = null;
        }
    }

    private void markNextLocation() {
        Tile nextTile;
        if (currentDecision.aim == null) {
            nextTile = this.tile;
        } else {
            nextTile = ants.getTile(tile, currentDecision.aim);
        }
        nextTurn.put(nextTile, this);
    }

    private void explain() {
        for (Layer layer : layers) {
            if (layer.lastDecision != null && !layer.lastDecision.action.equals(Decision.Action.DONTKNOW)) {
                Print.println(this + " " + layer.getClass().getSimpleName() + " " + layer.explain());
            }
        }
    }

    public String toString() {
        return "ANT " + id + " " + "(" + tile + ")";
    }

    public Tile getTile() {
        return tile;
    }

    public boolean willCollide(Aim decision) {
        Tile nextTile = ants.getTile(tile, decision);
        if (!ants.getIlk(nextTile).isPassable()) return true;
        if (nextTurn.get(nextTile) != null) return true;
        if (ants.getMyHills().contains(nextTile)) return true;
        return false;
    }

    public List<Tile> getCloseFoodTiles() {
        Set<Tile> foods = ants.getFoodTiles();
        return getCloseTiles(foods);
    }

    public Decision getCurrentDecision() {
        return currentDecision;
    }

    public List<Tile> getCloseTiles(Set<Tile> tiles) {
        return getCloseTiles(tiles, ants.getViewRadius2());
    }

    private List<Tile> getCloseTiles(Set<Tile> foods, int maxDistance) {
        SortedMap<Integer, Tile> closeFoodTiles = new TreeMap<Integer, Tile>();
        int minDistance = Integer.MAX_VALUE;
        for (Tile food : foods) {
            int distance = ants.getDistance(food, tile);
            if (distance <= maxDistance) {
                closeFoodTiles.put(distance, food);
            }
        }
        return new ArrayList<Tile>(closeFoodTiles.values());
    }

    public Tile getCloseHill() {
        List<Tile> closeTiles = getCloseTiles(ants.getEnemyHills());
        if (closeTiles.isEmpty()) {
            return null;
        }
        return closeTiles.get(0);
    }

    public void issueOrder() {
        if (currentDecision != null) {
            ants.issueOrder(tile, currentDecision.aim);
        }
    }

    public WorldMap getWorldMap() {
        return ants.getMap();
    }

    public List<Tile> getColonyCenters() {
        return new ArrayList<Tile>(ants.getMyHills());
    }

    public boolean isAlive() {
        return getWorldMap().getIlk(tile.getRow(), tile.getCol()).equals(Ilk.MY_ANT);
    }


}