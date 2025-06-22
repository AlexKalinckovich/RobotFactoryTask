package org.example.factory;

import org.example.utils.RobotParts;


import java.util.*;
import java.util.concurrent.Phaser;
import java.util.concurrent.locks.ReentrantLock;

public class StandardRobotFactory extends AbstractRobotFactory{

    private final int CREATE_PARTS_COUNT_PER_ROUND;
    private final int DAY_COUNT;

    private final Phaser phaser;
    private final Map<RobotParts,Integer> robotPartsCount = new HashMap<>();
    private final Random random = new Random();

    private final ReentrantLock lock = new ReentrantLock(true);

    public StandardRobotFactory(final Phaser phaser,
                                final int dayCount,
                                final int createPartsCountPerRound) {
        this.phaser = phaser;
        this.DAY_COUNT = dayCount;
        this.CREATE_PARTS_COUNT_PER_ROUND = createPartsCountPerRound;
    }

    @Override
    public List<RobotParts> getRandomRobotParts(int countOfParts) {
        final List<RobotParts> parts = new ArrayList<>(countOfParts);
        try {
            lock.lock();
            for (int i = 0; i < countOfParts; i++) {
                parts.addLast(getRandomRobotPart());
            }
        }finally {
            lock.unlock();
        }

        return parts;
    }

    @Override
    public void run() {
        int phase = 0;
        while (!phaser.isTerminated() && phase < DAY_COUNT) {
            phase = phaser.arriveAndAwaitAdvance();

            this.createRobotParts(CREATE_PARTS_COUNT_PER_ROUND);

            phaser.arriveAndDeregister();
        }
    }

    @Override
    protected void createRobotParts(int countOfParts) {
        for(int i = 0; i < countOfParts; i++) {
            final int part = random.nextInt(RobotParts.values().length);
            final RobotParts robotPart = robotParts[part];
            final int currentCount = robotPartsCount.getOrDefault(robotPart,0);
            robotPartsCount.put(robotPart,currentCount + 1);
        }
    }



    private RobotParts getRandomRobotPart() {
        final int part = random.nextInt(RobotParts.values().length);
        return robotParts[part];
    }
}
