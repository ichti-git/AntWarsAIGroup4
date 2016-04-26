package a4.antwarsaigraph;

import aiantwars.EAction;
import java.util.List;

/**
 *
 * @author ichti (Simon T)
 */
public class OneTurnMove {
    private final List<EAction> actionPath;
    private final Node start;
    private final Node end;
    
    public OneTurnMove(Node start, Node end, List<EAction> path) {
        this.start = start;
        this.end = end;
        this.actionPath = path;
    }
}
