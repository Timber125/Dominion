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
public class Chancellor extends ActionCard {
    
    public Chancellor() {
        super("chancellor", 3);
    }
    
    @Override
    public int moneygain(){
        return 2;
    }
    
    //todo: immediately put your deck in your discard pile
    @Override 
    public boolean hasSpecial(){
        return true;
    }
    
    @Override
    public SpecialCase special(Player victim, Player initiator, Environment env){
        if(victim.getSession().equals(initiator.getSession())){
            InteractionCase onself = new InteractionCase(victim, initiator);
            onself.setMinAmount(0);
            onself.setMaxAmount(1);
            onself.setMinCost(0);
            onself.setMaxCost(100);
            //onself.enable_hand();
            // Allow an empty "back.jpg" as if it would be your deck.
            // Since "back.jpg" cannot be initialized as a card, we take "chancellor" for now.
            onself.allowedIds.add("deck");
            onself.preloadedCards.add(new Chancellor()); 
            onself.enableEnvActions();
            onself.setFinishBehaviour(JSONUtilities.JSON.chancellor_finishbehaviour());
            onself.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_chancellor(victim, initiator, onself));
            return onself;
        }
        else return null;
    }
}
