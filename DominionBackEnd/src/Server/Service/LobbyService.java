/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server.Service;

import Server.ConnectionHandler;
import Server.JSONUtilities;
import Server.Server;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class LobbyService extends Service{
    
    private final int min_players = 2;
    private final int max_players = 4;
    
    private boolean is_started = false;
    
    
    private final DominionService game;
    private HashMap<String, Boolean> lobbyClients;
    
    
    public LobbyService(Server server, DominionService game){
        super(server);
        known_service_types.add("lobby");
        lobbyClients = new HashMap<>();
        this.game = game;
        game.deactivate();
    }
    @Override
    public void handleType(String type, JSONObject json) {
        // service_type == lobby
        // operation == vote || operation == unvote || operation = disconnected || operation = connect
        String operation = json.getString("operation");
        String sessionID = json.getString("session");
        
        switch(operation){
            case("disconnect"):{
                // Only if client was connected
                if(is_started) return;
                if(lobbyClients.containsKey(sessionID)){
                    String nickname = json.getString("author");
                    if(nickname == null) nickname = "unknown";
                    lobbyClients.remove(sessionID);
                    server.sendAll(replyLobbyDisconnect(sessionID, nickname));
                    notifyLobbyChange();
                }
                return;
            }
            case("connect"):{
                if(is_started) return;
                // Only if client was not connected
                if(!lobbyClients.containsKey(sessionID)){
                    lobbyClients.put(sessionID, false);
                    server.sendAll(replyLobbyConnect(sessionID));
                    notifyLobbyChange();
                }
                return;
            }
            case("vote"):{
                if(is_started) return;
                if(lobbyClients.containsKey(sessionID)){
                    // Only if the client wasnt already "voted in"
                    if(!lobbyClients.get(sessionID)) {
                        lobbyClients.put(sessionID, true);
                        server.sendAll(replyVoteChanged(sessionID));
                        notifyLobbyChange();
                    }
                    
                    
                }else{
                    // The lobby package is not valid, don't take action
                }
                return;
            }
            case("unvote"):{
                if(is_started) return;
                if(lobbyClients.containsKey(sessionID)){
                    // Only if the client was already "voted in"
                    if(lobbyClients.get(sessionID)){
                        lobbyClients.put(sessionID, false);
                        server.sendAll(replyVoteChanged(sessionID));
                        notifyLobbyChange();
                    }
                }else{
                    // The lobby package is not valid, don't take action
                }
                return;
            }
            default:{
                System.err.println("received an invalid lobby operation");
            }
            
        }        
        
        
        
    }

    private boolean isStartPossible(){
        int playercount = lobbyClients.keySet().size();
        boolean playercount_ok = false;
        if((playercount >= min_players) && (playercount <= max_players)){
            playercount_ok = true;
        }
       
        return (playercount_ok);
    }
    
    private boolean allReady(){
        boolean allready = true;
        for(String session : lobbyClients.keySet()){
            if(!lobbyClients.get(session)) {
                allready = false;
                break;
            }
        }
        return allready;
    }
    
    private void notifyLobbyChange() {
        if(allReady() && isStartPossible()){
            startGame();
        }
    }
    
    private JSONObject replyVoteChanged(String session){
        JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
        obj = JSONUtilities.JSON.addKeyValuePair("sysout", "[" + server.getNickname(session) + "] is " + ((lobbyClients.get(session))?("ready"):("not ready")), obj);
        return obj;
    }
   
    private JSONObject replyLobbyConnect(String session){
        JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
        obj = JSONUtilities.JSON.addKeyValuePair("sysout", "[" + server.getNickname(session) + "] sits down.", obj);
        return obj;
    }
    
    private JSONObject replyLobbyDisconnect(String session, String nickname){
        JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
        obj = JSONUtilities.JSON.addKeyValuePair("sysout", "[" + nickname + "] left the table!", obj);
        return obj;
    }

    private void notifyStart(){
        server.sendAll(replyGameStarted());
    }
    
    private void notifyStartPossible() {
        server.sendAll(replyStartGamePossible());
    }

    private void notifyStartImpossible() {
        server.sendAll(replyStartGameImpossible());
    }
        
    private JSONObject replyStartGamePossible(){
        JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
        obj = JSONUtilities.JSON.addKeyValuePair("sysout", "[LOBBY] Game can now start.", obj);
        return obj;
    }
    private JSONObject replyGameStarted(){
        JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
        obj = JSONUtilities.JSON.addKeyValuePair("sysout", "[LOBBY] Game has started.", obj);
        return obj;
    }
    private JSONObject replyStartGameImpossible(){
        JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
        obj = JSONUtilities.JSON.addKeyValuePair("sysout", "[LOBBY] Game can no longer start.", obj);
        return obj;
    }

    private void startGame() {
        game.initialize(lobbyClients.keySet());
        game.activate();
        notifyStart();
        is_started = true;
    }
    
    private void pauseGame(){
        // TODO
    }
}
// Emiels version