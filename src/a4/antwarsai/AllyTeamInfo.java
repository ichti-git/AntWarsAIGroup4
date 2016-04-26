package a4.antwarsai;

import aiantwars.EAntType;
import aiantwars.IAntInfo;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ichti (Simon T)
 */
public class AllyTeamInfo {
    private final Map<Integer, IAntInfo> carriers;
    private final Map<Integer, IAntInfo> scouts;
    private final Map<Integer, IAntInfo> warriors;
    private IAntInfo queen;
    
    private int carrierEggCount;
    private int scoutEggCount;
    private int warriorEggCount;

    public AllyTeamInfo() {
        this.carriers = new HashMap<>();
        this.scouts = new HashMap<>();
        this.warriors = new HashMap<>();
        this.queen = null;
        this.carrierEggCount = 0;
        this.scoutEggCount = 0;
        this.warriorEggCount = 0;
    }
    
    /**
     * adds an ant to the fitting map
     * @param ant 
     */
    public void addAnt(IAntInfo ant) {
        EAntType antType = ant.getAntType();
        switch (antType) {
            case QUEEN:
                queen = ant;
                break;
            case CARRIER:
                carriers.put(ant.antID(), ant);
                break;
            case SCOUT:
                scouts.put(ant.antID(), ant);
                break;
            case WARRIOR:
                warriors.put(ant.antID(), ant);
                break;
        }
    }
    
    /**
     * Same as addAnt()
     * @param thisAnt 
     */
    public void updateAnt(IAntInfo thisAnt) {
        addAnt(thisAnt);
    }
    
    
    /**
     * Removes an ant from the map, and returns it. Returns null if not found.
     * @param ant
     * @return 
     */
    public IAntInfo removeAnt(IAntInfo ant) {
        IAntInfo retAnt = null;
        EAntType antType = ant.getAntType();
        switch (antType) {
            case QUEEN:
                retAnt = queen;
                queen = null;
                break;
            case CARRIER:
                retAnt = carriers.remove(ant.antID());
                break;
            case SCOUT:
                retAnt = scouts.remove(ant.antID());
                break;
            case WARRIOR:
                retAnt = warriors.remove(ant.antID());
                break;
        }
        return retAnt;
    }
    
    public void addEgg(EAntType type) {
        switch (type) {
            case CARRIER:
                carrierEggCount++;
                break;
            case SCOUT:
                scoutEggCount++;
                break;
            case WARRIOR:
                warriorEggCount++;
                break;
        }
    }
    
    public void removeEgg(EAntType type) {
        switch (type) {
            case CARRIER:
                carrierEggCount--;
                break;
            case SCOUT:
                scoutEggCount--;
                break;
            case WARRIOR:
                warriorEggCount--;
                break;
        }
    }
    
    /**
     * returns the amount of ant of a given EAntType
     * @param antType
     * @return 
     */
    public int antCount(EAntType antType) {
        return antCount(antType, true);
    }
    public int antCount(EAntType antType, boolean countEggs) {
        int count;
        switch (antType) {
            case QUEEN:
                if (queen != null) count = 1;
                else count = 0;
                break;
            case CARRIER:
                count = carriers.size();
                if (countEggs) count += carrierEggCount;
                break;
            case SCOUT:
                count = scouts.size();
                if (countEggs) count += scoutEggCount;
                break;
            case WARRIOR:
                count = warriors.size();
                if (countEggs) count += warriorEggCount;
                break;
            default:
                count = 0;
        }
        return count;
    }
    
    /**
     * Return the total amount of all types of ants.
     * @return 
     */
    public int antCount() {
        return antCount(true);
    }
    public int antCount(boolean countEggs) {
        int q;
        if (queen != null) q = 1;
        else q = 0;
        int count = carriers.size() + scouts.size() + warriors.size() + q;
        if (countEggs) count += carrierEggCount + warriorEggCount + scoutEggCount;
        return count;
    }
    
    /**
     * Returns the list of ants of type CARRIER
     * @return 
     */
    public Map<Integer, IAntInfo> getCarriers() {
        return carriers;
    }
    
    /**
     * Returns the list of ants of type SCOUT
     * @return 
     */
    public Map<Integer, IAntInfo> getScouts() {
        return scouts;
    }

    /**
     * Returns the list of ants of type WARRIOR
     * @return 
     */
    public Map<Integer, IAntInfo> getWarriors() {
        return warriors;
    }

    /**
     * Returns the ant of type QUEEN
     * @return 
     */
    public IAntInfo getQueen() {
        return queen;
    }

    
    
    
}
