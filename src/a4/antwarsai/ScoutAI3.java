/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package a4.antwarsai;

import aiantwars.EAction;
import aiantwars.EAntType;
import aiantwars.IAntAI;
import aiantwars.IAntInfo;
import aiantwars.IEgg;
import aiantwars.ILocationInfo;
import java.util.List;
import java.util.Random;

/**
 *
 * @author yoyo
 */
public class ScoutAI3 extends SharedAI implements IAntAI {

    Random rand = new Random();
    private int[] nextExplorePoint = null;
    
    public ScoutAI3() {
        
    }

    public ScoutAI3(SharedInfo sharedInfo) {
        super(sharedInfo);
    }

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
                        sharedInfo.addLocBadQueen(loc);//thanks Simon
                    }
                }

            }
        }

        //Tries to move to an exploration point (currently just not seen locations).
        if (nextExplorePoint == null || sharedInfo.getSharedMap().getLocation(nextExplorePoint[0], nextExplorePoint[1]).getLocationInfo() != null) {

            while (!sharedInfo.getExplorationCoordinates().isEmpty() && sharedInfo.getSharedMap().getLocation(sharedInfo.getExplorationCoordinates().get(0)[0], sharedInfo.getExplorationCoordinates().get(0)[1]).getLocationInfo() != null) {
                sharedInfo.getExplorationCoordinates().remove(0);
            }
            if (!sharedInfo.getExplorationCoordinates().isEmpty()) {
                nextExplorePoint = sharedInfo.getExplorationCoordinates().remove(0);
                
                System.out.println("Moving to: " + nextExplorePoint[0] + "," + nextExplorePoint[1]);
            }
            else {
                //System.out.println("Turn: " + currentTurn + ". No more explore points");
                //add new explorationpoints?
            }
        }
        else {
            if (moves.isEmpty()) {
                moves = sharedInfo.getSharedMap().getFirstOneTurnMove(thisAnt, nextExplorePoint[0], nextExplorePoint[1], true);
            }
            if (moves.get(0) == EAction.MoveForward && !visibleLocations.isEmpty() && visibleLocations.get(0).getAnt() != null) {
                sharedInfo.getSharedMap().addTemporaryInvalidLocation(visibleLocations.get(0));
                moves = sharedInfo.getSharedMap().getFirstOneTurnMove(thisAnt, nextExplorePoint[0], nextExplorePoint[1], true);
                sharedInfo.getSharedMap().clearTemporaryInvalidLocations();
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
