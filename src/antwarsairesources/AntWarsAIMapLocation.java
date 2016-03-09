package antwarsairesources;

import aiantwars.ILocationInfo;

/**
 *
 * @author ichti (Simon T)
 */
public class AntWarsAIMapLocation {
    private final int x;
    private final int y;
    private ILocationInfo locationInfo;
    private int turnSeen;
    
    AntWarsAIMapLocation(int x, int y) {
        this.x = x;
        this.y = y;
        this.locationInfo = null;
        this.turnSeen = -1;
    }

    public ILocationInfo getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(ILocationInfo locationInfo) {
        this.locationInfo = locationInfo;
    }

    public int getTurnSeen() {
        return turnSeen;
    }

    public void setTurnSeen(int turnSeen) {
        this.turnSeen = turnSeen;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    
    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AntWarsAIMapLocation other = (AntWarsAIMapLocation) obj;
        if (this.x != other.x) {
            return false;
        }
        if (this.y != other.y) {
            return false;
        }
        return true;
    }
    
    
    
}
