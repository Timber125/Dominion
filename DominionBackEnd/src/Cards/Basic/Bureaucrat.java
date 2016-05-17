/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cards.Basic;

import Cards.Components.ActionCard;
import Cards.Components.Card;
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
    public SpecialCase special(Player victim, Player initiator){
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
                ic.setMinAmount(0);
                ic.setMaxAmount(100);
                ic.setMinCost(0);
                ic.setMaxCost(100);
                ic.enable_hand();
                for(Card c : victim.hand){
                    ic.manually_add_selectedspecial(c, true);
                }
                return ic;
            }else{
                ic.enableHandVictories();
                ic.setMinAmount(1);
                ic.setMaxAmount(1);
                ic.setMinCost(0);
                ic.setMaxCost(100);
                return ic;
            }
            
            
        }else{
            // The victim blocks the attack, and shows his reactioncard. 
            ic.setMinAmount(1);
            ic.setMaxAmount(1);
            ic.setMinCost(0);
            ic.setMaxCost(100);
            ic.set_reaction_only();
            
            return ic;
        }
        // No return needed: all cases return something.
    }
    //todo: gain a silver card, put it on top of your deck, each other player reveals a victory card and puts it on top of their deck
    
}
