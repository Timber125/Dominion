/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client.Menu;

import Client.Testing.MVCExample.*;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author admin
 */
public class Test extends Application{
    
    MVCManager manager;
    
    public static void main(String[] args){
        System.out.println("Should not start from command line.");
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Stage, FXML name, Screen title, width, height. 
        manager = new MVCManager(stage, "MVC.fxml", "MVC EXAMPLE", 500, 500);
        
        // Roep na 2 seconden de client op. 
        Thread.sleep(2000);
        manager.Finish("localhost", 13337);
    }
}
