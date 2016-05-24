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
public class Chapel extends ActionCard{
    
    public Chapel() {
        super("chapel", 2);
    }
    
    @Override
    public boolean hasSpecial(){
        return true;
    }
    
    @Override
    public SpecialCase special(Player victim, Player initiator, Environment env){
        if(victim.getSession().equals(initiator.getSession())){
            InteractionCase onself = new InteractionCase(victim, initiator);
            onself.setMinAmount(0);
            onself.setMaxAmount(4);
            onself.setMinCost(0);
            onself.setMaxCost(100);
            onself.enable_hand();
            // Allow all cards in hand
            for(Card c : victim.hand){
                onself.allowedIds.add("hand");
                onself.preloadedCards.add(c);                
            }
            onself.setFinishBehaviour(JSONUtilities.JSON.chapel_finishbehaviour());
            onself.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_chapel(victim, initiator, onself));
            return onself;
        }
        else return null;
    }
    //todo: Trash up to 4 cards from your hand
}
