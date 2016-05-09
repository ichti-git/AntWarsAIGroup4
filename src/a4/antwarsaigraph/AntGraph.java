package a4.antwarsaigraph;

import a4.antwarsairesources.AntWarsAIMap;
import aiantwars.EAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author ichti (Simon T)
 */
public class AntGraph implements Iterable<AntNode> {
    private List<AntNode> nodes;
    private List<AntEdge> edges;
    
    public AntGraph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }
    
    public AntNode createNode(int x, int y, int dir, AntWarsAIMap map, boolean useBlanks) {
        AntNode node = new AntNode(x, y, dir, this, map, useBlanks);
        nodes.add(node);
        return node;
    }
    
    public void addNode(AntNode node) {
        nodes.add(node);
    }
    
    public AntEdge createEdge(AntNode begin, AntNode end, int cost, List<EAction> path) {
       AntEdge edge = new AntEdge(begin, end, cost, path);
       edges.add(edge);
       begin.addEdge(edge);
       return edge;
    }
    
    public AntNode getNode(int x, int y, int dir) {
        for (AntNode node : nodes) {
            if (node.getX() == x && node.getY() == y && node.getDir() == dir) return node;
        }
        return null;
    }
    
    public void reset() {
        for (AntNode node : nodes) {
            node.reset();
        }
    }
    
    @Override
    public Iterator<AntNode> iterator() {
        return nodes.iterator();
    }

    public List<AntEdge> getEdges() {
        return edges;
    }

    public void addEdge(AntEdge edge) {
        edges.add(edge);
    }

    public AntEdge getEdge(AntNode start, AntNode end) {
        for (AntEdge edge : edges) {
            if (edge.getStart().equals(start) && edge.getEnd().equals(end)) return edge;
        }
        return null;
    }

    
}
