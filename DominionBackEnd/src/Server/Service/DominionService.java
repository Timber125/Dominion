/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server.Service;

import Server.Server;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class DominionService extends Service{

    public DominionService(Server server){
        super(server);
        known_service_types.add("dominion");
    }
    
    @Override
    public void handleType(String type, JSONObject json) {
        // Service_type = dominion. 
        // Implemented requests:
        // service_type="dominion", phase="draw", operation="draw_cards", repeat="5"
        // service_type="dominion", phase="draw", operation="finish"
        // service_type=dominion, phase="draw", operation="discard_hand", repeat="1"
        // service_type=dominion, phase="draw", operation="shuffle_deck_graveyard", repeat="1"
        if(type.equals("dominion")){
            String phase = json.getString("phase");
            switch(phase){
                case("draw"):{
                    drawPhase(json);
                    return;
                }
                default:{
                    System.out.println("Phase [" + phase + "] has not been implemented (yet?).");
                }
            }
        }else{
            System.out.println("Dominion service doesn't know how to handle 'all' addressing. Implement me!");
        }
        
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
    
}
