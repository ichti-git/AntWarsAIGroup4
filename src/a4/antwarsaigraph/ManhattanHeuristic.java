package a4.antwarsaigraph;

/**
 *
 * @author ichti (Simon T)
 */
public class ManhattanHeuristic implements IHeuristic {

    @Override
    public int getHeuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1-x2) + Math.abs(y1-y2);
    }
    
}
