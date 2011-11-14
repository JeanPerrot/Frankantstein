import ants.Ants;
import ants.Tile;
import map.AntMap;
import map.CostMap;
import map.HillEnemiesCache;
import map.PheromoneMap;
import subsume.Ant;
import subsume.fight.HeatMap;
import util.Print;
import util.TurnCount;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Starter bot implementation.
 */
public class MyBot extends Bot {

    AntMap nextTurn = new AntMap();
    AntMap thisTurn;
    Ants gameState;


    /**
     * Main method executed by the game engine for starting the bot.
     *
     * @param args command line arguments
     * @throws IOException if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        new MyBot().readSystemInput();
    }

    /**
     * For every ant check every direction in fixed order (N, E, S, W) and move it if the tile is
     * passable.
     */
    @Override
    public void doTurn() {
        long time = System.currentTimeMillis();
        CostMap.clearTurn();
        HeatMap.clearTurn();
        TurnCount.count++;
        Print.println("turn " + TurnCount.count);
        gameState = getAnts();
        gameState.markVisionExplored();
        HillEnemiesCache.get(gameState).clear();
        PheromoneMap.getPheromoneMap(gameState).markTerritory(gameState.getMyAnts());


        thisTurn = nextTurn;
        nextTurn = new AntMap();

        List<Ant> ants = getMyAnts();
        for (Ant ant : ants) {
            if (System.currentTimeMillis() - time > 230) {
                Print.println("**********************TIMEOUT PROTECTION KICKING IN********************************");
//                throw new RuntimeException("timeout");
            }
            ant.resolve();
        }
        for (Ant ant : ants) {
            ant.issueOrder();
        }
    }

    /**
     * get the ants. those that survived are remembered, the others are created.
     */
    private List<Ant> getMyAnts() {
        Set<Tile> antsTiles = gameState.getMyAnts();
        //we should have new ants and dead ants.
        List<Ant> retValue = new ArrayList<Ant>();
        HashSet<Tile> deadAnts = new HashSet<Tile>(thisTurn.getTiles());
        deadAnts.removeAll(antsTiles);
        for (Tile tile : deadAnts) {
//            System.out.println("it looks like ant at " + tile + "died");
        }

        for (Tile tile : antsTiles) {
            Ant ant = thisTurn.get(tile);
            if (ant == null) {
                //cute new baby ant
                ant = new Ant();
            }
            ant.setAnts(gameState);
            ant.setTile(tile);
            ant.setNextTurn(nextTurn);
            retValue.add(ant);
        }
        return retValue;
    }

}
