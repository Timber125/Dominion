/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import Cards.Components.Card;

/**
 *
 * @author admin
 */
public class RewardCase extends SpecialCase{
    private int cardgain = 0;
    public RewardCase(Player victim){
        super(victim);
    }
    
    public int cardgain(){
        return cardgain;
    }
    public void setCardGain(int cardgain){
        this.cardgain = cardgain;
    }
}
