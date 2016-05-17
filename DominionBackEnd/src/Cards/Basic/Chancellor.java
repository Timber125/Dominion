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
public class Chancellor extends ActionCard {
    
    public Chancellor() {
        super("chancellor", 3);
    }
    
    @Override
    public int moneygain(){
        return 2;
    }
    
    //todo: immediately put your deck in your discard pile
    
}
