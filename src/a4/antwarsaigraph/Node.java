package a4.antwarsaigraph;

import aiantwars.EAction;
import aiantwars.EAntType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author ichti (Simon T)
 */
public class Node implements Comparable<Node>, Iterable<Edge> {

    private final int x;
    private final int y;
    private final int dir;
    private final List<Edge> edges;
    private Node prev;
    private int cost;
    private int heuristic;
    private boolean visited;

    public Node(int x, int y, int dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        edges = new ArrayList<>();
        prev = null;
        cost = Integer.MAX_VALUE;
        heuristic = Integer.MAX_VALUE;
        visited = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDir() {
        return dir;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getHeuristic() {
        return heuristic;
    }

    public void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public int getFVal() {
        return cost + heuristic;
    }

    public void reset() {
        prev = null;
        cost = Integer.MAX_VALUE;
        heuristic = Integer.MAX_VALUE;
        visited = false;
    }

    @Override
    public int compareTo(Node o) {
        if (this.getFVal() < o.getFVal()) {
            return -1;
        }
        if (this.getFVal() > o.getFVal()) {
            return 1;
        }
        if (this.getHeuristic() < o.getHeuristic()) {
            return -1;
        }
        if (this.getHeuristic() > o.getHeuristic()) {
            return 1;
        }
        return 0;
    }

    @Override
    public Iterator<Edge> iterator() {
        return edges.iterator();
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }


}
