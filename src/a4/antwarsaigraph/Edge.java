package antwarsaigraph;

/**
 *
 * @author ichti (Simon T)
 */
public class Edge {
    private final Node start;
    private final Node end;
    private final int cost;

    public Edge(Node start, Node end, int cost) {
        this.start = start;
        this.end = end;
        this.cost = cost;
    }

    public Node getStart() {
        return start;
    }

    public Node getEnd() {
        return end;
    }

    public int getCost() {
        return cost;
    }
    
    
}
