/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cards.Basic;

import Cards.Components.ActionCard;

/**
 *
 * @author Jordy School
 */
public class Militia extends ActionCard {
    
    public Militia() {
        super("militia", 4);
    }
    
    @Override
    public int moneygain(){
        return 2;
    }
    
    //todo: each other player discards down to 3 action cards
}
