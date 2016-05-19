/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;

import Cards.Components.Card;
import Game.InteractionCase;
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
    
    public JSONObject make_client_conformation_model_empty(String info){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", info, json);
        json = addKeyValuePair("stat0", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json; 
    }
    
    public JSONObject make_client_conformation_model_test(String info){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", info, json);
        json = addKeyValuePair("stat0", make_player_public_stats_test().toString(), json);
        json = addKeyValuePair("stat1", make_player_public_stats_test().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_test().toString(), json);
        return json; 
    }
    
    public JSONObject make_player_public_stats_test(){
        ArrayList<String> identifiers = new ArrayList<String>();
        ArrayList<String> cardnames = new ArrayList<String>();
        identifiers.add("envrionment");
        cardnames.add("estate");
        identifiers.add("envrionment");
        cardnames.add("gold");
        identifiers.add("envrionment");
        cardnames.add("market");
        identifiers.add("hand");
        cardnames.add("copper");
        identifiers.add("deck");
        cardnames.add("estate");
        identifiers.add("discardpile");
        cardnames.add("back");
        identifiers.add("other");
        cardnames.add("estate");
        
        return make_player_public_stats("unused", 0, 0, 0, 0, true, identifiers, cardnames);
        
    }
    
    public JSONObject make_player_public_stats(String nickname, int decksize, int discardsize, int handsize, int victorypoints, boolean attackblocked, ArrayList<String> identifiers, ArrayList<String> cardnames){
        JSONObject json = create("nickname", nickname);
        json = addKeyValuePair("decksize", decksize + "", json);
        json = addKeyValuePair("discardsize", discardsize + "", json);
        json = addKeyValuePair("handsize", handsize + "", json);
        json = addKeyValuePair("victorypoints", victorypoints + "", json);
        json = addKeyValuePair("blocked", (attackblocked)?("1"):("0"), json);
        for(int i = 0; i < cardnames.size(); i++){
            json = addKeyValuePair(identifiers.get(i), cardnames.get(i), json);
        }
        return json;
    }
    public JSONObject make_player_public_stats_null(){
        return make_player_public_stats("unused", 0, 0, 0, 0, true, null, null);
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
    
    
    
    
}
