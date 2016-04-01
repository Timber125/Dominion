/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import Cards.Card;

/**
 *
 * @author admin
 */
public class Player {
    protected String mySession; 
    public Deck deck;
    public Player(String session){
        mySession = session;
        deck = new Deck(1); // deck-protocol-1: see deck-constructor(s) for explenation. 
    }
    public Card drawCard(){
        if(deck == null) return null;
        else return deck.getNext();
    }
}
