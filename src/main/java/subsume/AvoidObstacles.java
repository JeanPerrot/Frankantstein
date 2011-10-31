package subsume;

import ants.Aim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AvoidObstacles extends Layer {


    public AvoidObstacles(Ant ant) {
        super(ant);
    }

    @Override
    public Aim output() {
        Aim decision = ant.currentDecision;
        if (ant.willCollide(decision)) {
            List<Aim> aims = Arrays.asList(Aim.values());
            aims = new ArrayList<Aim>(aims);
            aims.add(null);
            for (Aim aim : aims) {
                if (!ant.willCollide(aim)) {
                    decision = aim;
                    break;
                }
            }
        }
        return decision;
    }
}