package subsume;

import ants.Aim;
import ants.Ants;
import ants.Tile;
import map.AntMap;
import map.WorldMap;

import java.util.*;

public class Ant {

    Ants ants;
    Tile tile;
    Aim currentDecision;
    AntMap nextTurn;

    Layer attackHill = new AttackAntHill(this);
    Layer seekFood = new SeekFood(this);
    Layer wanderAim = new WanderAim(this);
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
        currentDecision = attackHill.output();
        if (currentDecision == null) {
            currentDecision = seekFood.output();
        }
        if (currentDecision == null) {
            currentDecision = wanderAim.output();
            if (currentDecision==null){
//                System.out.println("no wandering aim");
            }
        }
        if (currentDecision == null) {
            currentDecision = followWall.output();
        }
        currentDecision = avoidObstacles.output();
        nextTurn.put(ants.getTile(tile, currentDecision), this);
        return currentDecision;
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

    public Aim getCurrentDecision() {
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
        ants.issueOrder(tile, currentDecision);
    }

    public WorldMap getWorldMap() {
        return ants.getMap();
    }

    public List<Tile> getColonyCenters() {
        return new ArrayList<Tile>(ants.getMyHills());
    }
}