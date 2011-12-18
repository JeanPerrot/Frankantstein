package ants;

import java.net.NoRouteToHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a direction in which to move an ant.
 */
public enum Aim {
    /**
     * North direction, or up.
     */
    NORTH(-1, 0, 'n', 0),

    /**
     * East direction or right.
     */
    EAST(0, 1, 'e', 1),

    /**
     * South direction or down.
     */
    SOUTH(1, 0, 's', 2),

    /**
     * West direction or left.
     */
    WEST(0, -1, 'w', 3);

    private static final Map<Character, Aim> symbolLookup = new HashMap<Character, Aim>();

    static {
        symbolLookup.put('n', NORTH);
        symbolLookup.put('e', EAST);
        symbolLookup.put('s', SOUTH);
        symbolLookup.put('w', WEST);
    }

    private final int rowDelta;

    private final int colDelta;

    private final int index;

    private final char symbol;

    Aim(int rowDelta, int colDelta, char symbol, int index) {
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
        this.symbol = symbol;
        this.index = index;
    }


    public Aim turnLeft() {
        return turn(-1);
    }

    public Aim turnRight() {
        return turn(1);
    }


    private Aim turn(int i) {
        int newIndex = (index + i) % 4;
        if (newIndex < 0) {
            newIndex += 4;
        }
        return values()[newIndex];
    }

    //otherAim is using this Aim as a referential, seeing it as 'NORTH'. give the true aim.
    //north.rotate() does nothing. east.rotate() turns one clockwise, etc.
    public Aim rotate(Aim otherAim){
        return otherAim.turn(index);
    }
    
    public Aim counterRotate(Aim otherAim){
        return otherAim.turn(-index);
    }



    /**
     * Returns rows delta.
     *
     * @return rows delta.
     */
    public int getRowDelta() {
        return rowDelta;
    }

    /**
     * Returns columns delta.
     *
     * @return columns delta.
     */
    public int getColDelta() {
        return colDelta;
    }

    /**
     * Returns symbol associated with this direction.
     *
     * @return symbol associated with this direction.
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Returns direction associated with specified symbol.
     *
     * @param symbol <code>n</code>, <code>e</code>, <code>s</code> or <code>w</code> character
     * @return direction associated with specified symbol
     */
    public static Aim fromSymbol(char symbol) {
        return symbolLookup.get(symbol);
    }

    public int getIndex() {
        return index;
    }
}
