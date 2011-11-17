package subsume.fight;

import java.util.Arrays;

// 6 tile state
public class CondensedState {
    private char[] state;

    //TODO - constructor...

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

    public char[] asChars(){
        return state;
    }
}