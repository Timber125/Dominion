/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 *
 * @author admin
 */
public class PrintService extends ServiceModel{
    
    // Printservice is responsible for direct print-commands from the server. 
    // Print-commands are used for chatting and "logging actions". 
    // The print service should listen on keyword "sysout".
    
    // If the print service does not have a link to a target text-area to write to, 
    // Then it will print to standard out, with the prefix "[redirected to stdout]".

    public TextArea output;
    
    
    public static PrintService create(){
        ArrayList<String> keywords = new ArrayList<>();
        keywords.add("sysout");
        return new PrintService(keywords);
    }
    
    private PrintService(ArrayList<String> keywords){
        super(keywords);
    }
    
    public void setOutput(TextArea out){
        output = out;
    }
    public void clearOutput(){
        if(output != null) output.clear();
    }
    public void removeOutput(){
        output = null;
    }
    
    @Override
    public void handle(final String json_stringified) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                 String msg = extract_message(json_stringified);
                if(output == null) System.out.println("[redirected to stdout] " + msg);
                else output.appendText(msg + "\n");
            }
        });
        
    }
    
    private String extract_message(String json_stringified){
        return JSonFactory.JSON.toJSON(json_stringified).getString("sysout");
    }
    
    
}
