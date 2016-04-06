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
    
    private ClientControlV2 controller;
    
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
                final String cardname = obj.getString("gain");
                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        controller.addCardToHand(cardname);
                    }
                });
                break;
            }
            default:{
                System.err.println("Action not defined in clientmodelservice: [" + action + "].");
            }
        }
    }
    
}
