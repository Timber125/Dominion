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
public class Remodel extends ActionCard {
    
    public Remodel() {
        super("remodel", 4);
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
                    ic.enable_hand();
                    for(Card c : victim.hand){
                        ic.allowedIds.add("hand");
                        ic.preloadedCards.add(c);
                    }
                    ic.setFinishBehaviour(JSONUtilities.JSON.remodel_finishbehaviour());
                    ic.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_remodel(victim, initiator, ic));
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
            ic.enable_environment();
            ic.setFinishBehaviour(JSONUtilities.JSON.create_interaction_finishbehaviour_remodel_future());
            ic.setStartBehaviour(JSONUtilities.JSON.make_client_conformation_model_empty("Opportunity from Remodel: Gain a card, add it to your discardpile."));
            return ic;
        }
        return null;
    }
}
