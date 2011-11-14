package subsume.fight;

import ants.Aim;
import subsume.Decision;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CondensedResolver {
    //TODO right representation?
    private Map<Aim, Set<Decision>> decisions = new HashMap<Aim, Set<Decision>>();
    private Decider decider = new Decider();
    private FightState state;

    public CondensedResolver(FightState state) {
        this.state = state;
    }

    public void decideDirectionally(FightState state, Aim aim) {
        CondensedState inAim = state.getDirectionalState(aim);
        Set<Decision> decisionsinAim = decider.decide(inAim);
        Set<Decision> rotated = rotate(decisionsinAim, aim);
        decisions.put(aim, rotated);
    }

    //rotate decisions made by the 6-tile state knowing that they were really made in the aim
    private Set<Decision> rotate(Set<Decision> decisionsinAim, Aim aim) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    //given four sets of decision (for each direction), pick decisions that are compatible with each directional choices
    public Decision resolve() {
        for (Aim aim : Aim.values()) {
            decideDirectionally(state, aim);
        }
        //TODO...
        return null;  //To change body of created methods use File | Settings | File Templates.
    }
}