package subsume;

import ants.Aim;
import ants.Tile;

public class FollowTheWall extends Layer {

    private Boolean leftHanded;
    private Aim previousDirection;
    private boolean following = false;

    public FollowTheWall(Ant ant) {
        super(ant);
        pickHandedness();
        pickDirection();
    }

    @Override
    public Decision output() {

        following = amIFollowing();

        if (!following) {
            //just go until we hit the wall.
            if (passes(ant.tile, previousDirection)) {
                return Decision.move(previousDirection);
            }

            startFollowing();
        }
        previousDirection = followOn();
        return Decision.move(previousDirection);

    }

    @Override
    public String explain() {
        return "";
    }

    private boolean amIFollowing() {
        //I either had a wall on my side, or I have one now.
        Aim side = turn(previousDirection);
        //do 180 in order to get previous location
        Tile previous = ant.ants.getTile(ant.tile, turn(side));
        return !passes(ant.tile, side) || !passes(previous, side);
    }

    private void startFollowing() {
        following = true;
        previousDirection = turnOpposite(previousDirection);
    }

    private Aim followOn() {
        Aim direction = turn(previousDirection);
        int count=0;
        while (!passes(ant.tile, direction) & count++<5) {
            direction = turnOpposite(direction);
        }
        return direction;

    }

    private Aim turnOpposite(Aim direction) {
        if (leftHanded) {
            return direction.turnRight();
        }
        return direction.turnLeft();
    }

    private boolean passes(Tile tile, Aim direction) {
        return ant.ants.getIlk(tile, direction).isPassable();
    }


    private Aim turn(Aim previousDirection) {
        if (leftHanded) {
            return previousDirection.turnLeft();
        }
        return previousDirection.turnRight();
    }

    private void pickDirection() {
        if (previousDirection == null) {
            pickNewDirection();
        }
    }

    private void pickNewDirection() {
        Aim newDir = null;
        do {
            newDir = Aim.values()[Deterministic.next(4)];
        } while (newDir == previousDirection);
        previousDirection = newDir;
    }

    private void pickHandedness() {
        if (leftHanded == null) {
            leftHanded = Deterministic.next(2) == 1;
        }
    }

}