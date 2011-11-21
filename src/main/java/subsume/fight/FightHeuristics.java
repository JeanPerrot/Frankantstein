package subsume.fight;

import ants.Aim;
import subsume.Decision;
import util.Print;

import java.security.KeyStore;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static subsume.fight.FightState.*;

public class FightHeuristics {

    private CondensedState state;

    public FightHeuristics(CondensedState state) {
        this.state = state;
    }

    public DirectionalDecisions applyPolicy() {
        if (victoryAhead()) {
            return must(Aim.NORTH);
        }
        if (defeatAhead()) {
            //just run away? right escapes...?
            return must(Aim.SOUTH);
        }
        //chicken policy - to be controlled by a 'suicide acceptable' parameter
//        if (suicideAhead()) {
//            return must(Aim.SOUTH);
//        }
//        if (defeatFarAhead()) {
//            return positive(Aim.EAST, Aim.WEST);
//        }
//        //chicken policy - to be controlled by a 'suicide acceptable' parameter
//        if (suicideFarAhead()) {
//            return positive(Aim.EAST, Aim.WEST);
//        }

        if (mixedAhead()) {
            if (state.fl1 == state.fr1) {
                return positive(Aim.EAST, Aim.WEST);
            }
            //non-symmetrical - go towards the fight. The fight is where the other guy is stronger
            if (compare(state.fl1, state.fl2) < 0) {
                return positive(Aim.WEST);
            } else return positive(Aim.EAST);
        }
        DirectionalDecisions decisions = new DirectionalDecisions(state.getAim());
        return decisions;
    }


    private boolean suicideFarAhead() {
        return state.f1 != TIE && state.f2 == TIE;
    }

    private boolean defeatFarAhead() {
        return state.f1 != LOSE && state.f2 == LOSE;
    }


    private DirectionalDecisions positive(Decision stay) {
        return positive(Arrays.asList(stay));
    }


    private DirectionalDecisions positive(Aim... aims) {
        Collection<Decision> decisions = collect(aims);
        return positive(decisions);
    }

    private DirectionalDecisions positive(Collection<Decision> decision) {
        DirectionalDecisions decisions = new DirectionalDecisions(state.getAim());
        decisions.setPositive(decision);
        return decisions;
    }

    private DirectionalDecisions must(Aim... aims) {
        DirectionalDecisions decisions = new DirectionalDecisions(state.getAim());
        decisions.setMust(collect(aims));
        return decisions;
    }

    private Collection<Decision> collect(Decision... decisions) {
        return Arrays.asList(decisions);
    }

    private boolean defeatAhead() {
        return state.f1 == LOSE;
    }

    private boolean suicideAhead() {
        return state.f1 == TIE;
    }


    private boolean mixedAhead() {
        return (state.f1 == WIN && (state.f2 == TIE || state.f2 == LOSE)) || (state.f1 == TIE && state.f2 == LOSE) || (state.f1==NONE && (state.f2==LOSE || state.f2==TIE   ));
    }

    private boolean victoryAhead() {
        return state.f2 == WIN && (state.f1 != LOSE && state.f1 != TIE);
    }

    private Set<Decision> collect(Aim... aims) {
        Set<Decision> retValue = new HashSet<Decision>();
        for (Aim aim : aims) {
            retValue.add(Decision.move(aim));
        }
        return retValue;
    }
}