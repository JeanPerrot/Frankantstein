package util;

import ants.Aim;
import subsume.Decision;

import java.util.ArrayList;
import java.util.Collection;

public class Rotate {

    //rotate decisions made by the n-tile state knowing that they were really made in the aim
    public static Decision rotate(Decision decision, Aim aim) {
        if (decision.dontKnow() || decision.stay()) {
            return decision;
        }
        return Decision.move(aim.rotate(decision.aim));
    }

    //rotate decisions made by the n-tile state knowing that they were really made in the aim. Reference is north.
    public static Collection<Decision> rotate(Collection<Decision> decisionsinAim, Aim aim) {
        Collection<Decision> retValue = new ArrayList<Decision>();
        for (Decision decision : decisionsinAim) {
            retValue.add(rotate(decision, aim));
        }
        return retValue;
    }
}