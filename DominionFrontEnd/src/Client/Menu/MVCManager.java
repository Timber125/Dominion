/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client.Menu;

import Client.ConnectionManager;
import Dominion.DynamicCard.MainV2;
import Dominion.Main;
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
public class MVCManager {
    
    public MVCModel myModel;
    public MVCController myControl;
    
    
    public Stage stage;
    
    public MVCManager(Stage stage, String FXMLname, String title, int height, int width){
        this.stage = stage;
        myModel = new MVCModel(this);
        myControl = new MVCController(myModel);
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXMLname));
        fxmlLoader.setController(myControl);
        
        try { 
            Parent root = (Parent) fxmlLoader.load();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            
            stage.setResizable(false);
            stage.show();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>(){

                @Override
                public void handle(WindowEvent t) {
                    System.exit(0);
                }
                
            });
        } catch (IOException ex) {
            System.err.println("IOException : " + FXMLname + " niet gevonden");
        }
        
        myModel.intialize(myControl);
        myControl.startup();
        
        
    }
    
    public void Finish(ConnectionManager connect, String username, String password){
        MainV2 main = new MainV2(stage, connect, username);
    }
}
