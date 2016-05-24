/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;

import Cards.Components.Card;
import Game.InteractionCase;
import Game.Player;
import java.util.ArrayList;
import java.util.Set;
import org.json.*;

/**
 *
 * @author admin
 */
public class JSONUtilities {
    public static JSONUtilities JSON = new JSONUtilities();
    private JSONUtilities(){
        
    }
    
    public JSONObject toJSON(String msg){
        JSONObject o = null;
        try{
            o = new JSONObject(msg);
        }catch(JSONException e){
            // Cast exception: msg is not a valid jsonobject.
            System.err.println("cast2json error: " + e);
            return null;
        }catch(NullPointerException npe){
            System.err.println("cast2json error: nullpointer message");
        }
        return o;
    }
    
    public boolean isJSON(String msg){
        return (toJSON(msg) != null);
    }
    
    public String toString(JSONObject obj){
        return obj.toString();
    }
    
    public JSONObject addKeyValuePair(String key, String value, JSONObject json){
        json.put(key, value);
        return json;
    }
    
    public String addKeyValuePair(String key, String value, String json){
        JSONObject obj = toJSON(json);
        obj.put(key, value);
        return obj.toString();
    }
    
    public JSONObject create(String key, String value){
        JSONObject json = new JSONObject();
        json.put(key, value);
        return json;
    }
    public JSONObject notify_shutdown(){
        JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
        obj = JSONUtilities.JSON.addKeyValuePair("sysout", "Server shutting down. You will be disconnected.", obj);
        return obj;
    }
    public JSONObject make_clients_notify_disconnect(String nickname){
        JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
        obj = JSONUtilities.JSON.addKeyValuePair("sysout", "Client [" + nickname + "] disconnected.", obj);
        return obj;
    }
    public JSONObject make_server_disconnect_client_from_lobby(String session, String nickname){
        JSONObject lobbyDisconnect = JSONUtilities.JSON.create("service_type", "lobby");
        lobbyDisconnect = JSONUtilities.JSON.addKeyValuePair("session", session, lobbyDisconnect);
        lobbyDisconnect = JSONUtilities.JSON.addKeyValuePair("author", nickname, lobbyDisconnect);
        lobbyDisconnect = JSONUtilities.JSON.addKeyValuePair("operation", "disconnect", lobbyDisconnect);
        return lobbyDisconnect;
    }
    public JSONObject make_client_print(String message){
        JSONObject json = create("action", "sysout");
        json = addKeyValuePair("sysout", message, json);
        return json;
    }
    public JSONObject make_client_gain(String cardname){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "gain", json);
        json = addKeyValuePair("gain", cardname, json);
        return json;
    }
    public JSONObject make_client_lose(String cardname, String id){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "lose", json);
        json = addKeyValuePair("lose", cardname, json);
        json = addKeyValuePair("loseID", id, json);
        return json;
    }
    public JSONObject make_client_turninfo(int actions, int purchases, int money){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "turninfo", json);
        json = addKeyValuePair("actioncount", actions + "", json);
        json = addKeyValuePair("purchasecount", purchases + "", json);
        json = addKeyValuePair("money", money + "", json);
        return json;
    }
    public JSONObject make_client_hand_valid(Set<String> cards){
        String cardEnumeration = "";
        for(String s : cards){
            cardEnumeration += "," + s;
        }
        cardEnumeration = cardEnumeration.substring(1); // Kap eerste komma weg, gaat sneller dan laatste
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "control", json);
        json = addKeyValuePair("subject", "hand", json);
        json = addKeyValuePair("items", cardEnumeration, json);
        json = addKeyValuePair("control", "clickable", json);
        return json;
    }
    public JSONObject make_client_hand_invalid(){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "control", json);
        json = addKeyValuePair("subject", "hand", json);
        json = addKeyValuePair("items", "all", json);
        json = addKeyValuePair("control", "unclickable", json);
        return json;
    }
    public JSONObject make_client_show_cardplayed(String cardname){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "control", json);
        json = addKeyValuePair("subject", "table", json);
        json = addKeyValuePair("control", "display", json);
        json = addKeyValuePair("items", cardname, json);
        return json;
    }
    public JSONObject make_client_nextphase_possible(){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "control", json);
        json = addKeyValuePair("subject", "phase", json);
        json = addKeyValuePair("control", "clickable", json);
        return json;
    }
    public JSONObject make_client_nextphase_impossible(){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "control", json);
        json = addKeyValuePair("subject", "phase", json);
        json = addKeyValuePair("control", "unclickable", json);
        return json;
    }
    public JSONObject make_client_update_environment(String stack_identifier, Integer new_count){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "control", json);
        json = addKeyValuePair("subject", "environment", json);
        json = addKeyValuePair("control", "updatecount", json);
        json = addKeyValuePair("stack", stack_identifier, json);
        json = addKeyValuePair("update", new_count.toString(), json);
        return json;
    }
    public JSONObject make_client_initialize_environment(String stack_identifier, String cardname, Integer count){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "control", json);
        json = addKeyValuePair("subject", "environment", json);
        json = addKeyValuePair("control", "init", json);
        json = addKeyValuePair("stack", stack_identifier, json);
        json = addKeyValuePair("update", count.toString(), json);
        json = addKeyValuePair("identifier", cardname, json);
        return json;
    }
    public JSONObject make_client_environment_valid(Set<Card> cards){
        String items = "";
        for(Card c : cards){
            items += "," + c.getName();
        }
        items = items.substring(1);
        
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "control" ,json);
        json = addKeyValuePair("subject", "environment", json);
        json = addKeyValuePair("items", items, json);
        json = addKeyValuePair("control", "clickable", json);
        return json;
    }
    public JSONObject make_client_environment_invalid(){
        String items = "all";
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "control", json);
        json = addKeyValuePair("subject", "environment", json);
        json = addKeyValuePair("items", items, json);
        json = addKeyValuePair("control", "unclickable", json);
        return json;
    }
    public JSONObject make_client_environment_valid(String items){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "control" ,json);
        json = addKeyValuePair("subject", "environment", json);
        json = addKeyValuePair("items", items, json);
        json = addKeyValuePair("control", "clickable", json);
        return json;
    }
    public JSONObject make_client_update_discardpile(String last_discarded_card){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "control", json);
        json = addKeyValuePair("subject", "environment", json);
        json = addKeyValuePair("control", "updateview", json);
        json = addKeyValuePair("stack", "discardpile", json);
        json = addKeyValuePair("update", last_discarded_card, json);
        return json;
    }
    
    public JSONObject make_client_update_trashpile(Card get) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "control", json);
        json = addKeyValuePair("subject", "environment", json);
        json = addKeyValuePair("control", "updateview", json);
        json = addKeyValuePair("stack", "trashpile", json);
        json = addKeyValuePair("update", get.getName(), json);
        return json;
    }
    
    public JSONObject make_client_conformation_model_empty(String info){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", info, json);
        json = addKeyValuePair("stat0", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json; 
    }
    
    public JSONObject update_client_confirmation_model(int entry, JSONObject playerstats, JSONObject confirmationmodel){
        confirmationmodel = addKeyValuePair("stat"+entry, playerstats.toString(), confirmationmodel);
        return confirmationmodel;
    }
        
    public JSONObject make_client_confirmation_model_bureaucrat_victim_hasvictorycard(Player init, Player victim, InteractionCase ic){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "Show a victorycard from your hand and put it on top of your deck.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        for(Card c : ic.preloadedCards) preloadedcardnames.add(c.getName());
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", ic.allowedIds, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json; 
    }
    public JSONObject make_client_confirmation_model_bureaucrat_victim_has_no_victorycard(Player init, Player victim, InteractionCase ic){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "You must show your whole hand to the attacker.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        for(Card c : ic.preloadedCards) preloadedcardnames.add(c.getName());
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", ic.allowedIds, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json; 
    }
    public JSONObject make_client_confirmation_model_attackblock(Player init, Player victim, InteractionCase ic){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "You can block the attack by playing a reactioncard.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        for(Card c : ic.preloadedCards) preloadedcardnames.add(c.getName());
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", ic.allowedIds, preloadedcardnames, true).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json; 
    }
    public JSONObject make_player_public_stats(Player p, String nickname, ArrayList<String> identifiers, ArrayList<String> cardnames, boolean blocked){
        return make_player_public_stats(nickname, p, p.deck.content.size(), p.deck.used.size(), p.hand.size(), p.calculateVictoryPoints(),blocked, identifiers, cardnames);
    }
  
    public JSONObject make_player_public_stats(String nickname, Player p, int decksize, int discardsize, int handsize, int victorypoints, boolean attackblocked, ArrayList<String> identifiers, ArrayList<String> cardnames){
        JSONObject json = create("nickname", nickname);
        json = addKeyValuePair("decksize", decksize + "", json);
        json = addKeyValuePair("discardsize", discardsize + "", json);
        json = addKeyValuePair("handsize", handsize + "", json);
        json = addKeyValuePair("victorypoints", victorypoints + "", json);
        json = addKeyValuePair("blocked", (attackblocked)?("1"):("0"), json);
        json = addKeyValuePair("entrycount", ((cardnames==null)?(0):(cardnames.size())) + "", json);
        if(cardnames==null) return json;
        for(int i = 0; i < cardnames.size(); i++){
            json = addKeyValuePair("cardname_" + i, cardnames.get(i), json);
            json = addKeyValuePair("identifier_" + i, identifiers.get(i), json);
        }
        return json;
    }
    public JSONObject make_player_public_stats_null(){
        return make_player_public_stats("unused",null, 0, 0, 0, 0, true, null, null);
    }
    
    public JSONObject make_client_close_confirmation(){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm_end", json);
        return json;
    }
    /**
     * SECTION REGARDING ACTION CARD BEHAVIOUR
     * @return rewardcase behaviour object
     */
    
    public JSONObject bureaucrat_standard_reward(){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("type", "env_gain", json);
        json = addKeyValuePair("dest", "top_of_deck", json);
        json = addKeyValuePair("identifier", "silver", json);
        return json;
    }
   
    public JSONObject create_interaction_finishbehaviour(Boolean showToInitiator, Boolean moveSomething, String pullFrom, String addTo, String foreach){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("showToInitiator", showToInitiator.toString(), json);
        json = addKeyValuePair("moveSomething", moveSomething.toString(), json);
        json = addKeyValuePair("pullFrom", pullFrom, json);
        json = addKeyValuePair("addTo", addTo, json);
        json = addKeyValuePair("iterative_action", foreach, json);
        return json;
    }
    public JSONObject bureaucrat_victim_hasvictorycard_finishbehaviour(){
        return create_interaction_finishbehaviour(true, true, "hand_victim", "top_of_deck_victim", "none"); 
    }
    public JSONObject bureaucrat_victim_hasnovictorycard_finishbehaviour(){
        return create_interaction_finishbehaviour(true, false, "none", "none", "none");
    }
    public JSONObject create_interaction_blocked_finishbehaviour(){
        return create_interaction_finishbehaviour(true, false, "none", "none", "none");
    }
    public JSONObject create_interaction_finishbehaviour_empty(){
        return create_interaction_finishbehaviour(false, false, "none", "none", "none");
    }

    public JSONObject garden_standard_reward() {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("type", "victory_gain", json);
        json = addKeyValuePair("amount", "based_on_deck", json);
        json = addKeyValuePair("fraction", "10", json); 
        json = addKeyValuePair("rounding", "down", json);
        return json;
    }

    public JSONObject adventurer_finishbehaviour() {
        return create_interaction_finishbehaviour(false, true, "none", "hand_victim", "none");
    }

    public JSONObject make_client_confirmation_model_adventurer(Player victim, Player initiator, InteractionCase onself) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "You draw cards until you draw 2 treasure cards. Add them to your hand, discard the rest.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        for(Card c : onself.preloadedCards) preloadedcardnames.add(c.getName());
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", onself.allowedIds, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;
    }

    public JSONObject cellar_finishbehaviour() {
        return create_interaction_finishbehaviour(false, true, "hand_victim", "discardpile_victim", "draw_card");
    }

    public JSONObject make_client_confirmation_model_cellar(Player victim, Player initiator, InteractionCase onself) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "Discard any number of cards. You may then draw an equal amount of cards you discarded.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        for(Card c : onself.preloadedCards) preloadedcardnames.add(c.getName());
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", onself.allowedIds, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;
    }

    public JSONObject make_client_confirmation_model_chancellor(Player victim, Player initiator, InteractionCase onself) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "You may discard your deck. Selecting the chancellor before confirming will discard your deck.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        for(Card c : onself.preloadedCards) preloadedcardnames.add(c.getName());
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", onself.allowedIds, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;
    }

    public JSONObject chancellor_finishbehaviour() {
        return create_interaction_finishbehaviour(false, true, "deck_victim", "discardpile_victim", "until_deck_discarded");
    }

    public JSONObject chapel_finishbehaviour() {
        return create_interaction_finishbehaviour(false, true, "hand_victim", "trashpile", "none");
    }

    public JSONObject make_client_confirmation_model_chapel(Player victim, Player initiator, InteractionCase onself) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "You may trash up to 4 cards from your hand.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        for(Card c : onself.preloadedCards) preloadedcardnames.add(c.getName());
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", onself.allowedIds, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;
    }

    public JSONObject feast_finishbehaviour() {
        return create_interaction_finishbehaviour(false, true, "environment", "discardpile_victim", "trash_my_tablecard");
    }

    public JSONObject make_client_confirmation_model_feast(Player victim, Player initiator, InteractionCase onself) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "You may choose one card costing up to 5, and put it on top of your discardpile. Feast will be trashed.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        for(Card c : onself.preloadedCards) preloadedcardnames.add(c.getName());
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", onself.allowedIds, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;
    }

    public JSONObject militia_notblocked_finishbehaviour() {
        return create_interaction_finishbehaviour(false, true, "hand_victim", "discardpile_victim", "none");
    }

    public JSONObject make_client_confirmation_model_militia_notblocked(Player victim, Player initiator, InteractionCase ic) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "You are being attacked by a militia! Discard down to three cards.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        for(Card c : ic.preloadedCards) preloadedcardnames.add(c.getName());
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", ic.allowedIds, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;
    }

    public JSONObject make_client_confirmation_model_militia_blocked(Player victim, Player initiator, InteractionCase ic) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "You are being attacked by a militia, but you can block it!", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        for(Card c : ic.preloadedCards) preloadedcardnames.add(c.getName());
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", ic.allowedIds, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;
    }
    
    

    public JSONObject mine_finishbehaviour() {
        return create_interaction_finishbehaviour(false, true, "hand_victim", "trashpile", "gain_treasure_costing_up_to_3_more");
    }

    public JSONObject make_client_confirmation_model_mine(Player victim, Player initiator, InteractionCase ic) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "You trash a treasurecard from your hand. You can then gain a treasurecard costing up to 3 more, put directly into your hand.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        for(Card c : ic.preloadedCards) preloadedcardnames.add(c.getName());
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", ic.allowedIds, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;
    }

    public JSONObject create_interaction_finishbehaviour_mine_future() {
        return create_interaction_finishbehaviour(false, true, "environment", "hand_victim", "none");
    }

    public JSONObject library_notfinished_finishbehaviour() {
        return create_interaction_finishbehaviour(false, false, "none", "none", "none");
    }

    public JSONObject make_client_confirmation_library(Player victim, Player initiator, InteractionCase ic) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "You are drawing up to 7 cards, you can discard any incoming actioncards.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        ArrayList<String> preloadedids = new ArrayList<>();
        for(Card c : ic.preloadedCards) {
            preloadedcardnames.add(c.getName());
            preloadedids.add("interaction");
        }
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", preloadedids, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;
    }
   
    public JSONObject library_finished_finishbehaviour() {
       return create_interaction_finishbehaviour(false, true, "interaction", "hand_victim", "none");
    }

    public JSONObject make_client_confirmation_library_finish(Player victim, Player initiator, InteractionCase ic) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "Library finished, please confirm.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        ArrayList<String> preloadedids = new ArrayList<>();
        for(Card c : ic.preloadedCards) {
            preloadedcardnames.add(c.getName());
            preloadedids.add("interaction");
        }
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", preloadedids, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;
    }

    public JSONObject moneylender_finishbehaviour() {
        return create_interaction_finishbehaviour(false, true, "hand_victim", "trashpile", "gain3moneythisturn");
    }

    public JSONObject make_client_confirmation_model_moneylender(Player victim, Player initiator, InteractionCase ic) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "Moneylender: trash a copper. After that, you will gain +3 money this turn.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        ArrayList<String> preloadedids = new ArrayList<>();
        for(Card c : ic.preloadedCards) {
            preloadedcardnames.add(c.getName());
            preloadedids.add("hand");
        }
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", preloadedids, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;
    }

    public JSONObject remodel_finishbehaviour() {
        return create_interaction_finishbehaviour(false, true, "hand_victim", "trashpile", "gain_anything_costing_up_to_2_more");
    }

    public JSONObject make_client_confirmation_model_remodel(Player victim, Player initiator, InteractionCase ic) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "You trash a card from your hand. You can then gain a card costing up to 2 more. Add it to your discardpile.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        for(Card c : ic.preloadedCards) preloadedcardnames.add(c.getName());
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", ic.allowedIds, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;
    }

    public JSONObject create_interaction_finishbehaviour_remodel_future() {
        return create_interaction_finishbehaviour(false, true, "environment", "discardpile_victim", "none");
    }

    public JSONObject workshop_finishbehaviour() {
        return create_interaction_finishbehaviour(false, true, "environment", "discardpile_victim", "none");
    }

    public JSONObject make_client_confirmation_model_workshop(Player victim, Player initiator, InteractionCase onself) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "You may choose one card costing up to 4, and put it on top of your discardpile.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        for(Card c : onself.preloadedCards) preloadedcardnames.add(c.getName());
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", onself.allowedIds, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;
    }

    public JSONObject throneroom_finishbehaviour() {
        return create_interaction_finishbehaviour(false, true, "interaction", "throneroomqueue", "throneroomspecial");
    }

    public JSONObject make_client_confirmation_model_throneroom(Player victim, Player initiator, InteractionCase ic) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "You may choose an action card. Play it twice.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        for(Card c : ic.preloadedCards) preloadedcardnames.add(c.getName());
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", ic.allowedIds, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;
    }

    public JSONObject spy_standard_finishbehaviour() {
        return create_interaction_finishbehaviour(true, false, "none", "none", "none");

    }

    public JSONObject make_client_confirmation_model_spy_notblocked(Player victim, Player initiator, InteractionCase ic) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "You are being attacked by spy! This is the top card of your deck. Your attacker might discard it.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        for(Card c : ic.preloadedCards) preloadedcardnames.add(c.getName());
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", ic.allowedIds, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;
    }

    public JSONObject create_interaction_finishbehaviour_spy() {
        return create_interaction_finishbehaviour(false, true, "interaction", "top_deck_victim", "if_selected_then_discardpile_victim");
    }

    public JSONObject create_interaction_finishbehaviour_thief() {
        return create_interaction_finishbehaviour(false, true, "interaction", "discardpile_victim", "if_not_selected");
    }

    public JSONObject thief_future_finished_finishbehaviour() {
        return create_interaction_finishbehaviour(false, true, "interaction", "trashpile", "if_selected_then_gain");
    }

    public JSONObject make_client_confirmation_thief_future_finish(Player victim, Player initiator, InteractionCase ic) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "Choose the cards you'd like to gain. The others will be trashed.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        for(Card c : ic.preloadedCards) preloadedcardnames.add(c.getName());
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", ic.allowedIds, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;
    }

  
    public JSONObject make_client_infomodel(Player[] p, String[] nickname){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "Environment information", json);
        for(int i = 0; i < 3; i++){
            if(p[i] != null) json = addKeyValuePair("stat" + i, make_player_public_stats(p[i], nickname[i], null, null, false).toString(),json);
            else json = addKeyValuePair("stat" + i, make_player_public_stats_null().toString(), json);
        }
        return json;
    }
    
    
    // MANUAL MERGE ??? EMIEL HAS NEW CODE, DO I HAVE EMIELS CODE?

    public JSONObject witch_standard_finishbehaviour() {
        return create_interaction_finishbehaviour(true, true, "interaction", "discardpile_victim", "none");
    }

    public JSONObject make_client_confirmation_model_witch_notblocked(Player victim, Player initiator, InteractionCase ic) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "You gain a curse.", json);
        ArrayList<String> preloadedcardnames = new ArrayList<>();
        for(Card c : ic.preloadedCards) preloadedcardnames.add(c.getName());
        json = addKeyValuePair("stat0", make_player_public_stats(victim, "You", ic.allowedIds, preloadedcardnames, false).toString(),json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;        
    }

    public JSONObject make_client_update_tablecards(Card c) {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "control", json);
        json = addKeyValuePair("subject", "table", json);
        json = addKeyValuePair("control", "add", json);
        json = addKeyValuePair("cardname", c.getName(), json);
        return json;
    }

    public JSONObject make_client_refresh_tableview() {
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "control", json);
        json = addKeyValuePair("subject", "table", json);
        json = addKeyValuePair("control", "clear", json);
        return json;
    }
    
    public JSONObject make_client_infomodel(Player[] p, String[] nickname, ArrayList<Card>[] victories, String nickname_winner){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", "Game ended! " + nickname_winner + " won.", json);
        for(int i = 0; i < 3; i++){
            ArrayList<String> identifiers = new ArrayList<>();
            ArrayList<String> cards = new ArrayList<>();
            for(int j = 0; j < victories[i].size(); j++){
                identifiers.add("victory");
                cards.add(victories[i].get(j).getName());
            }
            if(p[i] != null) json = addKeyValuePair("stat" + i, make_player_public_stats(p[i], nickname[i], identifiers, cards, false).toString(),json);
            else json = addKeyValuePair("stat" + i, make_player_public_stats_null().toString(), json);
        }
        return json;
    }
}
