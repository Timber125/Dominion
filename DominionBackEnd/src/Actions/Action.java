/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Actions;

import Cards.Components.Card;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public abstract class Action {
    /*
    
        Makes use of the clients print-service
        
        Use for notifications, custom messages... 
        if null, no notification should be sent.
    
    */
    public Card initiator;
    public Action(Card ini){
        initiator = ini;
    }
    public abstract JSONObject print_for_others();
    public abstract JSONObject print_for_player();
    
    /*
    To implement when game engine is functional
    
    public abstract JSONObject action_for_player();
    public abstract JSONObject action_for_others();
    
    */
}
