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
public class Smithy extends ActionCard{

    public Smithy(){
        super("smithy", 4);
    }
    
    @Override
    public int cardgain(){
        return 3;
    }
    
}
