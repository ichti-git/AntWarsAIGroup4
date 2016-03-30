package antwarsai;

import aiantwars.EAction;
import aiantwars.IAntInfo;
import aiantwars.ILocationInfo;
import antwarsairesources.AntWarsAIMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ichti (Simon T)
 */
public class SharedAI {
    protected static AntWarsAIMap sharedMap;
    protected int currentTurn;
    protected List<EAction> moves = new ArrayList<>();
    //protected static TeamAntsCollection allyAnts;
    protected static Map<Integer, IAntInfo> allyAnts = new HashMap<>();
    protected static int[] startPos;
    protected static int[] foodDepot;
    protected static int[] worldMax;
    protected static HashMap<Integer, ILocationInfo> locQueen = new HashMap<>();
    
    public void addLocBadQueen(ILocationInfo info)
    {
        locQueen.put(info.getAnt().getTeamInfo().getTeamID(), info);
        
    }
    
    protected void sharedOnHatch(IAntInfo thisAnt, ILocationInfo thisLocation, int worldSizeX, int worldSizeY) {
        allyAnts.put(thisAnt.antID(), thisAnt);
    }
    
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
        allyAnts.remove(thisAnt.antID());
    }
}
