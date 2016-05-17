/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import Cards.Components.Card;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class RewardCase extends SpecialCase{
    private int cardgain = 0;
    private JSONObject behaviour = null;
    public RewardCase(Player victim){
        super(victim);
    }
    
    public int cardgain(){
        return cardgain;
    }
    public void setCardGain(int cardgain){
        this.cardgain = cardgain;
    }
    public JSONObject reward_behaviour(){
        return behaviour;
    }
    public void setRewardBehaviour(JSONObject obj){
        this.behaviour = obj;
    }
    
}
