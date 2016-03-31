package antwarsai;

import aiantwars.EAction;
import aiantwars.IAntInfo;
import aiantwars.ILocationInfo;
import antwarsairesources.AntWarsAIMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ichti (Simon T)
 */
public class SharedAI {
    protected static AntWarsAIMap sharedMap;
    protected int currentTurn;
    protected List<EAction> moves = new ArrayList<>();
    protected static AllyTeamInfo allyTeamInfo = new AllyTeamInfo();
    //protected static Map<Integer, IAntInfo> allyAnts = new HashMap<>();
    protected static int[] startPos;
    protected static int[] foodDepot;
    protected static int[] worldMax;
    protected static HashMap<Integer, ILocationInfo> locQueen = new HashMap<>();
    
    public void addLocBadQueen(ILocationInfo info)
    {
        locQueen.put(info.getAnt().getTeamInfo().getTeamID(), info);
        
    }
    
    protected void sharedOnHatch(IAntInfo thisAnt, ILocationInfo thisLocation, int worldSizeX, int worldSizeY) {
        //allyAnts.put(thisAnt.antID(), thisAnt);
        allyTeamInfo.addAnt(thisAnt);
        allyTeamInfo.removeEgg(thisAnt.getAntType());
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
        allyTeamInfo.updateAnt(thisAnt);
    }
    
    protected void sharedOnAttacked(IAntInfo thisAnt, int dir, IAntInfo attacker, int damage) {
        
    }
    
    public void sharedOnDeath(IAntInfo thisAnt) {
        allyTeamInfo.removeAnt(thisAnt);
    }
}
