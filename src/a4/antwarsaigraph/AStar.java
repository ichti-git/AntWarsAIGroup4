package antwarsaigraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 *
 * @author ichti (Simon T)
 */
public class AStar {
    private Graph graph;
    private IHeuristic heuristic;
    
    public AStar(Graph graph) {
        this.graph = graph;
    }
    
    public AStar(Graph graph, IHeuristic heuristic) {
        this.graph = graph;
        this.heuristic = heuristic;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public void setHeuristic(IHeuristic heuristic) {
        this.heuristic = heuristic;
    }

    public Graph getGraph() {
        return graph;
    }

    public IHeuristic getHeuristic() {
        return heuristic;
    }
    
    public void reset() {
        graph.reset();
    }
    
    public List<Node> findShortestPath(Node start, Node goal) {
        Queue<Node> unvisited = new PriorityQueue<>();
        start.setCost(0);
        Node currentNode;
        unvisited.add(start);
        while (!unvisited.isEmpty()) {
            currentNode = unvisited.poll();
            for (Edge edge : currentNode) {
                Node end = edge.getEnd();
                int newCost = currentNode.getCost()+edge.getCost();
                if (newCost < end.getCost()) {
                    end.setCost(newCost);
                    end.setPrev(currentNode);
                    if (unvisited.contains(end)) {
                        unvisited.remove(end);
                    }
                }
                if (!end.isVisited() && !unvisited.contains(end)) {
                    end.setHeuristic(heuristic.getHeuristic(end.getX(), end.getY(), goal.getX(), goal.getY()));
                    unvisited.add(end);
                }
            }
            currentNode.setVisited(true);
            if (currentNode == goal) break;
            unvisited.remove(currentNode);
        }
        //System.out.println("Goal cost: "+goal.getCost());
        if (goal.getCost() < Integer.MAX_VALUE) {
            ArrayList<Node> res = new ArrayList<>();
            Node curNode = goal;
            do {
               res.add(curNode);
               curNode = curNode.getPrev();
            } 
            while(curNode != null);
            Collections.reverse(res);
            return res;
        }
        else {
            //System.out.println("return null astar");
            return null;
        }
    }
    
    public List<List<Node>> findPathBelowCost(Node start, int maxCost) {
        Queue<Node> unvisited = new PriorityQueue<>();
        start.setCost(0);
        Node currentNode;
        unvisited.add(start);
        while (!unvisited.isEmpty()) {
            currentNode = unvisited.poll();
            //System.out.println("Considering: "+currentNode.getX()+","+currentNode.getY());
            for (Edge edge : currentNode) {
                Node end = edge.getEnd();
                int newCost = currentNode.getCost()+edge.getCost();
                //System.out.println("newCost: " + newCost);
                if (newCost <= maxCost && newCost < end.getCost()) {
                    end.setCost(newCost);
                    end.setPrev(currentNode);
                    if (unvisited.contains(end)) {
                        unvisited.remove(end);
                    }
                }
                if (newCost <= maxCost && !end.isVisited() && !unvisited.contains(end)) {
                    end.setHeuristic(heuristic.getHeuristic(end.getX(), end.getY(), start.getX(), start.getY()));
                    unvisited.add(end);
                }
            }
            currentNode.setVisited(true);
            unvisited.remove(currentNode);
        }
        
        List<List<Node>> validPaths = new ArrayList<>();
        for (Node n : graph) {
            if (n != start && n.isVisited() && n.getCost() <= maxCost) {
            
                ArrayList<Node> res = new ArrayList<>();
                Node curNode = n;
                do {
                   res.add(curNode);
                   curNode = curNode.getPrev();
                } 
                while(curNode != null);
                Collections.reverse(res);
                validPaths.add(res);
            }
        }
        //System.out.println("Number of paths: "+ validPaths.size());
        //if (validPaths.isEmpty()) return null;
        return validPaths;
    }
    
}
