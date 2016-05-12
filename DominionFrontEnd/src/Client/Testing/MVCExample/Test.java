/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client.Testing.MVCExample;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author admin
 */
public class Test extends Application{
    public static void main(String[] args){
        System.out.println("Should not start from command line.");
    }

    @Override
    public void start(Stage stage) throws Exception {
        Thread.sleep(1000);
        
    }
}
