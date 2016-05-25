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
import Game.RewardCase;
import Game.SpecialCase;
import Server.JSONUtilities;

/**
 *
 * @author Jordy School
 */
public class Spy extends ActionCard{
    
    public Spy() {
        super("spy", 4);
        
    }
    
    @Override
    public int actiongain(){
        return 1;
    }
    
    @Override
    public int cardgain(){
        return 1;
    }
    
    //todo: each player reveals the top card of his deck, and you choose wether to discard or keep it
    
    @Override
    public boolean hasSpecial(){
        return true;
    }
    @Override
    public SpecialCase special(Player victim, Player initiator, Environment env){
        if(victim.getSession().equals(initiator.getSession())){
            return null;
        }
        InteractionCase ic = new InteractionCase(victim, initiator);
        // This is an attack case. 
        if(victim.getReactions().isEmpty()){
            // The victim cannot block the attack.
                ic.setMinAmount(1);
                ic.setMaxAmount(1);
                ic.setMinCost(0);
                ic.setMaxCost(100);
                ic.enable_environment();
                ic.enable_hand();
                // Allow numerical ids
                Card c = victim.deck.getNext();
                ic.allowedIds.add("other");
                ic.preloadedCards.add(c);
                ic.selectedSpecials.add(c);
                ic.selectedIds.add("other");
                ic.setFinishBehaviour(JSONUtilities.JSON.spy_standard_finishbehaviour());
                ic.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_spy_notblocked(victim, initiator, ic));

                return ic;            
        }else{
            // The victim blocks the attack, and shows his reactioncard. 
            ic.setMinAmount(1);
            ic.setMaxAmount(1);
            ic.setMinCost(0);
            ic.setMaxCost(100);
            ic.set_reaction_only();
            for(Card c : victim.getReactions()){
                ic.allowedIds.add("hand");
                ic.preloadedCards.add(c);
            }
            // Numerical ids
            ic.setFinishBehaviour(JSONUtilities.JSON.create_interaction_blocked_finishbehaviour());
            ic.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_attackblock(victim, initiator, ic));
            return ic;
        }
        // No return needed: all cases return something.
    }
    @Override
    public InteractionCase futurespecial(Player victim, Player initiator, Environment env){
        if(victim.getSession().equals(initiator.getSession())){
            InteractionCase ic = new InteractionCase(victim, initiator);
            ic.setMinAmount(0);
            ic.setMaxAmount(3);
            ic.setMinCost(0);
            ic.setMaxCost(100);
            ic.enable_environment();
            ic.allowedIds.add("other_0");
            ic.allowedIds.add("other_1");
            ic.allowedIds.add("other_2");
            ic.allowedIds.add("other_3");
            // NOTHING ENABLED!
            //ic.enable_hand();
            //ic.enable_environment();
            ic.setFinishBehaviour(JSONUtilities.JSON.create_interaction_finishbehaviour_spy());
            ic.setStartBehaviour(JSONUtilities.JSON.make_client_conformation_model_empty("Results from spy: select the cards you want your victims to discard."));
            return ic;
        }
        return null;
    }
    
}
