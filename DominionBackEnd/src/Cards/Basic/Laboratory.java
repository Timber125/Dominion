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
public class Laboratory extends ActionCard{

    public Laboratory() {
        super("laboratory", 5);
    }
    
    @Override
    public int cardgain(){
        return 2;
    }
    
    @Override
    public int actiongain(){
        return 1;
    }
}
