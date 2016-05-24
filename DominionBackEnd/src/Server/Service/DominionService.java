/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server.Service;

import Game.Engine;
import Server.JSONUtilities;
import Server.Server;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class DominionService extends Service{
    
    public boolean active;
    
    private Engine game;

    public DominionService(Server server){
        super(server);
        known_service_types.add("dominion");
        active = false; // initialize inactive
        // Could initliaize active, lobbyservice deactivates this either way. 
    }
    
    protected Engine getEngine(){
        return game;
    }
    
    public void activate(){
        if(game != null) active = true;
        else {
            System.err.println("Tried to activate dominionservice but game-engine is null");
        }
    }
    
    public void deactivate(){
        active = false;
    }
    
    @Override
    public void handleType(String type, JSONObject json) {
        if(!active){
            server.getClient(json.getString("session")).write(gameNotStartedMessage());
            return;
        }
        // Service_type = dominion. 
        // Implemented requests:
        // service_type="dominion", phase="draw", operation="draw_cards", repeat="5"
        // service_type="dominion", phase="draw", operation="finish"
        // service_type=dominion, phase="draw", operation="discard_hand", repeat="1"
        // service_type=dominion, phase="draw", operation="shuffle_deck_graveyard", repeat="1"
        if(type.equals("dominion")){
            switch(json.getString("operation")){
                case("cardoffer"):{
                    game.processCard(json);
                    break;
                }
                case("endphase"):{
                    game.nextPhase(json.getString("session"));
                    break;
                }
                case("buy"):{
                    game.processBuyRequest(json);
                    break;
                }
                case("interaction_confirm"):{
                    game.interactionConfirmRequest(json);
                    break;
                }
                case("askInfo"):{
                    game.showPlayerInfoTo(json.getString("session"));
                    break;
                }
                default:{
                    System.out.println("Unknown operation: [" + json.getString("operation") + "]");
                }
            }
        }else{
            System.out.println("Dominion service doesn't know how to handle 'all' addressing. Implement me!");
        }
        
    }
    
    public void actionPhase(JSONObject json){
        String operation = json.getString("operation");
        String session = json.getString("session");
        
    }
    
    public void drawPhase(JSONObject json){
        // service_type = dominion
        // phase = draw
        String operation = json.getString("operation");
        String identification = json.getString("session"); // REALLY REALLY REALLY INSECURE
        // Identification should be client's registered name, injected in this json based on its session token. 
        // Do not send any messages to clients with this information!
        switch(operation){
            case("finish"):{
                // Serverside print!
                System.out.println("Player " + "[" + identification + "]" + " finished his turn.");
                return;
            }
            case("draw_cards"):{
                // Serverside print!
                System.out.println("Player " + "[" + identification + "]" + " draws " + json.getString("repeat") + " cards.");
                return;
            }
            case("discard_hand"):{
                System.out.println("Player " + "[" + identification + "]" + " discards his hand.");
                return;
            }
            case("shuffle_deck_graveyard"):{
                System.out.println("Player " + "[" + identification + "]" + " shuffles his graveyard into his deck.");
                return;
            }
            default:{
                System.out.println("Operation [" + operation + "] not implemented in draw phase.");
            }
        }
    }
    
    
    private JSONObject gameNotStartedMessage(){
        JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
        obj = JSONUtilities.JSON.addKeyValuePair("sysout", "The game hasn't started yet.", obj);
        return obj;
    }

    
    
    
    
    
    
    
    
    
    
   
    protected void initialize(Collection<String> playersessions) {
        String[] players = new String[playersessions.size()];
        players = playersessions.toArray(players);
        ArrayList<String> listed = new ArrayList<>();
        for(int i = 0; i < playersessions.size(); i++) listed.add(players[i]);
        game = new Engine(listed, this.server);
    }
}
// EMIELS VERSION (CLONING OF JSONS)