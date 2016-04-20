/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Cards.Components;

import Actions.Action;
import Actions.ActionTest;
import java.util.ArrayList;

/**
 *
 * @author admin
 */
public abstract class Card {
    public String name;
    public int cost;
    public ArrayList<Action> actions;
    public Card(String name, int cost){
        this.name = name;
        this.cost = cost;
        this.actions = new ArrayList<>();
        this.init_standard_actions();
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
}
