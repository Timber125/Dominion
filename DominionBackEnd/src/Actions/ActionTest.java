/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Actions;

import Cards.Card;
import Server.JSONUtilities;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class ActionTest extends Action{
    
    public ActionTest(Card initiator){
        super(initiator);
    }

    @Override
    public JSONObject print_for_others() {
        JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
        obj = JSONUtilities.JSON.addKeyValuePair("sysout", "Card " + initiator.name + " played : " + "action test print -> others ", obj);
        return obj;
    }

    @Override
    public JSONObject print_for_player() {
        JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
        obj = JSONUtilities.JSON.addKeyValuePair("sysout", "Card " + initiator.name + " played : " + "action test print -> player", obj);
        return obj;
    }
    
}
