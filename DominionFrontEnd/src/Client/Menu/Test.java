/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client.Menu;

import Client.Testing.MVCExample.*;
import javafx.application.Application;
import javafx.stage.Stage;


public class Test extends Application{
    
    MVCManager manager;
<<<<<<< HEAD
    
 
=======
    /*
    public static void main(String[] args){
        System.out.println("Should not start from command line.");
    }
*/
>>>>>>> 788a1d0304db98cef30ec5772458376a6a77179e

    @Override
    public void start(Stage stage) throws Exception {
        // Stage, FXML name, Screen title, width, height. 
        manager = new MVCManager(stage, "MVC.fxml", "MVC EXAMPLE", 500, 500);
        
<<<<<<< HEAD
      
=======
        // Roep na 2 seconden de client op. 
        Thread.sleep(2000);
        manager.Finish("localhost", 13337);
>>>>>>> 788a1d0304db98cef30ec5772458376a6a77179e
    }
}
