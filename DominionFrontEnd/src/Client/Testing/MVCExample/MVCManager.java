/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client.Testing.MVCExample;

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
    public MVCManager(Stage stage, String FXMLname, String title, int height, int width){
        
        myModel = new MVCModel();
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
}
