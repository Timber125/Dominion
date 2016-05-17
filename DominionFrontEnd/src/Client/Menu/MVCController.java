/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client.Menu;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;



/**
 *
 * @author admin
 */
public class MVCController {
    
    @FXML
    TextField IP;

    @FXML
    TextField Port;

    @FXML
    Button Connect;
    
    @FXML
    Button Register;
    
    @FXML
    Button Login;
    
    @FXML
    TextField Username;
    
    @FXML
    TextField Password;
   
    
    
    
    public MVCModel model;
    public MVCController(MVCModel myModel){
        model = myModel;
    }

    void startup() {
        System.out.println("MVC Started");
        Connect.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent t) {
                String IPstring = IP.getText();
                String Portstring = Port.getText();
                boolean success = model.connectPushed(IPstring, Portstring);
                if(success) {
                    IP.setDisable(true);
                    Port.setDisable(true);
                    Connect.setDisable(true);
                }
                else {
                   //niets, error?
                }
            }
        
            
        });
        
        Register.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                // Hier komt code voor login 
                String a = Username.getText();
                String b = Password.getText();
                boolean succes = model.registerPushed(a, b);
                if (succes) {
                    Username.setDisable(true);
                    Password.setDisable(true);
                }
                
            }
        });
        
        Login.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event) {
                // Hier komt code voor login 
                String a = Username.getText();
                String b = Password.getText();
                String c = Port.getText();
                String d = IP.getText();
                boolean succes = model.loginPushed(d,Integer.parseInt(c),a,b);
                if (succes) {
                
                }
                
            }
        });
        
    }}  
         
          
        
     /* public count(final String string, final String substring)
     {
     int count = 0;
     int idx = 0;

     while ((idx = string.indexOf(substring, idx)) != -1)
     {
        idx++;
        count++;
     }

     return count; */
     
    /*
    void Indentify(){
        System.out.println("Port and IP OK, identify now.");
        Register.setOnAction(new EventHandler<ActionEvent>(){ 
            
        model.LogInPushed();
        String Usernamestring = Username.getText();
        Username.setText("");
        String Passwordstring = Password.getText();
        Password.setText("");
         
        }
    }  */
            /*}
            */              
    

