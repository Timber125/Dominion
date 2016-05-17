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
public class Cellar extends ActionCard{
    
    public Cellar() {
        super("cellar", 2);
    }
    
    @Override
    public int actiongain(){
        return 1;
    }
    
    //todo: discard cards, gain 1 card per card discarded
}
