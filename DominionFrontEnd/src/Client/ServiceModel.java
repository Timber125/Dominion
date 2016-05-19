/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import java.util.ArrayList;

/**
 *
 * @author admin
 */
public abstract class ServiceModel {
    public ArrayList<String> keywords;
    public ServiceModel(ArrayList<String> keywords){
        this.keywords = keywords;
    }
    
    public ServiceModel(String keyword){
        keywords = new ArrayList<>();
        keywords.add(keyword);
    }
    
    public abstract void handle(String json_stringified);
    
}
