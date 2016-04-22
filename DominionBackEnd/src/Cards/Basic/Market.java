/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Cards.Basic;

import Cards.Components.ActionCard;

/**
 *
 * @author admin
 */
public class Market extends ActionCard{

    public Market() {
        super("market", 5);
    }
    
    @Override
    public int cardgain(){
        return 1;
    }
    
    @Override
    public int actiongain(){
        return 1;
    }
    
    @Override
    public int purchasegain(){
        return 1;
    }
    
    @Override 
    public int moneygain(){
        return 1;
    }
}
