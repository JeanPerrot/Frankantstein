package subsume.fight;

import ants.Aim;

import java.util.Arrays;

// 8 tile state
//X123X
//45678

//XFL2F2FR2X
//-FL1F1FR1-
public class CondensedState {
    private boolean[] land;
    private Aim aim;
    private char[] state;
    public char f1;
    public char f2;
    public char fl1;
    public char fr1;
    public char fl2;
    public char fr2;
    public char l;
    public char r;

    public CondensedState(char[] state, boolean[] land, Aim aim) {
        this.state = state;
        this.land = land;
        this.aim = aim;
        this.fl2 = state[0];
        this.f2 = state[1];
        this.fr2 = state[2];
        this.l = state[3];
        this.fl1 = state[4];
        this.f1 = state[5];
        this.fr1 = state[6];
        this.r = state[7];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CondensedState that = (CondensedState) o;
        if (!Arrays.equals(state, that.state)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return state != null ? Arrays.hashCode(state) : 0;
    }

    public char[] asChars() {
        return state;
    }

    public Aim getAim() {
        return aim;
    }

    public boolean[] getLand() {
        return land;
    }

    public boolean isPassable(Aim aim){
        return land[aim.getIndex()];
    }

    @Override
    public String toString() {
        return "CondensedState{" +
                "aim=" + aim +
                ", state=" + new String(state) +
                ", land=" + land +
                '}';
    }
}