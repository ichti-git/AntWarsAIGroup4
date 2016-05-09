package a4.antwarsaigraph;

import aiantwars.EAction;
import java.util.List;

/**
 *
 * @author ichti (Simon T)
 */
public class AntEdge {
    private final AntNode start;
    private final AntNode end;
    private final int cost;
    private final List<EAction> path;

    public AntEdge(AntNode start, AntNode end, int cost, List<EAction> path) {
        this.start = start;
        this.end = end;
        this.cost = cost;
        this.path = path;
    }

    public AntNode getStart() {
        return start;
    }

    public AntNode getEnd() {
        return end;
    }

    public int getCost() {
        return cost;
    }

    public List<EAction> getPath() {
        return path;
    }
    
    
    
    
}
