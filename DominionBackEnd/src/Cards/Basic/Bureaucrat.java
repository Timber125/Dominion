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
public class Bureaucrat extends ActionCard {
    
    public Bureaucrat() {
        super("bureaucrat", 4);
    }
    
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
            RewardCase rc = new RewardCase(victim);
            rc.setRewardBehaviour(JSONUtilities.JSON.bureaucrat_standard_reward());
            return rc;
        }
        InteractionCase ic = new InteractionCase(victim, initiator);
        // This is an attack case. 
        if(victim.getReactions().isEmpty()){
            // The victim cannot block the attack.
            if(victim.getVictories().isEmpty()){
                // The victim has no victory cards, so must show his whole hand. 
                ic.setMinAmount(victim.hand.size());
                ic.setMaxAmount(100);
                ic.setMinCost(0);
                ic.setMaxCost(100);
                ic.enable_hand();
                // Allow numerical ids
                for(Card c : victim.hand){
                    ic.allowedIds.add("hand");
                    ic.preloadedCards.add(c);
                }
                ic.setFinishBehaviour(JSONUtilities.JSON.bureaucrat_victim_hasnovictorycard_finishbehaviour());
                ic.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_bureaucrat_victim_has_no_victorycard(victim, initiator, ic));

                return ic;
            }else{
                ic.enableHandVictories();
                ic.setMinAmount(1);
                ic.setMaxAmount(1);
                ic.setMinCost(0);
                ic.setMaxCost(100);
                // Allow numerical ids
                for(Card c : victim.getVictories()){
                    ic.allowedIds.add("hand");
                    ic.preloadedCards.add(c);
                }
                ic.setFinishBehaviour(JSONUtilities.JSON.bureaucrat_victim_hasvictorycard_finishbehaviour());
                ic.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_bureaucrat_victim_hasvictorycard(victim, initiator, ic));
                return ic;
            }
            
            
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
            ic.setMaxCost(0);
            // NOTHING ENABLED!
            //ic.enable_hand();
            //ic.enable_environment();
            ic.setFinishBehaviour(JSONUtilities.JSON.create_interaction_finishbehaviour_empty());
            ic.setStartBehaviour(JSONUtilities.JSON.make_client_conformation_model_empty("Results from bureaucrat"));
            return ic;
        }
        return null;
    }
}
