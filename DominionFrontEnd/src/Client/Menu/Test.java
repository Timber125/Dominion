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

    @Override
    public void start(Stage stage) throws Exception {
        // Stage, FXML name, Screen title, width, height. 
        manager = new MVCManager(stage, "MVC.fxml", "Dominion Menu", 500, 500);
    }
}
