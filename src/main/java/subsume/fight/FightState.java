package subsume.fight;

import ants.Aim;
import ants.Tile;
import map.WorldMap;
import subsume.Ant;

import java.util.HashMap;
import java.util.Map;

/*
 FightState captures the fighting state environment for a given ant
 */
public class FightState {
    public static final char WIN = '+';
    public static final char LOSE = '-';
    public static final char TIE = "0".charAt(0);
    public static final char NONE = '/';

    private boolean[] land = new boolean[4];

    private static HashMap<Character, Integer> comparator = new HashMap<Character, Integer>();

    static {
        comparator.put(WIN, 2);
        comparator.put(TIE, 1);
        comparator.put(NONE, 0);
        comparator.put(LOSE, -1);
    }

    public static int compare(char c, char d) {
        return comparator.get(c) - comparator.get(d);
    }


    public static FightState fromHeatMaps(Ant ant, HeatMap myAnts, HeatMap myEnemies, WorldMap map) {
        return new FightState(ant.getTile(), myAnts, myEnemies, map);
    }

    public CondensedState getDirectionalState(Aim aim) {
        return condensedStates.get(aim);
    }

    private char[][] data = new char[5][5];
    private Map<Aim, CondensedState> condensedStates = new HashMap<Aim, CondensedState>();

    public FightState(Tile center, HeatMap mine, HeatMap enemies, WorldMap worldMap) {
        int top = center.getRow() - 2;
        int left = center.getCol() - 2;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                int myInfluence = mine.exposure(top + i, left + j);
                int theirInfluence = enemies.exposure(top + i, left + j);
                int diff = myInfluence - theirInfluence;
                char c = NONE;
                if (diff < 0) c = LOSE;
                else if (diff > 0 && theirInfluence > 0) c = WIN;
                else if (diff == 0 && theirInfluence > 0) c = TIE;
                data[i][j] = c;
            }
        }
        buildDirectionalStates();
        int index = 0;
        for (Aim aim : Aim.values()) {
            int row = center.getRow() + aim.getRowDelta();
            int col = center.getCol() + aim.getColDelta();

            land[index++] = !worldMap.getIlk(row, col).isPassable();
        }
    }

    private void buildDirectionalStates() {
        char[] north = new char[8];
        char[] east = new char[8];
        char[] south = new char[8];
        char[] west = new char[8];
        int index = 0;
        for (int i = 1; i < 4; i++) {
            north[index] = data[0][i];
            east[index] = data[i][4];
            south[index] = data[4][4 - i];
            west[index] = data[4 - i][0];
            index++;
        }
        for (int i = 0; i < 5; i++) {
            north[index] = data[1][i];
            east[index] = data[i][3];
            south[index] = data[3][4 - i];
            west[index] = data[4 - i][1];
            index++;
        }
        addState(north, Aim.NORTH);
        addState(east, Aim.EAST);
        addState(south, Aim.SOUTH);
        addState(west, Aim.WEST);
    }

    private void addState(char[] state, Aim aim) {
        condensedStates.put(aim, new CondensedState(state, landFor(aim), aim));
    }

    //land tile as seen from aim (e.g. if aim==east, then first item is whether there is land east)
    private boolean[] landFor(Aim aim) {
        boolean[] retValue = new boolean[4];
        for (int i = 0; i < 4; i++) {
            retValue[i] = land[(i + aim.getIndex()) % 4];
        }
        return retValue;
    }
}