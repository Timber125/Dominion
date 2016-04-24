/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominion.DynamicCard;

import Client.ConnectionManager;
import Client.JSonFactory;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

/**
 *
 * @author admin
 */
public class ClientControlV2{
    
    @FXML
    Pane DominionPane;
    
    @FXML
    Pane ChatPane;
    
    @FXML
    Pane Pane1;
    
    @FXML
    Button ChatButton;
    
    @FXML
    TextArea ChatArea;
    
    @FXML
    TextField ChatText;
    
    @FXML
    Pane HandPane;
    
    @FXML
    AnchorPane HandCardPane;
    /*@FXML
    ImageView CardsHandView;
    */
    
    @FXML 
    Button EndPhase;
    
    @FXML
    Label ActionCount;
    
    @FXML
    Label BuyCount;
    
    @FXML
    Label CoinCount;
    
    @FXML
    ImageView CopperView;
    
    @FXML
    ImageView SilverView;
    
    @FXML
    ImageView GoldView;
    
    @FXML
    ImageView EstateView;
    
    @FXML
    ImageView DuchyView;
    
    @FXML
    ImageView ProvinceView;
    
    @FXML
    Pane EnvironmentPane;
    
    private ConnectionManager connection;
    private ClientModelService model;
    private String chat_alias;
    private MainV2 main;
    
    public ClientControlV2(String chat_alias, MainV2 main){
        this.chat_alias = chat_alias;
        this.main = main;
    }
    
    
    public void init() {
        ChatButton.setOnAction(displayError());
        EndPhase.setDisable(true);
    }
    
    
    public void setConnection(ConnectionManager manager){
        this.connection = manager;
        ChatButton.setOnAction(sendToServer());
        EndPhase.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent t) {
                main.control_end_phase();
            }
            
        });
    }
    
    public TextArea getDisplay(){
        return ChatArea;
    }
    
    private EventHandler sendToServer(){
        if(connection == null) {
            print_error();
            ChatButton.setOnAction(displayError());
            return displayError();
        }
        else return new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent t) {
                String msg = ChatText.getText();
                ChatText.setText(""); // Clear the input field
                connection.write(JSonFactory.JSON.protocol_chat(chat_alias, msg));
            }
            
        };
    }
    
    private EventHandler displayError(){
        return new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent t) {
                print_error();
            }
            
        };
    }
    
    private void print_error(){
        ChatArea.appendText("No connection found!\n");
    }
    
    protected void addCardToHand(ImageView iv){
        Pair<Integer, Integer> location = getLocationForNextCard();
        iv.setVisible(false);
        HandCardPane.getChildren().add(iv);
        iv.relocate(location.getKey(), location.getValue());
        iv.setVisible(true);
        card_count ++;
    }
   
    private Pair<Integer, Integer> getLocationForNextCard(){
        
        int locX = 0;
        int locY = 0;
        // De location function gebaseerd op CARD_WIDTH, CARD_HEIGHT, en HandCardPane.getWidth(). 
        int cards_per_line = (int) Math.round(Math.floor(HandCardPane.getWidth() / Card.CARD_WIDTH));
        //int used_width_per_line = cards_per_line * CARD_WIDTH;
        locX = (card_count % cards_per_line) * Card.CARD_WIDTH;
        locY = ((int)Math.round((Math.floor(card_count / cards_per_line)))) * Card.CARD_HEIGHT;
        
        //make scrollbar appear if necessary
        if((card_count + 1) % cards_per_line == 0){
            HandCardPane.setPrefHeight(HandCardPane.getPrefHeight() + Card.CARD_HEIGHT);
        }
        
        Pair<Integer, Integer> location = new Pair<>(locX,locY);
        return location;
    }
    
    private int card_count = 0;
    
    public void refreshHandView(){
        card_count = 0;
        
        HandCardPane.getChildren().clear();
        
        HandCardPane.setPrefHeight(Card.CARD_HEIGHT + 2);
    }
    
    public EventHandler<MouseEvent> enableClick(final Card c){
        return new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent t) {
                main.control_card_clicked(c);
            }
            
        };
    }
    public void appendTextToDisplay(String text){
        ChatArea.appendText(text);
    }
    public EventHandler<MouseEvent> disableClick(final Card c){
        return new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent t) {
                appendTextToDisplay("You cannot play this card right now! [" + c.getName() + "]\n");
            }
            
        };
    }
    
    public void updateTurnInfo(int actions, int buys, int coins){
        ActionCount.setText(actions + "");
        BuyCount.setText(buys + "");
        CoinCount.setText(coins + "");
    }
    
    
    public void initialize_treasurebuys() throws IOException{
        CopperView.setImage(Card.loadImage("copper"));
        SilverView.setImage(Card.loadImage("silver"));
        GoldView.setImage(Card.loadImage("gold"));
        
        CopperView.setOnMouseClicked(getBuyHandle("copper", CopperView));
        SilverView.setOnMouseClicked(getBuyHandle("silver", SilverView));
        GoldView.setOnMouseClicked(getBuyHandle("gold", GoldView));        
    }
    
    public void initialize_victorybuys() throws IOException{
        EstateView.setImage(Card.loadImage("estate"));
        DuchyView.setImage(Card.loadImage("duchy"));
        ProvinceView.setImage(Card.loadImage("province"));
        
        EstateView.setOnMouseClicked(getBuyHandle("estate", EstateView));
        DuchyView.setOnMouseClicked(getBuyHandle("duchy", DuchyView));
        ProvinceView.setOnMouseClicked(getBuyHandle("province", ProvinceView));
        

    }
    
    public EventHandler<MouseEvent> getBuyHandle(final String cardname,final ImageView me){
        return new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent t) {
                model.cardBuy(me, cardname);
                main.control_buy_card(cardname);
            }
            
        };
    }

    public void setModel(ClientModelService modelservice) throws IOException {
        this.model = modelservice;
        initialize_victorybuys();
        initialize_treasurebuys();
        // Initialize victories and treasures just like action environment! 
        // TODO
    }

   
    
    /**************************
     * 
     * Environment variables
     * 
     **************************/
    public void initializeActionStack(Card actionstack){
        this.EnvironmentPane.getChildren().add(actionstack.getView());
        actionstack.getView().relocate(action_cards_initialized * (Card.CARD_MEDIUM_WIDTH + 5),15);
        action_cards_initialized ++;
        actionstack.getView().setOnMouseClicked(getBuyHandle(actionstack.getName(), actionstack.getView()));
    }
    
    public int action_cards_initialized = 0;
    
}
