package antwarsai;

import aiantwars.EAction;
import aiantwars.IAntInfo;
import aiantwars.ILocationInfo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ichti (Simon T)
 */
public class SharedAI {
    protected int currentTurn;
    protected List<EAction> moves = new ArrayList<>();
    
    protected SharedInfo sharedInfo;
    
    public void setSharedInfo(SharedInfo shared) {
        sharedInfo = shared;
    }
    
    protected void sharedOnHatch(IAntInfo thisAnt, ILocationInfo thisLocation, int worldSizeX, int worldSizeY) {
        //allyAnts.put(thisAnt.antID(), thisAnt);
        sharedInfo.getAllyTeamInfo().addAnt(thisAnt);
        sharedInfo.getAllyTeamInfo().removeEgg(thisAnt.getAntType());
    }
    
    protected void sharedOnStartTurn(IAntInfo thisAnt, int turn) {
        currentTurn = turn; 
        moves.clear();
    }
    
    protected void sharedChooseAction(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions) {
        
        sharedInfo.getSharedMap().setLocationInfo(thisLocation, currentTurn);
        for (ILocationInfo locInfo : visibleLocations) {
            sharedInfo.getSharedMap().setLocationInfo(locInfo, currentTurn);
        }
        sharedInfo.getAllyTeamInfo().updateAnt(thisAnt);
    }
    
    protected void sharedOnAttacked(IAntInfo thisAnt, int dir, IAntInfo attacker, int damage) {
        
    }
    
    public void sharedOnDeath(IAntInfo thisAnt) {
        sharedInfo.getAllyTeamInfo().removeAnt(thisAnt);
    }
}
