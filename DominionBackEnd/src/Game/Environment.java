/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import Cards.Components.Card;
import Cards.Components.TreasureCard;
import Cards.Components.VictoryCard;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author admin
 */
public class Environment {
    public ArrayList<Card> trashpile;
    public ArrayList<Card> tablecards;
    /* Should have their own classes, extending Arraylist<T> */
    public HashMap<Integer, ArrayList<Card>> gamecards;
    public HashMap<Integer, ArrayList<VictoryCard>> victorycards;
    public HashMap<Integer, ArrayList<TreasureCard>> treasurecards;
    
    public Environment(int player_count){
        this.trashpile = new ArrayList<Card>();
        this.tablecards = new ArrayList<Card>();
        this.gamecards = new HashMap<>();
        this.victorycards = new HashMap<>();
        this.treasurecards = new HashMap<>();
    }

    public void cardPlayed(Card c){
        tablecards.add(c);
    }
    
    public Environment(int player_count, ArrayList<Card> game) {
        // Not implemented yet: 
            // Purpose = create a game with specified action-cards to buy. 
    }
}
