/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Cards.Basic;

import Cards.Components.TreasureCard;

/**
 *
 * @author admin
 */
public class Copper extends TreasureCard{

    public Copper() {
        super("copper", 0);
    }
    
    @Override
    public int moneygain(){
        return 1;
    }
    
}
