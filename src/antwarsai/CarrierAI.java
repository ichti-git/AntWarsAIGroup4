package antwarsai;

import aiantwars.EAction;
import aiantwars.EAntType;
import aiantwars.IAntAI;
import aiantwars.IAntInfo;
import aiantwars.IEgg;
import aiantwars.ILocationInfo;
import static antwarsai.SharedAI.sharedMap;
import java.util.List;

/**
 *
 * @author ichti (Simon T)
 */
public class CarrierAI extends SharedAI implements IAntAI {
    private CarrierState state = CarrierState.MakeStartSquare;
    
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
        EAction action;
        switch(state) {
            case FindFood:
                action = stateFindFood(thisAnt, thisLocation, visibleLocations, possibleActions);
                break;
            case DropFood:
                action = stateDropFood(thisAnt, thisLocation, visibleLocations, possibleActions);
                break;
            case MakeStartSquare:
                action = stateMakeStartSquare(thisAnt, thisLocation, visibleLocations, possibleActions);
                break;
            default:
                //System.out.println("state not found");
                action = EAction.Pass;
        }
        return action;
    }

    @Override
    public void onLayEgg(IAntInfo thisAnt, List<EAntType> types, IEgg egg) {
        //Carrier can't lay egg
    }

    @Override
    public void onAttacked(IAntInfo thisAnt, int dir, IAntInfo attacker, int damage) {
        sharedOnAttacked(thisAnt, dir, attacker, damage);
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
        if (thisLocation.getX() == foodDepot[0] && thisLocation.getY() == foodDepot[1]) {
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
            moves = sharedMap.getFirstOneTurnMove(thisAnt, sharedMap.getLocation(foodDepot[0], foodDepot[1]).getLocationInfo());
            action = moves.remove(0);
        }
        else {
            //System.out.println("Continuing move orders");
            action = moves.remove(0);
        }
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
        else if (possibleActions.contains(EAction.PickUpFood) && 
                 !(thisLocation.getX() == foodDepot[0] && 
                   thisLocation.getY() == foodDepot[1])) {
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
            List<ILocationInfo> foodLocations = sharedMap.getLocationsWithFood();
            ILocationInfo foodDrop = sharedMap.getLocation(foodDepot[0], foodDepot[1]).getLocationInfo();
            foodLocations.remove(foodDrop);
            if (foodLocations.isEmpty()) {
                //System.out.println("No locations with food found");
                spinPlease++;
                action = EAction.Pass;
            }
            else {
                //System.out.println("found food. Moving to food");
                moves = sharedMap.getFirstOneTurnMove(thisAnt, foodLocations);
                action = moves.remove(0);
                if (action == EAction.MoveForward || action == EAction.MoveBackward) spinPlease = 4;
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
        int x = 0;
        int y = 0;
        if (startPos[0] == 0) x = 1;
        if (startPos[0] == worldMax[0]) x = worldMax[0]-1;
        if (startPos[1] == 0) y = 1;
        if (startPos[1] == worldMax[1]) y = worldMax[1]-1;
        ILocationInfo tile4 = sharedMap.getLocation(x, y).getLocationInfo();
        
        if (tile4 == null ||
            (tile4.isFilled() && 
            !tile4.isRock())) {
            int direction = 0;
            if (foodDepot[1]+1 == y) direction = 0;
            if (foodDepot[0]+1 == x) direction = 1;
            if (foodDepot[1]-1 == y) direction = 2;
            if (foodDepot[0]-1 == x) direction = 3;
            
            if (thisLocation.getX() == foodDepot[0] &&
                thisLocation.getY() == foodDepot[1] &&
                thisAnt.getDirection() == direction) {
                if (possibleActions.contains(EAction.DigOut)) {
                    action = EAction.DigOut;
                    state = CarrierState.FindFood;
                }
                else action = EAction.Pass;
                
            }
            else {
                if (moves.isEmpty()) {
                    if (visibleLocations.get(0) != null &&
                        visibleLocations.get(0).getAnt() != null) {
                        sharedMap.addTemporaryInvalidLocation(visibleLocations.get(0));
                    }
                    moves = sharedMap.getFirstOneTurnMove(thisAnt, sharedMap.getLocation(foodDepot[0], foodDepot[1]).getLocationInfo(), direction);
                    sharedMap.clearTemporaryInvalidLocations();
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
        return action;
    }
    
    public enum CarrierState {
        FindFood, 
        DropFood,
        MakeStartSquare
    }
}
