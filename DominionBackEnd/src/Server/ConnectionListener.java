/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 *
 * @author admin
 */
public class ConnectionListener implements Observable, Runnable{
    
    protected Queue<String> buffer = new ArrayDeque<>();
    final protected BufferedReader inputstream;
    protected InvalidationListener handler; // Only one handler, can increase if design changes
    
    public ConnectionListener(BufferedReader inputstream){
        this.inputstream = inputstream;
    }
    @Override
    public void addListener(InvalidationListener il) {
        this.handler = il;
    }

    @Override
    public void removeListener(InvalidationListener il) {
        this.handler = null;
    }

    @Override
    public void run() {
        while(this.handler != null){
            // While there's still someone listening to me... :)
            try {
                String s = inputstream.readLine();
                buffer.offer(s);
                handler.invalidated(this);
            } catch (IOException ex) {
                System.err.println("IOException");
                System.err.println(ex);
            }
            
        }
    }
    
}
