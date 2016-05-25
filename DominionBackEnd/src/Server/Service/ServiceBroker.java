/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
 
package Server.Service;
 
import Server.JSONUtilities;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import org.json.JSONObject;
 
/**
*
* @author admin
*/
public class ServiceBroker implements Runnable{
    public static ServiceBroker instance = new ServiceBroker();
    private Map<Integer, Service> services = new TreeMap<>();
    private Map<String, List<Integer>> type_table = new TreeMap<>();
    private int service_count = 0;
    private boolean active = false;
    private volatile Queue<String> jsonrequests = new ArrayDeque<>();
    private int gameID;
 
    public int getGameID() {
        return gameID;
    }
   
    void setGameID(int gameID) {
        this.gameID = gameID;
    }
   
    private ServiceBroker(){
       
    }
   
    public void shutdown(){
        active = false;
    }
   
    public void start(){
        if(!active){
            active = true;
            Thread t = new Thread(this);
            t.start();
        }
    }
    public synchronized void offerRequest(String json_stringified){
        jsonrequests.add(json_stringified);
        System.err.println("jsonrequests:" + jsonrequests.size());
    }
   
    public void addService(Service serv){
        // Check if we can fill up a spot. if not, we will reach service_count+1 which is the logical id.
        int id = -1;
        for(int i = 0; i < service_count+1; i++){
            if(!services.containsKey(i)){
                id = i;
                break;
            }
        }
        if(id == -1){
            System.err.println("FATAL SERVICE ERROR; ID NOT FOUND");
            return;
        }
        // Add the service
        services.put(id, serv);
        service_count ++;
        // Add known-type shortcuts (=> faster service delivery)
        for(String type : serv.known_service_types){
            if(type_table.containsKey(type)) type_table.get(type).add(id);
            else{
                ArrayList<Integer> l = new ArrayList<>();
                l.add(id);
                type_table.put(type, l);
            }
        }
    }
    // Synchronized is not really necessary here, but just to be sure...
    private synchronized void handle(String json_stringified){
        System.err.println("Eventbroker handling json");
        System.err.println(json_stringified);
        JSONObject json = JSONUtilities.JSON.toJSON(json_stringified);
        String service_type = json.getString("service_type");
        if(type_table.containsKey(service_type)){
            for(Integer service_id : type_table.get(service_type)){
                services.get(service_id).handleType(service_type, json);
            }
        }
    }
 
    @Override
    public void run() {
        while(active){
            if(jsonrequests.size() != 0)System.out.println("REQUESTS: " + jsonrequests.size());
            if(!jsonrequests.isEmpty()){
                String json = jsonrequests.poll();
                this.handle(json);
            }
        }
    }
 
}
// EMIELS VERSION