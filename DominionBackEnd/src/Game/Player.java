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
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class Player {
    private String mySession; 
    public Deck deck;
    public ArrayList<Card> hand = new ArrayList<>();
    private InteractionCase current_interaction = null;
    private InteractionCase future_interaction = null;
    private Card iterative_interaction_factory = null;
    
    
    public Player(String session){
        mySession = session;
        //deck = new Deck(1); // deck-protocol-1: see deck-constructor(s) for explenation. 
        deck = new Deck(0); // Standard deck (protocol-0)
    }
    // unsafe?
    public String getSession(){
        return mySession;
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
    public Card takeCardFromDeck(){
        Card drawn = null;
        if(deck == null) return null;
        else{
            drawn = deck.getNext();
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
    public Card loseCard(String cardname, long id){
        ArrayList<Card> nextHand = new ArrayList<>();
        boolean extracted = false;
        Card lost = null;
        for(Card c : hand){
            if(!extracted){
                if(c.name.equals(cardname)){
                    extracted = true;
                    lost = c;
                    continue; // break?!
                }
            }
            nextHand.add(c);
        }
        hand = nextHand;
        return lost;
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
    
    public ArrayList<Card> getTreasures(){
        ArrayList<Card> result = new ArrayList<>();
        for(Card c : hand){
            if(c instanceof TreasureCard) result.add(c);
        }
        return result;
    }
    public ArrayList<Card> getVictories(){
        ArrayList<Card> result = new ArrayList<>();
        for(Card c : hand){
            if(c instanceof VictoryCard) result.add(c);
        }
        return result;
    }
    public ArrayList<Card> getActions(){
        ArrayList<Card> result = new ArrayList<>();
        for(Card c : hand){
            if(c instanceof ActionCard) result.add(c);
        }
        return result;
    }
    public ArrayList<Card> getReactions(){
        ArrayList<Card> result = new ArrayList<>();
        for(Card c : hand){
            if(c.is_block()) result.add(c);
        }
        return result;
    }
    
    public int calculateVictoryPoints(){
        int cards;
        int points = 0;
        ArrayList<Card> total = new ArrayList<>();
        total.addAll(deck.used);
        total.addAll(deck.content);
        total.addAll(hand);
        cards = total.size();
        
        for(Card c : total){
            if(c instanceof VictoryCard){
                points += c.victorygain();
                if(c.hasSpecial()){
                    SpecialCase spec = c.special(this, this, null);
                    if(spec instanceof RewardCase){
                        RewardCase reward =(RewardCase) spec;
                        points += handleVictoryRewardCase(reward.reward_behaviour());
                    }
                }
            }
        }
        return points;
    }
    public ArrayList<Card> getAllVictoryCards(){
        int cards;
        ArrayList<Card> victories = new ArrayList<>();
        ArrayList<Card> total = new ArrayList<>();
        total.addAll(deck.used);
        total.addAll(deck.content);
        total.addAll(hand);
        cards = total.size();
        
        for(Card c : total){
            if(c instanceof VictoryCard){
                victories.add(c);
            }
        }
        return victories;
    }
    public int numberOfTotalCards(){
        ArrayList<Card> total = new ArrayList<>();
        total.addAll(deck.used);
        total.addAll(deck.content);
        total.addAll(hand);
        return total.size();
    }
    private int handleVictoryRewardCase(JSONObject behaviour){
        if(behaviour.getString("type").equals("victory_gain")){
            if(behaviour.getString("amount").equals("based_on_deck")){
                
                int frac = Integer.parseInt(behaviour.getString("fraction"));
                int decksize = numberOfTotalCards();
                
                if(behaviour.getString("rounding").equals("down")){
                    return (int) Math.floor(decksize/frac);
                }
                else{
                    return (int) Math.ceil(decksize/frac);
                }
            }else{
                return 0;
            }
        }else{
            return 0;
        }
    }
    
    public void setInteraction(InteractionCase ic){
        this.current_interaction = ic;
    }
    public InteractionCase getCurrentInteraction(){
        return this.current_interaction;
    }
    
    public void setFutureInteraction(InteractionCase ic){
        this.future_interaction = ic;
    }
    public InteractionCase getFutureInteraction(){
        return this.future_interaction;
    }
    public InteractionCase getIterativeInteraction(InteractionCase prev, Environment env){
        if(iterative_interaction_factory == null) return null;
        return iterative_interaction_factory.future_futureSpecial(this, this, env, prev);
    }
    public void setIterativeInteractionFactory(Card c){
        iterative_interaction_factory = c;
    }
    public void deleteIterativeInteractionFactory(){
        iterative_interaction_factory = null;
    }
    public boolean hasIterativeInteractionFactory(){
        return (iterative_interaction_factory != null);
    }
}
// MY VERSION