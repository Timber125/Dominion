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
public class Spy extends ActionCard{
    
    public Spy() {
        super("spy", 4);
        
    }
    
    @Override
    public int actiongain(){
        return 1;
    }
    
    @Override
    public int cardgain(){
        return 1;
    }
    
    //todo: each player reveals the top card of his deck, and you choose wether to discard or keep it
}
