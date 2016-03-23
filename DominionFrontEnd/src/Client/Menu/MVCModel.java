/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client.Menu;

/**
 *
 * @author admin
 */
public class MVCModel {

    public MVCController myControl;
    public MVCManager myManager;
    
    public MVCModel(MVCManager manager){
        myManager = manager;
    }
    void intialize(MVCController myControl) {
        this.myControl = myControl;
    }

    void EnterPushed() {
        myManager.Finish("localhost", 13337);
    }
    
    
    
}
