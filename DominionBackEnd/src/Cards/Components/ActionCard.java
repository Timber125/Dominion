/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Cards.Components;

import Actions.Action;
import Actions.ActionTest;

/**
 *
 * @author admin
 */
public abstract class ActionCard extends Card{
    
    public ActionCard(String name, int cost) {
        super(name, cost);
        this.init_standard_actions();
    }
    
    private void init_standard_actions(){
        Action test = new ActionTest(this);
        actions.add(test);
        // Every action card implements the test-action , for now.
    }
    
}
