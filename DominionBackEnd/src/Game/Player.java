/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import Cards.Components.ActionCard;
import Cards.Components.Card;
import java.util.ArrayList;

/**
 *
 * @author admin
 */
public class Player {
    protected String mySession; 
    public Deck deck;
    public ArrayList<Card> hand = new ArrayList<>();
    public Player(String session){
        mySession = session;
        //deck = new Deck(1); // deck-protocol-1: see deck-constructor(s) for explenation. 
        deck = new Deck(0); // Standard deck (protocol-0)
    }
    public Card drawCard(){
        Card drawn = null;
        if(deck == null) return null;
        else {
            drawn = deck.getNext();
            hand.add(drawn);
            return drawn;
        }
    }
    public void discardHand(){
        ArrayList<Card> handreplica = new ArrayList<>();
        handreplica.addAll(hand);
        for(Card c : handreplica){
            deck.used.add(c);
            hand.remove(c);
        }
    }
    public boolean hasCard(String cardname){
        for(Card c : hand){
            if(c.name.equals(cardname)) return true;
        }
        return false;
    }
    public Card playCard(Environment env, String cardname, long id){
        // Id is unused, should be checked whether same id gets played twice
        ArrayList<Card> nextHand = new ArrayList<>();
        boolean extracted = false;
        Card played = null;
        for(Card c : hand){
            if(!extracted){
                if(c.name.equals(cardname)){
                    extracted = true;
                    env.cardPlayed(c);
                    played = c;
                    continue;
                }
            }
            nextHand.add(c);
        }
        hand = nextHand;
        return played;
    }

    public boolean hasActionCard() {
        for(Card c : hand){
            if(c instanceof ActionCard) return true;
        }
        return false;
    }
}
