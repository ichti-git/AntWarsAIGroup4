package a4.antwarsai;

import a4.antwarsai.AllyTeamInfo;
import aiantwars.ILocationInfo;
import a4.antwarsairesources.AntWarsAIMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ichti (Simon T)
 */
public class SharedInfo {
    private AntWarsAIMap sharedMap;
    private final AllyTeamInfo allyTeamInfo = new AllyTeamInfo();
    //protected static Map<Integer, IAntInfo> allyAnts = new HashMap<>();
    private int[] startPos;
    private int[] foodDepot;
    private int[] worldMax;
    private final HashMap<Integer, ILocationInfo> locQueen = new HashMap<>();
    private List<int[]> explorationCoordinates = new ArrayList<>(); 
    
    public void addLocBadQueen(ILocationInfo info)
    {
        locQueen.put(info.getAnt().getTeamInfo().getTeamID(), info);
    }

    public AntWarsAIMap getSharedMap() {
        return sharedMap;
    }

    public AllyTeamInfo getAllyTeamInfo() {
        return allyTeamInfo;
    }

    public int[] getStartPos() {
        return startPos;
    }

    public int[] getFoodDepot() {
        return foodDepot;
    }

    public int[] getWorldMax() {
        return worldMax;
    }

    public HashMap<Integer, ILocationInfo> getLocQueen() {
        return locQueen;
    }

    public List<int[]> getExplorationCoordinates() {
        return explorationCoordinates;
    }

    public void setSharedMap(AntWarsAIMap antWarsAIMap) {
        sharedMap = antWarsAIMap;
    }

    public void setStartPos(int[] startPos) {
        this.startPos = startPos;
    }

    public void setFoodDepot(int[] foodDepot) {
        this.foodDepot = foodDepot;
    }

    public void setWorldMax(int[] worldMax) {
        this.worldMax = worldMax;
    }

    public void setExplorationCoordinates(List<int[]> explorationCoordinates) {
        this.explorationCoordinates = explorationCoordinates;
    }
    
    public void setExplorationCoordinates(int[] coords) {
        this.explorationCoordinates.add(coords);
    }
}
