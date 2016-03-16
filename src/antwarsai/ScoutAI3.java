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
import antwarsairesources.AntWarsAIMap;
import java.util.List;
import java.util.Random;

/**
 *
 * @author yoyo
 */
public class ScoutAI3 extends SharedAI   implements IAntAI 
{
    Random rand = new Random();

    @Override
    public void onHatch(IAntInfo thisAnt, ILocationInfo thisLocation, int worldSizeX, int worldSizeY) {
          sharedMap = new AntWarsAIMap(worldSizeX, worldSizeY);
    }

    @Override
    public void onStartTurn(IAntInfo thisAnt, int turn) 
    {
     
    }

    
     public EAction returnRandomAction( List<EAction> possibleActions)
    {
        
        int size = possibleActions.size();
        int number = rand.nextInt(size);
        EAction ac = possibleActions.get(number);
        return ac;

    }
     
     public EAction choseHowToMoveAnt(List<EAction> possibleActions)
     {
         EAction action = null;
        if(possibleActions.contains((EAction.MoveForward)))
        {
          action = EAction.MoveForward;
        }else
        {
          action =  returnRandomAction(possibleActions);
        }
        return action; 
     }
    
    
    @Override
    public EAction chooseAction(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions)
    {
        
        sharedChooseAction(thisAnt, thisLocation, visibleLocations, possibleActions);
        
        EAction action = null;
         ILocationInfo loc=null;
         
        for(int i = 0;i<visibleLocations.size();i++)
        {
            if( (visibleLocations.get(i).getAnt() != null) )
            {
                if((visibleLocations.get(i).getAnt().getTeamInfo().getTeamID() != thisAnt.getTeamInfo().getTeamID()))
                {
                    if((visibleLocations.get(i).getAnt().getAntType().equals(EAntType.QUEEN)))
                    {
                      action = EAction.Pass;
                      loc = visibleLocations.get(i);
                      addLocBadQueen(loc);//thanks Simon
                        //JOptionPane.showMessageDialog(null, " The loc of the ant is "+loc.getX()+"  "+loc.getY());
                    }
                }

        }
        }
     
                
        
        
//         EAction action = null;
//        if(possibleActions.contains((EAction.MoveForward)))
//        {
//          action = EAction.MoveForward;
//        }else
//        {
//          action =  returnRandomAction(possibleActions);
//        }
if(action==null)
{
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
        
    }
    

}
