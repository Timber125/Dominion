/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import Cards.Card;
import Cards.Market;
import Cards.Village;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author admin
 */
public class Deck {
    public ArrayList<Card> content;
    public ArrayList<Card> used;
    public Deck(){
        constructorCode();
    }
    
    private void constructorCode(){
        content = new ArrayList<>();
        used = new ArrayList<>();
    }
    
    public Deck(int testCase){
        // Give testcase-numbers to create a specific deck. 
        // First test-deck: 
            // testCase == 1
        constructorCode();
        switch(testCase){
            case(1):{
                // Create a deck of 3 villages & 3 markets, place in used-pile
                used.add(new Village());
                used.add(new Village());
                used.add(new Market());
                used.add(new Village());
                used.add(new Market());
                used.add(new Market());
                break;
            }
            default:{
                constructorCode();
            }
        }
    }
    
    public void add(Card c){
        used.add(c);
    }
    
    public void reshuffle(){
        content.addAll(used);
        Collections.shuffle(content);
        used.clear();
    }
    
    public Card getNext(){
        if(content.size() >= 1){
            Card c = content.get(0);
            content.remove(0);
            return c;
        }else{
            if(used.size() >= 1){
                reshuffle();
                return getNext();
            }else{
                System.err.println("You don't have anymore cards!");
                return null;
            }
        }
    }
    
}
