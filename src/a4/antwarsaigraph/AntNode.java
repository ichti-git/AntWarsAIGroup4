package a4.antwarsaigraph;

import a4.antwarsairesources.AntWarsAIMap;
import aiantwars.EAction;
import aiantwars.EAntType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author ichti (Simon T)
 */
public class AntNode implements Comparable<AntNode>, Iterable<AntEdge> {

    private final int x;
    private final int y;
    private final int dir;
    private final List<AntEdge> edges;
    private final AntWarsAIMap map;
    private final boolean useBlanks;
    private final AntGraph graph;
    private AntNode prev;
    private int cost;
    private int heuristic;
    private boolean visited;

    public AntNode(int x, int y, int dir, AntGraph graph, AntWarsAIMap map, boolean useBlanks) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.map = map;
        this.useBlanks = useBlanks;
        this.graph = graph;
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

    public AntNode getPrev() {
        return prev;
    }

    public void setPrev(AntNode prev) {
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

    public AntEdge getEdge(AntNode node) {
        for (AntEdge edge : edges) {
            if (edge.getEnd().equals(node)) {
                return edge;
            }
        }
        return null;
    }

    public void reset() {
        prev = null;
        cost = Integer.MAX_VALUE;
        heuristic = Integer.MAX_VALUE;
        visited = false;
    }

    @Override
    public int compareTo(AntNode o) {
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
    public Iterator<AntEdge> iterator() {
        return edges.iterator();
    }

    public void addEdge(AntEdge edge) {
        edges.add(edge);
    }

    private int rc;

    public void makeEdges(EAntType antType) {
        EAction[] actions = new EAction[]{EAction.TurnLeft,
                                          EAction.TurnRight,
                                          EAction.MoveForward,
                                          EAction.MoveBackward};
        //System.out.println("recursive: " + x + ", " + y + ", " + dir);
        rc = 0;
        //recursiveActionEdges(x, y, dir, actions, antType, antTantTypeype.getMaxActionPoints(), 0, new ArrayList<>());
        recursiveActionEdges(x, y, dir, actions, antType, 5, 0, new ArrayList<>());
        //System.out.println("rc: " + rc);
    }

    private void recursiveActionEdges(int x,
                                      int y,
                                      int dir,
                                      EAction[] actions,
                                      EAntType antType,
                                      int max,
                                      int sum,
                                      List<EAction> path) {
        //System.out.println("recursive: " + x + ", " + y + ", " + dir + ", " + sum + ", " + max);
        //System.out.println("path: "+path);
        rc++;
        for (EAction action : actions) {
            int c = antType.getActionCost(action);

            if (sum + c > max) {
                if (map.getLocation(x, y) != null
                        && (useBlanks
                        || (map.getLocation(x, y).getLocationInfo() != null
                        && (!map.getLocation(x, y).getLocationInfo().isRock()
                        && !map.getLocation(x, y).getLocationInfo().isFilled())))) {
                    if (this.x == x && this.y == y && this.dir == dir) {
                        return;
                    }
                    AntNode node = graph.getNode(x, y, dir);
                    if (node == null) {
                        node = new AntNode(x, y, dir, graph, map, useBlanks);
                        graph.addNode(node);
                        //System.out.println("adding node :" + x + ", " + y + ", " + dir);
                    }
                    AntEdge edge = new AntEdge(this, node, sum + c, path);

                    graph.addEdge(edge);
                    addEdge(edge);
                    return;
                }
            }
//            if (graph.getEdge(this, node) != null) {
//                return;
//            }
            else {
                int xy;
                int p;
                List<EAction> newPath = new ArrayList<>(path);
                //Collections.copy(path, newPath);
                newPath.add(action);
                switch (action) {
                    case TurnLeft:
                        recursiveActionEdges(x, y, (dir + 3) % 4, actions, antType, max, sum + c, newPath);
                        break;
                    case TurnRight:
                        recursiveActionEdges(x, y, (dir + 1) % 4, actions, antType, max, sum + c, newPath);
                        break;
                    case MoveForward:
                        xy = dir % 2;
                        p = -1;
                        if (dir - 2 < 0) {
                            p = 1;
                        }
                        if (xy == 0) {
                            recursiveActionEdges(x, y + p, dir, actions, antType, max, sum + c, newPath);
                        }
                        else {
                            recursiveActionEdges(x + p, y, dir, actions, antType, max, sum + c, newPath);
                        }
                        break;
                    case MoveBackward:
                        xy = dir % 2;
                        p = 1;
                        if (dir - 2 < 0) {
                            p = -1;
                        }
                        if (xy == 0) {
                            recursiveActionEdges(x, y + p, dir, actions, antType, max, sum + c, newPath);
                        }
                        else {
                            recursiveActionEdges(x + p, y, dir, actions, antType, max, sum + c, newPath);
                        }
                        break;
                }
            }
        }
    }

}
