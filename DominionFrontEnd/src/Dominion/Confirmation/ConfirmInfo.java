/*
    public JSONObject make_client_confirmation_model_empty(String info){
        JSONObject json = create("action", "dominion");
        json = addKeyValuePair("act", "confirm", json);
        json = addKeyValuePair("info", info, json);
        json = addKeyValuePair("stat0", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat1", make_player_public_stats_null().toString(), json);
        json = addKeyValuePair("stat2", make_player_public_stats_null().toString(), json);
        return json;
        
    }
    
    public JSONObject make_player_public_stats(String nickname, int decksize, int discardsize, int handsize, int victorypoints, boolean attackblocked){
        JSONObject json = create("nickname", nickname);
        json = addKeyValuePair("decksize", decksize + "", json);
        json = addKeyValuePair("discardsize", discardsize + "", json);
        json = addKeyValuePair("handsize", handsize + "", json);
        json = addKeyValuePair("victorypoints", victorypoints + "", json);
        json = addKeyValuePair("blocked", (attackblocked)?("1"):("0"), json);
        return json;
    }
    public JSONObject make_player_public_stats_null(){
        return make_player_public_stats("unused", 0, 0, 0, 0, true);
    }
 */

package Dominion.Confirmation;

import Client.JSonFactory;
import Dominion.DynamicCard.Card;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class ConfirmInfo {
    
    public boolean[] used;
    public String[] nickname;
    public int[] decksize;
    public int[] discardsize;
    public int[] victorypoints;
    public int[] handsize;
    
    
    public boolean[] blocked;
    public ArrayList<Card>[] cards;
    
    public String info;
    
    public ConfirmInfo(JSONObject stats){
        this.info = stats.getString("info");
        int size = 3;
        
        used = new boolean[size];
        nickname = new String[size];
        decksize = new int[size];
        discardsize = new int[size];
        victorypoints = new int[size];
        handsize = new int[size];
        blocked = new boolean[size];
        cards = new ArrayList[size];
        info = "";
        
        for(int i = 0; i < size; i++){
            String dynamicstatstring = "stat" + i;
            String jsonString = stats.getString(dynamicstatstring);
            System.out.println("received string format : " + jsonString);
            JSONObject statobject = JSonFactory.JSON.toJSON(jsonString);
            extractInfoFrom(statobject, i);
        }
    }

    private void extractInfoFrom(JSONObject statobject, int index) {
        this.nickname[index] = statobject.getString("nickname");
        this.decksize[index] = Integer.parseInt(statobject.getString("decksize"));
        this.discardsize[index] = Integer.parseInt(statobject.getString("discardsize"));
        this.handsize[index] = Integer.parseInt(statobject.getString("handsize"));
        this.victorypoints[index] = Integer.parseInt(statobject.getString("victorypoints"));
        this.blocked[index] = (statobject.getString("blocked").equals("1")?(true):(false));
        
        if((nickname[index].equals("unused")) && (handsize[index] == 0) && (decksize[index] == 0) && (discardsize[index] == 0)){
            this.used[index] = false;
        }else{
            this.used[index] = true;
        }
        
    }
}
