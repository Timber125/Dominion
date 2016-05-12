/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominion.DynamicCard;

import Client.JSonFactory;
import Client.ServiceModel;
import Dominion.DynamicStack.Stack;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class ClientModelService extends ServiceModel{
    static final ArrayList<String> keywordprototype = new ArrayList<>();
    /*
        Sorry dak het zo ingewikkeld maak overal, 't is een gewoonte :p
        dit static blok wordt opgeroepen bij compilatie, dus nog voor de main start, 
        is keywordprotype al ge'initialiseerd' met dit static blok, zodat als de
        constructor ClientModelService() wordt opgeroepen, keywordprototype al gemaakt is,
        en we kunnen voldoen aan de restrictie dat super(keywords) het eerste statement moet zijn
        van de constructor. Omdat het een ServiceModel extend, en servicemodel moet een
        arraylist van keywords meekrijgen om geinitialiseerd te worden. 
    */
    static{
        keywordprototype.add("graphics");
        keywordprototype.add("dominion");
    }
    
    private final ClientControlV2 controller;
    
    protected ArrayList<Card> currentHand = new ArrayList<>();
    protected HashMap<String, Stack> environment = new HashMap<>();
    
    
    public ClientModelService(ClientControlV2 controller) {
        super(keywordprototype);
        this.controller = controller;
    }

    @Override
    public void handle(String json_stringified) {
        JSONObject obj = JSonFactory.JSON.toJSON(json_stringified);
        String action = obj.getString("act");
        switch(action){
            case("gain"):{
                String cardname = obj.getString("gain");
                final Card c = new Card(cardname, controller);
                currentHand.add(c);
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        controller.addCardToHand(c.getView());
                    }
                });
                break;
            }
            case("lose"):{
                final String cardname = obj.getString("lose");
                final Long id = Long.valueOf(obj.getString("loseID"));
                // Current implementation: Lose the first card with the same cardname. 
                // Later implemantation: Lose the exact card that you selected
                //                  You can verify this by checking the ID of the clicked card
                if(cardname.equals("all")){
                    currentHand.clear();
                    Platform.runLater(new Runnable(){

                        @Override
                        public void run() {
                            controller.refreshHandView();
                        }
                        
                    });
                    
                    break;
                }
                
                ArrayList<Card> nextHand = new ArrayList<>();
                boolean extracted = false;
                for(Card c : currentHand){
                    if(!extracted){
                        if(c.getName().equals(cardname) && c.getID().equals(id)){
                            extracted = true;
                            continue;
                        }
                    }
                    nextHand.add(c);
                }
                currentHand = nextHand;
                
                // Now lose all cards, and one by one add the remaining cards.
                Platform.runLater(new Runnable(){

                    @Override
                    public void run() {
                        controller.refreshHandView();
                        for(Card c : currentHand){
                            controller.addCardToHand(c.getView());
                        }
                    }
                    
                });
                break;
            }
            case("control"):{
                // Do some other shit
                handleControlPackage(obj);
                break;
            }
            case("turninfo"):{
                // Turninfo update
                updateTurnInfo(obj);
                break;
            }
            default:{
                System.err.println("Action not defined in clientmodelservice: [" + action + "].");
            }
        }
    }

    private void handleControlPackage(JSONObject obj) {
        String subject = obj.getString("subject");
        switch(subject){
            case("hand"):{
                String control = obj.getString("control");
                switch(control){
                    case("clickable"):{
                        ArrayList<Card> itemlist = new ArrayList<>();
                        if(obj.getString("items").equals("all")){
                            for(Card c : currentHand){
                                itemlist.add(c);
                            }
                        }
                        else{
                            final String[] items = obj.getString("items").split(",");
                            for(Card c : currentHand){
                                for(String name : items){
                                    if(c.getName().equals(name)){
                                        itemlist.add(c);
                                        break;
                                    }
                                }
                            }
                        }    
                        for(Card c : itemlist){
                            c.makeClickable();
                        }
                        
                        break;
                    }
                    case("unclickable"):{
                        ArrayList<Card> itemlist = new ArrayList<>();
                        if(obj.getString("items").equals("all")){
                            for(Card c : currentHand){
                                itemlist.add(c);
                            }
                        }
                        else{
                            final String[] items = obj.getString("items").split(",");
                            for(Card c : currentHand){
                                for(String name : items){
                                    if(c.getName().equals(name)){
                                        itemlist.add(c);
                                        break;
                                    }
                                }
                            }
                        }    
                        for(Card c : itemlist){
                            c.makeUnclickable();
                        }
                        
                        break;
                    }
                    default:{
                        System.out.println(control + " is an unknown control for subject " + subject);
                    }
                }
                break;
            }
            case("phase"):{
                String control = obj.getString("control");
                controller.EndPhase.setDisable(control.equals("unclickable"));
                break;
            }
            case("environment"):{
                String control = obj.getString("control");
                switch(control){
                    case("init"):{
                        if(obj.getString("stack").equals("Treasure")){
                            init_treasure();
                            Platform.runLater(new Runnable(){ 

                                @Override
                                public void run() {
                                    controller.initializeTreasureStack(environment.get("copper"), environment.get("silver"), environment.get("gold"));
                                }
                                
                            });
                            return;
                        }else if(obj.getString("stack").equals("Victory")){
                            init_victory();
                            Platform.runLater(new Runnable(){

                                @Override
                                public void run() {
                                    controller.initializeVictoryStack(environment.get("estate"), environment.get("duchy"), environment.get("province"));
                                }
                                
                            });
                            return;
                        }
                        // Initialize actioncards in environment
                        // Does not make use of "count" yet -> TODO!
                        final Card actionStack = new Card(obj.getString("stack"), controller, "MEDIUM");
                        final Stack realStack = new Stack(actionStack, 10);
                        environment.put(obj.getString("stack"), realStack);
                        
                        final Card disc_back = new Card("back", controller, "MEDIUM");
                        Stack discardpile;
                        discardpile = new Stack(disc_back, 0);
                        environment.put("discardpile", discardpile);
                        
                        final Card deck_back = new Card("back", controller, "MEDIUM");
                        Stack deck;
                        deck = new Stack(deck_back, 10);
                        environment.put("deck", deck);
                        
                        Platform.runLater(new Runnable(){

                            @Override
                            public void run() {
                                controller.initializeActionStack(realStack);
                                controller.initialize_myenvironment(environment.get("deck"), environment.get("discardpile"));
                            }
                        
                        });
                        break;
                    }
                    case("updatecount"):{
                        // Update count of environment cardstacks
                        final Stack st = environment.get(obj.getString("stack"));
                        final int count = Integer.parseInt(obj.getString("update"));
                        Platform.runLater(new Runnable(){

                            @Override
                            public void run() {
                                st.update(count);
                            }
                            
                        });
                        // If count == 0 -> should show the back.jpg of the card instead of the front. 
                        break;
                    }
                    case("clickable"):{
                        String names = obj.getString("items");
                        String[] namelist = names.split(",");
                        for(String cardname : namelist){
                            //environment.get(cardname).makeClickable();
                            // CAUSES BUG -> Imageviews cannot be made clickable, 
                            // Because the imageview does not link the view of the card. 
                            // Changing the cardview therefore will not have affected the glow
                            // And so the client crashes. 
                        }
                        break;
                    }
                    case("unclickable"):{
                        if(obj.getString("items").equals("all")){
                            for(String s : environment.keySet()){
                                //environment.get(s).makeUnclickable();
                                // See above bug.
                            }
                        }else{
                            System.err.println("NOT IMPLEMENTED YET -> unclickable environment specified cards");
                        }
                        break;
                    }
                    case("updateview"):{
                        String stackname = obj.getString("stack");
                        // Should be only used with discardpile atm 
                        
                        if(stackname.equals("discardpile")){
                            Stack s = environment.get(stackname);
                            String cardname = obj.getString("update");
                            Card medium_print = new Card(cardname, controller, "MEDIUM");
                            Stack replace = new Stack(medium_print, s.count);
                            environment.put(stackname, replace);
                            Platform.runLater(new Runnable(){

                                @Override
                                public void run() {
                                    controller.reinitialize_disc(environment.get("discardpile"));
                                }
                            
                            });
                        }else{
                            System.err.println("updateview on environment is only implemented (and only used) for discardpile.");
                        }
                        
                    }
                }
                break;
            }
            default:{
                System.err.println("Subject [" + subject +"] not implemented");
            }
        }
    }

    private void updateTurnInfo(JSONObject obj) {
        final int actions = Integer.parseInt(obj.getString("actioncount"));
        final int buys = Integer.parseInt(obj.getString("purchasecount"));
        final int coins = Integer.parseInt(obj.getString("money"));
        Platform.runLater(new Runnable(){

            @Override
            public void run() {
                controller.updateTurnInfo(actions, buys, coins);
            }
            
        });
    }
    
    
    
    public void cardBuy(Parent iv, String cardname){
        // Logic?
    }

    private void init_treasure() {
        Card copper = new Card("copper", controller);
        Card silver = new Card("silver", controller);
        Card gold = new Card("gold", controller);
        Stack copperstack = new Stack(copper, 60);
        Stack silverstack = new Stack(silver, 50);
        Stack goldstack = new Stack(gold, 40);
        /*environment.put("copper", copper);
        environment.put("silver", silver);
        environment.put("gold", gold);*/
        environment.put("copper", copperstack);
        environment.put("silver", silverstack);
        environment.put("gold", goldstack);
    }
    
    private void init_victory() {
        Card estate = new Card("estate", controller);
        Card duchy = new Card("duchy", controller);
        Card province = new Card("province", controller);
        /*environment.put("estate", estate);
        environment.put("duchy", duchy);
        environment.put("province", province);*/
        Stack estatestack = new Stack(estate, 8);
        Stack duchystack = new Stack(duchy, 8);
        Stack provincestack = new Stack(province, 8);
        environment.put("estate", estatestack);
        environment.put("duchy", duchystack);
        environment.put("province", provincestack);
    }

    
    
    
   
    
}
