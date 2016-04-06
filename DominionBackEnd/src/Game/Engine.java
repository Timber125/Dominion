/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import Cards.Card;
import Server.JSONUtilities;
import Server.Server;
import Server.Service.DominionService;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONObject;

/**
 *
 * @author admin
 * 
 */
public class Engine {
    
    /* Callback */
    private Server server;
    
    /* Accessible */
    protected ArrayList<String> operations_allowed;
    
    /* Frontend simulative variables */
    private int current_turn = 0;
    private HashMap<Integer, String> playerOrder;
    private HashMap<String, Player> players;
    private Environment env;
    
    
    
    /* Engine constructor -> no given game-action-cards */
    public Engine(ArrayList<String> playersessions, Server serv){
        operations_allowed = new ArrayList<>();
        playerOrder = new HashMap<>();
        server = serv;
        env = new Environment(playersessions.size());
        int counter = 0;
        players = new HashMap<>();
        for(int i = 0; i < playersessions.size(); i++){
            String psess = playersessions.get(i);
            playerOrder.put(i, psess);
            players.put(psess, new Player(psess));
        }
        startRoutine();
    }
    @Deprecated 
    public Engine(ArrayList<String> playersessions, ArrayList<Card> game, Server serv){
        operations_allowed = new ArrayList<>();
        playerOrder = new HashMap<>();
        server = serv;
        // Environment with given game-cards is not implemented (correctly) yet. 
        env = new Environment(playersessions.size(), game);
        int counter = 0;
        players = new HashMap<>();
        for(int i = 0; i < playersessions.size(); i++){
            String psess = playersessions.get(i);
            playerOrder.put(i, psess);
            players.put(psess, new Player(psess));
        }
        startRoutine();
    }
    
    public void startRoutine(){
        // All actions that server needs to do before any user interaction
        current_turn = players.keySet().size()-1;
        // Deal hands
        for(String session : players.keySet()){
            for(int i = 0; i < 5; i++) give_card_to_player_hand(session);
        }
        
        // Send "turn" to player one
        nextTurn();
        
    }
    
    /* 
        Current protocol scheme: 
    
        On start: Give everyone 5 cards.
        
        Turn 1 starts 
            Give playprivilege to p1
                [
                Wait for action 
                    process action 
                ]
                [Wait for finish turn] 
        Turn 2 starts
            Give playprivilege to p2 
            ... 
    
    */
    
    public void process(JSONObject action){
        
    }
    
    private void nextTurn(){
        current_turn ++;
        current_turn %= players.keySet().size();
        String sess = playerOrder.get(current_turn);
        
        JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
        obj = JSONUtilities.JSON.addKeyValuePair("sysout", "It is your turn.", obj);
        server.getClient(sess).write(obj);
    }
    private void give_card_to_player_hand(String session){
        String cardname = "moat";
        Player p = players.get(session);
        if(p == null){
            System.err.println("Crafted session intercepted");
            return;
        }
        Card drawn = p.drawCard();
        cardname = drawn.name;
        JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
        obj = JSONUtilities.JSON.addKeyValuePair("sysout", "You draw a card: " + cardname, obj);
        server.getClient(session).write(obj);
        
        JSONObject act = JSONUtilities.JSON.create("action", "dominion");
        act = JSONUtilities.JSON.addKeyValuePair("act","gain",act);
        act = JSONUtilities.JSON.addKeyValuePair("gain",cardname,act);
        server.getClient(session).write(act);
    }
}
