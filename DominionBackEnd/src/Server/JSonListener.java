/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;

import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author admin
 */
public class JSonListener extends ConnectionListener{

    public JSonListener(BufferedReader inputstream) {
        super(inputstream);
    }
    
    @Override
    public void run(){
        while(this.handler != null){
            // While there's still someone listening to me... :)
            try {
                String s = inputstream.readLine();
                if(JSONUtilities.JSON.isJSON(s)){
                    buffer.offer(s);
                    handler.invalidated(this);
                    System.err.println("Received a valid json from client.");
                }else{
                    System.err.println("Flushed a non-json message from client:");
                    System.err.println(s);
                }
            } catch (IOException ex) {
                System.err.println("IOException");
                System.err.println(ex);
                handler.invalidated(null); // Send teardown signal
                break;
            }
            
        }
    }
    
   
}
