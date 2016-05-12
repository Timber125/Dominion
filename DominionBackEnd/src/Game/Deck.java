/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import Cards.Basic.*;
import Cards.Components.*;
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
            case(0):{
                // Standard start-deck.
                int estate = 3;
                int copper = 7;
                for(int es = 0; es < estate; es++){
                    used.add(new Estate());
                }
                for(int co = 0; co < copper; co++){
                    used.add(new Copper());
                }
                break;
            }
            
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
    /*
        Add to discard pile
    */
    public void add(Card c){
        used.add(c);
    }
    
    public void addBottomOfDeck(Card c){
        content.add(c);
    }
    public void addBottomOfDeck(ArrayList<Card> cards){
        for(Card ca : cards){
            content.add(ca);
        }
    }
    public void addTopOfDeck(Card c){
        ArrayList<Card> newDeckOrder = new ArrayList<>();
        newDeckOrder.add(c);
        for(Card ca : content){
            newDeckOrder.add(ca);
        }
        this.content = newDeckOrder;
    }
    public void addTopOfDeck(ArrayList<Card> cards){
        ArrayList<Card> newDeckOrder = new ArrayList<>();
        for(Card ca : cards){
            newDeckOrder.add(ca);
        }
        for(Card ca : content){
            newDeckOrder.add(ca);
        }
        this.content = newDeckOrder;
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
