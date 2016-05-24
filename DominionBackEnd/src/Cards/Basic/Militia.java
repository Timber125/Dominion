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
public class Militia extends ActionCard {
    
    public Militia() {
        super("militia", 4);
    }
    
    @Override
    public int moneygain(){
        return 2;
    }
    
    //todo: each other player discards down to 3 cards
    @Override
    public boolean is_attack(){
        return true;
    }
    @Override
    public boolean hasSpecial(){
        return true;
    }
    
    @Override
    public SpecialCase special(Player victim, Player initiator, Environment env){
        if(victim.getSession().equals(initiator.getSession())){
            return null;
        }
        else {
            InteractionCase ic = new InteractionCase(victim, initiator);
            int victimcards = victim.hand.size();
            int discardamount = victim.hand.size() - 3;
            if(discardamount <= 0) return null;
            else{
                if(victim.getReactions().isEmpty()){
                    ic.setMaxAmount(discardamount);
                    ic.setMinAmount(discardamount);
                    ic.setMaxCost(100);
                    ic.setMinCost(0);
                    for(Card c : victim.hand){
                        ic.allowedIds.add("hand");
                        ic.preloadedCards.add(c);
                    }
                    ic.enable_hand();
                    ic.setFinishBehaviour(JSONUtilities.JSON.militia_notblocked_finishbehaviour());
                    ic.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_militia_notblocked(victim, initiator, ic));
            
                }else{
                    ic.setMaxAmount(1);
                    ic.setMinAmount(1);
                    ic.setMaxCost(100);
                    ic.setMinCost(0);
                    ic.set_reaction_only();
                    for(Card c : victim.getReactions()){
                        ic.allowedIds.add("hand");
                        ic.preloadedCards.add(c);
                    }
                    ic.setFinishBehaviour(JSONUtilities.JSON.create_interaction_blocked_finishbehaviour());
                    ic.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_militia_blocked(victim, initiator, ic));
            
                }
                
                return ic;
            }
        }
    }
    
    @Override
    public InteractionCase futurespecial(Player victim, Player initiator, Environment env){
        if(victim.getSession().equals(initiator.getSession())){
            InteractionCase ic = new InteractionCase(victim, initiator);
            ic.setMinAmount(0);
            ic.setMaxAmount(0);
            ic.setMinCost(0);
            ic.setMaxCost(0);
            
            // NOTHING ENABLED!
            //ic.enable_hand();
            //ic.enable_environment();
            ic.setFinishBehaviour(JSONUtilities.JSON.create_interaction_finishbehaviour_empty());
            ic.setStartBehaviour(JSONUtilities.JSON.make_client_conformation_model_empty("Results from militia"));
            return ic;
        }
        return null;
    }
}
