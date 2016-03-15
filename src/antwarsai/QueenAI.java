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
    private QueenState state = QueenState.FindFood;
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
        sharedOnHatch(thisAnt, thisLocation, worldSizeX, worldSizeY);
        startPos = new int[] {thisLocation.getX(), thisLocation.getY()};
        worldMax = new int[] {worldSizeX-1, worldSizeY-1};
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
            case LayCarrier:
                action = stateLayCarrier(thisAnt, thisLocation, visibleLocations, possibleActions);
                break;
            case FortressMode:
                action = stateFortressMode(thisAnt, thisLocation, visibleLocations, possibleActions);
                break;
            default:
                System.out.println("state not found");
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
            if (nextEgg == EAntType.CARRIER) egg.set(nextEgg, new CarrierAI());
            //if (nextEgg == EAntType.SCOUT) egg.set(nextEgg, new ScoutAI());
            if (nextEgg == EAntType.WARRIOR) egg.set(nextEgg, new PassAI());
            //if (nextEgg == EAntType.WARRIOR) egg.set(nextEgg, new WarriorAI());
        }
        else {
            System.out.println("Couldn't fine egg type?!");
        }
        
    }

    @Override
    public void onAttacked(IAntInfo thisAnt, int dir, IAntInfo attacker, int damage) {
        sharedOnAttacked(thisAnt, dir, attacker, damage);
    }

    @Override
    public void onDeath(IAntInfo thisAnt) {
        sharedOnDeath(thisAnt);
    }
    
    private EAction stateLayCarrier(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions) {
        int[] layEggLocation;
        int startX = startPos[0];
        int startY = startPos[1];
        if (sharedMap.getLocation(startX, startY+1) != null &&
            sharedMap.getLocation(startX, startY+1).getLocationInfo() != null &&
            !sharedMap.getLocation(startX, startY+1).getLocationInfo().isFilled() &&
            sharedMap.getLocation(startX, startY+2).getLocationInfo() != null &&
            !sharedMap.getLocation(startX, startY+2).getLocationInfo().isFilled()) {
            layEggLocation = new int[] {startX, startY, 0};
        }
        else if (sharedMap.getLocation(startX, startY-1) != null &&
                 sharedMap.getLocation(startX, startY-1).getLocationInfo() != null &&
                 !sharedMap.getLocation(startX, startY-1).getLocationInfo().isFilled() &&
                 sharedMap.getLocation(startX, startY-2).getLocationInfo() != null &&
                 !sharedMap.getLocation(startX, startY-2).getLocationInfo().isFilled()) {
            layEggLocation = new int[] {startX, startY, 2};
        }
        else if (sharedMap.getLocation(startX-1, startY) != null &&
                 sharedMap.getLocation(startX-1, startY).getLocationInfo() != null &&
                 !sharedMap.getLocation(startX-1, startY).getLocationInfo().isFilled() &&
                 sharedMap.getLocation(startX-2, startY).getLocationInfo() != null &&
                 !sharedMap.getLocation(startX-2, startY).getLocationInfo().isFilled()) {
            layEggLocation = new int[] {startX, startY, 3};
        }
        else if (sharedMap.getLocation(startX+1, startY) != null &&
                 sharedMap.getLocation(startX+1, startY).getLocationInfo() != null &&
                 !sharedMap.getLocation(startX+1, startY).getLocationInfo().isFilled()) {
            layEggLocation = new int[] {startX, startY, 1};
        }
        else {
            layEggLocation = new int[] {startX, startY, 3};
        }
        
        EAction action;
        if (layEggLocation[2] == thisAnt.getDirection() &&
            layEggLocation[0] == thisAnt.getLocation().getX() &&
            layEggLocation[1] == thisAnt.getLocation().getY()) {
            if (possibleActions.contains(EAction.LayEgg)) {
                int foodX = thisLocation.getX();
                int foodY = thisLocation.getY();
                if (layEggLocation[2] == 0) foodY++;
                if (layEggLocation[2] == 1) foodX++;
                if (layEggLocation[2] == 2) foodY--;
                if (layEggLocation[2] == 3) foodX--;
                foodDepot = new int[] {foodX, foodY};
                System.out.println("foodDepot: {"+foodX+","+foodY+"}");
                nextEgg = EAntType.CARRIER;
                action = EAction.LayEgg;
                state = QueenState.FortressMode;
            }
            else {
                
                System.out.println("Waiting to lay an egg");
                action = EAction.Pass;
            }
        } 
        else if (!moves.isEmpty()) {
            if (possibleActions.contains(moves.get(0))) {
                action = moves.remove(0);
            }
            else {
                System.out.println("found impossible action" + moves.get(0));
                action = EAction.Pass;
            }
        }
        else {
            moves = sharedMap.getFirstOneTurnMove(thisAnt, sharedMap.getLocation(layEggLocation[0], layEggLocation[1]).getLocationInfo(), layEggLocation[2]);
            action = moves.remove(0);
        }
        return action;
    }

    private EAction stateFortressMode(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions) {
        EAction action;
        
        if (foodDepot == null) {
            System.out.println("test");
            return EAction.Pass;
        }
        if (thisAnt.getHitPoints() < 10 && possibleActions.contains(EAction.EatFood)) {
                action = EAction.EatFood;
        }
        else if (thisLocation.getX() == foodDepot[0] && thisLocation.getY() == foodDepot[1]) {
            if (thisLocation.getFoodCount() > 0) {
                action = EAction.PickUpFood;
            }
            else {
                moves = sharedMap.getFirstOneTurnMove(thisAnt, sharedMap.getLocation(startPos[0], startPos[1]).getLocationInfo());
                action = moves.remove(0);
            }
        }
        else if (thisLocation.getX() == startPos[0] && thisLocation.getY() == startPos[1]) {
            if (thisAnt.getFoodLoad() > thisAnt.getAntType().getLayEggCost()) {
                if (nextEgg == EAntType.CARRIER) nextEgg = EAntType.SCOUT;
                else if (nextEgg == EAntType.SCOUT) nextEgg = EAntType.WARRIOR;
                //nextEgg = EAntType.WARRIOR;
                action = EAction.LayEgg;
            }
            else if (sharedMap.getLocation(foodDepot[0], foodDepot[1]).getLocationInfo().getFoodCount() > 5) {
                moves = sharedMap.getFirstOneTurnMove(thisAnt, sharedMap.getLocation(foodDepot[0], foodDepot[1]).getLocationInfo());
                action = moves.remove(0);
                //action = EAction.Pass;
            }
            else {
                action = EAction.Pass;
            }
        }
        else {
            if (moves.isEmpty()) {
                moves = sharedMap.getFirstOneTurnMove(thisAnt, sharedMap.getLocation(startPos[0], startPos[1]).getLocationInfo());
            }
            action = moves.remove(0);
        }
        
        return action;
    }
    
    public enum QueenState {
        FindFood, 
        LayCarrier, 
        LayScout, 
        LayDefender, 
        LayWarrior,
        FortressMode
        
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
                    System.out.println("found impossible action" + moves.get(0));
                    action = EAction.Pass;
                }

            }
            else if (possibleActions.contains(EAction.PickUpFood)) {
                action = EAction.PickUpFood;
                
                if (thisAnt.getFoodLoad()+1 >= thisAnt.getAntType().getActionCost(EAction.LayEgg)) {
                    if (allyAnts.size() == 1) {
                        state = QueenState.LayCarrier;
                    }
                    if (allyAnts.size() == 2) {
                        state = QueenState.LayScout;
                    }
                    if (allyAnts.size() == 3) {
                        state = QueenState.LayDefender;
                    }
                    if (allyAnts.size() > 3) {
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
                List<ILocationInfo> foodLocations = sharedMap.getLocationsWithFood();
                if (foodLocations.isEmpty()) {
                    System.out.println("No locations with food found");
                    spinPlease++;
                    action = EAction.Pass;
                }
                else {
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
}
