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
public class Silver extends TreasureCard{

    public Silver() {
        super("silver", 3);
    }
    
    @Override
    public int moneygain(){
        return 2;
    }
    
}
