package za.co.entelect.challenge;

import za.co.entelect.challenge.command.*;
import za.co.entelect.challenge.entities.*;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.*;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.abs;

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
            case SSMin:
                return 0; // SS 0
            case SS1:
                return 1; // SS 1
            case SS2:
                return 2; // SS 2
            case SS3:
                return 3; // SS 3
            case SSMax:
                return 4; // SS 4
            case SSB:
                return 5; // SS 5
            default:
                return 1; // SS Initial
        }
    }

    private static Integer getSpeedFromSpeedState(int SpeedState) {
        switch (SpeedState) {
            case 0:
                return SSMin; // SS 0
            case 1:
                return SS1; // SS 1
            case 2:
                return SS2; // SS 2
            case 3:
                return SS3; // SS 3
            case 4:
                return SSMax; // SS 4
            case 5:
                return SSB; // SS 5
            default:
                return SSI; // SS Initial
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
    private final static Command TWEET = new TweetCommand(0, 0); // default value

    private static Command TWEET(int lane, int block) {
        return new TweetCommand(lane, block);
    } // For use

    private final static Command TURN_RIGHT = new ChangeLaneCommand(1);
    private final static Command TURN_LEFT = new ChangeLaneCommand(-1);

    // Object Instantiate
    public Bot() {
        // Has no innate
    }

    // Fungsi untuk menjalankan algoritma greedy
    public Command run(GameState gameState) {
        Car myCar = gameState.player;
        Car opponent = gameState.opponent;

        // FUNGSI KELAYAKAN
        // Returns front terrain
        List<Terrain> front = getBlocksInLane(myCar.position.lane, myCar.position.block + 1, gameState);
        // Returns left terrain
        List<Terrain> left = null;
        if (myCar.position.lane != 1)
            left = getBlocksInLane(myCar.position.lane - 1, myCar.position.block, gameState);
        // Returns right terrain
        List<Terrain> right = null;
        if (myCar.position.lane != 4)
            right = getBlocksInLane(myCar.position.lane + 1, myCar.position.block, gameState);

        // Debug Lane
        // System.out.println(left);
        // System.out.println(front);
        // System.out.println(right);

        // FUNGSI SELEKSI
        // Predict Ending States For Commands
        // Returns Difference Of [FinalSpeedState - InitSpeedStat + AccelerationWeight,
        // FinalDamage - InitDamage + DamageWeight]
        List<Integer> PREDACCEL = PredictState(ACCELERATE, myCar, opponent, front);
        List<Integer> PREDDECEL = PredictState(DECELERATE, myCar, opponent, front);
        List<Integer> PREDLIZARD = PredictState(LIZARD, myCar, opponent, front);
        List<Integer> PREDOIL = PredictState(OIL, myCar, opponent, front);
        List<Integer> PREDBOOST = PredictState(BOOST, myCar, opponent, front);
        List<Integer> PREDEMP = PredictState(EMP, myCar, opponent, front);
        List<Integer> PREDFIX = PredictState(FIX, myCar, opponent, front);
        List<Integer> PREDTWEET = PredictState(TWEET, myCar, opponent, front);
        List<Integer> PREDLEFT = PredictState(TURN_LEFT, myCar, opponent, left);
        List<Integer> PREDRIGHT = PredictState(TURN_RIGHT, myCar, opponent, right);
        List<List<Integer>> PREDCOMMANDS = Arrays.asList(PREDACCEL, PREDDECEL, PREDLIZARD, PREDOIL, PREDBOOST, PREDEMP,
                PREDFIX, PREDTWEET, PREDLEFT, PREDRIGHT);

        // FUNGSI OBJEKTIF
        // mencari solusi dari himpunan kandidat dengan pertambahan speed state
        // maksimum, dan jika sama, pertambahan damage minimum
        List<Integer> BESTPRED = null;
        for (List<Integer> PRED : PREDCOMMANDS) {
            // System.out.println(PRED); // Debug
            if (BESTPRED == null) { // first
                BESTPRED = PRED;
                continue;
            }
            if (PRED == null)
                continue; // null guard
            if (BESTPRED.get(0) < PRED.get(0))
                BESTPRED = PRED; // prediksi baru lebih baik secara speed state
            else if (BESTPRED.get(0).equals(PRED.get(0)) && BESTPRED.get(1) > PRED.get(1))
                BESTPRED = PRED; // prediksi baru speed state sama namun lebih baik secara damage
        }
        // System.out.println(BESTPRED); // Debug

        // FUNGSI SOLUSI
        // memastikan ada jawaban di himpunan solusi
        int idx = 0;
        for (List<Integer> PRED : PREDCOMMANDS) {
            if (BESTPRED.equals(PRED))
                break;
            idx++;
        }

        switch (idx) {
            case 1:
                return DECELERATE;
            case 2:
                return LIZARD;
            case 3:
                return OIL;
            case 4:
                return BOOST;
            case 5:
                return EMP;
            case 6:
                return FIX;
            case 7:
                return TWEET(opponent.position.lane, opponent.position.block + opponent.speed + 1);
            case 8:
                return TURN_LEFT;
            case 9:
                return TURN_RIGHT;
            default:
                return ACCELERATE;
        }
        // System.out.println(i); // Debug
    }

    private Boolean hasPowerUp(PowerUps ToCheck, PowerUps[] PList) {
        for (PowerUps powerUp : PList) {
            if (powerUp.equals(ToCheck)) {
                return true;
            }
        }
        return false;
    }

    private List<Terrain> getBlocksInLane(int lane, int block, GameState gameState) {
        List<Lane[]> map = gameState.lanes;
        List<Terrain> blocks = new ArrayList<>();
        int startBlock = map.get(0)[0].position.block;

        Lane[] laneList = map.get(lane - 1);
        for (int i = max(block - startBlock, 0); i < block - startBlock + 20; i++) {
            if (laneList[i] == null || laneList[i].terrain == Terrain.FINISH) {
                break;
            }
            if (laneList[i].isOccupiedByCyberTruck)
                blocks.add(Terrain.WALL);
            else
                blocks.add(laneList[i].terrain);
        }
        return blocks;
    }

    private static boolean isEnemyDirectlyBehind(Car myCar, Car oppCar) {
        int posDiff = myCar.position.block - oppCar.position.block;
        return (myCar.position.lane == oppCar.position.lane && posDiff > 0 && posDiff <= oppCar.speed);
    }

    private static boolean isEnemyEMPable(Car myCar, Car oppCar) {
        return (abs(myCar.position.lane - oppCar.position.lane) <= 1 && oppCar.position.block > myCar.position.block);
    }

    // FUNGSI SELEKSI DAN FUNGSI KELAYAKAN
    private List<Integer> getNewStateFromCommand(Command command, List<Integer> SSDMG, Car myCar, Car opponent) {

        int MaxSS = getMaxSpeedStateFromDamage(SSDMG.get(1));

        if (command.equals(ACCELERATE)) {
            if (SSDMG.get(0) != 5)
                return Arrays.asList(min(min(SSDMG.get(0) + 1, 4), MaxSS), SSDMG.get(1), 0, 0);
            else
                return Arrays.asList(min(5, MaxSS), SSDMG.get(1), 0, 0);
        } else if (command.equals(DECELERATE))
            return Arrays.asList(max(SSDMG.get(0) - 1, 0), SSDMG.get(1), 0, 0);
        else if (command.equals(FIX)) {

            switch (SSDMG.get(1)) {
                case 0:
                    return Arrays.asList(SSDMG.get(0), 0, -5, 0);
                case 1:
                    return Arrays.asList(SSDMG.get(0), 0, 0, 0);
                case 2:
                    return Arrays.asList(SSDMG.get(0), 0, 5, 0);
                default:
                    return Arrays.asList(SSDMG.get(0), SSDMG.get(1) - 2, 5, 0);
            }

        } else if (command.equals(BOOST)) {
            if (hasPowerUp(PowerUps.BOOST, myCar.powerups) && SSDMG.get(0) < 5)
                return Arrays.asList(min(5, MaxSS), SSDMG.get(1), MaxSS - 5, 0);
            else
                return Arrays.asList(SSDMG.get(0), SSDMG.get(1), -5, 0);
        } else if (command.equals(OIL)) {
            if (hasPowerUp(PowerUps.OIL, myCar.powerups) && isEnemyDirectlyBehind(myCar, opponent)) { // and musuh
                                                                                                      // dibelakang (SS,
                                                                                                      // DMG) (desc,asc)
                return Arrays.asList(SSDMG.get(0), SSDMG.get(1), 1, -1);
            } else {
                return Arrays.asList(SSDMG.get(0), SSDMG.get(1), -5, 1);
            }
        } else if (command.equals(TWEET)) {
            if (hasPowerUp(PowerUps.TWEET, myCar.powerups)) {
                return Arrays.asList(SSDMG.get(0), SSDMG.get(1), (getSpeedStateFromSpeed(opponent.speed) - 1), -2);
            } else {
                return Arrays.asList(SSDMG.get(0), SSDMG.get(1), -5, 2);
            }
        } else if (command.equals(EMP)) {
            if (hasPowerUp(PowerUps.TWEET, myCar.powerups) && isEnemyEMPable(myCar, opponent)) { // and musuh di depan
                return Arrays.asList(SSDMG.get(0), SSDMG.get(1), (getSpeedStateFromSpeed(opponent.speed) - 1), -2);
            } else {
                return Arrays.asList(SSDMG.get(0), SSDMG.get(1), -5, 2);
            }
        } else if (command.equals(LIZARD)) {
            if (hasPowerUp(PowerUps.LIZARD, myCar.powerups) && myCar.speed == SSB) {
                return Arrays.asList(SSDMG.get(0), SSDMG.get(1), 2, 0);
            } else if (!hasPowerUp(PowerUps.LIZARD, myCar.powerups)) {
                return Arrays.asList(SSDMG.get(0), SSDMG.get(1), -5, 0);
            }
        }
        return Arrays.asList(SSDMG.get(0), SSDMG.get(1), 0, 0);
    }

    // FUNGSI SELEKSI DAN FUNGSI KELAYAKAN
    private static List<Integer> getNewStateFromTerrain(Terrain terrain, List<Integer> SSDMG) {
        int ss = SSDMG.get(0);
        int dmg = SSDMG.get(1);
        int bobotacc = SSDMG.get(2);
        int bobotdmg = SSDMG.get(3);
        if (terrain.equals(Terrain.WALL)) {
            ss = min(ss, 1);
            dmg = min(dmg + 2, 5);
        } else if (terrain.equals(Terrain.MUD) || terrain.equals(Terrain.OIL_SPILL)) {
            ss = max(0, ss - 1);
            dmg = min(dmg + 1, 5);
        }
        // pembobotan untuk power ups = (..., ..., bobotacc, bobotdmg)
        else if (terrain.equals(Terrain.OIL_POWER)) {
            bobotacc += 1;
        } else if (terrain.equals(Terrain.TWEET)) {
            bobotacc += 1;
            bobotdmg -= 2;
        } else if (terrain.equals(Terrain.BOOST)) {
            bobotacc += 2;
        } else if (terrain.equals(Terrain.EMP)) {
            bobotacc += 1;
            bobotdmg -= 2;
        } else if (terrain.equals(Terrain.LIZARD)) {
            bobotacc += 1;
            bobotdmg -= 2;
        }
        return Arrays.asList(ss, dmg, bobotacc, bobotdmg);
    }

    // FUNGSI SELEKSI DAN FUNGSI KELAYAKAN
    private List<Integer> PredictState(Command command, Car myCar, Car opponent, List<Terrain> Lanes) {
        List<Integer> State = Arrays.asList(getSpeedStateFromSpeed(myCar.speed), myCar.damage, 0, 0);
        List<Terrain> LaneCopy;

        if (Lanes == null) {
            return null;
        }

        State = getNewStateFromCommand(command, State, myCar, opponent); // nilai utk acc,dec,fix,boost
        LaneCopy = Lanes.subList(0, min(getSpeedFromSpeedState(State.get(0)), Lanes.size())); // lane yang dipakai pada
                                                                                              // turn
        // System.out.println(LaneCopy); // Debug

        if (command.equals(LIZARD)) {
            if (LaneCopy.size() != 0)
                State = getNewStateFromTerrain(LaneCopy.get(LaneCopy.size() - 1), State); // Only Last One If Lizard
        } else if (!command.equals(FIX)) {
            for (Terrain lane : LaneCopy) {
                State = getNewStateFromTerrain(lane, State);
            }
        }

        return Arrays.asList(State.get(0) - getSpeedStateFromSpeed(myCar.speed) + State.get(2),
                State.get(1) - myCar.damage + State.get(3));
    }
}
