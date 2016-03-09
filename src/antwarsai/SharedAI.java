package antwarsai;

import aiantwars.EAction;
import aiantwars.IAntInfo;
import aiantwars.ILocationInfo;
import antwarsairesources.AntWarsAIMap;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ichti (Simon T)
 */
public class SharedAI {
    protected static AntWarsAIMap sharedMap;
    protected int currentTurn;
    protected List<EAction> moves = new ArrayList<>();
    
    
    protected void sharedOnStartTurn(IAntInfo thisAnt, int turn) {
        currentTurn = turn; //change when getTurn is available
        moves.clear();
    }
    private String lts(ILocationInfo loc) {
        return loc.getX()+","+loc.getY();
    }
    protected void sharedChooseAction(IAntInfo thisAnt, ILocationInfo thisLocation, List<ILocationInfo> visibleLocations, List<EAction> possibleActions) {
        String debug = "Seing "+lts(thisLocation);
        sharedMap.setLocationInfo(thisLocation, currentTurn);
        for (ILocationInfo locInfo : visibleLocations) {
            debug += "; "+lts(locInfo);
            sharedMap.setLocationInfo(locInfo, currentTurn);
        }
        //System.out.println(debug);
        
    }
    
    protected void sharedOnAttacked(IAntInfo thisAnt, int dir, IAntInfo attacker, int damage) {
        
    }
    
    public void sharedOnDeath(IAntInfo thisAnt) {
        
    }
}
