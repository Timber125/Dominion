/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominion.DynamicStack;

import Dominion.DynamicCard.Card;
import Dominion.DynamicCard.ClientControlV2;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 * @author admin
 */
public class StackTest extends Application{

    @Override
    public void start(Stage stage) throws Exception {
        Card c = new Card("back");
        final Stack s = new Stack(c, 20);
        s.test(stage);
        stage.show();
        s.setOnMouseClicked(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent t) {
                s.update(s.count-1);
            }

          
            
        });
    }
    
}
