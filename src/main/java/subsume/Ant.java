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
    Layer attackHill = new AttackAntHill(this);
    Layer seekFood = new SeekFood(this);
    Layer occupyTerritory = new OccupyTerritory(this);
    Layer wanderAim = new WanderAim(this);
    Layer explore = new Explore(this);
    Layer followWall = new FollowTheWall(this);
    Layer avoidObstacles = new AvoidObstacles(this);


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

        currentDecision = Decision.DONTKNOW;
        currentDecision = defendHill.decide();
        if (currentDecision.dontKnow()) {
            currentDecision = attackHill.decide();
        }
        if (currentDecision.dontKnow()) {
            currentDecision = seekFood.decide();
        }
        if (currentDecision.dontKnow()){
            currentDecision=occupyTerritory.decide();
        }
//        if (currentDecision.dontKnow()) {
//            currentDecision = explore.decide();
//        }
        if (currentDecision.dontKnow()) {
            currentDecision = wanderAim.decide();
        }
        if (currentDecision.dontKnow()) {
            currentDecision = followWall.decide();
        }
        currentDecision = avoidObstacles.decide();
        markNextLocation();
        explain();
        return currentDecision.aim;
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
        for (Layer layer : Arrays.asList(defendHill,
                attackHill, seekFood, wanderAim, explore, followWall, avoidObstacles)) {
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
        return false;
    }

    public List<Tile> getCloseFoodTiles() {
        Set<Tile> foods = ants.getFoodTiles();
        return getCloseTiles(foods);
    }

    public Decision getCurrentDecision() {
        return currentDecision;
    }

    private List<Tile> getCloseTiles(Set<Tile> foods) {
        SortedMap<Integer, Tile> closeFoodTiles = new TreeMap<Integer, Tile>();
        int minDistance = Integer.MAX_VALUE;
        for (Tile food : foods) {
            int distance = ants.getDistance(food, tile);
            if (distance <= ants.getViewRadius2()) {
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
        ants.issueOrder(tile, currentDecision.aim);
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