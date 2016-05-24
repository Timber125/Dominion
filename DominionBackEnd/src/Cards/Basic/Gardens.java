/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cards.Basic;

import Cards.Components.VictoryCard;
import Game.Environment;
import Game.Player;
import Game.RewardCase;
import Game.SpecialCase;
import Server.JSONUtilities;

/**
 *
 * @author Jordy School
 */
public class Gardens extends VictoryCard {
    
    public Gardens() {
        super("gardens", 4);
    }
    
    //todo: Worth 1 victory for each 10 cards in your deck (rounded down)     
    @Override
    public boolean hasSpecial(){
        return true;
    }
    
    @Override
    public SpecialCase special(Player victim, Player initiator, Environment env){
        if(victim.getSession().equals(initiator.getSession())){
            RewardCase rc = new RewardCase(victim);
            rc.setRewardBehaviour(JSONUtilities.JSON.garden_standard_reward());
            return rc;
        }
        return null;
    }
}
