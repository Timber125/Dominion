/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client.Testing;

import Client.ConnectionManager;
import Client.PrintService;
import javafx.application.Application;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

/**
 *
 * @author admin
 */
public class Main extends Application{

    
    
    @Override
    public void start(Stage stage) throws Exception {
        ChatView chat = new ChatView(stage, JOptionPane.showInputDialog("Name: "));
        ConnectionManager connectionManager = new ConnectionManager("localhost", 13337);
        PrintService printerservice = PrintService.create();
        connectionManager.registerModel(printerservice);
        printerservice.setOutput(chat.getDisplay());
        chat.setConnectionManager(connectionManager);
        System.out.println("Server started: " + connectionManager.init_server());
        
       
    }
    
}
