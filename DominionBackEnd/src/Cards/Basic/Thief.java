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
public class Thief extends ActionCard {
    
    
    
    public Thief() {
        super("thief", 4);
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
        InteractionCase ic = new InteractionCase(victim, initiator);
        // This is an attack case. 
        if(victim.getReactions().isEmpty()){
            // The victim cannot block the attack.
                ic.setMinAmount(2);
                ic.setMaxAmount(2);
                ic.setMinCost(0);
                ic.setMaxCost(100);
                ic.enable_environment();
                ic.enable_hand();
                // Allow numerical ids
                Card c = victim.deck.getNext();
                ic.preloadedCards.add(c);
                ic.selectedSpecials.add(c);
                ic.selectedIds.add("other");
                ic.allowedIds.add("other");
                
                c = victim.deck.getNext();
                ic.preloadedCards.add(c);
                ic.selectedSpecials.add(c);
                ic.selectedIds.add("other");
                ic.allowedIds.add("other");
             
                 // Two top of deck cards added
                ic.setFinishBehaviour(JSONUtilities.JSON.spy_standard_finishbehaviour());
                ic.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_thief_notblocked(victim, initiator, ic));

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
            ic.enableEnvTreasure();
            ic.allowedIds.add("other_0");
            ic.allowedIds.add("other_1");
            ic.allowedIds.add("other_2");
            ic.allowedIds.add("other_3");
            // NOTHING ENABLED!
            //ic.enable_hand();
            //ic.enable_environment();
            ic.setFinishBehaviour(JSONUtilities.JSON.create_interaction_finishbehaviour_thief());
            ic.setStartBehaviour(JSONUtilities.JSON.make_client_conformation_model_empty("Results from thief: select up to 1 treasure card from each victim. The victim will trash this card. You may then gain any of these cards. The other cards will be discarded."));
            return ic;
        }
        return null;
    }
    
    @Override
    public InteractionCase future_futureSpecial(Player victim, Player initiator, Environment env, InteractionCase prev){
        //if(interactionFinished) return null;
        if(victim.getSession().equals(initiator.getSession())){
            InteractionCase ic = new InteractionCase(victim, initiator);
            // If we already have 7 cards, return null.
            
            for(int ix = 0; ix < prev.selectedSpecials.size(); ix++){
                String id = prev.selectedIds.get(ix);
                Card c = prev.selectedSpecials.get(ix);
                ic.preloadedCards.add(c);
                ic.allowedIds.add(id);
            }
            
                    ic.setMinAmount(0);
                    ic.setMaxAmount(prev.selectedSpecials.size());
                    ic.setMaxCost(100);
                    ic.setMinCost(0);
                    ic.enableAllTreasures();
                    ic.setFinishBehaviour(JSONUtilities.JSON.thief_future_finished_finishbehaviour());
                    ic.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_thief_future_finish(victim, initiator, ic));
                    //interactionFinished = true; // This was the last interaction
                    victim.setIterativeInteractionFactory(null); // Stop iteration
                    
                    return ic;
           
        }else{
            return null;
        }
    }
    
}
