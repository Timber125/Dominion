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
public class Feast extends ActionCard{
    
    public Feast() {
        super("feast", 4);
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
            onself.setMaxAmount(1);
            onself.setMinCost(0);
            onself.setMaxCost(5);
            onself.enable_environment();
            // Allow all cards that are buyable
            for(Card c : env.getAllBuyablesAsCards(5)){
                onself.allowedIds.add("environment");
                onself.preloadedCards.add(c);
            }
            onself.setFinishBehaviour(JSONUtilities.JSON.feast_finishbehaviour());
            onself.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_feast(victim, initiator, onself));
            return onself;
        }
        else return null;
    }
    //todo: trash this card, gain a card costing up to 4
}
