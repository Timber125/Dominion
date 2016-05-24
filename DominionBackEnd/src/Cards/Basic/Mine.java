/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cards.Basic;

import Cards.Components.ActionCard;
import Cards.Components.Card;
import Game.Environment;
import Game.InteractionCase;
import Game.Player;
import Game.SpecialCase;
import Server.JSONUtilities;

/**
 *
 * @author Jordy School
 */
public class Mine extends ActionCard{
    
    public Mine() {
        super("mine", 5);
    }
    
    @Override
    public boolean hasSpecial(){
        return true;
    }
    
     @Override
    public SpecialCase special(Player victim, Player initiator, Environment env){
        if(victim.getSession().equals(initiator.getSession())){
                    InteractionCase ic = new InteractionCase(victim, initiator);
                    ic.setMaxAmount(1);
                    ic.setMinAmount(1);
                    ic.setMaxCost(100);
                    ic.setMinCost(0);
                    ic.enableHandTreasure();
                    for(Card c : victim.getTreasures()){
                        ic.allowedIds.add("hand");
                        ic.preloadedCards.add(c);
                    }
                    ic.setFinishBehaviour(JSONUtilities.JSON.mine_finishbehaviour());
                    ic.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_mine(victim, initiator, ic));
                    return ic;
        }
        else return null;
    }
    
    @Override
    public InteractionCase futurespecial(Player victim, Player initiator, Environment env){
        if(victim.getSession().equals(initiator.getSession())){
            InteractionCase ic = new InteractionCase(victim, initiator);
            ic.setMinAmount(0);
            ic.setMaxAmount(0);
            ic.setMinCost(0);
            ic.setMaxCost(0);
            ic.enableEnvTreasure();
            ic.setFinishBehaviour(JSONUtilities.JSON.create_interaction_finishbehaviour_mine_future());
            ic.setStartBehaviour(JSONUtilities.JSON.make_client_conformation_model_empty("Opportunity from Mine: Gain one card, add it to your hand."));
            return ic;
        }
        return null;
    }
}
