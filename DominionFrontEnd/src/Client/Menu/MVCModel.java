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

    public boolean connectPushed(String IPString, String PortString) {
        
        /* myManager.Finish("localhost", 13337); */
        return true;
    }
    
    public boolean loginPushed(String address, int port, String username, String password){
        myManager.Finish(address, port, username, password);
        return true;
    }
    
    public boolean registerPushed(String Username, String Password){
        return true;  
    }
    
    
    
    }
    
    
    

