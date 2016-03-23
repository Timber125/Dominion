/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Main;

import Server.Server;
import java.io.IOException;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author admin
 */
public class ServerManager {
    public ServerModel model = new ServerModel();
    
    public Server getServer(){
        return model.getServer();
    }
    
    public ServerManager(Stage stage){
        ServerController control = new ServerController(model);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ServerView.fxml"));
        fxmlLoader.setController(control);
        try { 
            Parent root = (Parent) fxmlLoader.load();
            stage.setTitle("Dominion Server");
            stage.setScene(new Scene(root, 337, 200));
            
            stage.setResizable(false);
            stage.show();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>(){

                @Override
                public void handle(WindowEvent t) {
                    System.exit(0);
                }
                
            });
        } catch (IOException ex) {
            System.err.println("IOException : ServerView.fxml niet gevonden");
        }
        control.initialize();
        model.initialize(control);
    }
}
