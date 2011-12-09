package subsume.fight;

import ants.Ilk;
import ants.Tile;
import map.AntMap;
import subsume.Ant;
import util.Print;
import util.TurnCount;

public class Reward {

    //for each of my ants, dead and alive:
    //did they die in a fight?
    //did an enemy die in their fighting radius?

    public static Double getReward(Ant ant, AntMap lastTurn) {
        if (TurnCount.count == TurnCount.turnStop) {
            if (TurnCount.tilestop != null && ant.getTile().equals(TurnCount.tilestop)) {
                Print.println("reached break condition");
            }
        }
        if (!diedInFight(ant) && !wasInFight(ant)) {
            return null;
        }
        Double reward = differentialDeaths(ant, lastTurn);
        Print.println(ant+" was in a fight with reward "+reward);
        return reward;
    }

    private static boolean wasInFight(Ant ant) {
        //an ant died nearby OR there are enemies in sight
        for (Tile tile : ant.ants.getVisionOffsets()) {
            Ilk ilk = ant.getWorldMap().getIlk(ant.getTile().getRow()+tile.getRow(), ant.getTile().getCol()+tile.getCol());
            if (ilk.equals(Ilk.DEAD)) {
                return true;
            }
            if (ilk.equals(Ilk.ENEMY_ANT)) {
                return true;
            }
        }
        return false;
    }

    private static Double differentialDeaths(Ant ant, AntMap lastTurn) {
        int retValue = 0;
        for (Tile tile : ant.ants.getVisionOffsets()) {
            int row = ant.getTile().getRow() + tile.getRow();
            int col = ant.getTile().getCol() + tile.getCol();
            Ilk ilk = ant.getWorldMap().getIlk(row, col);
            if (ilk.equals(Ilk.DEAD)) {
                if (lastTurn.get(tile) != null) {
                    //our ant died...
                    retValue--;
                } else {
                    retValue++;
                }
            }
        }

        return (double) retValue;
    }

    private static boolean diedInFight(Ant ant) {
        boolean isDead = isDead(ant);
        //death is only from collisions or from enemies.
        return isDead && !diedFromCollision(ant);
    }

    //assuming we never mess up...
    private static boolean diedFromCollision(Ant ant) {
        return false;
    }


    private static boolean isDead(Ant ant) {
        Ilk ilk = ant.getWorldMap().getIlk(ant.getTile().getRow(), ant.getTile().getCol());
        return ilk.equals(Ilk.DEAD);
    }
}