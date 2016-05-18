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
    
    // Player current = players.get(playerOrder.get(current_turn));
    
    private int actions = 0;
    private int purchases = 0;
    private int money = 0;
    
    private HashMap<Integer, String> playerOrder;
    private HashMap<String, Player> players;
    private Environment env;
    
    
    private boolean interactionmode = false;
    
    /*
    *
    *   Baseset functions 
    
    +1 Action
    +1 Card
    +1 Buy
    +1 coin
    Make discard from hand ...
    Reveal Card from player x deck
    Put card in Hand player x
    Put Card in discardpile player x
    Put Card on top of deck player x
    Reveal cards from player x hand with/without interaction
    select cards from Card[]
    make card selectable
    gain a card costing up to x
    play a reaction
 
 
    (Trash card y)
    (Discard card y)
   
    *
    */
    
    
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
        // First send initialize-victory and treasures to clients. (no count is checked)
        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("Treasure", "Treasure", 40));
        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("Victory", "Victory", 8));
        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("deck_and_discard", "deck_and_discard", 0));
        // send the chosen actioncards to all players
        
        
        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("market", "market", 10));
        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("village", "village", 10));
        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("bureaucrat", "bureaucrat", 10));
        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("laboratory", "laboratory", 10));
        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("councilroom", "councilroom", 10));
        
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
        server.getClient(p.getSession()).write(obj);
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
        server.getClient(sess).write(JSONUtilities.JSON.make_client_environment_valid(env.getAllBuyables(money)));
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
        if(interactionmode) {
            processInteraction(json);
            return;
        }
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
                        System.err.println("Action card cannot be played -> not actionphase or nested in an action!");
                        actions ++;
                        return;
                    }
                }
                
                server.sendOne(JSONUtilities.JSON.make_client_lose(cardname, json.getString("id")), json.getString("session"));

                money += c.moneygain();
                actions += c.actiongain();
                purchases += c.purchasegain();
                int cardsgain = c.cardgain();
                for(int i = 0; i < cardsgain; i++){
                    give_card_to_player_hand(p.getSession());
                }
                server.sendAll(JSONUtilities.JSON.make_client_turninfo(actions, purchases, money));
                server.sendAll(JSONUtilities.JSON.make_client_print("*" + server.getNickname(json.getString("session")) + " played " + cardname));
                
                // Introduce check if the played actioncard has a special phase. 
                if(c.hasSpecial()){
                    System.out.println("- " + c.getName() + " has a special:");
                    for(Player victim : players.values()){
                        //if(victim.getSession().equals(p.getSession())) continue; // Do (!!) treat the initiator as a victim
                            SpecialCase spec = c.special(victim, p);
                            if(spec instanceof RewardCase){
                                System.out.println("--RewardCase initialized");
                                handleRewards((RewardCase) spec);
                            }else if(spec instanceof InteractionCase){
                                // in testing
                                System.out.println("-- InteractionCase initialized");
                                handleInteraction((InteractionCase) spec);
                            }
                    }
                }
                
                if(current_phase == 1){
                    boolean hasActions = p.hasActionCard();
                    if(hasActions && (cardsgain > 0)){
                         highlight_actions(p);
                    }
                    if(!hasActions) enterBuyPhase();
                }else if(current_phase == 2){
                    refreshBuyPhase();
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
            }else{
                refreshBuyPhase();
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
        make_client_update_deck(players.get(session));
        make_client_update_discardpile(players.get(session));
    }

    private void refreshBuyPhase() {
        server.getClient(playerOrder.get(current_turn)).write(JSONUtilities.JSON.make_client_environment_valid(env.getAllBuyables(money)));
    }
    
    
    
    /*
    public void special_phase(int special_ID){
        current_phase = special_ID;
    }
    */
    
    
    /****************
    *
    *   Section regarding ingame-functions
    *
    ****************/
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
        make_client_update_deck(p);
        
    }
    private void flushHand(String session){
        
        Player p = players.get(session);
        server.getClient(session).write(JSONUtilities.JSON.make_client_lose("all", new Long(0).toString()));
        p.discardHand();
        JSONObject obj = JSONUtilities.JSON.make_client_print("You discard your hand.");
        for(int i = 0; i < 5;i++) give_card_to_player_hand(session);
        make_client_update_deck(p);
        make_client_update_discardpile(p);
    }
    
    private void give_card_to_player_discardpile(String session, Card c){
        String cardname = c.getName();
        Player p = players.get(session);
        if(p == null){
            System.err.println("Crafted session intercepted");
            return;
        }
        p.deck.add(c);
        make_client_update_discardpile(p);
    }
    
    
    /*
    
        Ease of access
    
    */
    
    private void make_client_update_deck(Player p){
        JSONObject deckupdate = JSONUtilities.JSON.make_client_update_environment("deck", p.deck.content.size());
        server.getClient(p.getSession()).write(deckupdate);
    }
    private void make_client_update_discardpile(Player p){
        int index = p.deck.used.size()-1;
        String cardname = "back";
        if(index >= 0){
            cardname = p.deck.used.get(index).getName();
        }
        JSONObject discupdate = JSONUtilities.JSON.make_client_update_discardpile(cardname);
        server.getClient(p.getSession()).write(discupdate);
        JSONObject disc_count_update = JSONUtilities.JSON.make_client_update_environment("discardpile", index+1);
        server.getClient(p.getSession()).write(disc_count_update);
    }
    private void make_clients_update_environment(String cardidentifier){
        server.sendAll(JSONUtilities.JSON.make_client_turninfo(actions, purchases, money));
        server.sendAll(JSONUtilities.JSON.make_client_update_environment(cardidentifier, env.environment_amountcheck(cardidentifier)));
        server.sendAll(JSONUtilities.JSON.make_client_print("there are " + env.environment_amountcheck(cardidentifier) + " " + cardidentifier + "'s" + " left on the table."));
            
    }
    
    
    /*
    *
    *
    *   Section regarding special cases and interactive action cards.
    *
    *
    */
    private void handleRewards(RewardCase rewardCase) {
        Player victim = rewardCase.getVictim();
        for(int cardgains = 0; cardgains < rewardCase.cardgain(); cardgains++){
            give_card_to_player_hand(victim.getSession());
        }
        // Other rewards?
        JSONObject specialbehaviour = rewardCase.reward_behaviour();
        if(specialbehaviour != null){
            handleSpecialJSON(specialbehaviour, rewardCase.getVictim());
        }
    }

    private void handleInteraction(InteractionCase interactionCase) {
        // Send an interaction json to every client, based upon the interactioncase specifications. 
        // We need to block the engine until we receive enough cards to make every ic valid
        // When every ic is valid, we need to pass the ic's special cards to the initiator 
        // The initiator gives us back what he selected, + behaviour, in a rewardcase. 
        interactionCase.getVictim().setInteraction(interactionCase); // link the interactioncase. 
        System.out.println("--- handling interactioncase ---");
        if(!interactionCase.isValid()){
            System.out.println("----- interactioncase was found invalid. Sending confirmation to victim.");
            this.interactionmode = true;
            // All the information is in the interactioncase object. 
            // Right now we use a dummy information source to try trigger the client confirmation stage. 
            server.getClient(interactionCase.getVictim().getSession()).write(JSONUtilities.JSON.make_client_confirmation_model_empty("You are being attacked!"));
        }
        
    }
    
    private void handleSpecialJSON(JSONObject special, Player victim){
        String special_type = special.getString("type");
        switch(special_type){
            case("env_gain"):{
                Card c = env.environment_buy(special.getString("identifier"));
                String destination = special.getString("dest");
                switch(destination){
                    case("top_of_deck"):{
                        victim.deck.addTopOfDeck(c);
                        break;
                    }
                    case("bottom_of_deck"):{
                        victim.deck.addBottomOfDeck(c);
                        break;
                    }
                    case("top_of_discardpile"):{
                        victim.deck.add(c);
                        break;
                    }
                    default:{
                        victim.deck.add(c);
                    }
                }
                // Update clients
                make_clients_update_environment(special.getString("identifier"));
                make_client_update_deck(victim);
                make_client_update_discardpile(victim);
                break;
            }
        }
    }

    private void processInteraction(JSONObject json) {
        String session = json.getString("session");
        Player victim = players.get(session);
        String cardname = json.getString("cardname");
        Card RTIed = env.CardRTI(cardname);
        victim.getCurrentInteraction().process(RTIed, true, Long.parseLong(json.getString("id")));
        if(victim.getCurrentInteraction().isValid()){
            checkInteractionModeRequirements();
        }
    }

    private void checkInteractionModeRequirements() {
        for(Player p : players.values()){
            if(p.getCurrentInteraction() != null){
                if(!p.getCurrentInteraction().isValid()) return;
            }
        }
        proposeInteraction();
    }

    private void proposeInteraction() {
        interactionmode = false;
        
        for(Player p : players.values()){
            InteractionCase ic = p.getCurrentInteraction();
            // Extract values
            // Add an interaction to the initiator
            // Stay in interactionmode, but deny all requests not coming from initiator
            // When initiator gives back a resultJSON, we finish the interactions accordingly. 
        }
        // End the interactions given the interactionCases 
        
        // We get 3 interactioncases to inject into a JSONobject that should trigger the initiator's confirmframe
        // Interactionmode is not finished 
    }
    
}
