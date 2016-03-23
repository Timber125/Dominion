/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client.Menu;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;



/**
 *
 * @author admin
 */
public class MVCController {
    
    @FXML
    TextField Field;

    @FXML
    Label Output;

    @FXML
    Button Enter;

    
    
    public MVCModel model;
    public MVCController(MVCModel myModel){
        model = myModel;
    }

    void startup() {
        System.out.println("MVC Started");
        Enter.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent t) {
                model.EnterPushed();
            }
            
        });
    }
    
    
}
