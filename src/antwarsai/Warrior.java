/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package antwarsai;

import aiantwars.EAction;
import aiantwars.EAntType;
import aiantwars.IAntAI;
import aiantwars.IAntInfo;
import aiantwars.IEgg;
import aiantwars.ILocationInfo;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author yoyo
 */
public class Warrior extends SharedAI implements IAntAI {

    Random rand = new Random();

    @Override
    public void onHatch(IAntInfo thisAnt, ILocationInfo thisLocation, int worldSizeX, int worldSizeY) {

    }

    @Override
    public void onStartTurn(IAntInfo thisAnt, int turn) {

    }

    public EAction returnRandomAction(List<EAction> possibleActions) {

        int size = possibleActions.size();
        int number = rand.nextInt(size);
        EAction ac = possibleActions.get(number);
        return ac;

    }

    public EAction choseHowToMoveAnt(List<EAction> possibleActions) {
        EAction action = null;
        if (possibleActions.contains((EAction.MoveForward))) {
            action = EAction.MoveForward;
        }
        else {
            action = returnRandomAction(possibleActions);
        }
        return action;
    }

    @Override
    public EAction chooseAction(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions) {
        sharedChooseAction(thisAnt, thisLocation, visibleLocations, possibleActions);

        EAction action;
        for (Map.Entry<Integer, ILocationInfo> entry : locQueen.entrySet()) {
            //System.out.println(entry.getValue().getAnt());
            
            if (sharedMap.getLocation(entry.getValue()).getLocationInfo().getAnt() == null) {
                locQueen.remove(entry.getKey());
            }
        }
        //System.out.println(locQueen);
        if (locQueen.isEmpty()) {
            //System.out.println("I am in if locQueen");
            action = choseHowToMoveAnt(possibleActions);
        }
        
        else if ((visibleLocations.get(0).getAnt() != null)) {
            //System.out.println("locQueen not empty");
            //kill queen

            if ((visibleLocations.get(0).getAnt().getTeamInfo().getTeamID() != thisAnt.getTeamInfo().getTeamID())) {
                if ((visibleLocations.get(0).getAnt().getAntType().equals(EAntType.QUEEN))) {
                    //opponent queen
                    //System.out.println("sees queen");
                    if (possibleActions.contains((EAction.Attack))) {
                        action = EAction.Attack;
                    }
                    else {
                        
                        action = EAction.Pass;
                    }
                }
                else {
                    //Opponent ant not a queen
                    action = choseHowToMoveAnt(possibleActions);
                }
            }
            else {
                //ally ant
                //System.out.println("sees ally");
                sharedMap.addTemporaryInvalidLocation(visibleLocations.get(0));
                Map.Entry<Integer, ILocationInfo> entry = locQueen.entrySet().iterator().next();
                ILocationInfo firstvalue = entry.getValue();
                moves = sharedMap.getFirstOneTurnMove(thisAnt, firstvalue);
                action = moves.remove(0);
                if (action == EAction.Pass) {
                    action = EAction.TurnLeft;
                }
                sharedMap.clearTemporaryInvalidLocations();
            }
        }
        else {
            
            //sharedMap.addTemporaryInvalidLocation(visibleLocations.get(0));
            //System.out.println("sees nothing");
            Map.Entry<Integer, ILocationInfo> entry = locQueen.entrySet().iterator().next();
            ILocationInfo firstvalue = entry.getValue();
            moves = sharedMap.getFirstOneTurnMove(thisAnt, firstvalue);
            action = moves.remove(0);
        }
        return action;

    }
//We didnt remove the ant we killed
    @Override
    public void onLayEgg(IAntInfo thisAnt, List<EAntType> types, IEgg egg) {

    }

    @Override
    public void onAttacked(IAntInfo thisAnt, int dir, IAntInfo attacker, int damage) {

    }

    @Override
    public void onDeath(IAntInfo thisAnt) {
        
    }

}
