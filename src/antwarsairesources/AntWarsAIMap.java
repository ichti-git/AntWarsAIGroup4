package antwarsairesources;

import aiantwars.EAction;
import aiantwars.IAntInfo;
import aiantwars.ILocationInfo;
import antwarsaigraph.AStar;
import antwarsaigraph.Graph;
import antwarsaigraph.ManhattanHeuristic;
import antwarsaigraph.Node;
import antwarsaigraph.ZeroHeuristic;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 *
 * @author ichti (Simon T)
 */
public class AntWarsAIMap implements Iterable<AntWarsAIMapLocation> {
    private final AntWarsAIMapLocation[][] locations;
    private final int maxX;
    private final int maxY;
    
    public AntWarsAIMap(int worldSizeX, int worldSizeY) {
        maxX = worldSizeX;
        maxY = worldSizeY;
        locations = new AntWarsAIMapLocation[worldSizeX][worldSizeY];
        for (int x = 0; x < worldSizeX; x++) {
            for (int y = 0; y < worldSizeY; y++) {
                locations[x][y] = new AntWarsAIMapLocation(x, y);
            }
        }
    }

    public void setLocationInfo(ILocationInfo locInfo, int currentTurn) {
        int x = locInfo.getX();
        int y = locInfo.getY();
        locations[x][y].setLocationInfo(locInfo);
        locations[x][y].setTurnSeen(currentTurn);
    }

    public List<ILocationInfo> getLocationsWithFood() {
        List<ILocationInfo> food = new ArrayList<>();
        for (AntWarsAIMapLocation loc : this) {
            if (validLocation(loc) && loc.getLocationInfo().getFoodCount() > 0) food.add(loc.getLocationInfo());
        }
        return food;
    }
    
    
    //Different path methods/path related methods below
    
    //TODO Implement this one properly
    //It should find the path to the location with the lowest cost (in turns).
    
    /*
     * Follow methods should be available to the ants
     * Most of the functions will be done by refactoring the working one. The 
     * "heavy" work is done (hopefully).
     * List<EAction> getMoves(IAntInfo ant, List<ILocationInfo> locations): TODO
     * List<EAction> getMoves(IAntInfo ant, ILocationInfo location): TODO
     * List<EAction> getMoves(IAntInfo ant, ILocationInfo location, int direction): Works, but needs cleanup/refactoring 
     * List<EAction> getMoves(Iterable<Node> path): TODO
     * 
     * Maybe change from Iterable to some List type (easier to work with)
     * Iterable<Node> getShortestPath(IAntInfo ant, List<ILocationInfo> locations): TODO
     * Iterable<Node> getShortestPath(IAntInfo ant, ILocationInfo location): TODO
     * Iterable<Node> getShortestPath(IAntInfo ant, ILocationInfo location, int direction): TODO
     */
    
    /**
     * bla bla javadoc test
     * @param thisAnt The ant you want to calcul
     * @param locations targets
     * @return moves...
     */
    public List<EAction> getMoves(IAntInfo thisAnt, List<ILocationInfo> locations) {
        for (ILocationInfo loc : locations) {
            List<EAction> actions = getMoves(thisAnt, loc, thisAnt.getDirection());
            if (actions != null) return actions;
        }
        return null;
    }
    
    private final int turnCost = 2;
    private final int forwardCost = 3;
    private final int backwardCost = 4;
    /**
     * A lot of magic happens in this methods. Needs to be refactored and split 
     * in different methods. Explanation for this and the rest of the methods 
     * will come then, hopefully.
     * @param thisAnt
     * @param location
     * @param locationDirection
     * @return 
     */
    public List<EAction> getMoves(IAntInfo thisAnt, ILocationInfo location, int locationDirection) {
        if (location == null) return new ArrayList();
        //System.out.println("Target: "+location.getX()+","+location.getY());
        Graph APGraph = makeAPGraph();
        //System.out.println("HEYO " + APGraph.getEdges());
        
        AStar APAlgorithm = new AStar(APGraph, new ZeroHeuristic());
        int maxCost = thisAnt.getAntType().getMaxActionPoints();
        
        Graph OTMGraph = makeOTMGraph();
        for (Node node : OTMGraph) {
            APAlgorithm.reset();
            Node APNode = APGraph.getNode(node.getX(), node.getY(), node.getDir());
            for (Iterable<Node> it : APAlgorithm.findPathBelowCost(APNode, maxCost)) {
                Iterator<Node> iterator = it.iterator();
                Node begin = iterator.next();
                //System.out.println(APNode + " " + begin);
                Node end = iterator.next();
                while (iterator.hasNext()) end = iterator.next();
                if (begin.getX() == end.getX() && begin.getY() == end.getY() && begin.getDir() == end.getDir()) {}
                else {                
                    //System.out.println("Found a path from " + begin.getX()+","+begin.getY()+","+begin.getDir() + " to " + end.getX() + "," + end.getY()+","+end.getDir());

                    Node OTMBegin = OTMGraph.getNode(begin.getX(), begin.getY(), begin.getDir());
                    Node OTMEnd = OTMGraph.getNode(end.getX(), end.getY(), end.getDir());
                    OTMGraph.createEdge(OTMBegin, OTMEnd, 1); //create edge from the path, with cost of 1 (One turn)
                }
            }
        }
        //AStar OTMAlgorithm = new AStar(OTMGraph, new ZeroHeuristic());
        AStar OTMAlgorithm = new AStar(OTMGraph, new ManhattanHeuristic());
        Node start = OTMGraph.getNode(thisAnt.getLocation().getX(), thisAnt.getLocation().getY(), thisAnt.getDirection());
        
        Node goal = OTMGraph.getNode(location.getX(), location.getY(), locationDirection);
        System.out.println("______________________");
        System.out.println("Current location: "+thisAnt.getLocation().getX()+","+thisAnt.getLocation().getY()+","+thisAnt.getDirection());
        System.out.println("Target location: "+location.getX()+","+location.getY());
        //System.out.println("HEYO " + OTMGraph.getEdges());
        System.out.println("Trying "+nts(start)+" " + nts(goal));
        Iterable<Node> OTMPath = OTMAlgorithm.findShortestPath(start, goal);
        Iterator<Node> OTMIte = OTMPath.iterator();
        Node OTMFirst = OTMIte.next();
        Node OTMSecond = OTMIte.next();
        APAlgorithm.reset();
        Node APFirst = APGraph.getNode(OTMFirst.getX(), OTMFirst.getY(), OTMFirst.getDir());
        Node APSecond = APGraph.getNode(OTMSecond.getX(), OTMSecond.getY(), OTMSecond.getDir());
        Iterable<Node> APPath = APAlgorithm.findShortestPath(APFirst, 
                                                             APSecond);
        System.out.println(OTMFirst.getX()+","+OTMFirst.getY() + " " + OTMSecond.getX()+","+OTMSecond.getY());
        //System.out.println(APPath);
        System.out.println("APPATH: ");
        for (Node n : APPath) {
            System.out.println(nts(n));
        }
        System.out.println(APPathToActionList(APPath));
        return APPathToActionList(APPath);
    }
    
    
    //Debug helper function. No practical use. Returns a Nodes coordinates and direction as a String
    private String nts(Node node) {
        return node.getX()+","+node.getY()+","+node.getDir();
    }
    
    
    private List<EAction> APPathToActionList(Iterable<Node> path) {
        List<EAction> actions = new ArrayList<>();
        Iterator<Node> iterator = path.iterator();
        Node cur = iterator.next();
        Node prev;
        while (iterator.hasNext()) {
            prev = iterator.next();
            actions.add(nodesToAction(cur, prev));
            cur = prev;
        }
        return actions;
    }
    
    /**
     * Takes 2 Nodes. They have x, y and direction. 
     * The 2 Nodes should be neighbors in an AP Graph. 2 Nodes are neighbors if 
     * there is an EAction that can take them from node1 to node2. This is 
     * vague and could be explained further if necessary?
     * It returns the required EAction to move from node1 to node2.
     * If the 2 Nodes is not neighbors, EAction.Pass will be returned. If the 
     * method calling this method works correctly, the case of two not being 
     * neighbors should never happen.
     */
    private EAction nodesToAction(Node node1, Node node2) {
        //System.out.println("node1: "+node1.getX()+","+node1.getY()+","+node1.getDir() + "  node2: "+node2.getX()+","+node2.getY()+","+node2.getDir());
        if (node1.getX() == node2.getX() && node1.getY() == node2.getY()) {
            if (node2.getDir()-node1.getDir() == 1) return EAction.TurnRight;
            if (node2.getDir()-node1.getDir() == -3) return EAction.TurnRight;
            if (node2.getDir()-node1.getDir() == 3) return EAction.TurnLeft;
            if (node2.getDir()-node1.getDir() == -1) return EAction.TurnLeft;
        }
        if (node2.getX()-node1.getX() == 1) {
            if (node1.getDir() == 1) return EAction.MoveForward;
            if (node1.getDir() == 3) return EAction.MoveBackward;
        }
        if (node2.getX()-node1.getX() == -1) {
            if (node1.getDir() == 1) return EAction.MoveBackward;
            if (node1.getDir() == 3) return EAction.MoveForward;
        }
        if (node2.getY()-node1.getY() == 1) {
            if (node1.getDir() == 0) return EAction.MoveForward;
            if (node1.getDir() == 2) return EAction.MoveBackward;
        }
        if (node2.getY()-node1.getY() == -1) {
            if (node1.getDir() == 0) return EAction.MoveBackward;
            if (node1.getDir() == 2) return EAction.MoveForward;
        }
        System.out.println("Error in nodesToAction(). Not neighbors"); //Maybe do Exception? But will potentially fuck it up in tournament
        return EAction.Pass;
    }
    
    /**
     * This methods makes and returns a Graph which is going to be used as the 
     * OTM Graph (OTM means One Turn Move). Each location on the map is mapped 
     * to 4 Nodes in the Graph. One for each direction. The edges in an 
     * OTM Graph should be where an Ant can move from 1 Node to another Node 
     * in 1 turn (hence One Turn Move). All edges will then have a cost of 1 
     * (1 turn). Makes sense...?
     */
    private Graph makeOTMGraph() {
        Graph OTMGraph = new Graph();
        for (AntWarsAIMapLocation loc : this) {
            if (validLocation(loc)) {
                //System.out.println("OTMGraph: Adding "+loc.getX()+","+loc.getY());
                Node north = OTMGraph.createNode(loc.getX(), loc.getY(), 0);
                Node east = OTMGraph.createNode(loc.getX(), loc.getY(), 1);
                Node south = OTMGraph.createNode(loc.getX(), loc.getY(), 2);
                Node west = OTMGraph.createNode(loc.getX(), loc.getY(), 3);
            }
        }
        return OTMGraph;
    }
    
    /**
     * This methods makes and returns a Graph, which is going to be used as an 
     * AP Graph (AP means Action Point). Each location in the map is mapped to 
     * 4 Nodes. The edges is all the places an ant can move from one Node to 
     * another Node with 1 EAction. The cost of the edge is equal to the cost 
     * of the EAction in Action Points (Hence AP Graph).
     */
    private Graph makeAPGraph() {
        Graph APGraph = new Graph();
        
        //make nodes
        for (AntWarsAIMapLocation loc : this) {
            if (validLocation(loc)) {
                Node north = APGraph.createNode(loc.getX(), loc.getY(), 0);
                Node east = APGraph.createNode(loc.getX(), loc.getY(), 1);
                Node south = APGraph.createNode(loc.getX(), loc.getY(), 2);
                Node west = APGraph.createNode(loc.getX(), loc.getY(), 3);

                //edges internally
                APGraph.createEdge(north, east, turnCost);
                APGraph.createEdge(north, west, turnCost);
                APGraph.createEdge(south, east, turnCost);
                APGraph.createEdge(south, west, turnCost);
                APGraph.createEdge(east, north, turnCost);
                APGraph.createEdge(east, south, turnCost);
                APGraph.createEdge(west, north, turnCost);
                APGraph.createEdge(west, south, turnCost);
            }
        }
        //edges externally
        for (AntWarsAIMapLocation loc : this) {
            if (validLocation(loc)) {
                
                int x = loc.getX();
                int y = loc.getY();
                //direction 0/North/Up edge
                if (y < maxY-1 && locations[x][y+1].getLocationInfo() != null) {
                    makeExternalEdge(APGraph, loc, locations[x][y+1], 0);
                    //System.out.println("Adding external edges1");
                }
                //direction 2/South/Down edge
                if (y > 0 && locations[x][y-1].getLocationInfo() != null) {
                    makeExternalEdge(APGraph, loc, locations[x][y-1], 2);
                    //System.out.println("Adding external edges2");
                }
                //direction 1/East/Right edge
                if (x < maxX-1 && locations[x+1][y].getLocationInfo() != null) {
                    makeExternalEdge(APGraph, loc, locations[x+1][y], 1);
                    //System.out.println("Adding external edges3");
                }
                //direction 3/West/Left edge
                if (x > 0 && locations[x-1][y].getLocationInfo() != null) {
                    makeExternalEdge(APGraph, loc, locations[x-1][y], 3);
                    //System.out.println("Adding external edges3");
                }
            }
        }
        
        return APGraph;
    }

    /**
     * Adds edges to 2 Nodes in different locations in a Graph, based on a 
     * direction (forward). Hard to explain without visual stuff... 
     * This is used in the making of the AP Graph. It uses the EAction costs.
     */
    private void makeExternalEdge(Graph graph, AntWarsAIMapLocation location, AntWarsAIMapLocation target, int forward) {
        if (validLocation(target)) {
            int backward = (forward+2)%4;
            Node start = graph.getNode(location.getX(), location.getY(), forward);
            Node end = graph.getNode(target.getX(), target.getY(), forward);
            graph.createEdge(start, end, forwardCost);
            start = graph.getNode(location.getX(), location.getY(), backward);
            end = graph.getNode(target.getX(), target.getY(), backward);
            graph.createEdge(start, end, backwardCost);
        }
    }
    
    /**
     * Checks if a location is valid. A location is valid if it has been seen, 
     * is not filled with dirt and is not a rock (non existing). Used to check 
     * if Nodes should be created for a location.
     */
    private boolean validLocation(AntWarsAIMapLocation loc) {
        return loc.getLocationInfo() != null && !loc.getLocationInfo().isFilled() && !loc.getLocationInfo().isRock();
    }
    
    //If you iterate over this object, it will give you a list of MapLocations
    @Override
    public Iterator<AntWarsAIMapLocation> iterator() {
        Iterator<AntWarsAIMapLocation> it = new Iterator<AntWarsAIMapLocation>() {
            private int curX = 0;
            private int curY = 0;

            @Override
            public boolean hasNext() {
                return (curY < maxY) && (curX < maxX);
            }

            @Override
            public AntWarsAIMapLocation next() {
                AntWarsAIMapLocation ret = locations[curX][curY++];
                if (curY >= maxY) {
                    curY = 0;
                    curX++;
                }
                return ret;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }
    
    
    
}
