package a4;

import aiantwars.IAntAI;
import a4.antwarsai.QueenAI;
import tournament.player.PlayerFactory;

/**
 *
 * @author ichti (Simon T)
 */
public class A4 implements PlayerFactory<IAntAI> {
    
    @Override
    public IAntAI getNewInstance() {
        return new QueenAI();
    }
    
    @Override
    public String getID() {
        return "A4";
    }
    
    @Override
    public String getName() {
        return "Cockroaches";
    }
}
