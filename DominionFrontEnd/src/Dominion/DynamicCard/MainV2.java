/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominion.DynamicCard;

import Client.ConnectionManager;
import Client.JSonFactory;
import Client.PrintService;
import Client.SessionService;
import Client.Testing.ChatView;
import Dominion.Confirmation.ConfirmManager;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.swing.JOptionPane;

/**
 *
 * @author admin
 */
public class MainV2 extends Application{

    @Override
    public void start(Stage stage) throws Exception {
        MainV2 main = new MainV2(stage, new ConnectionManager("localhost", 13337), "testjiqlmfjsd");
    }
    
    public ConnectionManager connection;
    public MainV2(Stage stage, ConnectionManager initialized_connection, String username){
        String initname = username;
        connection = initialized_connection; //new ConnectionManager(address, port);
        PrintService printerservice = PrintService.create();
        SessionService sessionservice = SessionService.create(initname);
        connection.registerModel(printerservice);
        connection.registerModel(sessionservice);
        ClientControlV2 control = new ClientControlV2(initname, this);
        ClientModelService modelservice = new ClientModelService(control);
        connection.registerModel(modelservice);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ClientInterfaceV2.fxml"));
        fxmlLoader.setController(control);
        
        try { 
            Parent root = (Parent) fxmlLoader.load();
            stage.setTitle("Dominion Interface");
            stage.setScene(new Scene(root, 1280, 680));
            
            stage.setResizable(false);
            stage.show();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>(){

                @Override
                public void handle(WindowEvent t) {
                    System.exit(0);
                }
                
            });
        } catch (IOException ex) {
            System.err.println("IOException : ClientInterfaceV2.fxml niet gevonden");
        }
    control.init();
    printerservice.setOutput(control.getDisplay());
    sessionservice.setConnectionManager(connection);
    control.setConnection(connection);
    try{control.setModel(modelservice);} catch (IOException e){System.err.println("IOException");}
    System.out.println("Server started: " + connection.init_server());
    }
    
    
    // Protocol for sending a card-click on a card in your hand
    public void control_card_clicked(Card c){
        System.out.println("You clicked the card [" + c.getName() + "] with id[ " + c.getID().toString() +"] .");
        connection.write(JSonFactory.JSON.protocol_cardClicked(c));
    }

    void control_end_phase() {
        System.out.println("You clicked EndPhase");
        connection.write(JSonFactory.JSON.protocol_endPhase());
    }
    // Protocol for sending a card-click on a card in environment
    public void control_buy_card(String cardname){
        System.out.println("You clicked the stack of " + cardname + "'s.");
        connection.write(JSonFactory.JSON.protocol_cardBuy(cardname));
    }

    public void ready_changed(boolean selected, String chat_alias) {
        if(selected){
            System.out.println("You are ready");
            connection.write(JSonFactory.JSON.protocol_chat(chat_alias, "!lobbyvote"));
        }
        else{
            System.out.println("You are unready");
            connection.write(JSonFactory.JSON.protocol_chat(chat_alias, "!lobbyunvote"));
        }
    }

    public void join_changed(boolean selected, String chat_alias) {
        if(selected){
            System.out.println("you joined");
            connection.write(JSonFactory.JSON.protocol_chat(chat_alias, "!lobbyconnect"));
        }else{
            System.out.println("you unjoined");
            connection.write(JSonFactory.JSON.protocol_chat(chat_alias, "!lobbydisconnect"));
        }
    }
}
