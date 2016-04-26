package antwarsaigraph;

/**
 *
 * @author ichti (Simon T)
 */
public class ZeroHeuristic implements IHeuristic {

    @Override
    public int getHeuristic(int x1, int y1, int x2, int y2) {
        return 0;
    }
    
}
