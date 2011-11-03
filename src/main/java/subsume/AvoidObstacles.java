package subsume;

import ants.Aim;

import java.util.Arrays;
import java.util.List;

public class AvoidObstacles extends Layer {

    public AvoidObstacles(Ant ant) {
        super(ant);
    }

    @Override
    public Decision output() {
        Decision currentDecision = ant.currentDecision;
        Aim direction = currentDecision.aim;
        if (ant.willCollide(direction)) {
            List<Aim> turns;
            if (direction == null) {
                turns = Arrays.asList(Aim.values());
            } else {
                turns = Arrays.asList(direction.turnLeft(), direction.turnRight());
            }
            for (Aim aim : turns) {
                if (!ant.willCollide(aim)) {
                    direction = aim;
                    return Decision.move(direction);
                }
            }
            if (!ant.willCollide(null)) {
                return Decision.STAY;
            }
            Print.println("taking a chance here. we might die.");
            return Decision.move(direction.turnLeft().turnLeft());

        }
        return Decision.move(direction);
    }

    @Override
    public String explain() {
        return "(" + lastDecision.aim + ")";
    }
}