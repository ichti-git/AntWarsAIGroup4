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
import static antwarsai.SharedAI.sharedMap;
import java.util.List;
import java.util.Random;

/**
 *
 * @author yoyo
 */
public class ScoutAI3 extends SharedAI implements IAntAI {

    Random rand = new Random();
    private int[] nextExplorePoint = null;

    @Override
    public void onHatch(IAntInfo thisAnt, ILocationInfo thisLocation, int worldSizeX, int worldSizeY) {
    }

    @Override
    public void onStartTurn(IAntInfo thisAnt, int turn) {
        sharedOnStartTurn(thisAnt, turn);
    }

    public EAction returnRandomAction(List<EAction> possibleActions) {
        
        possibleActions.remove(EAction.Attack); //Never do a random attack. It might be a friendly ant.
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

        EAction action = null;
        ILocationInfo loc = null;

        for (int i = 0; i < visibleLocations.size(); i++) {
            if ((visibleLocations.get(i).getAnt() != null)) {
                if ((visibleLocations.get(i).getAnt().getTeamInfo().getTeamID() != thisAnt.getTeamInfo().getTeamID())) {
                    if ((visibleLocations.get(i).getAnt().getAntType().equals(EAntType.QUEEN))) {
                        action = EAction.Pass;
                        loc = visibleLocations.get(i);
                        addLocBadQueen(loc);//thanks Simon
                    }
                }

            }
        }

        //Tries to move to an exploration point (currently just not seen locations).
        if (nextExplorePoint == null || sharedMap.getLocation(nextExplorePoint[0], nextExplorePoint[1]).getLocationInfo() != null) {

            while (!explorationCoordinates.isEmpty() && sharedMap.getLocation(explorationCoordinates.get(0)[0], explorationCoordinates.get(0)[1]).getLocationInfo() != null) {
                explorationCoordinates.remove(0);
            }
            if (!explorationCoordinates.isEmpty()) {
                nextExplorePoint = explorationCoordinates.remove(0);
                
                System.out.println("Moving to: " + nextExplorePoint[0] + "," + nextExplorePoint[1]);
            }
            else {
                //System.out.println("Turn: " + currentTurn + ". No more explore points");
                //add new explorationpoints?
            }
        }
        else {
            if (moves.isEmpty()) {
                moves = sharedMap.getFirstOneTurnMove(thisAnt, nextExplorePoint[0], nextExplorePoint[1], true);
            }
            if (moves.get(0) == EAction.MoveForward && !visibleLocations.isEmpty() && visibleLocations.get(0).getAnt() != null) {
                sharedMap.addTemporaryInvalidLocation(visibleLocations.get(0));
                moves = sharedMap.getFirstOneTurnMove(thisAnt, nextExplorePoint[0], nextExplorePoint[1], true);
                sharedMap.clearTemporaryInvalidLocations();
            }
            if (moves.get(0) == EAction.MoveBackward && !possibleActions.contains(EAction.MoveBackward)) {
                System.out.println("Need to turn");
                moves.clear();
                moves.add(EAction.TurnLeft);
                moves.add(EAction.TurnLeft);
            }
            action = moves.remove(0);
        }
        
        if (action == null) {
            action = choseHowToMoveAnt(possibleActions);
        }

        return action;
    }

    @Override
    public void onLayEgg(IAntInfo thisAnt, List<EAntType> types, IEgg egg) {

    }

    @Override
    public void onAttacked(IAntInfo thisAnt, int dir, IAntInfo attacker, int damage) {

    }

    @Override
    public void onDeath(IAntInfo thisAnt) {
        sharedOnDeath(thisAnt);
    }

}
