package subsume;

import ants.Aim;

public class Decision {

    static Decision DONTKNOW = new Decision(null, Action.DONTKNOW);
    static Decision STAY = new Decision(null, Action.STAY);

    static Decision move(Aim aim) {
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

}