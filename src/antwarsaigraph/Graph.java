package antwarsaigraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author ichti (Simon T)
 */
public class Graph implements Iterable<Node> {
    private List<Node> nodes;
    private List<Edge> edges;
    
    public Graph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }
    
    public Node createNode(int x, int y, int dir) {
        Node node = new Node(x, y, dir);
        nodes.add(node);
        return node;
    }
    
    public Edge createEdge(Node begin, Node end, int cost) {
       Edge edge = new Edge(begin, end, cost);
       edges.add(edge);
       begin.addEdge(edge);
       return edge;
    }
    
    public Node getNode(int x, int y, int dir) {
        for (Node node : this) {
            if (node.getX() == x && node.getY() == y && node.getDir() == dir) return node;
        }
        return null;
    }
    
    public void reset() {
        for (Node node : this) {
            node.reset();
        }
    }
    
    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    public List<Edge> getEdges() {
        return edges;
    }

    
}
