/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client.Menu;

import Client.ConnectionManager;
import Client.JSonFactory;
import Client.ServiceModel;
import java.util.ArrayList;
import javafx.application.Platform;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class MVCModel extends ServiceModel {

    public MVCController myControl;
    public MVCManager myManager;
    
    private ConnectionManager myConnection;
    
    public MVCModel(MVCManager manager){
        super("menu");
        myManager = manager;
    }
    void intialize(MVCController myControl) {
        this.myControl = myControl;
    }

    public boolean connectPushed(String IPString, String PortString) {
        
        /* myManager.Finish("localhost", 13337); */
        myConnection = new ConnectionManager(IPString, Integer.parseInt(PortString));
        myManager.prepare(myConnection);
        return myConnection.init_server();
    }
    
    public boolean loginPushed(String address, int port, String username, String password){
        JSONObject obj = JSonFactory.JSON.protocol_database("login", username, password);
        myConnection.write(obj);
        return true;
    }
    
    public boolean registerPushed(String username, String password){
        JSONObject obj = JSonFactory.JSON.protocol_database("register", username, password);
        myConnection.write(obj);
        return true;  
    }

    @Override
    public void handle(String json_stringified) {
        JSONObject obj = JSonFactory.JSON.toJSON(json_stringified);
        String function = obj.getString("function");
        String succes = obj.getString("succes");
        
        switch(function){
            case ("register"):{
                if(succes.equals("true")){
                    System.out.println("check");
                }
                else{
                    System.out.println("fail");
                    Platform.runLater(new Runnable(){

                        @Override
                        public void run() {
                            myControl.Password.setEditable(true);
                            myControl.Username.setEditable(true);
                            myControl.Password.setDisable(false);
                            myControl.Username.setDisable(false);
                            myControl.Register.setDisable(false);
                        }
                        
                    });
                }
                break;
            }
            case ("login"):{
                if(succes.equals("true")){
                    myManager.Finish(obj.getString("username"));
                }
                else{
                    System.out.println("fail");
                }
            }
        }
    }
    
    
    
    }
    
    
    

