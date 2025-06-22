package org.example.factions;

import org.example.factory.AbstractRobotFactory;

import java.util.concurrent.Phaser;

public class WednesdayFaction extends AbstractFaction {

    public WednesdayFaction(final AbstractRobotFactory factory,
                            final Phaser phaser,
                            final int dayCount,
                            final int robotPartsPerRound) {
        super(factory,phaser,dayCount,robotPartsPerRound);
    }


    @Override
    public void run() {
        getRobotPartsBehavior();
    }




}
