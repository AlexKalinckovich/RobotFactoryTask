package org.example.factory;

import org.example.utils.RobotParts;

import java.util.List;

public abstract class AbstractRobotFactory implements Runnable {

    protected final RobotParts[] robotParts = {
            RobotParts.HEAD,
            RobotParts.TORSO,
            RobotParts.HAND,
            RobotParts.FEET
    };

    protected abstract void createRobotParts(int countOfParts);

    public abstract List<RobotParts> getRandomRobotParts(int countOfParts);
}
