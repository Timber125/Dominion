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
public class Village extends ActionCard{

    /*
        Dont make cards like this, village is just to test!
    */
    public Village() {
        super("village", 3);
    }
    
    @Override
    public int cardgain(){
        return 1;
    }
    @Override
    public int actiongain(){
        return 2;
    }
    
}
