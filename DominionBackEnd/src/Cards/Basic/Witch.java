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
public class Witch extends ActionCard{
    
    public Witch() {
        super("witch", 5);
    }
    
    @Override
    public int cardgain(){
        return 2;
    }
    
}
