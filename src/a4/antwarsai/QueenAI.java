package a4.antwarsai;

import aiantwars.EAction;
import aiantwars.EAntType;
import aiantwars.IAntAI;
import aiantwars.IAntInfo;
import aiantwars.IEgg;
import aiantwars.ILocationInfo;
import a4.antwarsairesources.AntWarsAIMap;
import a4.antwarsairesources.AntWarsAIMapLocation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author ichti (Simon T)
 */
public class QueenAI extends SharedAI implements IAntAI {

    //private AntWarsAIMap sharedInfo.getSharedMap();
    private final Random rnd = new Random();
    //private QueenState state = QueenState.Test;
    private QueenState state = QueenState.FindFood;
    private final int findfoodturn = 250;
    public QueenAI() {
        super();
        //sharedInfo = new SharedInfo();
    }

    /*
     * Queen states: - Start of game - After spawning first ant (carrier ant) -
     * After spawning 2nd ant (scout ant) - After spawning 3rd ant (defender
     * ant?) - Others? like being attacked.
     */
    @Override
    public void onHatch(IAntInfo thisAnt, ILocationInfo thisLocation, int worldSizeX, int worldSizeY) {
        sharedInfo = new SharedInfo();
        sharedInfo.setSharedMap(new AntWarsAIMap(worldSizeX, worldSizeY));
        //Only the queen should instanciate the sharedMap. 
        //If more than 1 queen is allowed, change so a new queen doesn't make 
        //a new sharedMap
        sharedOnHatch(thisAnt, thisLocation, worldSizeX, worldSizeY);
        sharedInfo.setStartPos(new int[]{thisLocation.getX(), thisLocation.getY()});
        sharedInfo.setWorldMax(new int[]{worldSizeX - 1, worldSizeY - 1});

        List<int[]> nullcoords = new ArrayList<>();
        for (AntWarsAIMapLocation mapLoc : sharedInfo.getSharedMap()) {
            if (mapLoc.getLocationInfo() == null) {
                nullcoords.add(new int[]{mapLoc.getX(), mapLoc.getY()});
            }
        }
        Collections.shuffle(nullcoords);
        sharedInfo.setExplorationCoordinates(nullcoords);
    }

    @Override
    public void onStartTurn(IAntInfo thisAnt, int turn) {
        sharedOnStartTurn(thisAnt, turn);
    }

    @Override
    public EAction chooseAction(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions) {
        sharedChooseAction(thisAnt, thisLocation, visibleLocations, possibleActions);
        EAction action;
        switch (state) {
            case FindFood:
                action = stateFindFood(thisAnt, thisLocation, visibleLocations, possibleActions);
                break;
            case LayCarrier:
                action = stateLayCarrier(thisAnt, thisLocation, visibleLocations, possibleActions);
                break;
            case LayScout:
                action = stateLayScout(thisAnt, thisLocation, visibleLocations, possibleActions);
                break;
            case LayWarrior:
                action = stateLayWarrior(thisAnt, thisLocation, visibleLocations, possibleActions);
                break;
            case FortressMode:
                action = stateFortressMode(thisAnt, thisLocation, visibleLocations, possibleActions);
                break;
            case Test:
                action = stateTest(thisAnt);
                break;
            default:
                //System.out.println("state not found");
                action = EAction.Pass;
        }
        return action;
    }

    EAntType nextEgg;

    @Override
    public void onLayEgg(IAntInfo thisAnt, List<EAntType> types, IEgg egg) {
        //EAntType type = types.get(rnd.nextInt(types.size())); 
        //System.out.println("ID: " + thisAnt.antID() + " onLayEgg: " + type);
        //egg.set(type, this);
        if (types.contains(nextEgg)) {
            if (nextEgg == EAntType.CARRIER) {
                egg.set(nextEgg, new CarrierAI(sharedInfo));
            }
            if (nextEgg == EAntType.SCOUT) {
                egg.set(nextEgg, new ScoutAI3(sharedInfo));
            }
            if (nextEgg == EAntType.WARRIOR) {
                egg.set(nextEgg, new BrandNewWarrior(sharedInfo));
            }

            //if (nextEgg == EAntType.WARRIOR) egg.set(nextEgg, new WarriorAI());
        }
        else {
            //System.out.println("Couldn't find egg type?!");
        }

        sharedInfo.getAllyTeamInfo().addEgg(nextEgg);

    }

    @Override
    public void onAttacked(IAntInfo thisAnt, int dir, IAntInfo attacker, int damage) {
        sharedOnAttacked(thisAnt, dir, attacker, damage);
    }

    @Override
    public void onDeath(IAntInfo thisAnt) {
        sharedOnDeath(thisAnt);
    }

    private EAction stateTest(IAntInfo thisAnt) {
        //System.out.println("test1");
        //return sharedMap.getFirstOneTurnMove(thisAnt, 7, 7, true).get(0);
        return EAction.Pass;
    }

    private EAction stateLayCarrier(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions) {

        int[] layEggLocation;
        int startX = sharedInfo.getStartPos()[0];
        int startY = sharedInfo.getStartPos()[1];
        if (startX == 0) {
            layEggLocation = new int[]{startX, startY, 1};
        }
        else {
            layEggLocation = new int[]{startX, startY, 3};
        }
        /*
         * if (sharedInfo.getSharedMap().getLocation(startX, startY + 1) != null
         * && sharedInfo.getSharedMap().getLocation(startX, startY +
         * 1).getLocationInfo() != null &&
         * !sharedInfo.getSharedMap().getLocation(startX, startY +
         * 1).getLocationInfo().isFilled() &&
         * sharedInfo.getSharedMap().getLocation(startX, startY +
         * 2).getLocationInfo() != null &&
         * !sharedInfo.getSharedMap().getLocation(startX, startY +
         * 2).getLocationInfo().isFilled()) { layEggLocation = new int[]{startX,
         * startY, 0}; } else if (sharedInfo.getSharedMap().getLocation(startX,
         * startY - 1) != null && sharedInfo.getSharedMap().getLocation(startX,
         * startY - 1).getLocationInfo() != null &&
         * !sharedInfo.getSharedMap().getLocation(startX, startY -
         * 1).getLocationInfo().isFilled() &&
         * sharedInfo.getSharedMap().getLocation(startX, startY -
         * 2).getLocationInfo() != null &&
         * !sharedInfo.getSharedMap().getLocation(startX, startY -
         * 2).getLocationInfo().isFilled()) { layEggLocation = new int[]{startX,
         * startY, 2}; } else if (sharedInfo.getSharedMap().getLocation(startX -
         * 1, startY) != null && sharedInfo.getSharedMap().getLocation(startX -
         * 1, startY).getLocationInfo() != null &&
         * !sharedInfo.getSharedMap().getLocation(startX - 1,
         * startY).getLocationInfo().isFilled() &&
         * sharedInfo.getSharedMap().getLocation(startX - 2,
         * startY).getLocationInfo() != null &&
         * !sharedInfo.getSharedMap().getLocation(startX - 2,
         * startY).getLocationInfo().isFilled()) { layEggLocation = new
         * int[]{startX, startY, 3}; } else if
         * (sharedInfo.getSharedMap().getLocation(startX + 1, startY) != null &&
         * sharedInfo.getSharedMap().getLocation(startX + 1,
         * startY).getLocationInfo() != null &&
         * !sharedInfo.getSharedMap().getLocation(startX + 1,
         * startY).getLocationInfo().isFilled()) { layEggLocation = new
         * int[]{startX, startY, 1}; } else { layEggLocation = new int[]{startX,
         * startY, 3}; }
         *
         */
        EAction action;
        if (layEggLocation[2] == thisAnt.getDirection()
                && layEggLocation[0] == thisAnt.getLocation().getX()
                && layEggLocation[1] == thisAnt.getLocation().getY()
                && thisAnt.getAntType().getActionCost(EAction.LayEgg) < thisAnt.getFoodLoad()) {
            if (possibleActions.contains(EAction.LayEgg)) {
                int foodX = thisLocation.getX();
                int foodY = thisLocation.getY();
                if (layEggLocation[2] == 1) {
                    foodX++;
                }
                if (layEggLocation[2] == 3) {
                    foodX--;
                }
                sharedInfo.setFoodDepot(new int[]{foodX, foodY});
                //System.out.println("foodDepot: {"+foodX+","+foodY+"}");
                nextEgg = EAntType.CARRIER;
                action = EAction.LayEgg;
                if (currentTurn < findfoodturn) {
                    state = QueenState.FindFood;
                }
                else {
                    state = QueenState.FortressMode;
                }
            }
            else {

                //System.out.println("Waiting to lay an egg");
                action = EAction.Pass;
            }
        }
        else if (!moves.isEmpty()) {
            if (possibleActions.contains(moves.get(0))) {
                action = moves.remove(0);
            }
            else {
                //System.out.println("found impossible action" + moves.get(0));
                action = EAction.Pass;
            }
        }
        else {
            moves = sharedInfo.getSharedMap().getFirstOneTurnMove(thisAnt, sharedInfo.getSharedMap().getLocation(layEggLocation[0], layEggLocation[1]).getLocationInfo(), layEggLocation[2]);
            action = moves.remove(0);
        }
        return action;

    }

    private EAction stateFortressMode(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions) {
        EAction action;

        if (sharedInfo.getAllyTeamInfo().antCount() == 1 || sharedInfo.getAllyTeamInfo().antCount(EAntType.CARRIER) < 1) {
            state = QueenState.FindFood;
        }
        if (sharedInfo.getFoodDepot() == null) {
            //System.out.println("test");
            return EAction.Pass;
        }
        if (thisAnt.getHitPoints() < 10 && possibleActions.contains(EAction.EatFood)) {
            action = EAction.EatFood;
        }
        else if (thisLocation.getX() == sharedInfo.getFoodDepot()[0] && thisLocation.getY() == sharedInfo.getFoodDepot()[1]) {
            if (thisLocation.getFoodCount() > 0 && possibleActions.contains(EAction.PickUpFood)) {
                action = EAction.PickUpFood;
            }
            else {
                moves = sharedInfo.getSharedMap().getFirstOneTurnMove(thisAnt, sharedInfo.getSharedMap().getLocation(sharedInfo.getStartPos()[0], sharedInfo.getStartPos()[1]).getLocationInfo());
                action = moves.remove(0);
            }
        }
        else if (thisLocation.getX() == sharedInfo.getStartPos()[0] && thisLocation.getY() == sharedInfo.getStartPos()[1]) {
            if (thisAnt.getFoodLoad() - thisAnt.getAntType().getLayEggCost() > 0) {
                if (sharedInfo.getAllyTeamInfo().antCount(EAntType.CARRIER) < 1) {
                    nextEgg = EAntType.CARRIER;
                }
                else if (sharedInfo.getAllyTeamInfo().antCount(EAntType.SCOUT) < 1) {
                    nextEgg = EAntType.SCOUT;
                }
                else { //if (allyTeamInfo.antCount(EAntType.WARRIOR) < 100) {
                    nextEgg = EAntType.WARRIOR;
                }
                action = EAction.LayEgg;
            }
            else if (sharedInfo.getSharedMap().getLocation(sharedInfo.getFoodDepot()[0], sharedInfo.getFoodDepot()[1]).getLocationInfo().getFoodCount() > 5
                    && thisAnt.getFoodLoad() < thisAnt.getAntType().getMaxFoodLoad()) {
                moves = sharedInfo.getSharedMap().getFirstOneTurnMove(thisAnt, sharedInfo.getSharedMap().getLocation(sharedInfo.getFoodDepot()[0], sharedInfo.getFoodDepot()[1]).getLocationInfo());
                action = moves.remove(0);
                //action = EAction.Pass;
            }
            else {
                action = EAction.Pass;
            }
        }
        else {
            if (moves.isEmpty()) {
                moves = sharedInfo.getSharedMap().getFirstOneTurnMove(thisAnt, sharedInfo.getSharedMap().getLocation(sharedInfo.getStartPos()[0], sharedInfo.getStartPos()[1]).getLocationInfo());
            }
            action = moves.remove(0);
        }

        return action;

    }

    private EAction stateLayScout(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions) {
        
        if (currentTurn < findfoodturn) {
            state = QueenState.FindFood;
        }
        else {
            state = QueenState.FortressMode;
        }
        nextEgg = EAntType.SCOUT;
        return EAction.LayEgg;
    }
    
    private EAction stateLayWarrior(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions) {
        
        if (currentTurn < findfoodturn) {
            state = QueenState.FindFood;
        }
        else {
            state = QueenState.FortressMode;
        }
        nextEgg = EAntType.WARRIOR;
        return EAction.LayEgg;
    }

    public enum QueenState {
        FindFood,
        LayCarrier,
        LayScout,
        LayDefender,
        LayWarrior,
        FortressMode,
        Test

    }

    int spinPlease = 4; //Increment to avoid spin, Decrement to spin
    //Starting state. Find food until you can lay an egg.

    private EAction stateFindFood(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions) {

        EAction action;
        if (!moves.isEmpty()) {
            if (possibleActions.contains(moves.get(0))) {
                action = moves.remove(0);
            }
            else {
                //System.out.println("found impossible action" + moves.get(0));
                action = EAction.Pass;
            }
        }
        else if (possibleActions.contains(EAction.PickUpFood)) {
            action = EAction.PickUpFood;

            if (thisAnt.getFoodLoad() > thisAnt.getAntType().getActionCost(EAction.LayEgg)) {
                if (sharedInfo.getAllyTeamInfo().antCount(EAntType.CARRIER) < 1) {
                    state = QueenState.LayCarrier;
                }
                else if (sharedInfo.getAllyTeamInfo().antCount(EAntType.SCOUT) < 1) {
                    state = QueenState.LayScout;
                }
                else { //if (allyTeamInfo.antCount(EAntType.WARRIOR) < 100) {
                    state = QueenState.LayWarrior;
                }

            }
        }
        else if (thisAnt.getHitPoints() < 10) {
            action = EAction.EatFood;
        }
        else if (spinPlease > 0 && possibleActions.contains(EAction.TurnLeft)) {
            action = EAction.TurnLeft;
            spinPlease--;
        }
        else if (thisAnt.getActionPoints() == thisAnt.getAntType().getMaxActionPoints()) {
            List<ILocationInfo> foodLocations = sharedInfo.getSharedMap().getLocationsWithFood();
            if (foodLocations.isEmpty()) {
                //System.out.println("No locations with food found");
                spinPlease++;
                action = EAction.Pass;
            }
            else {
                moves = sharedInfo.getSharedMap().getFirstOneTurnMove(thisAnt, foodLocations);
                action = moves.remove(0);
                if (action == EAction.MoveForward || action == EAction.MoveBackward) {
                    spinPlease = 4;
                }
            }
        }
        else {
            //System.out.println("Pass?");
            action = EAction.Pass;
        }
        return action;
    }
    @Override
    public void onStartMatch(int worldSizeX, int worldSizeY) {
    }

    @Override
    public void onStartRound(int round) {
    }

    @Override
    public void onEndRound(int yourMajor, int yourMinor, int enemyMajor, int enemyMinor) {
    }

    @Override
    public void onEndMatch(int yourScore, int yourWins, int enemyScore, int enemyWins) {
    }
}
