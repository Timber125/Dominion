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
public class Witch extends ActionCard{
    
    public Witch() {
        super("witch", 5);
    }
    
    @Override
    public int cardgain(){
        return 2;
    }
    
    // TODO
    //EACH OTHER PLAYER ADDS A CURSE TO THEIR DISCARDPILE
    //IF NOT BLOCKED
    
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
                Card c = env.environment_buy("curse");
                if(c == null) return null;
                ic.allowedIds.add("other");
                ic.preloadedCards.add(c);
                ic.selectedSpecials.add(c);
                ic.selectedIds.add("other");
                ic.setFinishBehaviour(JSONUtilities.JSON.witch_standard_finishbehaviour());
                ic.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_witch_notblocked(victim, initiator, ic));

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
            ic.setMaxAmount(0);
            ic.setMinCost(0);
            ic.setMaxCost(100);
            // NOTHING ENABLED!
            //ic.enable_hand();
            //ic.enable_environment();
            ic.setFinishBehaviour(JSONUtilities.JSON.create_interaction_finishbehaviour_spy());
            ic.setStartBehaviour(JSONUtilities.JSON.make_client_conformation_model_empty("Results from witch."));
            return ic;
        }
        return null;
    }
}
