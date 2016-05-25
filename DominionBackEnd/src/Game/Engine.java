/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import Cards.Basic.Throneroom;
import Cards.Components.ActionCard;
import Cards.Components.Card;
import Cards.Components.TreasureCard;
import Server.JSONUtilities;
import Server.RTIUtilities;
import Server.Server;
import Server.Service.DominionService;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author admin
 * 
 */
public class Engine implements InvalidationListener{
    
    /* Callback */
    private Server server;
    
    /* Accessible */
    protected ArrayList<String> operations_allowed;
    
    /* Implemented actions */
    private String action_names_implemented = "adventurer,bureaucrat,cellar,chancellor,chapel,councilroom,feast,festival,gardens,laboratory,library,market,militia,mine,moat,moneylender,remodel,smithy,spy,thief,throneroom,village,witch,woodcutter,workshop";
    private String[] actioncards_implemented = action_names_implemented.split(",");
    
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
        
        // adventurer
        // bureaucrat
        // cellar
        // chancellor
        // chapel
        
        // feast
        // festival
        // laboratory
        // gardens
        // library
        String[] actions = new String[10];
        for(int i = 0; i < 10; i++) actions[i] = "";
        ArrayList<Integer> taken_keys = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            Random r = new Random();
            int taken = -1;
            while((taken == -1) || taken_keys.contains(taken)) taken = r.nextInt(actioncards_implemented.length);
            taken_keys.add(taken);
        }
        
        for(int i = 0; i < 10; i++){
            actions[i] = actioncards_implemented[taken_keys.get(i)];
        }
        
        
        env = new Environment(playerOrder.keySet().size(), actions);
        
        for(int i = 0; i < 10; i++){
            server.sendAll(JSONUtilities.JSON.make_client_initialize_environment(actions[i], actions[i], 10));
        }
        
        make_clients_update_environment("cursepile");
        make_clients_update_environment("estate");
        make_clients_update_environment("duchy");
        make_clients_update_environment("province");
        
//        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("throneroom", "throneroom", 10));
//        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("witch", "witch", 10));
//        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("moat", "moat", 10));
//        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("spy", "spy", 10));
//        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("thief", "thief", 10));
//        
//        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("woodcutter", "woodcutter", 10));
//        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("village", "village", 10));
//        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("market", "market", 10));
//        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("militia", "militia", 10));
//        server.sendAll(JSONUtilities.JSON.make_client_initialize_environment("moneylender", "moneylender", 10));
            /*
            actions[5] = "woodcutter";
            actions[6] = "village";
            actions[7] = "market";
            actions[8] = "militia";
            actions[9] = "moneylender";
        */
        
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
        current_phase = 3; // NOTHING-ALLOWED PHASE
        String sess = getCurrentPlayerSession();
        server.getClient(sess).write(JSONUtilities.JSON.make_client_nextphase_impossible());
        Player p = players.get(sess);
        for(Card c : env.tablecards){
            p.deck.used.add(c);
        }
        env.tablecards.clear();
        flushHand(sess);
        server.sendAll(JSONUtilities.JSON.make_client_refresh_tableview());
        
        
        // Check for game end
        int empty_stack_counters = 0;
        boolean province_stack_empty = false;
        for(String stackname : env.environment_library.keySet()){
            if(stackname.equals("province")){
                if(env.environment_library.get(stackname).isEmpty()) province_stack_empty = true;
                
            }
            else if(env.environment_library.get(stackname).isEmpty()) empty_stack_counters ++;
        }
        
        boolean gameEnded = (province_stack_empty || (empty_stack_counters >= 3));
        if(gameEnded){
            // GAME END
            for(String session : players.keySet()){
                showGameEndedTo(session);
            }
        }
        else nextTurn();
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
    public void handleAction(Card c, Player p){
                money += c.moneygain();
                actions += c.actiongain();
                purchases += c.purchasegain();
                int cardsgain = c.cardgain();
                for(int i = 0; i < cardsgain; i++){
                    give_card_to_player_hand(p.getSession());
                }
                server.sendAll(JSONUtilities.JSON.make_client_turninfo(actions, purchases, money));
                server.sendAll(JSONUtilities.JSON.make_client_print("*" + server.getNickname(p.getSession()) + " played " + c.getName()));
                
                // Introduce check if the played actioncard has a special phase. 
                if(c.hasSpecial()){
                    System.out.println("- " + c.getName() + " has a special:");
                    for(Player victim : players.values()){
                        //if(victim.getSession().equals(p.getSession())) continue; // Do (!!) treat the initiator as a victim
                            SpecialCase spec = c.special(victim, p, env);
                            if(spec instanceof RewardCase){
                                System.out.println("--RewardCase initialized");
                                handleRewards((RewardCase) spec);
                            }else if(spec instanceof InteractionCase){
                                // in testing
                                System.out.println("-- InteractionCase initialized");
                                handleInteraction((InteractionCase) spec);
                            }
                            
                            SpecialCase future = c.futurespecial(victim, p, env);
                            if((future != null) && (future instanceof InteractionCase)){
                                System.out.println("-- FutureInteractionCase initialized");
                                p.setFutureInteraction((InteractionCase)future);
                            }
                            
                            if(spec instanceof InteractionCase){
                                InteractionCase iterativeFuture = c.future_futureSpecial(victim, victim, env, (InteractionCase) spec);
                                if(iterativeFuture != null) p.setIterativeInteractionFactory(c);
                            }
                    }
                }
                
                if(!interactionmode){
                    if(!env.throneRoomQueue.isEmpty()){
                        handleAction(env.throneRoomQueue.poll(), players.get(playerOrder.get(current_turn)));
                    }
                }
                
                 if(current_phase == 1){
                    boolean hasActions = p.hasActionCard();
                    if(hasActions && this.actions > 0){
                         highlight_actions(p);
                    }
                    if(!hasActions && !interactionmode) enterBuyPhase();
                    else if(this.actions <= 0) enterBuyPhase();
                }else if(current_phase == 2){
                    refreshBuyPhase();
                }
                
                
    }
    public synchronized void processCard(JSONObject json) {
        if(interactionmode) {
            processInteraction(json);
            return;
        }
        if(isRequestFromCurrentTurnPlayer(json)){
            Player p = getPlayerWhoSent(json);
            String cardname = json.getString("cardname");
            if(doesPlayerHaveCardInHand(p, json.getString("cardname"))){
                Card c = p.playCard(env, cardname, Long.parseLong(json.getString("id")));
                server.sendAll(JSONUtilities.JSON.make_client_update_tablecards(c));
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

                handleAction(c, p);
               
                
                
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
        }else if(env.environment_library.get(card).isEmpty()){
            server.getClient(sess).write(JSONUtilities.JSON.make_client_print("The stack of " + card + "'s is empty, buying is not allowed."));
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
        for(Player p : players.values()){
            make_client_update_deck(p);
            make_client_update_discardpile(p);
        }
        make_clients_update_cursepile();
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
    *server.sendAll(JSONUtilities.JSON.make_client_turninfo(actions, purchases, money));
        server.sendAll(JSONUtilities.JSON.make_client_update_environment(cardidentifier, env.environment_amountcheck(cardidentifier)));
        server.sendAll(JSONUtilities.JSON.make_client_print("there are " + env.environment_amountcheck(cardidentifier) + " " + cardidentifier + "'s" + " left on the table."));
         
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
        //if(!interactionCase.isValid()){
            //System.out.println("----- interactioncase was found invalid. Sending confirmation to victim.");
          // All interactions get sent to clients! They need to confirm to pass the confirmstage. 
            this.interactionmode = true;
            // All the information is in the interactioncase object. 
            // Right now we use a dummy information source to try trigger the client confirmation stage. 
            interactionCase.addListener(this);
            server.getClient(interactionCase.getVictim().getSession()).write(interactionCase.getStartBehaviour());
        //}
        
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
                    case("discardpile"):{
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
        try{
            String catchjsonexc = json.getString("parentindex");
        }catch(JSONException jse){
            System.err.println("Client tried to do something illegal...");
            return;
        }
        boolean succesfullyAdded = victim.getCurrentInteraction().process(RTIed, (json.getString("id") + "_" + json.getString("parentindex")));
        System.err.println("Interactioncase received " + cardname + " with id " + json.getString("id") + " and got accepted: [" + succesfullyAdded + "]");
    }
/*
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
*/
    @Override
    public void invalidated(Observable observable) {
        if(observable instanceof InteractionCase){
        InteractionCase invalidated = (InteractionCase)observable;
        // Check interaction-flow! interaction closes, it gets notified. 
        JSONObject finishbehaviour = invalidated.getFinishBehaviour();
        
        ArrayList<Card> selectedCardsFromThisInteractionCase = invalidated.getSelected();
        ArrayList<String> selectedIdentifiersFromThisInteractionCase = invalidated.getIds();
        ArrayList<String> selectedCardNamesFromThisInteractionCase = new ArrayList<>();
        
        for(int index = 0; index < selectedIdentifiersFromThisInteractionCase.size(); index++){
            String thisIdentifier = selectedIdentifiersFromThisInteractionCase.get(index);
            // Check if the identifier's first character is a digit.
            // If this is the case, it means the identifier is long-parseable
            // If the identifier is a long, it comes out of the hand of the victim.
            // Identifiers from victims hands get "other" notitifier in front. 
            if(Character.isDigit(thisIdentifier.charAt(0))){
                selectedIdentifiersFromThisInteractionCase.remove(index);
                selectedIdentifiersFromThisInteractionCase.add(index, "other_" + thisIdentifier);
            }
            Card linkedToThisIdentifier = selectedCardsFromThisInteractionCase.get(index);
            selectedCardNamesFromThisInteractionCase.add(linkedToThisIdentifier.name);
            
        }
        
        // Handle behaviour: 

        JSONObject handlebehaviour = invalidated.getFinishBehaviour();
        
        if(handlebehaviour.getString("showToInitiator").equals(((Boolean)true).toString())){
            if(invalidated.getInitiator().getFutureInteraction() == null){
                System.out.println("---> Tried to show interactionresult to initiator, but his futureInteraction == null");
            }else{
                // Switch victim and initiator positions in indexFor function, since we are swicthing roles in the interaction.
                System.out.println("---> showing interactinoresult to initiator, containing " + selectedCardNamesFromThisInteractionCase.size() + " entries with ids:");
                ArrayList<String> copy_for_injection = new ArrayList<>();
                for(String s : selectedIdentifiersFromThisInteractionCase) copy_for_injection.add(s + "_" + myIndexFor(invalidated.getVictim(), invalidated.getInitiator()));
                selectedIdentifiersFromThisInteractionCase = copy_for_injection;
                for(String s : selectedIdentifiersFromThisInteractionCase) System.out.println(s);
                invalidated.getInitiator().getFutureInteraction().setStartBehaviour(JSONUtilities.JSON.update_client_confirmation_model(myIndexFor(invalidated.getVictim(), invalidated.getInitiator()), JSONUtilities.JSON.make_player_public_stats( invalidated.getVictim(),server.getClient(invalidated.getVictim().getSession()).getNickname(), copy_for_injection, selectedCardNamesFromThisInteractionCase, (selectedCardNamesFromThisInteractionCase.isEmpty())), invalidated.getInitiator().getFutureInteraction().getStartBehaviour()));
                for(int ix = 0; ix < invalidated.selectedSpecials.size(); ix++){
                    Card c = invalidated.selectedSpecials.get(ix);
                    String id = copy_for_injection.get(ix) + "_" + ix;
                    invalidated.getInitiator().getFutureInteraction().preloadedCards.add(c);
                    invalidated.getInitiator().getFutureInteraction().fromPreviousInteraction.put(id, c);
                }
                
            }
        }
        
        if(handlebehaviour.getString("moveSomething").equals(((Boolean)true).toString())){
            String from = handlebehaviour.getString("pullFrom");
            String to = handlebehaviour.getString("addTo");
            String foreach = handlebehaviour.getString("iterative_action");
            
            System.out.println("handling a pull from " + from + " to " + to + " with foreach " + foreach);
            if(from.equals("hand_victim") && to.equals("top_of_deck_victim")){
                for(int ix = 0; ix < selectedIdentifiersFromThisInteractionCase.size(); ix++){
                    System.out.println("Identifier is " + selectedIdentifiersFromThisInteractionCase.get(ix));
                    if(selectedIdentifiersFromThisInteractionCase.get(ix).startsWith("other")){
                        Long victimCardID = Long.parseLong(selectedIdentifiersFromThisInteractionCase.get(ix).split("_")[1]);
                        Card lost = invalidated.getVictim().loseCard(selectedCardNamesFromThisInteractionCase.get(ix), victimCardID);
                        invalidated.getVictim().deck.addTopOfDeck(lost);
                        server.sendOne(JSONUtilities.JSON.make_client_lose(lost.getName(), victimCardID.toString()), invalidated.getVictim().getSession());
                        make_client_update_deck(invalidated.getVictim());
                        if(!foreach.equals("none")){
                            System.out.println("uncatched foreach");
                        }
                    }
                }
            }else if(from.equals("none") && (to.equals("hand_victim"))){
                for(int ix = 0; ix < selectedIdentifiersFromThisInteractionCase.size(); ix++){
                    if(selectedIdentifiersFromThisInteractionCase.get(ix).startsWith("other")){
                            Card thecard = selectedCardsFromThisInteractionCase.get(ix);
                            invalidated.getVictim().hand.add(thecard);
                        
                            JSONObject obj = JSONUtilities.JSON.make_client_print("Adventurer gave you a : " + selectedCardNamesFromThisInteractionCase.get(ix));
                            server.getClient(invalidated.getVictim().getSession()).write(obj);
        
                            JSONObject act = JSONUtilities.JSON.make_client_gain(selectedCardNamesFromThisInteractionCase.get(ix));
                            server.getClient(invalidated.getVictim().getSession()).write(act);
                            make_client_update_deck(invalidated.getVictim());
                            make_client_update_discardpile(invalidated.getVictim());
                    }
                }
            }else if(from.equals("hand_victim") && (to.equals("discardpile_victim"))){
                for(int ix = 0; ix < selectedIdentifiersFromThisInteractionCase.size(); ix++){
                    if(selectedIdentifiersFromThisInteractionCase.get(ix).startsWith("other")){
                        System.out.println("Discarding from hand: id " + selectedIdentifiersFromThisInteractionCase.get(ix));
                        Long victimCardID = Long.parseLong(selectedIdentifiersFromThisInteractionCase.get(ix).split("_")[1]);
                        Card lost = invalidated.getVictim().loseCard(selectedCardNamesFromThisInteractionCase.get(ix), victimCardID);
                        invalidated.getVictim().deck.used.add(lost);
                        server.sendOne(JSONUtilities.JSON.make_client_lose(lost.getName(), victimCardID.toString()), invalidated.getVictim().getSession());
                        if(foreach.equals("draw_card")){
                            // This is the cellar branch of this function
                            give_card_to_player_hand(invalidated.getVictim().getSession());
                            
                        }else if(foreach.equals("none")){
                            // This is the militia branch of this function
                            System.out.println("militiabranch taken");
                        }
                        make_client_update_deck(invalidated.getVictim());
                        make_client_update_discardpile(invalidated.getVictim());
                    }
                }
            }else if(from.equals("deck_victim") && (to.equals("discardpile_victim"))){
                if(foreach.equals("until_deck_discarded")){
                    // Its (probably) a chancellor deckdiscard!
                    if(selectedCardNamesFromThisInteractionCase.isEmpty()){
                        // Do nothing! The chancellor was not selected, if it even is a chancellor.
                        
                    }else{
                        if(selectedCardNamesFromThisInteractionCase.get(0).equals("chancellor")){
                            invalidated.getVictim().deck.reshuffle();
                            make_client_update_deck(invalidated.getVictim());
                            make_client_update_discardpile(invalidated.getVictim());
                            server.sendAll(JSONUtilities.JSON.make_client_print(server.getNickname(invalidated.getVictim().getSession()) + " reshuffeled his/her deck with a chancellor."));
                        }else{
                            System.out.println("Non implemented branch of behaviour tree was reached, should not happen");
                        }
                    }
                }
            }else if(from.equals("hand_victim") && (to.equals("trashpile"))){
                if(foreach.equals("none")){
                    for(int ix = 0; ix < selectedIdentifiersFromThisInteractionCase.size(); ix++){
                        if(selectedIdentifiersFromThisInteractionCase.get(ix).startsWith("other")){
                        System.out.println("Chappeling id " + selectedIdentifiersFromThisInteractionCase.get(ix));
                        Long victimCardID = Long.parseLong(selectedIdentifiersFromThisInteractionCase.get(ix).split("_")[1]);
                        Card lost = invalidated.getVictim().loseCard(selectedCardNamesFromThisInteractionCase.get(ix), victimCardID);
                        env.trashpile.add(lost);
                        server.sendOne(JSONUtilities.JSON.make_client_lose(lost.getName(), victimCardID.toString()), invalidated.getVictim().getSession());
                        server.sendAll(JSONUtilities.JSON.make_client_print(server.getNickname(invalidated.getVictim().getSession()) + " trashed a " + lost.getName()));
                        }
                    }
                    server.sendAll(JSONUtilities.JSON.make_client_update_environment("trashpile", env.trashpile.size()));
                    server.sendAll(JSONUtilities.JSON.make_client_update_trashpile(env.trashpile.get(env.trashpile.size()-1)));
                }else if(foreach.equals("gain_treasure_costing_up_to_3_more")){
                    // Its a mine
                    for(int ix = 0; ix < selectedIdentifiersFromThisInteractionCase.size(); ix++){
                        if(selectedIdentifiersFromThisInteractionCase.get(ix).startsWith("other")){
                        System.out.println("Trashing id " + selectedIdentifiersFromThisInteractionCase.get(ix));
                        Long victimCardID = Long.parseLong(selectedIdentifiersFromThisInteractionCase.get(ix).split("_")[1]);
                        Card lost = invalidated.getVictim().loseCard(selectedCardNamesFromThisInteractionCase.get(ix), victimCardID);
                        env.trashpile.add(lost);
                        ArrayList<String> identifiers = new ArrayList<>();
                        ArrayList<String> cardnames = new ArrayList<>();
                        for(Card c : env.getAllBuyablesAsCards(lost.getCost() + 3)){
                            if(c instanceof TreasureCard){
                                invalidated.getVictim().getFutureInteraction().preloadedCards.add(c);
                                invalidated.getVictim().getFutureInteraction().allowedIds.add("environment");
                                invalidated.getVictim().getFutureInteraction().enableEnvTreasure();
                            }
                        }
                        invalidated.getVictim().getFutureInteraction().setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_mine(invalidated.getVictim(), invalidated.getInitiator(), invalidated.getVictim().getFutureInteraction()));
                        invalidated.getVictim().getFutureInteraction().setMaxCost(lost.getCost() + 3);
                        invalidated.getVictim().getFutureInteraction().setMinCost(0);
                        invalidated.getVictim().getFutureInteraction().setMinAmount(1);
                        invalidated.getVictim().getFutureInteraction().setMaxAmount(1);
                        
                        //invalidated.getVictim().getFutureInteraction().setStartBehaviour(JSONUtilities.JSON.update_client_confirmation_model(0, JSONUtilities.JSON.make_player_public_stats( invalidated.getVictim(),server.getClient(invalidated.getVictim().getSession()).getNickname(), identifiers, cardnames, false), invalidated.getInitiator().getFutureInteraction().getStartBehaviour()));
                       
                        server.sendOne(JSONUtilities.JSON.make_client_lose(lost.getName(), victimCardID.toString()), invalidated.getVictim().getSession());
                        server.sendAll(JSONUtilities.JSON.make_client_print(server.getNickname(invalidated.getVictim().getSession()) + " trashed a " + lost.getName()));
                        
                        
                        }
                    }
                    server.sendAll(JSONUtilities.JSON.make_client_update_environment("trashpile", env.trashpile.size()));
                    server.sendAll(JSONUtilities.JSON.make_client_update_trashpile(env.trashpile.get(env.trashpile.size()-1)));
                
                }else if(foreach.equals("gain3moneythisturn")){
                    Long victimCardID = Long.parseLong(selectedIdentifiersFromThisInteractionCase.get(0).split("_")[1]);
                    Card lost = invalidated.getVictim().loseCard(selectedCardNamesFromThisInteractionCase.get(0), victimCardID);
                    env.trashpile.add(lost);
                    this.money += 3;
                    server.sendOne(JSONUtilities.JSON.make_client_lose(lost.getName(), victimCardID.toString()), invalidated.getVictim().getSession());
                    server.sendAll(JSONUtilities.JSON.make_client_update_environment("trashpile", env.trashpile.size()));
                    server.sendAll(JSONUtilities.JSON.make_client_update_trashpile(env.trashpile.get(env.trashpile.size()-1)));
                    server.sendAll(JSONUtilities.JSON.make_client_turninfo(actions, purchases, money));
                    server.sendAll(JSONUtilities.JSON.make_client_print(server.getNickname(invalidated.getVictim().getSession()) + " trashed a " + lost.getName()));
                        
                }else if(foreach.equals("gain_anything_costing_up_to_2_more")){
                    // Its a remodel
                    for(int ix = 0; ix < selectedIdentifiersFromThisInteractionCase.size(); ix++){
                        if(selectedIdentifiersFromThisInteractionCase.get(ix).startsWith("other")){
                        System.out.println("Trashing id " + selectedIdentifiersFromThisInteractionCase.get(ix));
                        Long victimCardID = Long.parseLong(selectedIdentifiersFromThisInteractionCase.get(ix).split("_")[1]);
                        Card lost = invalidated.getVictim().loseCard(selectedCardNamesFromThisInteractionCase.get(ix), victimCardID);
                        env.trashpile.add(lost);
                        ArrayList<String> identifiers = new ArrayList<>();
                        ArrayList<String> cardnames = new ArrayList<>();
                        for(Card c : env.getAllBuyablesAsCards(lost.getCost() + 2)){
                                invalidated.getVictim().getFutureInteraction().preloadedCards.add(c);
                                invalidated.getVictim().getFutureInteraction().allowedIds.add("environment");
                                invalidated.getVictim().getFutureInteraction().enable_environment();
                        }
                        invalidated.getVictim().getFutureInteraction().setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_remodel(invalidated.getVictim(), invalidated.getInitiator(), invalidated.getVictim().getFutureInteraction()));
                        invalidated.getVictim().getFutureInteraction().setMaxCost(lost.getCost() + 2);
                        invalidated.getVictim().getFutureInteraction().setMinCost(0);
                        invalidated.getVictim().getFutureInteraction().setMinAmount(1);
                        invalidated.getVictim().getFutureInteraction().setMaxAmount(1);
                        
                        //invalidated.getVictim().getFutureInteraction().setStartBehaviour(JSONUtilities.JSON.update_client_confirmation_model(0, JSONUtilities.JSON.make_player_public_stats( invalidated.getVictim(),server.getClient(invalidated.getVictim().getSession()).getNickname(), identifiers, cardnames, false), invalidated.getInitiator().getFutureInteraction().getStartBehaviour()));
                       
                        server.sendOne(JSONUtilities.JSON.make_client_lose(lost.getName(), victimCardID.toString()), invalidated.getVictim().getSession());
                        server.sendAll(JSONUtilities.JSON.make_client_print(server.getNickname(invalidated.getVictim().getSession()) + " trashed a " + lost.getName()));
                        
                        
                        }
                    }
                    server.sendAll(JSONUtilities.JSON.make_client_update_environment("trashpile", env.trashpile.size()));
                    server.sendAll(JSONUtilities.JSON.make_client_update_trashpile(env.trashpile.get(env.trashpile.size()-1)));
                
                }
            }else if(from.equals("environment") && to.equals("discardpile_victim")){
                // Its probably a feast, or some feast-ish card
                if(foreach.equals("trash_my_tablecard")){
                    for(int ix = 0; ix < selectedIdentifiersFromThisInteractionCase.size(); ix++){
                        if(selectedIdentifiersFromThisInteractionCase.get(ix).startsWith("env")){
                        System.out.println("Feast used on " + selectedCardNamesFromThisInteractionCase.get(ix));
                        Card bought = env.environment_buy(selectedCardNamesFromThisInteractionCase.get(ix));
                        invalidated.getVictim().deck.used.add(bought);
                        
                        // Trash the feast from the table. 
                        Card feasttrash = env.tablecards.get(env.tablecards.size()-1);
                        env.tablecards.remove(env.tablecards.size()-1);
                        env.trashpile.add(feasttrash);
        //todododododod                // Update clients for tablecards and trashpile!!! todo
                        
                        make_client_update_discardpile(invalidated.getVictim());
                        make_clients_update_environment(bought.getName());
                        make_clients_update_environment("trashpile");
                        
                        server.sendAll(JSONUtilities.JSON.make_client_print(server.getNickname(invalidated.getVictim().getSession()) + " gained a " + bought.getName() + " with feast."));
                        
                        }
                    }
                }else if(foreach.equals("none")){
                    for(int ix = 0; ix < selectedIdentifiersFromThisInteractionCase.size(); ix++){
                        if(selectedIdentifiersFromThisInteractionCase.get(ix).startsWith("env")){
                        System.out.println("Remodel/workshop used on " + selectedCardNamesFromThisInteractionCase.get(ix));
                        Card bought = env.environment_buy(selectedCardNamesFromThisInteractionCase.get(ix));
                        invalidated.getVictim().deck.used.add(bought);
                        
                        make_client_update_discardpile(invalidated.getVictim());
                        make_clients_update_environment(bought.getName());
                        
                        server.sendAll(JSONUtilities.JSON.make_client_print(server.getNickname(invalidated.getVictim().getSession()) + " gained a " + bought.getName() + "."));
                        
                        }
                    }
                }
            }else if(from.equals("environment") && (to.equals("hand_victim"))){
                if(foreach.equals("none")){
                    for(int ix = 0; ix < selectedIdentifiersFromThisInteractionCase.size(); ix++){
                        if(selectedIdentifiersFromThisInteractionCase.get(ix).startsWith("env")){
                        Card gained = env.environment_buy(selectedCardNamesFromThisInteractionCase.get(ix));
                        invalidated.getVictim().hand.add(gained);
                        server.sendOne(JSONUtilities.JSON.make_client_gain(gained.getName()), invalidated.getVictim().getSession());
                        server.sendAll(JSONUtilities.JSON.make_client_update_environment(gained.getName(), env.environment_library.get(gained.getName()).size()));
                        }
                    }
                
                }
            }else if(from.equals("interaction") && (to.equals("hand_victim"))){
                for(int ix = 0; ix < selectedIdentifiersFromThisInteractionCase.size(); ix++){
                            Card thecard = selectedCardsFromThisInteractionCase.get(ix);
                            invalidated.getVictim().hand.add(thecard);
                        
                            JSONObject obj = JSONUtilities.JSON.make_client_print("Library gave you a : " + selectedCardNamesFromThisInteractionCase.get(ix));
                            server.getClient(invalidated.getVictim().getSession()).write(obj);
        
                            JSONObject act = JSONUtilities.JSON.make_client_gain(selectedCardNamesFromThisInteractionCase.get(ix));
                            server.getClient(invalidated.getVictim().getSession()).write(act);
                            make_client_update_deck(invalidated.getVictim());
                            make_client_update_discardpile(invalidated.getVictim());
                }
            }else if(from.equals("interaction") && (to.equals("throneroomqueue"))){
                            Card thecard = selectedCardsFromThisInteractionCase.get(0);
                            env.throneRoomQueue.offer(thecard);
                            env.throneRoomQueue.offer(thecard);
                            invalidated.getVictim().loseCard(thecard.getName(), Long.parseLong(selectedIdentifiersFromThisInteractionCase.get(0).split("_")[1]));
                            env.tablecards.add(thecard);
                            server.sendOne(JSONUtilities.JSON.make_client_lose(thecard.getName(), ((selectedIdentifiersFromThisInteractionCase.get(0).split("_")[1]))), invalidated.getVictim().getSession());
                            server.sendAll(JSONUtilities.JSON.make_client_print(server.getNickname(invalidated.getVictim().getSession()) + " lost a " + thecard.getName()));
                            server.sendAll(JSONUtilities.JSON.make_client_refresh_tableview());
                            
            }else if(from.equals("interaction") && (to.equals("top_deck_victim"))){
                if(foreach.equals("if_selected_then_discardpile_victim")){
                   HashMap<String, Card> mapping = new HashMap<>();
                   System.out.println("spying on " + invalidated.preloadedCards.size() + " cards");
                   System.out.println("however, there are " + invalidated.selectedSpecials.size() + " selected specials.");
                   System.out.println("also " + invalidated.selectedIds.size() + " selected ids, and " + invalidated.allowedIds.size() + " allowed ids");
                   
                   // Allowed : other_2, other_3
                   // selectedIds : other_0_0, other_1_1 -> door naar volgende interactie
                   // preloadedCards : other_0, other_1 -> deze moeten eigenlijk de specials zijn
                   // selectedSpecials : other_0, other_1 -> preloadedcards moeten discarded worden op basis van
                   
                   
                   
                   for(int ix = 0; ix < invalidated.preloadedCards.size(); ix++){
                        if(ix >= invalidated.allowedIds.size()) continue;
                        if(ix >= players.keySet().size()) continue;
                        int indexforvictim = ix;
                        Player p = indexForMe(invalidated.getInitiator(), ix);
                        System.out.println("got player " + server.getNickname(p.getSession()) + " from indexFor(" + server.getNickname(invalidated.getInitiator().getSession()) + " , " + ix + ");");
                        String searchID = "other_" + indexforvictim;
                        boolean isSelected = false;
                        Card card = invalidated.preloadedCards.get(ix);
                        String theid = searchID;
                        for(int ixix = 0; ixix < invalidated.selectedIds.size(); ixix++){
                             Card c = invalidated.selectedSpecials.get(ixix);
                             String id = invalidated.selectedIds.get(ixix);
                             
                             if(id.startsWith(searchID)){
                                 // Card is selected
                                 isSelected = true;
                                 card = c;
                                 invalidated.selectedSpecials.remove(ixix);
                                 invalidated.selectedIds.remove(ixix);
                                 break;
                             }
                        }
                        
                        if(isSelected){
                            p.deck.used.add(card);
                            System.out.println("ADDED " + card.getName() + " TO DISCARDPILE OF " + server.getNickname(p.getSession()));
                        }
                        else{
                            p.deck.addTopOfDeck(card);
                            System.out.println("ADDED " + card.getName() + " TO TOP OF DECK OF " + server.getNickname(p.getSession()));
                        }
                        
                        make_client_update_deck(p);
                        make_client_update_discardpile(p);
                    
                    }
                }
            }else if(from.equals("interaction") && to.equals("discardpile_victim")){
                if(foreach.equals("none")){
                    // WITCH
                   System.out.println("Witching on " + invalidated.preloadedCards.size() + " cards");
                   System.out.println("however, there are " + invalidated.selectedSpecials.size() + " selected specials.");
                   System.out.println("also " + invalidated.selectedIds.size() + " selected ids, and " + invalidated.allowedIds.size() + " allowed ids");
                   ArrayList<String> newSelectedIds = new ArrayList<>();
                   for(int ix = 0; ix < invalidated.selectedIds.size(); ix++){
                       Card c = invalidated.selectedSpecials.get(ix);
                       String id = invalidated.selectedIds.get(ix);
                       invalidated.getVictim().deck.used.add(c);
                       make_client_update_discardpile(invalidated.getVictim());
                   }
                   make_clients_update_cursepile();
                    
                }
                else if(foreach.equals("if_not_selected")){
                   HashMap<String, Card> mapping = new HashMap<>();
                   System.out.println("thieving on " + invalidated.preloadedCards.size() + " cards");
                   System.out.println("however, there are " + invalidated.selectedSpecials.size() + " selected specials.");
                   System.out.println("also " + invalidated.selectedIds.size() + " selected ids, and " + invalidated.allowedIds.size() + " allowed ids");
                   ArrayList<String> newSelectedIds = new ArrayList<>();
                   
                   for(String s : invalidated.selectedIds) System.out.println("selected id : " + s);
                   for(Card c : invalidated.selectedSpecials) System.out.println("selected card : " + c.getName());
                   for(Card c : invalidated.preloadedCards) System.out.println("preloadedcard name : " + c.getName());
                   for(String s : invalidated.allowedIds) System.out.println("allowed id : " + s);
                   for(String s : invalidated.fromPreviousInteraction.keySet()) System.out.println("fromprevious : " + s + " - " + invalidated.fromPreviousInteraction.get(s).getName());
                   HashMap<String, Card> to_next_interaction = new HashMap<>();
                   for(String previd : invalidated.fromPreviousInteraction.keySet()){
                       Card linked = invalidated.fromPreviousInteraction.get(previd);
                       int indexlinked = Integer.parseInt(previd.split("_")[1]);
                       Player victimlinked = indexForMe(invalidated.getInitiator(), indexlinked);
                       boolean isSelected = false;
                       for(int ix = 0; ix < invalidated.selectedSpecials.size(); ix++){
                           Card selected = invalidated.selectedSpecials.get(ix);
                           String selectid = invalidated.selectedIds.get(ix);
                           int selectedindexlinked = Integer.parseInt(selectid.split("_")[1]);
                           Player victimselectedlinked = indexForMe(invalidated.getInitiator(), selectedindexlinked);
                           if(indexlinked == selectedindexlinked){
                               if(linked.getName().equals(selected.getName())){
                                   // linked is selected from id victimlinked
                                   isSelected = true;
                                   invalidated.selectedSpecials.remove(ix);
                                   invalidated.selectedIds.remove(ix);
                                   break;
                               }
                           }
                           
                       }
                       if(isSelected){
                           
                           to_next_interaction.put(previd, linked);
                       }else{
                           victimlinked.deck.used.add(linked);
                           make_client_update_discardpile(victimlinked);
                       }
                   }
                   invalidated.selectedIds.clear();
                   invalidated.selectedSpecials.clear();
                   for(String s : to_next_interaction.keySet()){
                       invalidated.selectedIds.add(s);
                       invalidated.selectedSpecials.add(to_next_interaction.get(s));
                   }
                }
            }else if(from.equals("interaction") && (to.equals("trashpile"))){
                if(foreach.equals("if_selected_then_gain")){
                   System.out.println("thieving on " + invalidated.preloadedCards.size() + " cards");
                   System.out.println("however, there are " + invalidated.selectedSpecials.size() + " selected specials.");
                   System.out.println("also " + invalidated.selectedIds.size() + " selected ids, and " + invalidated.allowedIds.size() + " allowed ids");
                   
                   for(String previd : invalidated.fromPreviousInteraction.keySet()){
                       Card linked = invalidated.fromPreviousInteraction.get(previd);
                       int indexlinked = Integer.parseInt(previd.split("_")[1]);
                       Player victimlinked = indexForMe(invalidated.getInitiator(), indexlinked);
                       
                       for(int ix = 0; ix < invalidated.selectedSpecials.size(); ix++){
                           Card selected = invalidated.selectedSpecials.get(ix);
                           String selectid = invalidated.selectedIds.get(ix);
                           int selectedindexlinked = Integer.parseInt(selectid.split("_")[0]);
                           Player victimselectedlinked = indexForMe(invalidated.getInitiator(), selectedindexlinked);
                           if(indexlinked == selectedindexlinked){
                               if(linked.getName().equals(selected.getName())){
                                   // linked is selected from id victimlinked
                               }
                           }
                       }
                   }
                   
                   ArrayList<String> selectedIdReal = new ArrayList<>();
                   for(String s : invalidated.selectedIds){
                       System.out.println("selectedid : " + s);
                       selectedIdReal.add(s.split("_")[0] + "_" + s.split("_")[1]);
                   }
                   invalidated.selectedIds = selectedIdReal;
                   // IDCA IS UNIQUE
                   ArrayList<String> notselectedcardids = new ArrayList<>();
                   ArrayList<Card> notselectedcards = new ArrayList<>();
                   
                   for(int ix = 0; ix < invalidated.preloadedCards.size(); ix++){
                       Card ca = invalidated.preloadedCards.get(ix);
                       if(ix >= invalidated.allowedIds.size()) continue;
                       String idca = invalidated.allowedIds.get(ix);
                       boolean isselected = false;
                       for(int ixi = 0; ixi < invalidated.selectedIds.size(); ixi++){
                           String idselected = invalidated.selectedIds.get(ixi);
                           if(idselected.equals(idca)){
                               isselected = true;
                               break;
                           }
                       }
                       if(!isselected){
                           notselectedcardids.add(idca);
                           notselectedcards.add(ca);
                       }
                   }
                   for(int ix = 0; ix < notselectedcards.size(); ix++){
                       Card notselectedcard = notselectedcards.get(ix);
                       String notselectedcardid = notselectedcardids.get(ix);
                       String indexforvictim = notselectedcardid.split("_")[1];
                       Player victim = indexForMe(invalidated.getInitiator(), Integer.parseInt(indexforvictim));
                       env.trashpile.add(notselectedcard);
                       make_clients_update_environment("trashpile");
                       System.out.println("trashed a " + notselectedcard.getName());
                   }
                   
                   for(int ix = 0; ix < invalidated.selectedSpecials.size(); ix++){
                       Card selectedcard = invalidated.selectedSpecials.get(ix);
                       String selectedcardid = invalidated.selectedIds.get(ix);
                       String indexforvictim = selectedcardid.split("_")[1];
                       Player victim = invalidated.getInitiator();
                       victim.deck.used.add(selectedcard);
                       make_client_update_discardpile(victim);
                       System.out.println("discarded a " + selectedcard.getName() + " to player[" + victim.getSession() + "] discardpile");
                   }
                }
            }
            // MoveSomething currently moves everything. 
            
            // hand_victim
            // top_of_deck_victim
            
            
            
        }
        
        
        boolean allDone = true;
        for(Player p : players.values()){
            if((p.getCurrentInteraction() != null) && (!p.getCurrentInteraction().isConfirmed())) allDone = false;
        }
        
        if(allDone){
            // Check if initiator needs another interaction!
            // If not, check if he has other actions. Because if not, enterBuyPhase. 
            System.out.println("Flushing interactioncases to next stage");
            
            for(Player p : players.values()){
                p.setInteraction(null);
            }
            // Not now, to test flow. 
            boolean futures = false;
            for(Player p : players.values()){
                if(p.getFutureInteraction() != null) futures = true;
                if(p.hasIterativeInteractionFactory()) futures = true;
            }
            System.out.println("Found a future!");
            // Flush futures to current if there are any, and send them.
            if(futures){
                for(Player p : players.values()){
                    if(p.getFutureInteraction() == null){
                        if(!p.hasIterativeInteractionFactory()){
                            // this player has no iterative interaction factory
                        }else{
                            p.setInteraction(p.getIterativeInteraction(invalidated, env));
                            p.getCurrentInteraction().addListener(this);
                            server.sendOne(p.getCurrentInteraction().getStartBehaviour(), p.getSession());
                        }
                    }else{
                        p.setInteraction(p.getFutureInteraction());
                        p.setFutureInteraction(null);
                        if(p.getCurrentInteraction() != null){
                            p.getCurrentInteraction().addListener(this);
                            server.sendOne(p.getCurrentInteraction().getStartBehaviour(), p.getSession());
                        }
                    }
                }
            }else{
                interactionmode = false;
                
                if(!env.throneRoomQueue.isEmpty()){
                    handleAction(env.throneRoomQueue.poll(), players.get(playerOrder.get(current_turn)));
                }
                else if(!invalidated.getInitiator().hasActionCard() || this.actions == 0) enterBuyPhase();
                else{
                    highlight_actions(players.get(playerOrder.get(current_turn)));
                }
            }
            
        }
        // All interactions closed? Make an initiator interactioncase maybe?
        // Check finishbehaviour!
    }
    }
    
    public void addInteractionCaseFor(InteractionCase interaction){
        interaction.addListener(this);
        interaction.getVictim().setInteraction(interaction);
    }
    
    public void removeInteractionCaseFor(InteractionCase interaction){
        interaction.getVictim().setInteraction(null);
    }

    public void interactionConfirmRequest(JSONObject json) {
        Player confirmed_player = players.get(json.getString("session"));
        if(confirmed_player.getCurrentInteraction() == null){
            // Its an info-model. Client may close. 
            server.sendOne(JSONUtilities.JSON.make_client_close_confirmation(), json.getString("session"));
            return;
        }
        InteractionCase ic = confirmed_player.getCurrentInteraction();
        System.err.println("--> Confirming interaction success: " + ic.isValid());
        if(ic.isValid()){
            server.getClient(confirmed_player.getSession()).write(JSONUtilities.JSON.make_client_close_confirmation());
            ic.confirm();
        }else{
            server.getClient(confirmed_player.getSession()).write(JSONUtilities.JSON.make_client_print("Your interaction is not valid yet!"));
        }
    }
    
    private int myIndexFor(Player me, Player other){
        int myIndex = -1;
        int yourIndex = -1;
        int index = 0;
        for(String s : playerOrder.values()){
            if(s.equals(me.getSession())) myIndex = index;
            if(s.equals(other.getSession())) yourIndex = index;
            index ++;
        }
        
        if(myIndex < yourIndex) return myIndex;
        else return myIndex -1;
    }
    private Player indexForMe(Player p, int a){
        int index = 0;
        int myIndex = -1;
        for(String s : playerOrder.values()){
            System.out.println("session with index " + index + " = " + s);
            if(s.equals(p.getSession())) myIndex = index;
            index ++;
        }
        System.out.println("MY INDEX IS: " + myIndex);
        
        if(myIndex <= a) return players.get(playerOrder.get(a+1));
        else return players.get(playerOrder.get(a));
    }
    public void showGameEndedTo(String session) {
        Player me = players.get(session);
        Player[] pl = new Player[3];
        String[] nick = new String[3];
        ArrayList<Card>[] victories = new ArrayList[3];
        String nicknameMostPoints = "";
        int mostPoints = -1;
        for(int i = 0; i < 3; i++){
            pl[i] = null;
            nick[i] = null;
            victories[i] = new ArrayList<>();
        }
        for(Player p : players.values()){
            if(p.getSession().equals(me.getSession())){
                if(p.calculateVictoryPoints() > mostPoints){
                    nicknameMostPoints = server.getNickname(p.getSession());
                    mostPoints = p.calculateVictoryPoints();
                }else if(p.calculateVictoryPoints() == mostPoints){
                    nicknameMostPoints += " and " + server.getNickname(p.getSession());
                }
                continue;
            };
            int index = myIndexFor(p, me);
            String name = server.getClient(p.getSession()).getNickname();
            pl[index] = p;
            nick[index] = name;
            victories[index] = p.getAllVictoryCards();
            int victorypoints = p.calculateVictoryPoints();
            if(victorypoints > mostPoints){
                mostPoints = victorypoints;
                nicknameMostPoints = server.getClient(p.getSession()).getNickname();
            }else if(victorypoints == mostPoints){
                nicknameMostPoints += " and " + server.getClient(p.getSession()).getNickname();
            }
        }
        JSONObject info = JSONUtilities.JSON.make_client_infomodel(pl, nick, victories, nicknameMostPoints);
        server.sendOne(info, session);
    }
    
    public void showPlayerInfoTo(String session) {
        Player me = players.get(session);
        Player[] pl = new Player[3];
        String[] nick = new String[3];
        for(int i = 0; i < 3; i++){
            pl[i] = null;
            nick[i] = null;
        }
        for(Player p : players.values()){
            if(p.getSession().equals(me.getSession())) continue;
            int index = myIndexFor(p, me);
            String name = server.getClient(p.getSession()).getNickname();
            pl[index] = p;
            nick[index] = name;
        }
        JSONObject info = JSONUtilities.JSON.make_client_infomodel(pl, nick);
        server.sendOne(info, session);
    }

    private void make_clients_update_cursepile() {
        server.sendAll(JSONUtilities.JSON.make_client_update_environment("cursepile", env.environment_amountcheck("curse")));
        server.sendAll(JSONUtilities.JSON.make_client_print("there are " + env.environment_amountcheck("curse") + " " + "curse" + "'s" + " left on the table."));
    }
    
    
    
}
// MANUAL MERGE