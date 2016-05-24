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
import org.json.JSONException;
import org.json.JSONObject;
 
/**
*
* @author Emiel
*/
public class JDBCMemoryDatabaseService extends Service {
    private int gameID;
    private Connection connection;
   
    public JDBCMemoryDatabaseService(Server server){
        super(server);
        known_service_types.add("database");
        try{
           
        this.gameID = getNewGameID();
            Class.forName("org.apache.derby.jdbc.ClientDriver");
        }
            catch(ClassNotFoundException e){
                e.printStackTrace();
                System.out.println("qsdggsgf");
                    }
           
            catch(Exception e){
                e.printStackTrace();
                System.out.println("azer");
               
            }
   
    try{
        //connection = DriverManager.getConnection("jdbc:derby://localhost:1527/TesterDB", "app", "root");
        //connection = DriverManager.getConnection("jdbc:derby://localhost:1527/dominion", "root", "root");
        connection = DriverManager.getConnection("jdbc:derby:testtest/test;create=true");
        String createUsers = "CREATE TABLE USERS(username VARCHAR(20) not NULL, password VARCHAR(20) not NULL, PRIMARY KEY(username))";
        String createGames = "CREATE TABLE GAMES(gameID INTEGER not NULL GENERATED ALWAYS AS IDENTITY(START WITH 1, INCREMENT BY 1), "
                + "player1 VARCHAR(20), player2 VARCHAR(20), player3 VARCHAR(20), player4 VARCHAR(20), PRIMARY KEY (gameID), "
                + "FOREIGN KEY(player1) references users, FOREIGN KEY(player2) references users, FOREIGN KEY(player3) references users, FOREIGN KEY(player4) references users)";
        String createCurrentUser = "CREATE TABLE CURRENTUSERS(username varchar(20) not null, session varchar(20) not null, primary key (username), foreign key(username) references users)";
        String createGame_user_session = "create table game_user_session(gameID int not null, username varchar(20) not null, session varchar(20) not null, primary key(username, gameid), foreign key(username) references users, foreign key(gameID) references games)";
       
        PreparedStatement preparedStatement = connection.prepareStatement(createUsers, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.executeUpdate();
        preparedStatement = connection.prepareStatement(createGames, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.executeUpdate();
        preparedStatement = connection.prepareStatement(createCurrentUser, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.executeUpdate();
        preparedStatement = connection.prepareStatement(createGame_user_session, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.executeUpdate();
        System.out.println("iets");
    }   catch(SQLException e){
        e.printStackTrace();
        //de syntax if exists is vreemd in deze sql taal, maar we krijgen in exceptie als de tabel al bestaat
        return;
    }
    }
   
    @Override
    public void handleType(String type, JSONObject json){
        String function = json.getString("function");
        if(function.equals("gameID")){
            handleServiceRequest(json);
            return;
        }
        if(function.equals("log")){
            handleServiceRequest(json);
            return;
        }
        String session = null;
        try{
            session = json.getString("session");
        }catch(JSONException e){
            session = null;
        }
        catch(NullPointerException ex){
            session = null;
        }
        String username = json.getString("username");
        String password = json.getString("password");
        JSONObject obj = JSONUtilities.JSON.create("action", "menu");
        obj = JSONUtilities.JSON.addKeyValuePair("username", username, obj);
       switch(function){
            case("register"):{
               try{
                    obj = JSONUtilities.JSON.addKeyValuePair("function", "register", obj);
                    if(register(username, password)){
                  
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
                        //addCurrentUser(username, password);
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
   
    protected void handleServiceRequest(JSONObject json) {
        String function = json.getString("function");
        switch(function){
            case("gameID"):{
                try{
                JSONObject obj = JSONUtilities.JSON.create("service_type", "lobby");
                String player1 = json.getString("player0");
                String player2 = json.getString("player1");
                String player3 = json.getString("player2");
                String player4 = json.getString("player3");
                String session1 = json.getString("session0");
                String session2 = json.getString("session1");
                String session3 = json.getString("session2");
                String session4 = json.getString("session3");
                if(player3.equals("none")){
                player3 = null;
                }
                 if(player4.equals("none")){
                player4 = null;
                }
                if(session3.equals("none")){
                    session3 = null;
                }
                if(session4.equals("none")){
                    session4 = null;
                }
                Integer ID = addGame(player1, player2, player3, player4, session1, session2, session3, session4);
                obj = JSONUtilities.JSON.addKeyValuePair("gameID", ID.toString(), obj);
                obj = JSONUtilities.JSON.addKeyValuePair("operation", "gameID", obj);
                ServiceBroker.instance.offerRequest(obj.toString());
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
            break;
            case("log"):{
                try{
                updateJSON(Integer.parseInt(json.getString("gameID")), json.getString("jsonString"));
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        }
       
    }
   
    protected int getID(){
        return gameID;
    }
   
    protected ArrayList<String> getNames() throws SQLException{
        try{
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT username FROM users");
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println(resultSet.toString());
            List<String> names = new ArrayList<>();
            while (resultSet.next()) {
                String actualName = resultSet.getString("username");
                names.add(actualName);
                System.out.println(actualName);
            }
            return (ArrayList<String>)names;
        } catch (SQLException e){
            return null;
        }
    }
   
    protected int addGame(String user1, String user2, String user3, String user4, String session1, String session2, String session3, String session4) throws SQLException{
        addGameRow(user1, user2, user3, user4);
        int newID = getNewGameID();
        addGameUserSessionRow(newID, user1, session1);
        addGameUserSessionRow(newID, user2, session2);
        addGameUserSessionRow(newID, user3, session3);
        addGameUserSessionRow(newID, user4, session4);
        addGameLogTable(newID);
        return newID;
    }
   
    public boolean addGameRow(String user1, String user2, String user3, String user4) throws SQLException{
        try{
       
        String insertSQL = "INSERT INTO games"
                + "(player1, player2, player3, player4) VALUES(?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, user1);
        preparedStatement.setString(2, user2);
        preparedStatement.setString(3, user3);
        preparedStatement.setString(4, user4);
        preparedStatement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
   
    protected boolean addGameLogTable(int gameid) throws SQLException{
        try{
        String insertSQL = "CREATE TABLE gameLog" + gameid
                + "(jsonID int not null primary key generated always as identity(start with 1, increment by 1), jsonString varchar(255) not null)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
   
    protected boolean addGameUserSessionRow(int gameid, String username, String session){
        if(username != null){
        try{
        String insertSQL = "INSERT INTO game_user_session"
                + "(gameid, username, session) VALUES(?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.setInt(1, gameid);
        preparedStatement.setString(2, username);
        preparedStatement.setString(3, session);
        preparedStatement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
        }
        return false;
    }
   
    protected void addCurrentUser(String username, String session){
        try{
        String insertSQL = "INSERT INTO currentusers"
                + "(username, session) VALUES(?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, session);
        preparedStatement.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
   
    protected int getNewGameID() throws SQLException{
        try{
           
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT MAX(gameid) FROM games");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                 gameID = resultSet.getInt(1);
            }
            return gameID;
        } catch (SQLException e){
            return -1;
        } catch (NullPointerException e){
            return 0;
        }
    }
   
    protected ArrayList<Integer> getPlayersOfGame(int gameID)throws SQLException{
        try{
         PreparedStatement preparedStatement = connection.prepareStatement("SELECT user1, user2, user3, user4 FROM games WHERE gameID = ?");
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
   
    protected void addIfNotZero(int number, ArrayList<Integer> list){
        if(number!=0){
            list.add(number);
        }
    }
   
    protected boolean login(String username, String password) throws SQLException{
        if(!getNames().contains(username)){
            return false;
        }
        else{
            try{
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT password FROM users WHERE username = ?");
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
   
    protected boolean register(String name, String password) throws SQLException{
        if(getNames().contains(name)){
            return false;
        }
        else{
            System.out.println("NAME IS NOT KNOWN -> continueing");
        try{
        String insertSQL = "INSERT INTO users"
                + "(username, password) VALUES(?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL, PreparedStatement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, password);
        preparedStatement.executeUpdate();
        }catch(SQLException e){
            System.out.println("ECXEPTION");
            e.printStackTrace();
            return false;
        }
        return true;
        }
    }
   
    protected boolean updateJSON(int gameID, String jsonString) throws SQLException{
        try{
        String insertSQL = "INSERT INTO gamelog" + gameID
                + "(jsonString) VALUES(?)";
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
   
    protected ArrayList<String> getJsonArray(int gameID) throws SQLException{
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
   
    protected boolean canLoadGame(String username, int gameID) throws SQLException{
        if(getPlayersOfGame(gameID).contains(username)){
            return true;
        }
        return false;
    }
   
    protected boolean createJsonTable(int gameID) throws SQLException{
        try{
         PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE gamelog"+gameID+" (jsonID INT NOT NULL AUTO_INCREMENT, jsonString VARCHAR(255), PRIMARY KEY(jsonID))");
         preparedStatement.executeUpdate();
         return true;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
   
    protected ArrayList<String> getJsonArrayFromTable(int gameID) throws SQLException{
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
   
    protected boolean updateJsonInTable(int gameID, String jsonString) throws SQLException{
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
