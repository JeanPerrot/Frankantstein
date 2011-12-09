import ants.Ants;
import ants.Tile;
import map.AntMap;
import map.CostMap;
import map.HillEnemiesCache;
import map.PheromoneMap;
import subsume.Ant;
import subsume.fight.FightState;
import subsume.fight.HeatMap;
import subsume.fight.QLearning;
import subsume.fight.Reward;
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

    QLearning qlearning = new QLearning();

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
        CostMap.clearTurn();
        HeatMap.clearTurn();
        TurnCount.count++;
        Print.println("turn " + TurnCount.count);
        gameState = getAnts();
        gameState.markVisionExplored();
        HillEnemiesCache.get(gameState).clear();
        PheromoneMap.getPheromoneMap(gameState).markTerritory(gameState.getMyAnts());

        AntMap lastTurn = thisTurn;
        thisTurn = nextTurn;
        nextTurn = new AntMap();
        List<Ant> ants = getMyAnts();

        doReinforcementLearning(lastTurn);

        for (Ant ant : ants) {
            if (gameState.getTimeRemaining() < 25) {
                Print.println("**********************TIMEOUT PROTECTION KICKING IN********************************");
//                break;
//                throw new RuntimeException("timeout");
            }
            ant.resolve();
        }
        for (Ant ant : ants) {
            ant.issueOrder();
        }
    }

    private void doReinforcementLearning(AntMap lastTurn) {
        if (lastTurn==null){
            return;
        }
        Print.debug("reinforcement learning...");

        if (!Ant.reinforcementLearning) return;
        for (Ant ant : lastTurn.getAnts()) {
            Double reward = Reward.getReward(ant, lastTurn);
            //null reward means not applicable
            if (reward == null) {
                continue;
            }
            //get the previous fighting state from somewhere
            FightState previousState = ant.getLastFightState();
            qlearning.learn(previousState, ant.getCurrentDecision(), reward);
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
                Print.println("no ant found at " + tile + ", creating one");
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
