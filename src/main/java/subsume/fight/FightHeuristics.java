package subsume.fight;

import ants.Aim;
import subsume.Decision;
import util.Print;

import java.security.KeyStore;
import java.util.*;

import static subsume.fight.FightState.*;

public class FightHeuristics {

    private CondensedState state;

    public FightHeuristics(CondensedState state) {
        this.state = state;
    }

    public DirectionalDecisions applyPolicy(boolean suicideOk) {
        if (victoryAhead()) {
            return must(Aim.NORTH);
        }
        if (defeatAhead()) {
            //just run away? right escapes...?
            if (state.isPassable(Aim.SOUTH))
                return must(Aim.SOUTH);
            return positive(getPassable(Aim.WEST, Aim.EAST));

        }

        if (defeatRight() && defeatFrontRight()) {
            if (state.isPassable(Aim.SOUTH))
                return must(Aim.SOUTH);
            if (state.isPassable(Aim.WEST))
                return must(Aim.WEST);
        }

        if (defeatLeft() && defeatFrontLeft()) {
            if (state.isPassable(Aim.SOUTH))
                return must(Aim.SOUTH);
            if (state.isPassable(Aim.EAST))
                return must(Aim.EAST);
        }

        if (defeatLeft() && defeatFrontLeft()) {
            return must(Aim.SOUTH);
        }

        if (defeatFarAhead()) {
            DirectionalDecisions positive = positive(getPassable(Aim.EAST, Aim.WEST, Aim.SOUTH));
            positive.getPositive().add(Decision.STAY);
            return positive;
//            return positive(getPassable(Aim.EAST, Aim.WEST));
        }

        //chicken policy - to be controlled by a 'suicide acceptable' parameter
        if (!suicideOk) {

            if (suicideAhead()) {
                if (state.isPassable(Aim.SOUTH))
                    return must(Aim.SOUTH);
                return positive(getPassable(Aim.WEST, Aim.EAST));
            }
            //chicken policy - to be controlled by a 'suicide acceptable' parameter
            if (suicideFarAhead()) {
                return positive(getPassable(Aim.EAST, Aim.WEST));
            }
        }

        if (mixedAhead()) {
            if (state.fl1 == state.fr1) {
                return positive(getPassable(Aim.EAST, Aim.WEST));
            }
            //non-symmetrical - go towards the fight. The fight is where the other guy is stronger
            if (compare(state.fl1, state.fl2) < 0) {
                if (state.isPassable(Aim.WEST))
                    return positive(Aim.WEST);
                else return positive(Decision.STAY);
            } else {
                if (state.isPassable(Aim.EAST))
                    return positive(Aim.EAST);
                else return positive(Decision.STAY);
            }
        }
        DirectionalDecisions decisions = new DirectionalDecisions(state.getAim());
        return decisions;
    }

    private Aim[] getPassable(Aim... aims) {
        List<Aim> retValue = new ArrayList<Aim>();
        for (Aim aim : aims) {
            if (state.isPassable(aim))
                retValue.add(aim);
        }
        return retValue.toArray(new Aim[0]);
    }


    private boolean suicideFarAhead() {
        return state.f1 != TIE && state.f2 == TIE;
    }

    private boolean defeatFarAhead() {
        return state.f1 != LOSE && state.f2 == LOSE;
    }

    private boolean defeatRight() {
        return state.fr1 == LOSE;
    }

    private boolean defeatLeft() {
        return state.fl1 == LOSE;
    }

    private boolean defeatFrontRight() {
        return state.fr2 == LOSE;
    }

    private boolean defeatFrontLeft() {
        return state.fl2 == LOSE;
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
        return (state.f1 == WIN && (state.f2 == TIE || state.f2 == LOSE)) || (state.f1 == TIE && state.f2 == LOSE) || (state.f1 == NONE && (state.f2 == LOSE || state.f2 == TIE));
    }

    private boolean victoryAhead() {
        return state.f2 == WIN && (state.f1 != LOSE && state.f1 != TIE);
    }

    private Collection<Decision> collect(Aim... aims) {
        List<Decision> retValue = new ArrayList<Decision>();
        for (Aim aim : aims) {
            retValue.add(Decision.move(aim));
        }
        return retValue;
    }
}