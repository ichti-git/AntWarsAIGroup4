package a4.antwarsairesources;

import aiantwars.EAction;
import aiantwars.IAntInfo;
import aiantwars.ILocationInfo;
import a4.antwarsaigraph.AStar;
import a4.antwarsaigraph.AntAStar;
import a4.antwarsaigraph.AntGraph;
import a4.antwarsaigraph.AntNode;
import a4.antwarsaigraph.Graph;
import a4.antwarsaigraph.ManhattanHeuristic;
import a4.antwarsaigraph.Node;
import a4.antwarsaigraph.ZeroHeuristic;
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
    private List<ILocationInfo> temporaryInvalidLocations = new ArrayList<>();
    //private final int turnCost = 2;
    //private final int forwardCost = 3;
    //private final int backwardCost = 4;

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

//    public void addTemporaryInvalidLocation(int x, int y) {
//        temporaryInvalidLocations.add(new int[] {x, y});
//    }
//    
    public void clearTemporaryInvalidLocations() {
        temporaryInvalidLocations.clear();
    }

    /**
     *
     * @param x
     * @param y
     * @return Returns null if location is out of bounds.
     */
    public AntWarsAIMapLocation getLocation(int x, int y) {
        if (x < 0 || y < 0 || x >= maxX || y >= maxY) {
            return null;
        }
        return locations[x][y];
    }

    public AntWarsAIMapLocation getLocation(ILocationInfo loc) {
        return getLocation(loc.getX(), loc.getY());
    }

    public List<ILocationInfo> getLocationsWithFood() {
        List<ILocationInfo> food = new ArrayList<>();
        for (AntWarsAIMapLocation loc : this) {
            if (validLocation(loc, false) && loc.getLocationInfo().getFoodCount() > 0) {
                food.add(loc.getLocationInfo());
            }
        }
        return food;
    }

    //Different path methods/path related methods below
    /*
     * Follow methods should be available to the ants getFirstOneTurnMove :
     * first One Turn Move of path getOneTurnMoves : List of One Turn Moves for
     * path getMovesList : List of moves getMapPath : List of Locations for
     * path. Not implented. TODO
     *
     */
    public List<EAction> getFirstOneTurnMove(List<List<EAction>> OTMList) {
        if (OTMList.isEmpty()) {
            return new ArrayList<>();
        }
        else {
            return OTMList.get(0);
        }
    }

    public List<EAction> getFirstOneTurnMove(IAntInfo ant, ILocationInfo location, int direction) {
        return getFirstOneTurnMove(ant, location, direction, false);
    }

    public List<EAction> getFirstOneTurnMove(IAntInfo ant, ILocationInfo location, int direction, boolean useBlanks) {
        return getFirstOneTurnMove(getOneTurnMoves(ant, location, direction, useBlanks));
    }

    public List<EAction> getFirstOneTurnMove(IAntInfo ant, ILocationInfo location) {
        return getFirstOneTurnMove(ant, location, false);
    }

    public List<EAction> getFirstOneTurnMove(IAntInfo ant, ILocationInfo location, boolean useBlanks) {
        return getFirstOneTurnMove(getOneTurnMoves(ant, location, useBlanks));
    }

    public List<EAction> getFirstOneTurnMove(IAntInfo ant, List<ILocationInfo> locations) {
        return getFirstOneTurnMove(ant, locations, false);
    }

    public List<EAction> getFirstOneTurnMove(IAntInfo ant, List<ILocationInfo> locations, boolean useBlanks) {
        return getFirstOneTurnMove(getOneTurnMoves(ant, locations, useBlanks));
    }

    public List<EAction> getFirstOneTurnMove(IAntInfo ant, int x, int y, boolean useBlanks) {
        //System.out.println("test2");
        return getFirstOneTurnMove(getOneTurnMoves(ant, x, y, useBlanks));
    }

    public List<List<EAction>> getOneTurnMoves(IAntInfo ant, int x, int y, int direction) {
        return getOneTurnMoves(ant, x, y, direction, false);
    }

    public List<List<EAction>> getOneTurnMoves(IAntInfo ant, int x, int y, int direction, boolean useBlanks) {
        if (!useBlanks && getLocation(x, y) == null) {
            return new ArrayList();
        }
        
        AntGraph graph = new AntGraph();
        AntNode goal = new AntNode(x, y, direction, graph, this, useBlanks);
        AntNode start = new AntNode(ant.getLocation().getX(), ant.getLocation().getY(), ant.getDirection(), graph, this, useBlanks);
        graph.addNode(goal);
        graph.addNode(start);

        AntAStar AStar = new AntAStar(new ZeroHeuristic(), ant.getAntType());
        //long startTime = System.currentTimeMillis(); 
        List<AntNode> path = AStar.findShortestPath(start, goal);
//        long stopTime = System.currentTimeMillis();
//        long ela = stopTime - startTime;
//        System.out.println("AStar time: " + ela);
        return AntPathToActionList(path);
        
        /*
        Graph APGraph = makeAPGraph(ant, useBlanks);

        AStar APAlgorithm = new AStar(APGraph, new ZeroHeuristic());

        ILocationInfo start = ant.getLocation();
        List<Node> APPath = APAlgorithm.findShortestPath(APGraph.getNode(start.getX(), start.getY(), ant.getDirection()), APGraph.getNode(x, y, direction));

        List<List<EAction>> movesList = new ArrayList<>();
        movesList.add(APPathToActionList(APPath));
        return movesList;
        * */
    }

    public List<List<EAction>> getOneTurnMoves(IAntInfo ant, ILocationInfo location, int direction) {
        return getOneTurnMoves(ant, location, direction, false);
    }

    public List<List<EAction>> getOneTurnMoves(IAntInfo ant, ILocationInfo location, int direction, boolean useBlanks) {
        if (location == null) {
            return new ArrayList();
        }
        return getOneTurnMoves(ant, location.getX(), location.getY(), direction, useBlanks);
        /*
        Graph APGraph = makeAPGraph(ant, useBlanks);
        AStar APAlgorithm = new AStar(APGraph, new ZeroHeuristic());

        int maxCost = ant.getAntType().getMaxActionPoints();
        ILocationInfo start = ant.getLocation();
        List<Node> APPath = APAlgorithm.findShortestPath(APGraph.getNode(start.getX(), start.getY(), ant.getDirection()), APGraph.getNode(location.getX(), location.getY(), direction));

        List<List<EAction>> movesList = new ArrayList<>();
        movesList.add(APPathToActionList(APPath));
        return movesList;
        * */
    }

    public List<List<EAction>> getOneTurnMoves(IAntInfo ant, ILocationInfo location) {
        return getOneTurnMoves(ant, location, false);
    }

    public List<List<EAction>> getOneTurnMoves(IAntInfo ant, ILocationInfo location, boolean useBlanks) {
        int cost = Integer.MAX_VALUE;
        List<List<EAction>> best = null;
        for (int i = 0; i < 4; i++) {
            List<List<EAction>> OTM = getOneTurnMoves(ant, location, i, useBlanks);
            if (OTM.size() < cost) {
                best = OTM;
            }
        }
        return best;
    }

    public List<List<EAction>> getOneTurnMoves(IAntInfo ant, int x, int y, boolean useBlanks) {
        //System.out.println("test3");
        int cost = Integer.MAX_VALUE;
        List<List<EAction>> best = null;
        for (int i = 0; i < 4; i++) {
            List<List<EAction>> OTM = getOneTurnMoves(ant, x, y, i, useBlanks);
            if (OTM.size() < cost) {
                best = OTM;
            }
        }
        return best;
    }

    public List<List<EAction>> getOneTurnMoves(IAntInfo ant, List<ILocationInfo> locations) {
        return getOneTurnMoves(ant, locations, false);
    }

    public List<List<EAction>> getOneTurnMoves(IAntInfo ant, List<ILocationInfo> locations, boolean useBlanks) {
        int cost = Integer.MAX_VALUE;
        List<List<EAction>> best = null;
        for (ILocationInfo location : locations) {
            List<List<EAction>> OTM = getOneTurnMoves(ant, location, useBlanks);
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
        return getMovesList(ant, location, direction, false);
    }

    public List<EAction> getMovesList(IAntInfo ant, ILocationInfo location, int direction, boolean useBlanks) {
        return getMovesList(getOneTurnMoves(ant, location, direction, useBlanks));
    }

    public List<EAction> getMovesList(IAntInfo ant, ILocationInfo location) {
        return getMovesList(ant, location, false);
    }

    public List<EAction> getMovesList(IAntInfo ant, ILocationInfo location, boolean useBlanks) {
        return getMovesList(getOneTurnMoves(ant, location, useBlanks));
    }

    public List<EAction> getMovesList(IAntInfo ant, List<ILocationInfo> locations) {
        return getMovesList(ant, locations, false);
    }

    public List<EAction> getMovesList(IAntInfo ant, List<ILocationInfo> locations, boolean useBlanks) {
        return getMovesList(getOneTurnMoves(ant, locations, useBlanks));
    }

    /*
     * TODO public List<AntWarsAIMapLocation> getMapPath(IAntInfo ant,
     * ILocationInfo location, int direction) {
     *
     * }
     *
     * public List<AntWarsAIMapLocation> getMapPath(IAntInfo ant, ILocationInfo
     * location) {
     *
     * }
     *
     * public List<AntWarsAIMapLocation> getMapPath(IAntInfo ant,
     * List<ILocationInfo> locations) {
     *
     * }
     */
    /**
     * Don't use this function. Replaced with getFirstOneTurnMove(). bla bla
     * javadoc test
     *
     * @param thisAnt The ant you want to calcul
     * @param locations targets
     * @return moves...
     * @deprecated use getFirstOneTurnMove() instead
     */
    @Deprecated
    public List<EAction> getMoves(IAntInfo thisAnt, List<ILocationInfo> locations) {
        for (ILocationInfo loc : locations) {
            List<EAction> actions = getMoves(thisAnt, loc, thisAnt.getDirection());
            if (actions != null) {
                return actions;
            }
        }
        return null;
    }

    /**
     * Don't use this function. Replaced with getFirstOneTurnMove(). A lot of
     * magic happens in this methods. Needs to be refactored and split in
     * different methods. Explanation for this and the rest of the methods will
     * come then, hopefully.
     *
     * @param ant
     * @param location
     * @param direction
     * @return
     * @deprecated use getFirstOneTurnMove() instead
     */
    @Deprecated
    public List<EAction> getMoves(IAntInfo ant, ILocationInfo location, int direction) {
        return getFirstOneTurnMove(ant, location, direction);

    }

    /**
     * Makes a OneTurnMove list. A list of EAction for the ant to use, which can
     * be done in one turn.
     *
     * @param path
     * @return
     */
    private List<EAction> APPathToActionList(List<Node> path) {
        List<EAction> actions = new ArrayList<>();
        Iterator<Node> iterator = path.iterator();
        Node cur = iterator.next();
        Node prev;
        while (iterator.hasNext()) {
            //System.out.println(cur.getX()+","+cur.getY()+","+cur.getDir());
            prev = iterator.next();
            actions.add(nodesToAction(cur, prev));
            cur = prev;
        }
        return actions;
    }

    /**
     * Takes 2 Nodes. They have x, y and direction. The 2 Nodes should be
     * neighbors in an AP Graph. 2 Nodes are neighbors if there is an EAction
     * that can take them from node1 to node2. This is vague and could be
     * explained further if necessary? It returns the required EAction to move
     * from node1 to node2. If the 2 Nodes is not neighbors, EAction.Pass will
     * be returned. If the method calling this method works correctly, the case
     * of two not being neighbors should never happen.
     */
    private EAction nodesToAction(Node node1, Node node2) {
        //System.out.println("node1: "+node1.getX()+","+node1.getY()+","+node1.getDir() + "  node2: "+node2.getX()+","+node2.getY()+","+node2.getDir());
        if (node1.getX() == node2.getX() && node1.getY() == node2.getY()) {
            if (node2.getDir() - node1.getDir() == 1) {
                return EAction.TurnRight;
            }
            if (node2.getDir() - node1.getDir() == -3) {
                return EAction.TurnRight;
            }
            if (node2.getDir() - node1.getDir() == 3) {
                return EAction.TurnLeft;
            }
            if (node2.getDir() - node1.getDir() == -1) {
                return EAction.TurnLeft;
            }
        }
        if (node2.getX() - node1.getX() == 1) {
            if (node1.getDir() == 1) {
                return EAction.MoveForward;
            }
            if (node1.getDir() == 3) {
                return EAction.MoveBackward;
            }
        }
        if (node2.getX() - node1.getX() == -1) {
            if (node1.getDir() == 1) {
                return EAction.MoveBackward;
            }
            if (node1.getDir() == 3) {
                return EAction.MoveForward;
            }
        }
        if (node2.getY() - node1.getY() == 1) {
            if (node1.getDir() == 0) {
                return EAction.MoveForward;
            }
            if (node1.getDir() == 2) {
                return EAction.MoveBackward;
            }
        }
        if (node2.getY() - node1.getY() == -1) {
            if (node1.getDir() == 0) {
                return EAction.MoveBackward;
            }
            if (node1.getDir() == 2) {
                return EAction.MoveForward;
            }
        }
        System.out.println("Error in nodesToAction(). Not neighbors"); //Maybe do Exception? But will potentially fuck it up in tournament
        return EAction.Pass;
    }

    /**
     * This methods makes and returns a Graph which is going to be used as the
     * OTM Graph (OTM means One Turn Move). Each location on the map is mapped
     * to 4 Nodes in the Graph. One for each direction. The edges in an OTM
     * Graph should be where an Ant can move from 1 Node to another Node in 1
     * turn (hence One Turn Move). All edges will then have a cost of 1 (1
     * turn). Makes sense...?
     */
    private Graph makeOTMGraph(AStar APAlgorithm, int maxCost, boolean useBlanks) {
        Graph OTMGraph = new Graph();
        for (AntWarsAIMapLocation loc : this) {
            if (validLocation(loc, useBlanks)) {
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
                while (iterator.hasNext()) {
                    end = iterator.next();
                }
                if (begin.getX() == end.getX() && begin.getY() == end.getY() && begin.getDir() == end.getDir()) {
                }
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
     * AP Graph (AP means Action Point). Each location in the map is mapped to 4
     * Nodes. The edges is all the places an ant can move from one Node to
     * another Node with 1 EAction. The cost of the edge is equal to the cost of
     * the EAction in Action Points (Hence AP Graph).
     */
    private Graph makeAPGraph(IAntInfo ant, boolean useBlanks) {
        int turnCost = ant.getAntType().getActionCost(EAction.TurnLeft);
        int backwardCost = ant.getAntType().getActionCost(EAction.MoveBackward);
        int forwardCost = ant.getAntType().getActionCost(EAction.MoveForward);
        Graph APGraph = new Graph();

        //make nodes
        for (AntWarsAIMapLocation loc : this) {
            if (validLocation(loc, useBlanks)) {
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
            if (validLocation(loc, useBlanks)) {
                int x = loc.getX();
                int y = loc.getY();
                //direction 0/North/Up edge
                if (y < maxY - 1 && validLocation(locations[x][y + 1], useBlanks)) {
                    makeExternalEdge(APGraph, loc, locations[x][y + 1], 0, forwardCost, backwardCost, useBlanks);
                    //System.out.println("Adding external edges1");
                }
                //direction 2/South/Down edge
                if (y > 0 && validLocation(locations[x][y - 1], useBlanks)) {
                    makeExternalEdge(APGraph, loc, locations[x][y - 1], 2, forwardCost, backwardCost, useBlanks);
                    //System.out.println("Adding external edges2");
                }
                //direction 1/East/Right edge
                if (x < maxX - 1 && validLocation(locations[x + 1][y], useBlanks)) {
                    makeExternalEdge(APGraph, loc, locations[x + 1][y], 1, forwardCost, backwardCost, useBlanks);
                    //System.out.println("Adding external edges3");
                }
                //direction 3/West/Left edge
                if (x > 0 && validLocation(locations[x - 1][y], useBlanks)) {
                    makeExternalEdge(APGraph, loc, locations[x - 1][y], 3, forwardCost, backwardCost, useBlanks);
                    //System.out.println("Adding external edges3");
                }
            }
        }

        return APGraph;
    }

    /**
     * Adds edges between 2 Nodes in different locations in a Graph, based on a
     * direction (forward). Hard to explain without visual stuff... This is used
     * in the making of the AP Graph. It uses the EAction costs.
     */
    private void makeExternalEdge(Graph graph,
                                  AntWarsAIMapLocation location,
                                  AntWarsAIMapLocation target,
                                  int forward,
                                  int forwardCost,
                                  int backwardCost,
                                  boolean useBlanks) {
        if (validLocation(target, useBlanks)) {
            int backward = (forward + 2) % 4;
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
     * if Nodes should be created for a location. Checks if location is in
     * temporaryInvalidLocations
     */
    private boolean validLocation(AntWarsAIMapLocation loc, boolean useBlanks) {
        ILocationInfo iLoc = loc.getLocationInfo();
        if (useBlanks && iLoc == null) {
            return true;
        }
        else {
            return iLoc != null
                    && !iLoc.isFilled()
                    && !iLoc.isRock()
                    && !temporaryInvalidLocations.contains(loc.getLocationInfo());
        }
    }

    //If you iterate over this object, it will give you a list of AntWarsAIMapLocation
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
     * Goes through the OTMPath and makes OneTurnMoves for each node, using the
     * APAlgorithm/APGraph.
     *
     * @param OTMPath
     * @param APAlgorithm
     * @return
     */
    private List<List<EAction>> PathToActionList(List<Node> OTMPath, AStar APAlgorithm) {
        List<List<EAction>> movesList = new ArrayList<>();
        for (int i = 1; i < OTMPath.size(); i++) {
            Node OTMFirst = OTMPath.get(i - 1);
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

    private List<List<EAction>> AntPathToActionList(List<AntNode> AntPath) {
        List<List<EAction>> movesList = new ArrayList<>();
        for (int i = 1; i < AntPath.size(); i++) {
            AntNode first = AntPath.get(i - 1);
            AntNode second = AntPath.get(i);
            movesList.add(first.getEdge(second).getPath());
        }
        return movesList;
    }

    //private List<List<EAction>> APPAthToActionList(List<Node> APPath) {
    /*
     * List<List<EAction>> movesList = new ArrayList<>(); for (int i = 1; i <
     * APPath.size(); i++) { List<EAction> actions =
     * nodesToAction(APPath.get(i-1), APPath.get(i)); }
     *
     */
    //}
    public void addTemporaryInvalidLocation(ILocationInfo loc) {
        temporaryInvalidLocations.add(loc);
    }

}
