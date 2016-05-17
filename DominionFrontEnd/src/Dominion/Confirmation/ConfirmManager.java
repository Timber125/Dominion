/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominion.Confirmation;

import Dominion.DynamicCard.ClientModelService;
import java.io.IOException;
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
    private Stage s;
    
    public ConfirmManager(Stage stage, String FXMLname, String title, int height, int width, ClientModelService callback){
        this.s = stage;
        myModel = new ConfirmModel(callback);
        myControl = new ConfirmController(myModel);
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXMLname));
        fxmlLoader.setController(myControl);
        
        try { 
            Parent root = (Parent) fxmlLoader.load();
            s.setTitle(title);
            s.setScene(new Scene(root, width, height));
            
            s.setResizable(false);
            s.show();
            s.setOnCloseRequest(new EventHandler<WindowEvent>(){

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
        this.s = stage;
        myModel = new ConfirmModel(null);
        myControl = new ConfirmController(myModel);
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXMLname));
        fxmlLoader.setController(myControl);
        
        try { 
            Parent root = (Parent) fxmlLoader.load();
            s.setTitle(title);
            s.setScene(new Scene(root, width, height));
            
            s.setResizable(false);
            s.show();
            s.setOnCloseRequest(new EventHandler<WindowEvent>(){

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
       Platform.runLater(new Runnable(){

           @Override
           public void run() {
               s.hide();
           }
           
       });
    }
    public void show(){
      Platform.runLater(new Runnable(){

           @Override
           public void run() {
               s.show();
           }
           
       });
    }
    public void insert_information(ConfirmInfo information){
        myModel.insert_info(information);
    }
    public void clear_information(){
        myModel.clear_info();
    }
    
}
