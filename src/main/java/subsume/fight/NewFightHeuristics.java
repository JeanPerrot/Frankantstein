package subsume.fight;

import ants.Aim;
import subsume.Decision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static subsume.fight.FightState.*;

//"avoid death"
public class NewFightHeuristics {

    private CondensedState state;

    public NewFightHeuristics(CondensedState state) {
        this.state = state;
    }

    public DirectionalDecisions applyPolicy(boolean suicideOk) {
        if (victoryAhead()) {
            if (victoryFrontLeft() && victoryFrontRight()) {
                return must(Aim.NORTH);
            } else {
                if (!suicideOk) {
                    return positive(Decision.STAY);
                } else
                    return must(Aim.NORTH);
            }
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


        //chicken policy - to be controlled by a 'suicide acceptable' parameter
        if (!suicideOk) {
            if (suicideAhead()) {
                if (state.isPassable(Aim.SOUTH))
                    return must(Aim.SOUTH);
                return positive(getPassable(Aim.WEST, Aim.EAST));
            }
            //chicken policy - to be controlled by a 'suicide acceptable' parameter
            if (suicideFarAhead()) {
                return positive(Decision.STAY);
            }
        }

        if (mixedAhead()) {
            return positive(Decision.STAY);
        }
        if (defeatFarAhead()) {
            DirectionalDecisions positive = positive(Decision.STAY);
            return positive;
        }

        DirectionalDecisions decisions = new DirectionalDecisions(state.getAim());
        return decisions;
    }

    private DirectionalDecisions must(Decision... stay) {
        DirectionalDecisions decisions = new DirectionalDecisions(state.getAim());
        decisions.setMust(collect(stay));
        return decisions;
    }

    private boolean victoryFrontLeft() {
        return state.fl2 == WIN;
    }

    private boolean victoryFrontRight() {
        return state.fr2 == WIN;
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