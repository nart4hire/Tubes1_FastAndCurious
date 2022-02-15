package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;

import java.security.SecureRandom;

public class Bot {

    // Speed State Definition
    private static final int SSMin = 0;
    private static final int SS1 = 3;
    private static final int SS2 = 6;
    private static final int SS3 = 8;
    private static final int SSMax = 9;

    private static final int SSI = 5;
    private static final int SSB = 15;

    // Command Definition
    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command DECELERATE = new DecelerateCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command EMP = new EmpCommand();
    private final static Command FIX = new FixCommand();
    private static Command TWEET(int lane, int block) { return new TweetCommand(lane, block); }

    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);

    // Object Instantiate
    public Bot() {
        // Has no innate
    }

    public Command run(GameState gameState) {
        Car myCar = gameState.player;
        Car opponent = gameState.opponent;

        // Returns front terrain
        List<Object> front = getBlocksInLane(myCar.position.lane, myCar.position.block + 1, gameState);

        // Returns right terrain
        List<Object> right = null;
        if (myCar.position.lane != 4) right = getBlocksInLane(myCar.position.lane + 1, myCar.position.block, gameState);

        List<Object> left = null;
        if (myCar.position.lane != 1) left = getBlocksInLane(myCar.position.lane - 1, myCar.position.block, gameState);

        //        System.out.println(front);
        //        System.out.println(left);
        //        System.out.println(right);
        
        // Logic
        
        //Fix first if too damaged to move
        if(myCar.damage == 5) {
            return FIX;
        }
        //Accelerate first if going to slow
        if(myCar.speed <= 3) {
            return ACCELERATE;
        }

        //Basic fix logic
        if(myCar.damage >= 5) {
            return FIX;
        }

        //Basic avoidance logic
        if (front.contains(Terrain.MUD) || front.contains(Terrain.WALL)) {
            if (hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                return LIZARD;
            }
            else if  (myCar.position.lane == 1) return TURN_RIGHT;
            else return TURN_LEFT;
        }

        //Basic improvement logic
        if (hasPowerUp(PowerUps.BOOST, myCar.powerups)) {
            return BOOST;
        }

        //Basic aggression logic
        if (myCar.speed == SSMax) {
            if (hasPowerUp(PowerUps.OIL, myCar.powerups)) {
                return OIL;
            }
            if (hasPowerUp(PowerUps.EMP, myCar.powerups)) {
                return EMP;
            }
        }

        return ACCELERATE;
    }

    private Boolean hasPowerUp(PowerUps ToCheck, PowerUps[] PList) {
        for (PowerUps powerUp: PList) {
            if (powerUp.equals(ToCheck)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns map of blocks and the objects in the for the current lanes, returns
     * the amount of blocks that can be traversed at max speed.
     **/
    private List<Object> getBlocksInLane(int lane, int block, GameState gameState) {
        List<Lane[]> map = gameState.lanes;
        List<Object> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i < block - startBlock + Bot.SSMax; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }
            blocks.add(laneList[i].terrain);
        }
        return blocks;
    }

}
