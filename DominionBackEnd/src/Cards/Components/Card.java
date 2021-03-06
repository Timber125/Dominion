/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Cards.Components;


import Game.Environment;
import Game.InteractionCase;
import Game.Player;
import Game.SpecialCase;
import java.util.ArrayList;

/**
 *
 * @author admin
 */
public abstract class Card {
    public String name;
    public int cost;
    public Card(String name, int cost){
        this.name = name;
        this.cost = cost;
        this.init_standard_actions();
    }
    
    public int getCost(){
        return cost;
    }
    public String getName(){
        return name;
    }

    // Standard actions of this will be nothing, but an Action Card-standard action 
    // would be something like 'Player who plays this card -> actions = actions - 1 ' 
    // The specific card will add its specific actions later. 
    private void init_standard_actions() {
        // Nothing
    }
    
    public int moneygain(){
        return 0;
    }
    public int actiongain(){
        return 0;
    }
    public int purchasegain(){
        return 0;
    }
    public int victorygain(){
        return 0;
    }
    public int cardgain(){
        return 0;
    }
    public SpecialCase special(Player victim, Player initiator, Environment env){
        return null;
    }
    public SpecialCase futurespecial(Player victim, Player initiator, Environment env){
        return null;
    }
    public boolean hasSpecial(){
        return false;
    }
    public boolean is_attack(){
        return false;
    }
    public boolean is_block(){
        return false;
    }
    public InteractionCase future_futureSpecial(Player victim, Player initiator, Environment env, InteractionCase prev){
        return null;
    }
}
// MY VERSION