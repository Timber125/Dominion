/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import Cards.Components.ActionCard;
import Cards.Components.Card;
import Server.JSONUtilities;
import Server.RTIUtilities;
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
    private int current_phase = 0;
    
    private int actions = 0;
    private int purchases = 0;
    private int money = 0;
    
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
        
        // First send the chosen actioncards to all players
        
        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("market", "market", 10));
        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("village", "village", 10));
        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("woodcutter", "woodcutter", 10));
        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("laboratory", "laboratory", 10));
        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("smithy", "smithy", 10));
        
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
        
        money = 0;
        actions = 1;
        purchases = 1;
        server.sendAll(JSONUtilities.JSON.make_client_turninfo(actions, purchases, money));
        
        // Verzend de andere spelers ('sendAllExcept deze sessie') dat het deze speler's beurt is.
        String playerTurnName = server.getNickname(sess);
        server.sendAllExcept(sess, JSONUtilities.JSON.make_client_print("It is now " + playerTurnName + "'s turn."));
        
        enterActionPhase();
    }
    private void highlight_actions(Player p){
        Set<String> actioncardnames = new HashSet<>();
        for(Card c : p.hand){
            if(c instanceof ActionCard){
                actioncardnames.add(c.name);
            }
        }
        JSONObject obj = JSONUtilities.JSON.make_client_hand_valid(actioncardnames);
        server.getClient(p.mySession).write(obj);
    }
    private void enterActionPhase(){
        current_phase = 1;
        String sess = getCurrentPlayerSession();
        server.getClient(sess).write(JSONUtilities.JSON.make_client_nextphase_impossible());
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
            server.getClient(sess).write(JSONUtilities.JSON.make_client_nextphase_possible());
        }else{
            JSONObject msg = JSONUtilities.JSON.make_client_print("You skip the action phase, you have no actions.");
            server.getClient(sess).write(msg);
            enterBuyPhase();
        }
        
    }
    private void enterBuyPhase(){
        current_phase = 2;
        String sess = getCurrentPlayerSession();
        Player p = players.get(sess);
        server.getClient(sess).write(JSONUtilities.JSON.make_client_nextphase_impossible());
        server.getClient(sess).write(JSONUtilities.JSON.make_client_hand_invalid());
        server.getClient(sess).write(JSONUtilities.JSON.make_client_print("You enter the buy phase."));
        boolean has_gold = false;
        Set<String> treasurecardnames = new HashSet<>();
        treasurecardnames.add("copper");
        treasurecardnames.add("silver");
        treasurecardnames.add("gold");
        server.getClient(sess).write(JSONUtilities.JSON.make_client_hand_valid(treasurecardnames));
        server.getClient(sess).write(JSONUtilities.JSON.make_client_nextphase_possible());
    }
    private void endTurn(){
        current_phase = 3;
        String sess = getCurrentPlayerSession();
        server.getClient(sess).write(JSONUtilities.JSON.make_client_nextphase_impossible());
        Player p = players.get(sess);
        for(Card c : env.tablecards){
            p.deck.used.add(c);
        }
        env.tablecards.clear();
        flushHand(sess);
        nextTurn();
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
        
        Player p = players.get(session);
        server.getClient(session).write(JSONUtilities.JSON.make_client_lose("all", new Long(0).toString()));
        p.discardHand();
        JSONObject obj = JSONUtilities.JSON.make_client_print("You discard your hand.");
        for(int i = 0; i < 5;i++) give_card_to_player_hand(session);
    }

    
    
    
    
    
    
    
    
    
    
    
    /*********************
     * 
     *  Section regarding receiving cards from clients.
     * 
     * *******************/
   
    public boolean isRequestFromCurrentTurnPlayer(JSONObject json){
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
        if(isRequestFromCurrentTurnPlayer(json)){
            Player p = getPlayerWhoSent(json);
            String cardname = json.getString("cardname");
            if(doesPlayerHaveCardInHand(p, json.getString("cardname"))){
                Card c = p.playCard(env, cardname, Long.parseLong(json.getString("id")));
                
                if(c instanceof ActionCard){
                    actions --;
                    if(actions < 0) {
                        System.err.println("Action card cannot be played -> need more actions!");
                        actions ++;
                        return;
                    }
                    if(current_phase != 1){
                        System.err.println("Action card cannot be played -> not actionphase!");
                        actions ++;
                        return;
                    }
                }
                server.sendOne(JSONUtilities.JSON.make_client_lose(cardname, json.getString("id")), json.getString("session"));

                money += c.moneygain();
                actions += c.actiongain();
                purchases += c.actiongain();
                int cardsgain = c.cardgain();
                for(int i = 0; i < cardsgain; i++){
                    give_card_to_player_hand(p.mySession);
                }
                server.sendAll(JSONUtilities.JSON.make_client_turninfo(actions, purchases, money));
                server.sendAll(JSONUtilities.JSON.make_client_print("*" + server.getNickname(json.getString("session")) + " played " + cardname));
                if(current_phase == 1){
                    boolean hasActions = p.hasActionCard();
                    if(hasActions && (cardsgain > 0)){
                         highlight_actions(p);
                    }
                    if(!hasActions) enterBuyPhase();
                }
                
                
            }
        }
    }
    public void processBuyRequest(JSONObject json){
        String sess = json.getString("session");
        String card = json.getString("cardname");
        Player p = getPlayerWhoSent(json);
        if(current_phase != 2){
            server.getClient(sess).write(JSONUtilities.JSON.make_client_print("You can only buy in the buy-phase!"));
        }else if(!isRequestFromCurrentTurnPlayer(json)){
            server.getClient(sess).write(JSONUtilities.JSON.make_client_print("You can only buy during your own turn!"));
        }else{
            int price = env.environment_pricecheck(card);
            if((money >= price) && (purchases >= 1)){
                money -= price;
                purchases -= 1;
                p.deck.used.add(env.environment_buy(card));
                // Update turn info to all players
                // Update environment cardcounts to all players
                // Update message player bought this card to all players
                server.sendAll(JSONUtilities.JSON.make_client_turninfo(actions, purchases, money));
                server.sendAll(JSONUtilities.JSON.make_client_update_environment(card, env.environment_amountcheck(card)));
                server.sendAll(JSONUtilities.JSON.make_client_print("*" + server.getNickname(sess) + " bought " + card + "."));
                server.sendAll(JSONUtilities.JSON.make_client_print("there are " + env.environment_amountcheck(card) + " " + card + "'s" + " left on the table."));
            }
            if(purchases <= 0){
                endTurn();
            }
        }
    }
    public void nextPhase(String session) {
        if(!session.equals(playerOrder.get(current_turn))){
            System.err.println("Intercepted malicious nextPhase package from client " + server.getNickname(session) +".");
            return;
        }
        if(current_phase == 1){
            enterBuyPhase();
        }
        else if(current_phase == 2){
            endTurn();
        }
    }
    
    
}
