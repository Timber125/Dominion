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
public class Duchy extends VictoryCard{

    public Duchy() {
        super("duchy", 5);
    }
    @Override
    public int victorygain(){
        return 3;
    }
}
