package subsume.fight;

import ants.Aim;
import ants.Tile;
import subsume.Ant;

import java.util.HashMap;
import java.util.Map;

//TODO
/*
 FightState captures the fighting state environment for a given ant
 */
public class FightState {
    public static final char WIN = '+';
    public static final char LOSE = '-';
    public static final char TIE = '0';
    public static final char NONE = '/';

    public static FightState fromHeatMaps(Ant ant, HeatMap myAnts, HeatMap myEnemies) {
        return new FightState(ant.getTile(), myAnts, myEnemies);
    }

    public CondensedState getDirectionalState(Aim aim) {
        return condensedStates.get(aim);
    }

    private char[][] data = new char[5][5];
    private Map<Aim, CondensedState> condensedStates = new HashMap<Aim, CondensedState>();

    public FightState(Tile center, HeatMap mine, HeatMap enemies) {
        int top = center.getRow() - 2;
        int left = center.getCol() - 2;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                int myInfluence = mine.exposure(top + i, left + j);
                int theirInfluence = enemies.exposure(top + i, left + j);
                int diff = myInfluence - theirInfluence;
                char c = NONE;
                if (diff < 0) c = LOSE;
                else if (diff > 0) c = WIN;
                else if (myInfluence > 0) c = TIE;
                data[i][j] = c;
            }
        }
        buildDirectionalStates();
    }

    private void buildDirectionalStates() {
        char[] north = new char[6];
        char[] east = new char[6];
        char[] south = new char[6];
        char[] west = new char[6];
        int index = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 1; j < 3; j++) {
                north[index] = data[i][j];
                east[index] = data[j][4 - i];
                south[index] = data[4 - i][4 - j];
                west[index] = data[4 - j][i];
                index++;
            }
        }
    }
}