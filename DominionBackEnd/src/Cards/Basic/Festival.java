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
public class Festival extends ActionCard{
    
    public Festival() {
        super("festival", 5);
    }
    
    @Override
    public int actiongain(){
        return 2;
    }
    
    @Override
    public int purchasegain(){
        return 1;
    }
    
    @Override
    public int moneygain(){
        return 2;
    }
           
    
}
