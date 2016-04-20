/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import Cards.Components.ActionCard;
import Cards.Components.Card;
import Server.JSONUtilities;
import Server.Server;
import Server.Service.DominionService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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
    private String getCurrentPlayerSession(){
        return playerOrder.get(current_turn);
    }
    private void nextTurn(){
        // bepaal de speler die aan beurt is
        current_turn ++;
        current_turn %= players.keySet().size();
        String sess = getCurrentPlayerSession();
        
        // Verzend chatbericht -> it is your turn
        JSONObject obj = JSONUtilities.JSON.make_client_print("It is your turn.");
        server.getClient(sess).write(obj);
        
        // Verzend de andere spelers ('sendAllExcept deze sessie') dat het deze speler's beurt is.
        String playerTurnName = server.getNickname(sess);
        server.sendAllExcept(sess, JSONUtilities.JSON.make_client_print("It is now " + playerTurnName + "'s turn."));
        
        enterActionPhase();
    }
    
    private void enterActionPhase(){
        String sess = getCurrentPlayerSession();
        Player p = players.get(sess);
        boolean has_actions = false;
        Set<String> actioncardnames = new HashSet<>();
        for(Card c : p.hand){
            if(c instanceof ActionCard){
                actioncardnames.add(c.name);
            }
        }
        if(!actioncardnames.isEmpty()){
            JSONObject msg = JSONUtilities.JSON.make_client_print("You enter the action phase.");
            server.getClient(sess).write(msg);
            JSONObject obj = JSONUtilities.JSON.make_client_hand_valid(actioncardnames);
            server.getClient(sess).write(obj);
        }else{
            JSONObject msg = JSONUtilities.JSON.make_client_print("You skip the action phase, you have no actions.");
            server.getClient(sess).write(msg);
            enterBuyPhase();
        }
        
    }
    private void enterBuyPhase(){
        String sess = getCurrentPlayerSession();
        Player p = players.get(sess);
        server.getClient(sess).write(JSONUtilities.JSON.make_client_hand_invalid());
        server.getClient(sess).write(JSONUtilities.JSON.make_client_print("You enter the buy phase."));
        boolean has_gold = false;
        Set<String> treasurecardnames = new HashSet<>();
        treasurecardnames.add("copper");
        treasurecardnames.add("silver");
        treasurecardnames.add("gold");
        server.getClient(sess).write(JSONUtilities.JSON.make_client_hand_valid(treasurecardnames));
    }
    private void endTurn(){
        String sess = getCurrentPlayerSession();
        Player p = players.get(sess);
    }
    private void give_card_to_player_hand(String session){
        String cardname = "moat"; // fallback cardname 
        Player p = players.get(session);
        if(p == null){
            System.err.println("Crafted session intercepted");
            return;
        }
        Card drawn = p.drawCard();
        cardname = drawn.name;
        JSONObject obj = JSONUtilities.JSON.make_client_print("You draw a card: " + cardname);
        server.getClient(session).write(obj);
        
        JSONObject act = JSONUtilities.JSON.make_client_gain(cardname);
        server.getClient(session).write(act);
    }
    private void flushHand(String session){
        JSONObject obj = JSONUtilities.JSON.make_client_print("You discard your hand.");
        Player p = players.get(session);
        for(Card c : p.hand){
            server.getClient(session).write(JSONUtilities.JSON.make_client_lose(c.name));
        }
        p.discardHand();
        for(int i = 0; i < 5;i++) give_card_to_player_hand(session);
    }

    
    
    
    
    
    
    
    
    
    
    
    /*********************
     * 
     *  Section regarding receiving cards from clients.
     * 
     * *******************/
   
    public boolean isCardFromCurrentTurnPlayer(JSONObject json){
        String clientsession = json.getString("session");
        String currentsession = playerOrder.get(current_turn);
        return clientsession.equals(currentsession);
    }
    public Player getPlayerWhoSent(JSONObject json){
        String clientsession = json.getString("session");
        Player client = players.get(clientsession);
        return client;
    }
    public boolean doesPlayerHaveCardInHand(Player p, String cardname){
        return p.hasCard(cardname);
    }
    
    public void processCard(JSONObject json) {
        if(isCardFromCurrentTurnPlayer(json)){
            Player p = getPlayerWhoSent(json);
            String cardname = json.getString("cardname");
            if(doesPlayerHaveCardInHand(p, json.getString("cardname"))){
                p.playCard(env, cardname, Long.parseLong(json.getString("id")));
                server.sendAll(JSONUtilities.JSON.make_client_print("*" + server.getNickname(json.getString("session")) + " played " + cardname));
                server.sendOne(JSONUtilities.JSON.make_client_lose(cardname), json.getString("session"));
            }
        }
    }
    
    
}
