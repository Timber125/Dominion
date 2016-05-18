/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server.Service;

import Server.AbstractConnectionHandler;
import Server.ConnectionHandler;
import Server.JSONUtilities;
import Server.Server;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import org.json.JSONObject;

/**
 *
 * @author admin
 */
public class ChatService extends Service{
    public ChatService(Server server){
        super(server);
        known_service_types.add("chat");
    }

    @Override
    public void handleType(String type, JSONObject json) {
        if(type.equals("chat")) handle_chat(json);
        System.err.println("chatservice received a json");
    }
    
    public void handle_chat(JSONObject json){
        String author = json.getString("author");
        String message = json.getString("message");
        if(message.charAt(0) == '!'){
            // Substring! The '!' is removed before sending to "handle_command".
            handle_command(message.substring(1), json);
            return;
        }
        String writeback = author + ": " + message;
        JSONObject reply = JSONUtilities.JSON.create("action", "sysout");
        reply = JSONUtilities.JSON.addKeyValuePair("sysout", writeback, reply);
        for(AbstractConnectionHandler ch : server.clients){
            ch.write(reply);
        }
        
    }
    
    public void handle_command(String command, JSONObject json){
        if(command.startsWith("rename ")){
            AbstractConnectionHandler ch = server.getClient(json.getString("session"));
            String rename = command.substring(7).trim();
            String oldname = ch.getNickname();
            ch.changeNickname(rename);
            ch.write(RenameReply(rename));
            server.sendAll(RenameAnnouncement(oldname, rename));
            return;
        }else if(command.startsWith("playcard ")){
            String cardname = command.substring(8).trim();
            String session = json.getString("session");
            ServiceBroker.instance.offerRequest(SimulateCardOffer(session, cardname).toString());
        }
        switch(command){
            case("lobbyconnect"):{
                ServiceBroker.instance.offerRequest(SimulateLobbyConnect(json.getString("session")).toString());
                return;
            }
            case("lobbydisconnect"):{
                ServiceBroker.instance.offerRequest(SimulateLobbyDisconnect(json.getString("session")).toString());
                return;
            }
            case("lobbyvote"):{
                ServiceBroker.instance.offerRequest(SimulateLobbyVote(json.getString("session"), true).toString());
                return;
            }
            case("lobbyunvote"):{
                ServiceBroker.instance.offerRequest(SimulateLobbyVote(json.getString("session"), false).toString());
                return;
            }
            case("finishturn"):{
                ServiceBroker.instance.offerRequest(SimulateFinishTurn(json.getString("session")).toString());
                return;
            }
            case("draw5"):{
                ServiceBroker.instance.offerRequest(SimulateDrawCards(json.getString("session")).toString());
                return;
            }
            case("discardhand"):{
                ServiceBroker.instance.offerRequest(SimulateDiscardHand(json.getString("session")).toString());
                return;
            }
            case("shuffle"):{
                ServiceBroker.instance.offerRequest(SimulateShuffle(json.getString("session")).toString());
                return;
            }
            case("help"):{
                JSONObject help = HelpCommand();
                for(AbstractConnectionHandler ch : server.clients){
                    if(ch.validSession(json.toString()))ch.write(help);
                }
                return;
            }
            default:{
                String writeback = "command [" + command + "] not found.";
                JSONObject reply = JSONUtilities.JSON.create("action", "sysout");
                reply = JSONUtilities.JSON.addKeyValuePair("sysout", writeback, reply);
                for(AbstractConnectionHandler ch : server.clients){
                    // Only address the command issuer.
                    if(ch.validSession(json.toString())) ch.write(reply);
                }
            }
        }
    }
    
    
    private JSONObject HelpCommand(){
        String helpmessage = "Command list:\n";
        helpmessage += "!finishturn: Simulates dominion-action finishturn\n";
        helpmessage += "!draw5: Simulates dominion-action draw_card, repeat=5\n";
        helpmessage += "!discardhand: Simulates dominion-action discard_hand, repeat=1\n";
        helpmessage += "!shuffle: Simulates dominion-action shuffle_deck_graveyard, repeat=1\n";
        helpmessage += "!rename nickname: set a new nickname, no spaces allowed.";
        helpmessage += "!lobbyconnect: take a seat at the table\n";
        helpmessage += "!lobbydisconnect: leave the gametable\n";
        helpmessage += "!lobbyvote: ready to start game\n";
        helpmessage += "!lobbyunvote: not ready to start game\n";
        helpmessage += "!help: Displays this help message";
        JSONObject help = JSONUtilities.JSON.create("action", "sysout");
        help = JSONUtilities.JSON.addKeyValuePair("sysout", helpmessage, help);
        return help;
    }
    
    private JSONObject RenameReply(String name){
        JSONObject obj = JSONUtilities.JSON.create("action", "session");
        JSONUtilities.JSON.addKeyValuePair("newname", name, obj);
        return obj;
    }
    
    private JSONObject RenameAnnouncement(String old, String newname){
        JSONObject obj = JSONUtilities.JSON.create("action", "sysout");
        JSONUtilities.JSON.addKeyValuePair("sysout", "*** " + old + " changed his name to " + newname, obj);
        return obj;
    }
    
    private JSONObject SimulateFinishTurn(String session){
        JSONObject commandObj = JSONUtilities.JSON.create("session", session);
        commandObj = JSONUtilities.JSON.addKeyValuePair("service_type", "dominion", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("operation", "finish", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("repeat", "1", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("phase", "draw", commandObj);
        return commandObj;
    }
    
    private JSONObject SimulateDrawCards(String session){
        JSONObject commandObj = JSONUtilities.JSON.create("session", session);
        commandObj = JSONUtilities.JSON.addKeyValuePair("service_type", "dominion", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("operation", "draw_cards", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("repeat", "5", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("phase", "draw", commandObj);
        return commandObj;
    }
    
    private JSONObject SimulateDiscardHand(String session){
        JSONObject commandObj = JSONUtilities.JSON.create("session", session);
        commandObj = JSONUtilities.JSON.addKeyValuePair("service_type", "dominion", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("operation", "discard_hand", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("repeat", "1", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("phase", "draw", commandObj);
        return commandObj;
    }

    private JSONObject SimulateShuffle(String session){
        JSONObject commandObj = JSONUtilities.JSON.create("session", session);
        commandObj = JSONUtilities.JSON.addKeyValuePair("service_type", "dominion", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("operation", "shuffle_deck_graveyard", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("repeat", "1", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("phase", "draw", commandObj);
        return commandObj;
    }
    
    private JSONObject SimulateLobbyConnect(String session){
        JSONObject commandObj = JSONUtilities.JSON.create("session", session);
        commandObj = JSONUtilities.JSON.addKeyValuePair("service_type", "lobby", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("operation", "connect", commandObj);
        return commandObj;
    }
    
    private JSONObject SimulateLobbyDisconnect(String session){
        JSONObject commandObj = JSONUtilities.JSON.create("session", session);
        commandObj = JSONUtilities.JSON.addKeyValuePair("service_type", "lobby", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("operation", "disconnect", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("author", server.getNickname(session), commandObj);
        return commandObj;
    }
    
    private JSONObject SimulateLobbyVote(String session, boolean vote){
        JSONObject commandObj = JSONUtilities.JSON.create("session", session);
        commandObj = JSONUtilities.JSON.addKeyValuePair("service_type", "lobby", commandObj);
        if(vote) commandObj = JSONUtilities.JSON.addKeyValuePair("operation", "vote", commandObj);
        else commandObj = JSONUtilities.JSON.addKeyValuePair("operation", "unvote", commandObj);
        return commandObj;
    }
    
    private JSONObject SimulateCardOffer(String session, String cardname){
        JSONObject commandObj = JSONUtilities.JSON.create("session", session);
        commandObj = JSONUtilities.JSON.addKeyValuePair("service_type", "dominion", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("phase", "action", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("operation", "playcard", commandObj);
        commandObj = JSONUtilities.JSON.addKeyValuePair("card", cardname, commandObj);
        return commandObj;
    }
}
