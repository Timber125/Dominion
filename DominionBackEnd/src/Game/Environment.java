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
    public int[] standard_victorycard_counts = new int[3];
    public int[] standard_treasurecard_counts = new int[3];
    public HashMap<String, Integer> actioncards = new HashMap<>();
     
    public Environment(int player_count){
        this.trashpile = new ArrayList<Card>();
        this.tablecards = new ArrayList<Card>();
        this.actioncards = new HashMap<>();
        
        if(player_count == 2){
            standard_victorycard_counts[0] = 8;
            standard_victorycard_counts[1] = 8;
            standard_victorycard_counts[2] = 8;
            standard_treasurecard_counts[0] = 60;
            standard_treasurecard_counts[1] = 50;
            standard_treasurecard_counts[2] = 40;
        }
    }

    public void cardPlayed(Card c){
        tablecards.add(c);
    }
    
    public Environment(int player_count, ArrayList<Card> game) {
        // Not implemented yet: 
            // Purpose = create a game with specified action-cards to buy. 
    }
}
