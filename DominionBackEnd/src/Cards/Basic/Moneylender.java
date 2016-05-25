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
public class Moneylender extends ActionCard{
    
    public Moneylender() {
        super("moneylender", 4);
    }
    
    //todo: trash a copper from your hand, if you do gain +3
    
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
                    boolean copperfound = false;
                    for(Card c : victim.getTreasures()){
                        if(c.getName().equals("copper")){
                            ic.allowedIds.add("hand");
                            ic.preloadedCards.add(c);
                            copperfound = true;
                        }
                    }
                    if(!copperfound) return null;
                    ic.setFinishBehaviour(JSONUtilities.JSON.moneylender_finishbehaviour());
                    ic.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_moneylender(victim, initiator, ic));
                    return ic;
        }
        else return null;
    }
    
}
