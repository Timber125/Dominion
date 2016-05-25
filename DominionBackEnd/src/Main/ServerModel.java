/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Main;

import Server.Server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author admin
 */
public class ServerModel {

    private ServerController controller;
    
    private Server server;
    
    protected Server getServer(){
        return server;
    }
    
    private boolean port_valid = false;
    
    public ServerModel() {
        
    }
    
    public void initialize(ServerController control){
        controller = control;
        boolean success = true;
        controller.getInternal().getItems().clear();
        controller.getInternal().setEditable(false);
        controller.getInternal().getItems().addAll(getInternalAliases());
        controller.getInternal().setValue(controller.getInternal().getItems().get(0));
        controller.getExternal().setText(getExternal());
        controller.unlock_settings();
        controller.getStart().setDisable(false);
    }
    
    
    
    public void onStartPressed(){
        int port = Integer.parseInt(controller.getPort().getText());
        int webport = Integer.parseInt(controller.getWebPort().getText());
        String address = controller.getInternal().getValue().toString();
        controller.lock_settings();
        System.out.println("Starting Main Server [" + address + "] [" + port + "]");
        System.out.println("Starting Web Server [" + address + "] [" + webport + "]");
        server = new Server(address, port, webport);
        Thread t = new Thread(server);
        t.start();
        try {
            Thread.sleep(50); // give 50 ms startup-time before we check if its active
        } catch (InterruptedException ex) {
            System.err.println("Sleep interrupted");
        }
        controller.setStatus(server.isActive());
    }
    
    public void onStopPressed(){
        System.out.println("stop");
        
        server.shutdown();
        
        boolean active = server.isActive();
        if(!active){
            controller.unlock_settings();
            controller.setStatus(false);
        }        
    }
    
    public boolean onPortChange(String s){
            if(s == null) return portInvalid();
            if(s.trim().equals("")) return portInvalid();
            try{
                int i = Integer.parseInt(s);
                if((i >= 1) && (i <= 65535)) return portValid();
                return portInvalid();
            }catch(Exception e){
                // Cast exception? Invalid!
                return portInvalid();
            }
    }
    
    private boolean portInvalid(){
        if(port_valid){
            port_valid = false;
            controller.getStart().setDisable(true);
        }
        return false;
    }
    
    private boolean portValid(){
        if(!port_valid){
            port_valid = true;
            controller.getStart().setDisable(false);
        }
        return true;
    }
    
    private String getExternal(){
        String ext = "Unresolved";
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip = in.readLine(); //you get the IP as a String
            ext = ip;
        } catch (MalformedURLException ex) {
            System.err.println("External ip unresolved");
        } catch (IOException ex){
            System.err.println("External ip not resolved");
        }
        
        return ext;
    }
    
    private SortedSet<String> getInternalAliases(){
        SortedSet<String> host_aliases = new TreeSet<>();
        host_aliases.add("127.0.0.1");
        try {
            InetAddress local = InetAddress.getLocalHost();
            host_aliases.add(local.getHostAddress());
            local = InetAddress.getLoopbackAddress();
            host_aliases.add(local.getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(ServerModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try{
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)){
                for (InetAddress inetAddress : Collections.list(netint.getInetAddresses())) {
                    host_aliases.add(inetAddress.getHostAddress());
                }
            }
        }catch(SocketException se){
            System.err.println(se);
        }
        
        return host_aliases;
    }

    public boolean onWebPortChange(String s) {
        if(s == null) return portInvalid();
        if(s.trim().equals("")) return portInvalid();
        try{
            int i = Integer.parseInt(s);
            if((i >= 1) && (i <= 65535)) return portValid();
            return portInvalid();
        }catch(Exception e){
            // Cast exception? Invalid!
            return portInvalid();
        }
    }
}
