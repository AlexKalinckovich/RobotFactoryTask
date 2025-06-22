package org.example.factions;

import org.example.factory.AbstractRobotFactory;
import org.example.utils.RobotParts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Phaser;

public abstract class AbstractFaction implements Runnable {


    protected final int DAY_COUNT;
    protected final int ROBOT_PARTS_PER_ROUND;

    protected final AbstractRobotFactory factory;
    protected final Map<RobotParts,Integer> robotPartsCount = new HashMap<>();
    protected final Phaser phaser;


    protected AbstractFaction(final AbstractRobotFactory factory,
                              final Phaser phaser,
                              final int dayCount,
                              final int robotPartsPerRound) {
        this.factory = factory;
        this.phaser = phaser;
        this.DAY_COUNT = dayCount;
        this.ROBOT_PARTS_PER_ROUND = robotPartsPerRound;
    }


    public Map<RobotParts, Integer> getRobotPartsCount() {
        return robotPartsCount;
    }

    protected void collectRobotParts(final List<RobotParts> robotParts) {
        for (final RobotParts robotPart : robotParts) {
            final int currentRobotPartCount = robotPartsCount.getOrDefault(robotPart, 0);
            robotPartsCount.put(robotPart, currentRobotPartCount + 1);
        }
    }

    protected void getRobotPartsBehavior(){
        int phase = 0;
        while(!phaser.isTerminated() && phase < DAY_COUNT) {
            phase = phaser.arriveAndAwaitAdvance();

            final List<RobotParts> robotParts = this.factory.getRandomRobotParts(ROBOT_PARTS_PER_ROUND);
            this.collectRobotParts(robotParts);

            phaser.arriveAndDeregister();
        }
    }
}
