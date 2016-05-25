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
import java.util.Set;
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
        if("gameID".equals(json.getString("operation"))){
            handleServiceRequest(json);
            return;
        }
        String operation = null;
        try{operation = json.getString("operation");
        }catch(NullPointerException e){
            operation = null;
        }
        String sessionID = null;
        try{sessionID = json.getString("session");
        }catch(NullPointerException e){
            sessionID = null;
        }
       
        switch(operation){
            case("disconnect"):{
                if(is_started) return;
                // Only if client was connected
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
   
    private void handleServiceRequest(JSONObject json) {
        Integer gameID = Integer.parseInt(json.getString("gameID"));
        startGame(gameID);
       
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
            prepareStartGame();
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
   
    private void prepareStartGame(){
        JSONObject obj = JSONUtilities.JSON.create("service_type", "database");
        obj = JSONUtilities.JSON.addKeyValuePair("function", "gameID", obj);
        Set<String> sessionSet = lobbyClients.keySet();
        String[] sessions = sessionSet.toArray(new String[sessionSet.size()]);
        for(int i = 0; i < 4; i++){
            if(i < sessions.length){
            obj = JSONUtilities.JSON.addKeyValuePair("session" + i, sessions[i], obj);
            obj = JSONUtilities.JSON.addKeyValuePair("player" + i, server.getNickname(sessions[i]), obj);
            }
            else{
                obj = JSONUtilities.JSON.addKeyValuePair("session" + i, "none", obj);
                obj = JSONUtilities.JSON.addKeyValuePair("player" + i, "none", obj);
            }
        }
        ServiceBroker.instance.offerRequest(obj.toString());
       
    }
 
    private void startGame(int gameID) {
        ServiceBroker.instance.setGameID(gameID);
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