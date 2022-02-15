package za.co.entelect.challenge;

import jdk.internal.util.Preconditions;
import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Bot {

    // Speed State Definition
    private static final int SSMin = 0;
    private static final int SS1 = 3;
    private static final int SS2 = 6;
    private static final int SS3 = 8;
    private static final int SSMax = 9;
    private static final int SSB = 15;
    private static final int SSI = 5;

    private static Integer getSpeedStateFromSpeed(int Speed) {
        switch (Speed) {
            case SSMin: return 0; // SS 0
            case SS1: return 1;   // SS 1
            case SS2: return 2;   // SS 2
            case SS3: return 3;   // SS 3
            case SSMax: return 4; // SS 4
            case SSB: return 5;   // SS 5
            default: return -1;   // SS Initial
        }
    }

    private static Integer getSpeedFromSpeedState(int SpeedState) {
        switch (SpeedState) {
            case 0: return SSMin; // SS 0
            case 1: return SS1;   // SS 1
            case 2: return SS2;   // SS 2
            case 3: return SS3;   // SS 3
            case 4: return SSMax; // SS 4
            case 5: return SSB;   // SS 5
            default: return SSI;  // SS Initial
        }
    }

    private static Integer getMaxSpeedStateFromDamage(int damage) {
        return 5 - damage;
    }

    // Command Definition
    private final static Command ACCELERATE = new AccelerateCommand();
    private final static Command DECELERATE = new DecelerateCommand();
    private final static Command LIZARD = new LizardCommand();
    private final static Command OIL = new OilCommand();
    private final static Command BOOST = new BoostCommand();
    private final static Command EMP = new EmpCommand();
    private final static Command FIX = new FixCommand();
    private final static Command TWEET = new TweetCommand(0, 0);                      // default value
    private static Command TWEET(int lane, int block) { return new TweetCommand(lane, block); }  // For use

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
        List<Terrain> front = getBlocksInLane(myCar.position.lane, myCar.position.block + 1, gameState);

        // Returns left terrain
        List<Terrain> left = null;
        if (myCar.position.lane != 1) left = getBlocksInLane(myCar.position.lane - 1, myCar.position.block, gameState);

        // Returns right terrain
        List<Terrain> right = null;
        if (myCar.position.lane != 4) right = getBlocksInLane(myCar.position.lane + 1, myCar.position.block, gameState);

        // Predict Ending States For Commands
        // Returns Difference Of [FinalSpeedState - InitSpeedState, FinalDamage - InitDamage]
        List<Integer> PREDACCEL = PredictState(ACCELERATE, myCar,opponent, front);
        List<Integer> PREDDECEL = PredictState(DECELERATE, myCar,opponent, front);
        List<Integer> PREDLIZARD = PredictState(LIZARD, myCar,opponent, front);
        List<Integer> PREDOIL = PredictState(OIL, myCar,opponent, front);
        List<Integer> PREDBOOST = PredictState(BOOST, myCar,opponent, front);
        List<Integer> PREDEMP = PredictState(EMP, myCar,opponent, front);
        List<Integer> PREDFIX = PredictState(FIX, myCar,opponent, front);
        List<Integer> PREDTWEET = PredictState(TWEET, myCar,opponent, front);
        List<Integer> PREDLEFT = PredictState(TURN_LEFT, myCar,opponent, left);
        List<Integer> PREDRIGHT = PredictState(TURN_RIGHT, myCar,opponent, right);



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
            if (hasPowerUp(PowerUps.TWEET, myCar.powerups)) {
                return TWEET(opponent.position.lane, opponent.position.block + 1);
            }
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
    private List<Terrain> getBlocksInLane(int lane, int block, GameState gameState) {
        List<Lane[]> map = gameState.lanes;
        List<Terrain> blocks = new ArrayList<>();
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

    // pembobotan nilai
    private List<Integer> getNewStateFromCommand(Command command, List<Integer> SSDMG, Car myCar) {
        int MaxSS = getMaxSpeedStateFromDamage(SSDMG.get(1));
        if (command.equals(ACCELERATE)) return Arrays.asList(min(min(SSDMG.get(0) + 1, 4), MaxSS), SSDMG.get(1));
        else if (command.equals(DECELERATE)) return Arrays.asList(max(SSDMG.get(0) - 1, 0), SSDMG.get(1));
        else if (command.equals(BOOST)) return Arrays.asList(min(5, MaxSS), SSDMG.get(1));
        else if (command.equals(FIX)) {

            if (myCar.damage == 0){
                return Arrays.asList(SSDMG.get(0) - 2, max(SSDMG.get(1) - 2, 0));
            } else if (myCar.damage == 1){
                return Arrays.asList(SSDMG.get(0) , max(SSDMG.get(1) - 2, 0));
            } else if (myCar.damage == 2 ){
                return Arrays.asList(SSDMG.get(0) + 2 , max(SSDMG.get(1) - 2, 0));
            } else {
                return Arrays.asList(SSDMG.get(0) + 2 , max(SSDMG.get(1) - 2, 0));

            }

        }
        else return Arrays.asList(min(SSDMG.get(0), SSDMG.get(1)), SSDMG.get(1));
    }

    private static List<Integer> getNewStateFromTerrain(Terrain terrain, List<Integer> SSDMG, Car opponent, Car myCar) {
        int dmg;
        if (terrain.equals(Terrain.WALL)) dmg = min(SSDMG.get(1) + 2, 5);
        if (terrain.equals(Terrain.MUD) || terrain.equals(Terrain.OIL_SPILL)) dmg = min(SSDMG.get(1) + 1, 5);

        // pembobotan untuk power ups = (speed lost opponent, damage taken by opponent)
        if (terrain.equals(Terrain.OIL_POWER)){
            // cara lihat kebelakang gimana ?
            // jika musuh dibelakang + point else 0
            return Arrays.asList(SSDMG.get(0) + 1,0);
        } else if (terrain.equals(Terrain.TWEET)){
            int speedloss = getSpeedStateFromSpeed(opponent.speed) - 1;
            return Arrays.asList(speedloss, -2); // damage diurutkan dari yg paling kecil
        } else if (terrain.equals(Terrain.BOOST)){
            int speedgain = 5 - getSpeedStateFromSpeed(myCar.speed);
            return Arrays.asList(speedgain,0);
        }
        else dmg = min(SSDMG.get(1), 5);
        return Arrays.asList(min(SSDMG.get(0), getMaxSpeedStateFromDamage(dmg)), dmg);
        // urutan belum diimplement
    }

    private List<Integer> PredictState(Command command, Car myCar, Car opponent, List<Terrain> Lanes) {
        List<Integer> State = Arrays.asList(getSpeedStateFromSpeed(myCar.speed), myCar.damage);
        if (Lanes == null) {
            return null;
        }

        State = getNewStateFromCommand(command, State, myCar); // nilai utk acc,dec,fix,boost

        if (command.equals(LIZARD)) {
            State = getNewStateFromTerrain(Lanes.get(Lanes.size() - 1), State, opponent, myCar); // Only Last One If Lizard
        }
        else if (!command.equals(FIX)){
            for (Terrain lane : Lanes) {
                State = getNewStateFromTerrain(lane, State,opponent,myCar);
                if (lane.equals(Terrain.WALL)) break;
            }
            // pembobotan power ups , check
            // pembobotan pemakaian power up selain boost, check
            // pembobotan fix, check
            // main logic 
        }

        return Arrays.asList(State.get(0) - getSpeedStateFromSpeed(myCar.speed), State.get(1) - myCar.damage);
    }

}

// changes
// 1. PredicState add paramter opponent
// 1. getNewStateFromCommand add paramater myCar
// 2. getNewStateFromTerrain add paramater opponent myCar
