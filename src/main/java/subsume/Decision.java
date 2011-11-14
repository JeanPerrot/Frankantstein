package subsume;

import ants.Aim;

public class Decision {

    public  static Decision DONTKNOW = new Decision(null, Action.DONTKNOW);
    public static Decision STAY = new Decision(null, Action.STAY);

    public static Decision move(Aim aim) {
        return new Decision(aim, Action.MOVE);
    }

    Aim aim;
    Action action;

    public Decision(Aim aim, Action action) {
        this.aim = aim;
        this.action = action;
    }

    public boolean dontKnow(){
        return this.action.equals(Action.DONTKNOW);
    }

    public static enum Action {MOVE, STAY, DONTKNOW}

    public String toString(){
        return action+(aim!=null?"("+aim+")":"");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Decision decision = (Decision) o;

        if (action != decision.action) return false;
        if (aim != decision.aim) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = aim != null ? aim.hashCode() : 0;
        result = 31 * result + (action != null ? action.hashCode() : 0);
        return result;
    }
}