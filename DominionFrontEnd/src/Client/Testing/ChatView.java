/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client.Testing;

import Client.ConnectionManager;
import Client.JSonFactory;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

/**
 *
 * @author admin
 */
public class ChatView {
    public TextArea display;
    private final TextField inputfield;
    private final Button send;
    private ConnectionManager connectionManager;
    private final String author;
    public ChatView(Stage stage){
        display = new TextArea();
        inputfield = new TextField();
        send = new Button("Send");
        send.setOnAction(displayError());
        author = JOptionPane.showInputDialog(null,"Your name please?");
        createView(stage);
    }
    public ChatView(Stage stage, String name){
        display = new TextArea();
        inputfield = new TextField();
        send = new Button("Send");
        send.setOnAction(displayError());
        author = name;
        createView(stage);
    }
    
    public void setConnectionManager(ConnectionManager manager){
        connectionManager = manager;
        send.setOnAction(sendToServer());
    }
    
    public TextArea getDisplay(){
        return display;
    }
    
    private EventHandler sendToServer(){
        if(connectionManager == null) return null;
        else return new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent t) {
                String msg = inputfield.getText();
                inputfield.setText(""); // Clear the input field
                connectionManager.write(JSonFactory.JSON.protocol_chat(author, msg));
            }
            
        };
    }
    
    private EventHandler displayError(){
        return new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent t) {
                display.appendText("No connection found!\n");
            }
            
        };
    }

  
  
    private void createView(Stage stage) {
        Pane root = new Pane();
        root.getChildren().add(display);
        root.getChildren().add(inputfield);
        root.getChildren().add(send);
        display.resize(280, 200);
        display.relocate(10, 10);
        inputfield.resize(150, 40);
        inputfield.relocate(10, 205);
        send.resize(100, 40);
        send.relocate(175, 205);
        
        Scene scene = new Scene(root, 300, 250);
        
        stage.setTitle("Testing Chat Service");
        stage.setScene(scene);
        stage.show();    
    }

    
    
   
}
