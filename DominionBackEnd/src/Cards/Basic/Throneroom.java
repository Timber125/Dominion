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
public class Throneroom extends ActionCard {
    
    public Throneroom() {
        super("throneroom", 4);
    }
    
    
    @Override
    public boolean hasSpecial(){
        return true;
    }
    //todo: double the use of an action card
    @Override
    public SpecialCase special(Player victim, Player initiator, Environment env){
        if(victim.getSession().equals(initiator.getSession())){
                    InteractionCase ic = new InteractionCase(victim, initiator);
                    ic.setMaxAmount(1);
                    ic.setMinAmount(1);
                    ic.setMaxCost(100);
                    ic.setMinCost(0);
                    ic.enableHandActions();
                    if(victim.getActions().isEmpty()) return null;
                    for(Card c : victim.getActions()){
                        ic.allowedIds.add("hand");
                        ic.preloadedCards.add(c);
                    }
                    ic.setFinishBehaviour(JSONUtilities.JSON.throneroom_finishbehaviour());
                    ic.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_throneroom(victim, initiator, ic));
                    return ic;
        }
        else return null;
    }
    
}
