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
public class Moat extends ActionCard{
    
    public Moat() {
        super("moat", 2);
    }
    
    @Override
    public int cardgain(){
        return 2;
    }
    
    //todo: when another player plays an attackcard, reveal from hand and block the attack
}
