/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominion.Confirmation;

import Client.ConnectionManager;
import Dominion.DynamicCard.Card;
import Dominion.DynamicCard.ClientModelService;
import java.io.IOException;
import java.util.ArrayList;
import javafx.application.Platform;
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
public class ConfirmManager {
    
    public ConfirmModel myModel;
    public ConfirmController myControl;
    
    public ConfirmManager(Stage stage, String FXMLname, String title, int height, int width, ClientModelService callback, ConnectionManager connection){
        myModel = new ConfirmModel(callback, connection);
        myControl = new ConfirmController(myModel);
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXMLname));
        fxmlLoader.setController(myControl);
        
        try { 
            Parent root = (Parent) fxmlLoader.load();
            stage.setTitle(title);
            stage.setScene(new Scene(root, width, height));
            
            stage.setResizable(false);
            stage.show();
        } catch (IOException ex) {
            System.err.println("IOException : " + FXMLname + " niet gevonden");
        }
        
        myModel.intialize(myControl);
        myModel.setStage(stage);
        myControl.startup();
        
    }
    
    
    /**
     * NO CALLBACK! INTERFACE TESTING ONLY
     * @param stage
     * @param FXMLname
     * @param title
     * @param height
     * @param width
     * @deprecated
     */
    @Deprecated
    public ConfirmManager(Stage stage, String FXMLname, String title, int height, int width){
        myModel = new ConfirmModel(null, null);
        myControl = new ConfirmController(myModel);
        myModel.setStage(stage);
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
    
    
    
    public void hide(){
       myModel.hide();
    }
    public void show(){
       myModel.show();
    }
    public void insert_information(ConfirmInfo information, ArrayList<Card> handcards){
        myModel.insert_info(information, handcards);
    }
    public void clear_information(){
        myModel.clear_info();
    }
    
}
// DID I REMOVE ANYTHING? CHECK EMIELS CODE, HE HAS MORE