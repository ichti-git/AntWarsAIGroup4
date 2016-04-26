package a4.antwarsai;

import aiantwars.EAction;
import aiantwars.EAntType;
import aiantwars.IAntAI;
import aiantwars.IAntInfo;
import aiantwars.IEgg;
import aiantwars.ILocationInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author ichti (Simon T)
 */
public class CarrierAI extends SharedAI implements IAntAI {

    private CarrierState state = CarrierState.MakeStartSquare;
    private final List<IAntInfo> attackerInfo = new ArrayList<>();
    private final List<ILocationInfo> attackerLocations = new ArrayList<>();

    public CarrierAI(SharedInfo sharedInfo) {
        super(sharedInfo);
    }

    @Override
    public void onHatch(IAntInfo thisAnt, ILocationInfo thisLocation, int worldSizeX, int worldSizeY) {
        sharedOnHatch(thisAnt, thisLocation, worldSizeX, worldSizeY);
//        foodDepot = new int[] { thisLocation.getX(), thisLocation.getY()};
    }

    @Override
    public void onStartTurn(IAntInfo thisAnt, int turn) {
        sharedOnStartTurn(thisAnt, turn);
    }

    @Override
    public EAction chooseAction(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions) {
        sharedChooseAction(thisAnt, thisLocation, visibleLocations, possibleActions);
        //EAction action;
        switch (state) {
            case FindFood:
                return stateFindFood(thisAnt, thisLocation, visibleLocations, possibleActions);
                //break;
            case DropFood:
                return stateDropFood(thisAnt, thisLocation, visibleLocations, possibleActions);
                //break;
            case MakeStartSquare:
                return stateMakeStartSquare(thisAnt, thisLocation, visibleLocations, possibleActions);
                //break;
            default:
                //System.out.println("state not found");
                return EAction.Pass;
        }
        //return action;
    }

    @Override
    public void onLayEgg(IAntInfo thisAnt, List<EAntType> types, IEgg egg) {
        //Carrier can't lay egg
    }

    @Override
    public void onAttacked(IAntInfo thisAnt, int dir, IAntInfo attacker, int damage) {
        sharedOnAttacked(thisAnt, dir, attacker, damage);
        attackerInfo.add(attacker);
        attackerLocations.add(attacker.getLocation());
    }

    @Override
    public void onDeath(IAntInfo thisAnt) {
        sharedOnDeath(thisAnt);
    }

    public void setState(CarrierState state) {
        this.state = state;
    }

    private EAction stateDropFood(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions) {
        EAction action = EAction.Pass;
        if (thisLocation.getX() == sharedInfo.getFoodDepot()[0] && thisLocation.getY() == sharedInfo.getFoodDepot()[1]) {
            if (thisAnt.getFoodLoad() == 0) {
                //System.out.println("No food to drop, changing state (This shouldn't be reached)");
                state = CarrierState.FindFood;
            }
            else if (possibleActions.contains(EAction.DropFood)) {
                action = EAction.DropFood;
                if (thisAnt.getFoodLoad() == 1) {
                    //System.out.println("Out of food, changing state");
                    state = CarrierState.FindFood;
                }
            }
            else {
                //System.out.println("Can't drop food, waiting to drop food");
            }
        }
        else if (moves.isEmpty()) {
            //System.out.println("Finding path to food drop");
            //Ant priority?
            if (!visibleLocations.isEmpty()
                    && attackerInfo != null
                    && attackerLocations.contains(visibleLocations.get(0))) {
                sharedInfo.getSharedMap().addTemporaryInvalidLocation(visibleLocations.get(0));
            }
            moves = sharedInfo.getSharedMap().getFirstOneTurnMove(thisAnt, sharedInfo.getSharedMap().getLocation(sharedInfo.getFoodDepot()[0], sharedInfo.getFoodDepot()[1]).getLocationInfo());
            sharedInfo.getSharedMap().clearTemporaryInvalidLocations();
            action = moves.remove(0);
        }
        else {
            //System.out.println("Continuing move orders");
            action = moves.remove(0);
        }
        attackerInfo.clear();
        return action;
    }

    int foodLoadReturn = 4;
    int spinPlease = 4;

    private EAction stateFindFood(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions) {

        EAction action;
        if (!moves.isEmpty()) {
            if (possibleActions.contains(moves.get(0))) {
                action = moves.remove(0);
                //System.out.println("doing move order: " + action);
            }
            else {
                //System.out.println("found impossible action: " + moves.get(0));
                action = EAction.Pass;
            }
        }
        else if (thisAnt.getHitPoints() < 10 && possibleActions.contains(EAction.EatFood)) {
            //System.out.println("eat food    ");
            action = EAction.EatFood;
        }
        else if (possibleActions.contains(EAction.PickUpFood)
                && !(thisLocation.getX() == sharedInfo.getFoodDepot()[0]
                && thisLocation.getY() == sharedInfo.getFoodDepot()[1])) {
            //System.out.println("picking up food");
            action = EAction.PickUpFood;
            if (thisAnt.getFoodLoad() > foodLoadReturn) {
                foodLoadReturn += 2;
                state = CarrierState.DropFood;
                //System.out.println("changing to drop food");
            }
        }
        else if (spinPlease > 0 && possibleActions.contains(EAction.TurnLeft)) {
            //System.out.println("spin");
            action = EAction.TurnLeft;
            spinPlease--;
        }
        else if (thisAnt.getActionPoints() == thisAnt.getAntType().getMaxActionPoints()) {
            List<ILocationInfo> foodLocations = sharedInfo.getSharedMap().getLocationsWithFood();
            ILocationInfo foodDrop = sharedInfo.getSharedMap().getLocation(sharedInfo.getFoodDepot()[0], sharedInfo.getFoodDepot()[1]).getLocationInfo();
            foodLocations.remove(foodDrop);
            if (foodLocations.isEmpty()) {
                //System.out.println("No locations with food found");
                //spinPlease++;
                //action = EAction.Pass;
                action = returnRandomAction(possibleActions);
            }
            else {
                //System.out.println("found food. Moving to food");
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

    private EAction stateMakeStartSquare(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions) {
        EAction action;
        //Assumes start positions is in the corners.
        /*
        int x = 0;
        int y = 0;
        if (sharedInfo.getStartPos()[0] == 0) {
            x = 1;
        }
        if (sharedInfo.getStartPos()[0] == sharedInfo.getWorldMax()[0]) {
            x = sharedInfo.getWorldMax()[0] - 1;
        }
        if (sharedInfo.getStartPos()[1] == 0) {
            y = 1;
        }
        if (sharedInfo.getStartPos()[1] == sharedInfo.getWorldMax()[1]) {
            y = sharedInfo.getWorldMax()[1] - 1;
        }
        ILocationInfo tile4 = sharedInfo.getSharedMap().getLocation(x, y).getLocationInfo();

        if (tile4 == null
                || (tile4.isFilled()
                && !tile4.isRock())) {
            int direction = 0;
            if (sharedInfo.getFoodDepot()[1] + 1 == y) {
                direction = 0;
            }
            if (sharedInfo.getFoodDepot()[0] + 1 == x) {
                direction = 1;
            }
            if (sharedInfo.getFoodDepot()[1] - 1 == y) {
                direction = 2;
            }
            if (sharedInfo.getFoodDepot()[0] - 1 == x) {
                direction = 3;
            }

            if (thisLocation.getX() == sharedInfo.getFoodDepot()[0]
                    && thisLocation.getY() == sharedInfo.getFoodDepot()[1]
                    && thisAnt.getDirection() == direction) {
                if (possibleActions.contains(EAction.DigOut)) {
                    action = EAction.DigOut;
                    state = CarrierState.FindFood;
                }
                else {
                    action = EAction.Pass;
                }

            }
            else {
                if (moves.isEmpty()) {
                    if (visibleLocations.get(0) != null
                            && visibleLocations.get(0).getAnt() != null) {
                        sharedInfo.getSharedMap().addTemporaryInvalidLocation(visibleLocations.get(0));
                    }
                    moves = sharedInfo.getSharedMap().getFirstOneTurnMove(thisAnt, sharedInfo.getSharedMap().getLocation(sharedInfo.getFoodDepot()[0], sharedInfo.getFoodDepot()[1]).getLocationInfo(), direction);
                    sharedInfo.getSharedMap().clearTemporaryInvalidLocations();
                }
                if (possibleActions.contains(moves.get(0))) {
                    action = moves.remove(0);
                }
                else if (moves.get(0) == EAction.MoveBackward) {
                    action = EAction.TurnLeft;
                }
                else {
                    action = EAction.Pass;
                }
            }
        }
        else {
            state = CarrierState.FindFood;
            action = EAction.Pass;
        }
        */
        state = CarrierState.FindFood;
        action = EAction.Pass;
        return action;
    }

        public EAction returnRandomAction(List<EAction> possibleActions) {
        
        possibleActions.remove(EAction.Attack); //Never do a random attack. It might be a friendly ant.
        int size = possibleActions.size();
        Random rand = new Random();
        int number = rand.nextInt(size);
        EAction ac = possibleActions.get(number);
        return ac;

    }
        
    public enum CarrierState {
        FindFood,
        DropFood,
        MakeStartSquare
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


/* Layers...
 * ml for each layer?
 * 
 * 
 */