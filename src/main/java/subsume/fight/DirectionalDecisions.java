package subsume.fight;

import ants.Aim;
import subsume.Decision;
import util.Rotate;

import java.util.Collection;
import java.util.Collections;

public class DirectionalDecisions {
    private Aim aim;
    private Collection<Decision> positive = Collections.emptyList();
    private Collection<Decision> must = Collections.emptyList();

    public DirectionalDecisions(Aim aim) {
        this.aim = aim;
    }

    public Collection<Decision> getPositive() {
        return positive;
    }

    public void setPositive(Collection<Decision> positive) {
        this.positive = positive;
    }

    public Collection<Decision> getMust() {
        return must;
    }

    public void setMust(Collection<Decision> must) {
        this.must = must;
    }


    public void rotate() {
        positive = Rotate.rotate(positive, aim);
        must = Rotate.rotate(must, aim);
        aim=Aim.NORTH;
    }

    @Override
    public String toString() {
        return "DirectionalDecisions{" +
                "aim=" + aim +
                ", positive=" + positive +
                ", must=" + must +
                '}';
    }
}
