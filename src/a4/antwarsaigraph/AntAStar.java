package a4.antwarsaigraph;

import aiantwars.EAntType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 *
 * @author ichti (Simon T)
 */
public class AntAStar {

    private final EAntType antType;
    private final IHeuristic heuristic;

    public AntAStar(IHeuristic heuristic, EAntType antType) {
        this.heuristic = heuristic;
        this.antType = antType;
    }

    public List<AntNode> findShortestPath(AntNode start, AntNode goal) {
        //System.out.println("new path");
        Queue<AntNode> unvisited = new PriorityQueue<>();
        start.setCost(0);
        AntNode currentNode;
        unvisited.add(start);
        
        while (!unvisited.isEmpty()) {
            currentNode = unvisited.poll();
            currentNode.makeEdges(antType);
            //System.out.println("currentnode: " + currentNode.getX() + ", " + currentNode.getY() + ", " + currentNode.getDir());
            for (AntEdge edge : currentNode) {
                AntNode end = edge.getEnd();
                int newCost = currentNode.getCost() + edge.getCost();
                if (newCost < end.getCost()) {
                    end.setCost(newCost);
                    end.setPrev(currentNode);
                    if (unvisited.contains(end)) {
                        unvisited.remove(end);
                    }
                }
                if (!end.isVisited() && !unvisited.contains(end)) {
                    end.setHeuristic(heuristic.getHeuristic(end.getX(), end.getY(), goal.getX(), goal.getY()));
                    //System.out.println("adding: " + end.getX() + ", " + end.getY() + ", " + end.getDir());
                    unvisited.add(end);
                }
            }
            currentNode.setVisited(true);
            if (currentNode == goal) {
                break;
            }
            unvisited.remove(currentNode);
        }
        //System.out.println("Goal cost: "+goal.getCost());
        if (goal.getCost() < Integer.MAX_VALUE) {
            ArrayList<AntNode> res = new ArrayList<>();
            AntNode curNode = goal;
            do {
                res.add(curNode);
                curNode = curNode.getPrev();
            }
            while (curNode != null);
            Collections.reverse(res);
            return res;
        }
        else {
            //System.out.println("return null astar");
            return null;
        }
    }
}
