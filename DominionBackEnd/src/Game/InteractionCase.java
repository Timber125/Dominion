/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import Cards.Components.ActionCard;
import Cards.Components.Card;
import Cards.Components.TreasureCard;
import Cards.Components.VictoryCard;
import java.util.ArrayList;

/**
 *
 * @author admin
 */
public class InteractionCase extends SpecialCase{
    
    private ArrayList<Card> selectedSpecials = new ArrayList<>();
    private ArrayList<Long> selectedIds = new ArrayList<>();
    private Player initiator;
    private Player victim;
    
    private int minimum_amount = 0;
    private int maximum_amount = 0;
    
    private int minimum_cost = 0;
    private int maximum_cost = 0;
    
    private boolean treasure_env_enabled = false;
    private boolean treasure_hand_enabled = false;
    
    private boolean victory_env_enabled = false;
    private boolean victory_hand_enabled = false;
    
    private boolean action_env_enabled = false;
    private boolean action_hand_enabled = false;
    
    private boolean reaction_only = false;
    
    private boolean blockable = false;
    
    public InteractionCase(Player victim, Player initiator){
        super(victim);
        this.initiator = initiator;
    }
    public InteractionCase(Player victim){
        super(victim);
        // Initialize initiator and victim!!!
    }
    public void setInitiator(Player p){
        this.initiator = p;
    }
    public Player getInitiator(){
        return this.initiator;
    }
    public boolean isValid(){
        if(reaction_only){
            for(Card c : selectedSpecials) if(!c.is_block()) return false;
            return true;
        }
        if((selectedSpecials.size() >= minimum_amount)&&(selectedSpecials.size() <= maximum_amount)){
            for(Card c : selectedSpecials) if((c.getCost() > maximum_cost)&&(c.getCost() < minimum_cost)) return false;
            return true;
        }else{
            return false;
        }
    }
    public void set_reaction_only(){
        reaction_only = true;
    }
    public void unset_reaction_only(){
        reaction_only = false;
    }
    public void setMinCost(int min){
        minimum_cost = min;
    }
    public void setMaxCost(int max){
        maximum_cost = max;
    }
    public void setMinAmount(int min){
        minimum_amount = min;
    }
    public void setMaxAmount(int max){
        maximum_amount = max;
    }
    public void enableAllTreasures(){
        treasure_env_enabled = true;
        treasure_hand_enabled = true;
    }
    public void disableAllTreasures(){
        treasure_env_enabled = false;
        treasure_hand_enabled = false;
    }
    public void enableAllVictories(){
        victory_env_enabled = true;
        victory_hand_enabled = true;
    }
    public void disableAllVictories(){
        victory_env_enabled = false;
        victory_hand_enabled = false;
    }
    public void enableAllActions(){
        action_env_enabled = true;
        action_hand_enabled = true;
    }
    public void disableAllActions(){
        action_env_enabled = false;
        action_hand_enabled = false;
    }
    public void enableHandTreasure(){
        treasure_hand_enabled = true;
    }
    public void disableHandTreasure(){
        treasure_hand_enabled = false;
    }
    public void enableEnvTreasure(){
        treasure_env_enabled = true;
    }
    public void disableEnvTreasure(){
        treasure_env_enabled = false;
    }
    public void enableHandVictories(){
        victory_hand_enabled = true;
    }
    public void disableHandVictories(){
        victory_hand_enabled = false;
    }
    public void enableEnvVictories(){
        victory_env_enabled = true;
    }
    public void disableEnvVictories(){
        victory_env_enabled = false;
    }
    public void enableHandActions(){
        action_hand_enabled = true;
    }
    public void disableHandActions(){
        action_hand_enabled = false;
    }
    public void enableEnvActions(){
        action_env_enabled = true;
    }
    public void disableEnvActions(){
        action_env_enabled = false;
    }
    public void enable_blockable(){
        blockable = true;
    }
    public void disable_blockable(){
        blockable = false;
    }
    public void enable_hand(){
        action_hand_enabled = true;
        victory_hand_enabled = true;
        treasure_hand_enabled = true;
    }
    public void disable_hand(){
        action_hand_enabled = false;
        victory_hand_enabled = false;
        treasure_hand_enabled = false;
    }
    public void enable_environment(){
        action_env_enabled = true;
        victory_env_enabled = true;
        treasure_env_enabled = true;
    }
    public void disable_environment(){
        action_env_enabled = false;
        victory_env_enabled = false;
        treasure_env_enabled = false;
    }
    public ArrayList<Card> getSelected(){
        return selectedSpecials;
    }
    public boolean process(Card c, boolean fromHand, long id){
        if(reaction_only){
            if(c.is_block()){
                selectedSpecials.add(c);
                selectedIds.add(id);
                return true;
            }
            else return false;
        }
        String name = c.getName();
        int cost = c.getCost();
        if((cost > maximum_cost) || (cost < minimum_cost)) return false;
        if(selectedSpecials.size() >= maximum_amount) return false;
        
        if(fromHand){
            if(selectedIds.contains(id)) return false; // Card has been processed already. 
            
            if(c instanceof ActionCard){
                if(!action_hand_enabled) return false;
            }else if(c instanceof TreasureCard){
                if(!treasure_hand_enabled) return false;
            }else if(c instanceof VictoryCard){
                if(!victory_hand_enabled) return false;
            }
        }else{
            if(c instanceof ActionCard){
                if(!action_env_enabled) return false;
            }else if(c instanceof TreasureCard){
                if(!treasure_env_enabled) return false;
            }else if(c instanceof VictoryCard){
                if(!victory_env_enabled) return false;
            }
        }
        
        // If this code is reached, all validations have passed, and nothing returned false.
        selectedSpecials.add(c);
        selectedIds.add(id);
        return true;
    }
    
    public void manually_add_selectedspecial(Card c, boolean fromhand){
        selectedSpecials.add(c);
        selectedIds.add(0L + selectedSpecials.size()); // Manual iterative id's
    }
    
    
}
