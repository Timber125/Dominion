/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Cards.Basic;

import Cards.Components.VictoryCard;

/**
 *
 * @author admin
 */
public class Estate extends VictoryCard{

    public Estate() {
        super("estate", 2);
    }
    
    @Override
    public int victorygain(){
        return 1;
    }
    
}
