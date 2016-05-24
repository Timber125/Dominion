/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Main;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

/**
 *
 * @author admin
 */
public class ServerController {
    @FXML
    TextField PortField;
    
    @FXML
    TextField WebPortField;
    
    @FXML
    ComboBox<String> InternalAddress;
    
    @FXML 
    TextField ExternalAddress;
    
    @FXML
    Button Start;
    
    @FXML
    Button Stop;
    
    @FXML
    Label Status;
    
    final ServerModel model;
    
    public ServerController(ServerModel model){
        this.model = model;
    }
    
    public Button getStart(){return Start;}
    public Button getStop(){return Stop;}
    public TextField getPort(){return PortField;}
    public TextField getWebPort(){return WebPortField;}
    public ComboBox getInternal(){return InternalAddress;}
    public TextField getExternal(){return ExternalAddress;}
    public Label getStatus(){return Status;}
    
    public void initialize(){
        Start.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent t) {
                model.onStartPressed();
            }
            
        });
        
        Stop.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent t) {
                model.onStopPressed();
            }
            
        });
        
        PortField.setOnKeyReleased(new EventHandler<KeyEvent>(){

            @Override
            public void handle(KeyEvent t) {
                model.onPortChange(PortField.getText());
            }

           
            
        });
        
        WebPortField.setOnKeyReleased(new EventHandler<KeyEvent>(){

            @Override
            public void handle(KeyEvent t) {
                model.onWebPortChange(WebPortField.getText());
            }
            
        });
        
    }
    
    public void lock_settings(){
        InternalAddress.setEditable(false);
        ExternalAddress.setEditable(false);
        InternalAddress.setDisable(true);
        ExternalAddress.setDisable(true);
        PortField.setEditable(false);
        WebPortField.setEditable(false);
        Start.setDisable(true);
        Stop.setDisable(false);
    }
    
    public void unlock_settings(){
        InternalAddress.setEditable(false);
        ExternalAddress.setEditable(false);
        InternalAddress.setDisable(false);
        ExternalAddress.setDisable(true);
        PortField.setEditable(true);
        WebPortField.setEditable(true);
        Start.setDisable(false);
        Stop.setDisable(true);
    }
    
    public void setStatus(boolean active){
        Status.setText("");
        if(active){
            Status.setTextFill(Color.web("#00FF00")); // Green
            Status.setText("ONLINE");
        }else{
            Status.setTextFill(Color.web("#FF0000")); // Red
            Status.setText("OFFLINE");
        }
    }
}
// MY VERSION