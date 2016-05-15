/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import Cards.Basic.*;
import Cards.Components.Card;
import Server.RTIUtilities;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author admin
 */
public class Environment {
     /* RTI Utilities, should only be used in engine */
    final private RTIUtilities RTI = new RTIUtilities();
    
    
    public ArrayList<Card> trashpile;
    public ArrayList<Card> tablecards;
    
    /* Should have their own classes, extending Arraylist<T> */
    public HashMap<String, ArrayList<Card>> environment_library = new HashMap<>();
    
    public Environment(int player_count){
        this.trashpile = new ArrayList<Card>();
        this.tablecards = new ArrayList<Card>();
        ArrayList<Card> estates = new ArrayList<>();
        ArrayList<Card> duchys = new ArrayList<>();
        ArrayList<Card> provinces = new ArrayList<>();
        ArrayList<Card> coppers = new ArrayList<>();
        ArrayList<Card> silvers = new ArrayList<>();
        ArrayList<Card> golds = new ArrayList<>();
        if(player_count == 2){
            for(int i = 0; i < 8; i++){
                estates.add(new Estate());
                duchys.add(new Duchy());
                provinces.add(new Province());
            }
            for(int i = 0; i < 40; i++){
                coppers.add(new Copper());
                silvers.add(new Silver());
                golds.add(new Gold());
            }
            for(int i = 0; i < 10; i++){
                coppers.add(new Copper());
                silvers.add(new Silver());
            }
            for(int i = 0; i < 10; i++){
                coppers.add(new Copper());
            }
            
            environment_library = new HashMap<>();
            environment_library.put("copper", coppers);
            environment_library.put("silver", silvers);
            environment_library.put("gold", golds);
            environment_library.put("estate", estates);
            environment_library.put("duchy", duchys);
            environment_library.put("province", provinces);
            
            /*
                We have no other actioncards but these, so we kinda force environment 
                without further specifications to use these.
            
                add actioncards by: increasing num_actioncards, and adding the name of the 
                card to the actions[] array. 
            
            */
            int num_cards_per_stack = 10;
            int num_actioncards = 5;
            String[] actions = new String[num_actioncards];
            actions[0] = "market";
            actions[1] = "village";
            actions[2] = "councilroom";
            actions[3] = "laboratory";
            actions[4] = "woodcutter";
            
            // Auto-setup
            
            ArrayList<Card>[] actioncards = new ArrayList[num_actioncards];
            for(int i = 0; i < num_actioncards; i++){
                actioncards[i] = new ArrayList<>();
            }
            
            for(int i = 0; i < num_cards_per_stack; i ++){
                for(int j = 0; j < num_actioncards; j++){
                    actioncards[j].add(RTI.getCardByName(actions[j]));
                }
            }
            
            for(int i = 0; i < num_actioncards; i++){
                environment_library.put(actions[i], actioncards[i]);
            }
            
            
        }else{
            System.err.println("3 PLAYERS OR MORE NOT IMPLEMENTED IN ENVIRONMENT!");
        }
    }
    public int environment_amountcheck(String name){
        return environment_library.get(name).size();
    }
    public int environment_pricecheck(String name){
        ArrayList<Card> cardstack = environment_library.get(name);
        if(cardstack.isEmpty()) return -1;
        else return cardstack.get(0).cost;
    }
    public Card environment_buy(String name){
        ArrayList<Card> cardstack = environment_library.get(name);
        if(cardstack.isEmpty()) return null;
        else{
            Card drawn = cardstack.get(0);
            cardstack.remove(0);
            environment_library.put(name, cardstack);
            return drawn;
        }
    }
    

    public void cardPlayed(Card c){
        tablecards.add(c);
    }
    
    public Environment(int player_count, ArrayList<Card> game) {
        // Not implemented yet: 
            // Purpose = create a game with specified action-cards to buy. 
    }
    public String getAllBuyables(int maxmoney){
        String s = "";
        for(String key : environment_library.keySet()){
            int c = environment_library.get(key).get(0).getCost();
            if(maxmoney >= c) s += "," + environment_library.get(key).get(0).getName();
        }
        if(s.length() > 1) return s.substring(1);
        else return "";
    }
}
