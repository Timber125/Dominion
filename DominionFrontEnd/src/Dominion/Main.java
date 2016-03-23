/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominion;

import Client.ConnectionManager;
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
public class Main extends Application{

    @Override
    public void start(Stage stage) throws Exception {
        Main main = new Main(stage, "localhost", 13337);
    }
    
    public Main(Stage stage, String address, int port){
        ConnectionManager connection = new ConnectionManager("localhost", 13337);
        PrintService printerservice = PrintService.create();
        SessionService sessionservice = SessionService.create();
        connection.registerModel(printerservice);
        connection.registerModel(sessionservice);
        ExampleControl control = new ExampleControl(JOptionPane.showInputDialog(null,"Enter your chat-alias: "));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Example.fxml"));
        fxmlLoader.setController(control);
        
        try { 
            Parent root = (Parent) fxmlLoader.load();
            stage.setTitle("Dominion Interface");
            stage.setScene(new Scene(root, 600, 400));
            
            stage.setResizable(false);
            stage.show();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>(){

                @Override
                public void handle(WindowEvent t) {
                    System.exit(0);
                }
                
            });
        } catch (IOException ex) {
            System.err.println("IOException : Example.fxml niet gevonden");
        }
    
       
    control.initialize();
    printerservice.setOutput(control.getDisplay());
    sessionservice.setConnectionManager(connection);
    control.setConnection(connection);
    System.out.println("Server started: " + connection.init_server());
    }
}
