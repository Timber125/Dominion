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
        MainV2 main = new MainV2(stage, "localhost", 13337);
    }
    
    public ConnectionManager connection;
    
    public MainV2(Stage stage, String address, int port){
        String initname = JOptionPane.showInputDialog(null,"Enter your chat-alias: ");
        connection = new ConnectionManager("localhost", 13337);
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
    System.out.println("Server started: " + connection.init_server());
    }
    
    
    public void control_card_clicked(Card c){
        System.out.println("You clicked the card [" + c.getName() + "] with id[ " + c.getID().toString() +"] .");
        connection.write(JSonFactory.JSON.protocol_cardClicked(c));
    }

    void control_end_phase() {
        System.out.println("You clicked EndPhase");
        connection.write(JSonFactory.JSON.protocol_endPhase());
    }
}
