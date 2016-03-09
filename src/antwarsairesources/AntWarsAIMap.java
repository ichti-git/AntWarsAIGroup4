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
    
    private final int turnCost = 2;
    private final int forwardCost = 3;
    private final int backwardCost = 4;
    
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
    
    public AntWarsAIMapLocation getLocation(int x, int y) {
        return locations[x][y];
    }

    public List<ILocationInfo> getLocationsWithFood() {
        List<ILocationInfo> food = new ArrayList<>();
        for (AntWarsAIMapLocation loc : this) {
            if (validLocation(loc) && loc.getLocationInfo().getFoodCount() > 0) food.add(loc.getLocationInfo());
        }
        return food;
    }
    
    
    
    //Different path methods/path related methods below
    /*
     * Follow methods should be available to the ants
     * getFirstOneTurnMove : first One Turn Move of path
     * getOneTurnMoves : List of One Turn Moves for path
     * getMovesList : List of moves
     * getMapPath : List of Locations for path. Not implented. TODO
     * 
     */
    
    public List<EAction> getFirstOneTurnMove(List<List<EAction>> OTMList) {
        return OTMList.get(0);
    }
    
    public List<EAction> getFirstOneTurnMove(IAntInfo ant, ILocationInfo location, int direction) {
        return getFirstOneTurnMove(getOneTurnMoves(ant, location, direction));
    }
    
    public List<EAction> getFirstOneTurnMove(IAntInfo ant, ILocationInfo location) {
        return getFirstOneTurnMove(getOneTurnMoves(ant, location));
    }
    
    public List<EAction> getFirstOneTurnMove(IAntInfo ant, List<ILocationInfo> locations) {
        return getFirstOneTurnMove(getOneTurnMoves(ant, locations));
    }
    
    
    public List<List<EAction>> getOneTurnMoves(IAntInfo ant, ILocationInfo location, int direction) {
        if (location == null) return new ArrayList();
        
        Graph APGraph = makeAPGraph();
        AStar APAlgorithm = new AStar(APGraph, new ZeroHeuristic());
        
        
        int maxCost = ant.getAntType().getMaxActionPoints();
        Graph OTMGraph = makeOTMGraph(APAlgorithm, maxCost);
        //AStar OTMAlgorithm = new AStar(OTMGraph, new ZeroHeuristic());
        AStar OTMAlgorithm = new AStar(OTMGraph, new ManhattanHeuristic());
        
        Node start = OTMGraph.getNode(ant.getLocation().getX(), ant.getLocation().getY(), ant.getDirection());
        Node goal = OTMGraph.getNode(location.getX(), location.getY(), direction);
        
        
        //Debug stuff
        /*
        System.out.println("______________________");
        System.out.println("Current location: "+ant.getLocation().getX()+","+ant.getLocation().getY()+","+ant.getDirection());
        System.out.println("Target location: "+location.getX()+","+location.getY());
        //System.out.println("HEYO " + OTMGraph.getEdges());
        System.out.println("Trying "+nts(start)+" " + nts(goal));
        */
        
        List<Node> OTMPath = OTMAlgorithm.findShortestPath(start, goal);
        return PathToActionList(OTMPath, APAlgorithm);
    }
    
    public List<List<EAction>> getOneTurnMoves(IAntInfo ant, ILocationInfo location) {
        int cost = Integer.MAX_VALUE;
        List<List<EAction>> best = null;
        for (int i = 0; i < 4; i++) {
            List<List<EAction>> OTM = getOneTurnMoves(ant, location, i);
            if (OTM.size() < cost) {
                best = OTM;
            }
        }
        return best;
    }
    
    public List<List<EAction>> getOneTurnMoves(IAntInfo ant, List<ILocationInfo> locations) {
        int cost = Integer.MAX_VALUE;
        List<List<EAction>> best = null;
        for (ILocationInfo location : locations) {
            List<List<EAction>> OTM = getOneTurnMoves(ant, location);
            if (OTM.size() < cost) {
                best = OTM;
            }
        }
        return best;
    }
    
    
    public List<EAction> getMovesList(List<List<EAction>> OneTurnMoves) {
        List<EAction> list = new ArrayList<>();
        for (List<EAction> actions : OneTurnMoves) {
            for (EAction a : actions) {
                list.add(a);
            }
        }
        return list;
    }
    
    public List<EAction> getMovesList(IAntInfo ant, ILocationInfo location, int direction) {
        return getMovesList(getOneTurnMoves(ant, location, direction));
    }
    
    public List<EAction> getMovesList(IAntInfo ant, ILocationInfo location) {
        return getMovesList(getOneTurnMoves(ant, location));
    }
    
    public List<EAction> getMovesList(IAntInfo ant, List<ILocationInfo> locations) {
        return getMovesList(getOneTurnMoves(ant, locations));
    }
    
    /* TODO
    public List<AntWarsAIMapLocation> getMapPath(IAntInfo ant, ILocationInfo location, int direction) {
        
    }
    
    public List<AntWarsAIMapLocation> getMapPath(IAntInfo ant, ILocationInfo location) {
        
    }
    
    public List<AntWarsAIMapLocation> getMapPath(IAntInfo ant, List<ILocationInfo> locations) {
        
    }
    */
    
    /**
     * Don't use this function. Replaced with getFirstOneTurnMove().
     * bla bla javadoc test
     * @param thisAnt The ant you want to calcul
     * @param locations targets
     * @return moves...
     * @deprecated 
     */
    @Deprecated
    public List<EAction> getMoves(IAntInfo thisAnt, List<ILocationInfo> locations) {
        for (ILocationInfo loc : locations) {
            List<EAction> actions = getMoves(thisAnt, loc, thisAnt.getDirection());
            if (actions != null) return actions;
        }
        return null;
    }
    
    /**
     * Don't use this function. Replaced with getFirstOneTurnMove().
     * A lot of magic happens in this methods. Needs to be refactored and split 
     * in different methods. Explanation for this and the rest of the methods 
     * will come then, hopefully.
     * @param ant
     * @param location
     * @param direction
     * @return 
     * @deprecated
     */
    @Deprecated
    public List<EAction> getMoves(IAntInfo ant, ILocationInfo location, int direction) {
        return getFirstOneTurnMove(ant, location, direction);
        /*
        if (location == null) return new ArrayList();
        
        Graph APGraph = makeAPGraph();
        AStar APAlgorithm = new AStar(APGraph, new ZeroHeuristic());
        
        
        int maxCost = ant.getAntType().getMaxActionPoints();
        
        Graph OTMGraph = makeOTMGraph(APAlgorithm, maxCost);
        //AStar OTMAlgorithm = new AStar(OTMGraph, new ZeroHeuristic());
        AStar OTMAlgorithm = new AStar(OTMGraph, new ManhattanHeuristic());
        Node start = OTMGraph.getNode(ant.getLocation().getX(), ant.getLocation().getY(), ant.getDirection());
        
        Node goal = OTMGraph.getNode(location.getX(), location.getY(), direction);
        System.out.println("______________________");
        System.out.println("Current location: "+ant.getLocation().getX()+","+ant.getLocation().getY()+","+ant.getDirection());
        System.out.println("Target location: "+location.getX()+","+location.getY());
        //System.out.println("HEYO " + OTMGraph.getEdges());
        System.out.println("Trying "+nts(start)+" " + nts(goal));
        Iterable<Node> OTMPath = OTMAlgorithm.findShortestPath(start, goal);
        //Iterable<Node> OTMPath = getShortestPath(thisAnt, location, locationDirection);
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
        * */
    }
    
    /*
    public List<Node> getShortestPath(IAntInfo ant, List<ILocationInfo> locations) {
        
    }
    public List<Node> getShortestPath(IAntInfo ant, ILocationInfo location) {
        
    }
    * */
    /*
    public List<Node> getShortestPath(IAntInfo ant, ILocationInfo location, int direction) {
        
        if (location == null) return new ArrayList();
        Graph APGraph = makeAPGraph();
        AStar APAlgorithm = new AStar(APGraph, new ZeroHeuristic());
        
        int maxCost = ant.getAntType().getMaxActionPoints();
        Graph OTMGraph = makeOTMGraph(APAlgorithm, maxCost);
        AStar OTMAlgorithm = new AStar(OTMGraph, new ZeroHeuristic());
        //AStar OTMAlgorithm = new AStar(OTMGraph, new ManhattanHeuristic());
        
        Node start = OTMGraph.getNode(ant.getLocation().getX(), ant.getLocation().getY(), ant.getDirection());
        Node goal = OTMGraph.getNode(location.getX(), location.getY(), direction);
        System.out.println("______________________");
        System.out.println("Current location: "+ant.getLocation().getX()+","+ant.getLocation().getY()+","+ant.getDirection());
        System.out.println("Target location: "+location.getX()+","+location.getY());
        //System.out.println("HEYO " + OTMGraph.getEdges());
        System.out.println("Trying "+nts(start)+" " + nts(goal));
        return OTMAlgorithm.findShortestPath(start, goal);
    }
    */
    
    //Debug helper function. No practical use. Returns a Nodes coordinates and direction as a String
    private String nts(Node node) {
        return node.getX()+","+node.getY()+","+node.getDir();
    }
    
    /**
     * Makes a OneTurnMove list. A list of EAction for the ant to use, which
     * can be done in one turn.
     * @param path
     * @return 
     */
    private List<EAction> APPathToActionList(List<Node> path) {
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
    private Graph makeOTMGraph(AStar APAlgorithm, int maxCost) {
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
        
        for (Node node : OTMGraph) {
            APAlgorithm.reset();
            Graph APGraph = APAlgorithm.getGraph();
            Node APNode = APGraph.getNode(node.getX(), node.getY(), node.getDir());
            for (Iterable<Node> it : APAlgorithm.findPathBelowCost(APNode, maxCost)) {
                Iterator<Node> iterator = it.iterator();
                Node begin = iterator.next();
                Node end = iterator.next();
                while (iterator.hasNext()) end = iterator.next();
                if (begin.getX() == end.getX() && begin.getY() == end.getY() && begin.getDir() == end.getDir()) {}
                else {                
                    Node OTMBegin = OTMGraph.getNode(begin.getX(), begin.getY(), begin.getDir());
                    Node OTMEnd = OTMGraph.getNode(end.getX(), end.getY(), end.getDir());
                    OTMGraph.createEdge(OTMBegin, OTMEnd, 1); //create edge from the path, with cost of 1 (One turn)
                }
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

    
    /**
     * Goes through the OTMPath and makes OneTurnMoves for each node, using 
     * the APAlgorithm/APGraph.
     * @param OTMPath
     * @param APAlgorithm
     * @return 
     */
    private List<List<EAction>> PathToActionList(List<Node> OTMPath, AStar APAlgorithm) {
        List<List<EAction>> movesList = new ArrayList<>();
        for (int i = 1; i < OTMPath.size(); i++) {
            Node OTMFirst = OTMPath.get(i-1);
            Node OTMSecond = OTMPath.get(i);
            APAlgorithm.reset();
            Graph APGraph = APAlgorithm.getGraph();
            Node APFirst = APGraph.getNode(OTMFirst.getX(), OTMFirst.getY(), OTMFirst.getDir());
            Node APSecond = APGraph.getNode(OTMSecond.getX(), OTMSecond.getY(), OTMSecond.getDir());
            List<Node> APPath = APAlgorithm.findShortestPath(APFirst, APSecond);
            movesList.add(APPathToActionList(APPath));
            
        }
        return movesList;
    }
    
    
    
}
