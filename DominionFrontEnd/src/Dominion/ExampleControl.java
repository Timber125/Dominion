/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominion;

import Client.ConnectionManager;
import Client.JSonFactory;
import java.awt.event.MouseEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

/**
 *
 * @author admin
 */
public class ExampleControl {
    @FXML
    Pane DominionPane;
    
    @FXML
    Pane ChatPane;
    
    @FXML
    Pane Pane1;
    
    @FXML
    Button ChatButton;
    
    @FXML
    TextArea ChatArea;
    
    @FXML
    TextField ChatText;
    
    private ConnectionManager connection;
    private String chat_alias;
    
    public ExampleControl(String chat_alias){
        this.chat_alias = chat_alias;
    }
    
    public void initialize(){
        ChatButton.setOnAction(displayError());
        String image = this.getClass().getResource("/resources/background.jpg").toExternalForm();
        DominionPane.setStyle("-fx-background-image: url('" + image + "'); " +
           "-fx-background-position: center center; " +
           "-fx-background-repeat: stretch;");
    }
    
    public void setConnection(ConnectionManager manager){
        this.connection = manager;
        ChatButton.setOnAction(sendToServer());
    }
    
    public TextArea getDisplay(){
        return ChatArea;
    }
    
    private EventHandler sendToServer(){
        if(connection == null) {
            print_error();
            ChatButton.setOnAction(displayError());
            return displayError();
        }
        else return new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent t) {
                String msg = ChatText.getText();
                ChatText.setText(""); // Clear the input field
                connection.write(JSonFactory.JSON.protocol_chat(chat_alias, msg));
            }
            
        };
    }
    
    private EventHandler displayError(){
        return new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent t) {
                print_error();
            }
            
        };
    }
    
    private void print_error(){
        ChatArea.appendText("No connection found!\n");
    }
}
