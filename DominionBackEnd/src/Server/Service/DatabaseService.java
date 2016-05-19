/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Server.ConnectionHandler;
import Server.JSONUtilities;
import Server.Server;
import org.json.JSONObject;

/**
 *
 * @author Emiel
 */
public class DatabaseService extends Service {
    private Connection connection;
    
    public DatabaseService(Server server){
        super(server);
        known_service_types.add("database");
        try{
            Class.forName("com.mysql.jdbc.Driver");
        }
            catch(ClassNotFoundException e){
                    }
    
    try{
        connection = DriverManager.getConnection("jdbc:mysql://localhost/dominion", "root", "root");
    }   catch(SQLException e){
    }
    }
    
    @Override
    public void handleType(String type, JSONObject json){
        String function = json.getString("function");
        String username = json.getString("username");
        String password = json.getString("password");
        
        JSONObject obj = JSONUtilities.JSON.create("action", "menu");
        obj = JSONUtilities.JSON.addKeyValuePair("username", username, obj);

        
        switch(function){
            case("register"):{
               try{
                   obj = JSONUtilities.JSON.addKeyValuePair("function", "register", obj);
               if(! register(username, password)){
                   
                   obj = JSONUtilities.JSON.addKeyValuePair("succes", "true", obj);
                   //action: menu
                    }
               else{
                   
                   obj = JSONUtilities.JSON.addKeyValuePair("succes", "false", obj);
               }
                   }catch(SQLException e){
                   }
               
                   
            }
            break;
            case("login"):{
                try{
                    obj = JSONUtilities.JSON.addKeyValuePair("function", "login", obj);
                    if(login(username, password)){
                        obj = JSONUtilities.JSON.addKeyValuePair("succes", "true", obj);
                    }
                    else{
                        obj = JSONUtilities.JSON.addKeyValuePair("succes", "false", obj);
                    }
                }
                catch(SQLException e){
                }
            }
            
        }
        server.getClient(json.getString("session")).write(obj);
    }
    
    public ArrayList<String> getNames() throws SQLException{
        try{
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT name FROM user");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> names = new ArrayList<String>();
            while (resultSet.next()) {
                String actualName = resultSet.getString("name");
                names.add(actualName);
            }
            return (ArrayList<String>)names;
        } catch (SQLException e){
            return null;
        }
    }
    
    public ArrayList<Integer> getPlayersOfGame(int gameID)throws SQLException{
        try{
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT user1, user2, user3, user4 FROM game WHERE gameID = ?");
         preparedStatement.setInt(1, gameID);
            ResultSet resultSet = preparedStatement.executeQuery();
            ArrayList<Integer> IDs = new ArrayList<Integer>();
            while (resultSet.next()) {
                addIfNotZero(resultSet.getInt("user1"), IDs);
                addIfNotZero(resultSet.getInt("user2"), IDs);
                addIfNotZero(resultSet.getInt("user3"), IDs);
                addIfNotZero(resultSet.getInt("user4"), IDs);
                }
            
            return (ArrayList<Integer>)IDs;
        }catch (SQLException e){
            return null;
        }
    }
    
    public void addIfNotZero(int number, ArrayList<Integer> list){
        if(number!=0){
            list.add(number);
        }
    }
    
    public int getIdOfPlayer(String username)throws SQLException{
         try{
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT userID FROM user WHERE name = ?");
         preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            int playerID = 0;
            while (resultSet.next()) {
                playerID = resultSet.getInt(1);
            }
            return playerID;
        } catch (SQLException e){
            return -1;
        }
    }
    
    public boolean login(String username, String password) throws SQLException{
        if(!getNames().contains(username)){
            return false;
        }
        else{
            try{
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT password FROM user WHERE name = ?");
                preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String result = resultSet.getString("password");
                if(result.equals(password)){
                    return true;
                }
            }
            }catch(SQLException e){
                
            }
            return false;
        }
    }
    
    public boolean register(String name, String password) throws SQLException{
        if(getNames().contains(name)){
            return false;
        }
        else{
        
        try{
        String insertSQL = "INSERT INTO user"
                + "(userID, name, password) VALUES(NULL, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, password);
        preparedStatement.executeUpdate();
        }catch(SQLException e){
        }
        return true;
        }
    }
    
    public boolean updateJSON(int gameID, String jsonString) throws SQLException{
        try{
        String insertSQL = "INSERT INTO gamelog"
                + "(gameID, jsonID, jsonString) VALUES(?, NULL, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, gameID);
        preparedStatement.setString(2, jsonString);
        preparedStatement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
            System.out.println("false");
            return false;
        }
        return true;
    }
    
    public ArrayList<String> getJsonArray(int gameID) throws SQLException{
        try{
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT jsonString FROM gamelog WHERE gameID = ?");
         preparedStatement.setInt(1, gameID);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> jsonStrings = new ArrayList<String>();
            while (resultSet.next()) {
                String jsonString = resultSet.getString("jsonString");
                jsonStrings.add(jsonString);
            }
            return (ArrayList<String>)jsonStrings;
        } catch (SQLException e){
            return null;
        }
    }
    
    public boolean canLoadGame(String username, int gameID) throws SQLException{
        if(getPlayersOfGame(gameID).contains(username)){
            return true;
        }
        return false;
    }
    
    public boolean createJsonTable(int gameID) throws SQLException{
        try{
         PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE gamelog"+gameID+" (jsonID INT NOT NULL AUTO_INCREMENT, jsonString VARCHAR(255), PRIMARY KEY(jsonID))");
         preparedStatement.executeUpdate();
         return true;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    
    public ArrayList<String> getJsonArrayFromTable(int gameID) throws SQLException{
        try{
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT jsonString FROM gamelog" + gameID);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> jsonStrings = new ArrayList<String>();
            while (resultSet.next()) {
                String jsonString = resultSet.getString("jsonString");
                System.out.println(jsonString);
                jsonStrings.add(jsonString);
            }
            return (ArrayList<String>)jsonStrings;
        } catch (SQLException e){
            return null;
        }
    }
    
    public boolean updateJsonInTable(int gameID, String jsonString) throws SQLException{
        try{
        String insertSQL = "INSERT INTO gamelog" + gameID
                + "(jsonID, jsonString) VALUES(NULL, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, jsonString);
        preparedStatement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
            System.out.println("false");
            return false;
        }
        return true;
    }
    
}
