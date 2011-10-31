import ants.Ants;
import ants.Tile;
import map.AntMap;
import subsume.Ant;

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
    int turn = 0;


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
        turn++;
        gameState = getAnts();
        gameState.markVisionExplored();

        thisTurn = nextTurn;
        nextTurn = new AntMap();

        List<Ant> ants = getMyAnts();
        for (Ant ant : ants) {
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
