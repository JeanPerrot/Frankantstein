package subsume.fight;

import ants.Aim;
import subsume.Decision;
import util.Print;

import java.util.*;

public class CondensedResolver {
    //TODO right representation?
    private Map<Aim, DirectionalDecisions> decisions = new HashMap<Aim, DirectionalDecisions>();
    private Decider decider = new Decider();
    private FightState state;

    public CondensedResolver(FightState state) {
        this.state = state;
    }

    public void decideDirectionally(FightState state, Aim aim) {
        CondensedState inAim = state.getDirectionalState(aim);

        DirectionalDecisions decisionsInAim = decider.decide(inAim);
        Print.println(decisionsInAim.toString());

        decisionsInAim.rotate();
        decisions.put(aim, decisionsInAim);
    }


    //given four sets of decision (for each direction), pick decisions that are compatible with each directional choices
    public Decision resolve() {
        for (Aim aim : Aim.values()) {
            decideDirectionally(state, aim);
        }
        //dumbest thing, majority...
        Map<Decision, Integer> mustCounts = getMustCounts();
        Map<Decision, Integer> positiveCounts = getPositiveCounts();
        if (mustCounts.isEmpty() && positiveCounts.isEmpty()) {
            return Decision.DONTKNOW;
        }
        if (mustCounts.size() == 1) {
            return mustCounts.keySet().iterator().next();
        }
        if (mustCounts.isEmpty() && positiveCounts.size() == 1) {
            return positiveCounts.keySet().iterator().next();
        }
        //really?
        Map<Integer, Decision> sortedMust = sort(mustCounts);
        Map<Integer, Decision> sortedPositive = sort(positiveCounts);
        for (Decision decision : sortedMust.values()) {
            if (decision.dontKnow()) {
                continue;
            }
            return decision;
        }
        for (Decision decision : sortedPositive.values()) {
            if (decision.dontKnow()) {
                continue;
            }
            return decision;
        }

        return Decision.DONTKNOW;
    }

    private Map<Integer, Decision> sort(Map<Decision, Integer> mustCounts) {
        Map<Integer, Decision> sorted = new TreeMap<Integer, Decision>(Collections.<Integer>reverseOrder());
        for (Map.Entry<Decision, Integer> entry : mustCounts.entrySet()) {
            sorted.put(entry.getValue(), entry.getKey());
        }
        return sorted;
    }

    private Map<Decision, Integer> getMustCounts() {
        Map<Decision, Integer> mustCounts = new HashMap<Decision, Integer>();
        for (DirectionalDecisions inAim : decisions.values()) {
            for (Decision decision : inAim.getMust()) {
                Integer count = mustCounts.get(decision);
                if (count == null) count = 0;
                count++;
                mustCounts.put(decision, count);
            }
        }
        return mustCounts;
    }

    //copy paste! give me a closure...
    private Map<Decision, Integer> getPositiveCounts() {
        Map<Decision, Integer> positiveCounts = new HashMap<Decision, Integer>();
        for (DirectionalDecisions inAim : decisions.values()) {
            for (Decision decision : inAim.getPositive()) {
                Integer count = positiveCounts.get(decision);
                if (count == null) count = 0;
                count++;
                positiveCounts.put(decision, count);
            }
        }
        return positiveCounts;
    }
}