/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominion.DynamicCard;

import Client.JSonFactory;
import Client.ServiceModel;
import java.util.ArrayList;
import javafx.application.Platform;
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
    
    
    
    public void cardBuy(ImageView iv, String cardname){
        // Logic?
    }

    
    
    
   
    
}
