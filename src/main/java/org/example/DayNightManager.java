package org.example;

import org.example.factions.AbstractFaction;
import org.example.factions.WednesdayFaction;
import org.example.factions.WorldFaction;
import org.example.factory.AbstractRobotFactory;
import org.example.factory.StandardRobotFactory;
import org.example.utils.RobotParts;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class DayNightManager {

    private final int THREAD_COUNT = 3;
    private final int START_PHASE_COUNT = 1;
    private final int DAY_COUNT = 100;
    private final int ROBOT_PARTS_PER_PHASE = 5;

    private final Phaser phaser = new Phaser(START_PHASE_COUNT);
    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    private final AbstractRobotFactory robotFactory;
    private final AbstractFaction worldFaction;
    private final AbstractFaction wednesdayFaction;

    public DayNightManager(){
        this.robotFactory = new StandardRobotFactory(this.phaser,DAY_COUNT,ROBOT_PARTS_PER_PHASE);
        this.worldFaction = new WorldFaction(robotFactory,this.phaser,DAY_COUNT, ROBOT_PARTS_PER_PHASE);
        this.wednesdayFaction = new WednesdayFaction(robotFactory,this.phaser,DAY_COUNT, ROBOT_PARTS_PER_PHASE);
    }

    public void start(){

        phaser.register();
        executor.submit(robotFactory);

        phaser.register();
        executor.submit(worldFaction);

        phaser.register();
        executor.submit(wednesdayFaction);

        for(int i = 0; i < DAY_COUNT; i++){
            phaser.arriveAndAwaitAdvance();

            phaser.arriveAndAwaitAdvance();
        }
        showWinner();
        executor.shutdown();
    }

    private void showWinner(){
        final Map<RobotParts,Integer> worldFactionRobotPartsCount = this.worldFaction.getRobotPartsCount();
        final Map<RobotParts,Integer> wednesdayRobotPartsCount = this.wednesdayFaction.getRobotPartsCount();

        final int worldFactionRobotsBuild = calculateFullRobotBuilds(worldFactionRobotPartsCount);
        final int wednesdayFactionRobotBuild = calculateFullRobotBuilds(wednesdayRobotPartsCount);

        if(worldFactionRobotsBuild > wednesdayFactionRobotBuild){
            System.out.println("World faction is the winner!");
        }else if(wednesdayFactionRobotBuild > worldFactionRobotsBuild){
            System.out.println("Wednesday faction is the winner!");
        }else{
            System.out.println("Equal!");
        }

    }

    private int calculateFullRobotBuilds(final Map<RobotParts,Integer> robotPartsCount){
        final int headCount = robotPartsCount.getOrDefault(RobotParts.HEAD, 0);
        final int torsoCount = robotPartsCount.getOrDefault(RobotParts.TORSO, 0);
        final int handCount = robotPartsCount.getOrDefault(RobotParts.HAND, 0);
        final int feetCount = robotPartsCount.getOrDefault(RobotParts.FEET, 0);
        return getMinValue(headCount, torsoCount, handCount, feetCount);
    }

    private int getMinValue(final int... values){
        int min = values[0];
        final int size = values.length;
        for(int i = 1; i < size; i++){
            min = Math.min(min, values[i]);
        }
        return min;
    }
}
