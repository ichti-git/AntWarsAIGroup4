package antwarsai;

import aiantwars.EAction;
import aiantwars.EAntType;
import aiantwars.IAntAI;
import aiantwars.IAntInfo;
import aiantwars.IEgg;
import aiantwars.ILocationInfo;
import antwarsairesources.AntWarsAIMap;
import java.util.List;
import java.util.Random;

/**
 *
 * @author ichti (Simon T)
 */
public class QueenAI extends SharedAI implements IAntAI {
    private final Random rnd = new Random();
    
    /* 
     * Queen states:
     *  - Start of game
     *  - After spawning first ant (carrier ant)
     *  - After spawning 2nd ant (scout ant)
     *  - After spawning 3rd ant (defender ant?)
     *  - Others? like being attacked.
     */
    @Override
    public void onHatch(IAntInfo thisAnt, ILocationInfo thisLocation, int worldSizeX, int worldSizeY) {
        sharedMap = new AntWarsAIMap(worldSizeX, worldSizeY);
        //Only the queen should instanciate the sharedMap. 
        //If more than 1 queen is allowed, change so a new queen doesn't make 
        //a new sharedMap
    }

    @Override
    public void onStartTurn(IAntInfo thisAnt, int turn) {
        sharedOnStartTurn(thisAnt, turn);
        //spinCounter = 2;
        //spinPlease--;
    }

    //int spinCounter = 2;
    int spinPlease = 3; //Increment to avoid spin, Decrement to spin
    @Override
    public EAction chooseAction(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions) {
        sharedChooseAction(thisAnt, thisLocation, visibleLocations, possibleActions);
        EAction action;
        if (!moves.isEmpty()) {
            if (possibleActions.contains(moves.get(0))) {
                action = moves.remove(0);
            }
            else {
                System.out.println("found impossible action" + moves.get(0));
                action = EAction.Pass;
            }
                
        }
        else if (possibleActions.contains(EAction.PickUpFood)) {
            action = EAction.PickUpFood;
        }
        else if (thisAnt.getFoodLoad() > 3) {
            action = EAction.EatFood;
        }
        else if (spinPlease > 0 && possibleActions.contains(EAction.TurnLeft)) {
            action = EAction.TurnLeft;
            spinPlease--;
            //spinCounter--;
            //if (spinCounter == 0) spinPlease = 1; //spinPlease = false;
        }
        else if (thisAnt.getActionPoints() == thisAnt.getAntType().getMaxActionPoints()) {
            List<ILocationInfo> foodLocations = sharedMap.getLocationsWithFood();
            if (foodLocations.size() == 0) {
                System.out.println("No locations with food found");
                spinPlease++;
                action = EAction.Pass;
            }
            else {
                moves = sharedMap.getMoves(thisAnt, foodLocations);
                action = moves.remove(0);
                if (action == EAction.MoveForward || action == EAction.MoveBackward) spinPlease = 3;
            }
        }
        else {
            action = EAction.Pass;
        }
        return action;
    }

    @Override
    public void onLayEgg(IAntInfo thisAnt, List<EAntType> types, IEgg egg) {
        EAntType type = types.get(rnd.nextInt(types.size())); 
        System.out.println("ID: " + thisAnt.antID() + " onLayEgg: " + type);
        egg.set(type, this);
    }

    @Override
    public void onAttacked(IAntInfo thisAnt, int dir, IAntInfo attacker, int damage) {
        sharedOnAttacked(thisAnt, dir, attacker, damage);
    }

    @Override
    public void onDeath(IAntInfo thisAnt) {
        sharedOnDeath(thisAnt);
    }
    
}
