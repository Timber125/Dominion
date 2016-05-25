/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
 
package Dominion.DynamicCard;
 
import Client.ConnectionManager;
import Client.JSonFactory;
import Dominion.DynamicStack.Stack;
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
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
    /*
   
    Replaced by dynamic cards.
    
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
    */
    @FXML
    Pane EnvironmentPane;
   
    @FXML
    Pane TreasurePane;
   
    @FXML
    Pane VictoryPane;
  
    @FXML
    CheckBox Join;
   
    @FXML
    CheckBox Ready;
   
    @FXML
    AnchorPane Anchor;
   
    @FXML
    Button PlayerInfo;
   
    @FXML
    AnchorPane TableCardContent;
    
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
        Join.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t){
                main.join_changed(Join.selectedProperty().get(), chat_alias);
                if(Join.selectedProperty().get()){
                    Ready.setDisable(false);
                    Ready.setOnAction(new EventHandler<ActionEvent>(){
                        @Override
                        public void handle(ActionEvent t){
                            main.ready_changed(Ready.selectedProperty().get(), chat_alias);
                        }
                    });
                }
                else{
                    Ready.setDisable(true);
                }
            }
        });
        Ready.setDisable(true);
        PlayerInfo.setOnAction(askPlayerInfo());
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
   
    private EventHandler askPlayerInfo(){
        if(connection == null) {
            print_error();
            PlayerInfo.setOnAction(displayError());
            return displayError();
        }
        else return new EventHandler<ActionEvent>(){
 
            @Override
            public void handle(ActionEvent t) {
                connection.write(JSonFactory.JSON.protocol_dominion("none", "askInfo", 1));
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
   
    /*
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
    */
    public EventHandler<MouseEvent> getBuyHandle(final String cardname,final Parent me){
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
        //initialize_victorybuys();
        //initialize_treasurebuys();
        // Initialize victories and treasures just like action environment!
        // TODO
    }
 
  
    
    /**************************
     *
     * Environment variables
     *
     **************************/
    public void initializeActionStack(Stack actionstack){
        this.EnvironmentPane.getChildren().add(actionstack.getView());
        actionstack.getView().relocate(action_cards_initialized * (Card.CARD_MEDIUM_WIDTH + 5),10);
        action_cards_initialized ++;
        actionstack.getView().setOnMouseClicked(getBuyHandle(actionstack.getCard().getName(), actionstack.getView()));
    }
    
    
   
    public void initializeTreasureStack(Stack copper, Stack silver, Stack gold){
        initializeGold(gold);
        initializeSilver(silver);
        initializeCopper(copper);
    }
    public void initializeVictoryStack(Stack estate, Stack duchy, Stack province){
        initializeProvince(province);
        initializeDuchy(duchy);
        initializeEstate(estate);
    }
   
    public int action_cards_initialized = 0;
 
    private void initializeGold(Stack gold) {
        int offsetX = 0;
        int offsetY = 0;
        TreasurePane.getChildren().add(gold.getView());
        gold.getView().relocate(offsetX, offsetY);
        gold.getView().setOnMouseClicked(getBuyHandle("gold", gold.getView()));
    }
 
    private void initializeSilver(Stack silver) {
        int offsetX = 0;
        int offsetY = Card.CARD_HEIGHT;
        TreasurePane.getChildren().add(silver.getView());
        silver.getView().relocate(offsetX, offsetY);
        silver.getView().setOnMouseClicked(getBuyHandle("silver", silver.getView()));
    }
 
    private void initializeCopper(Stack copper) {
        int offsetX = 0;
        int offsetY = 2*Card.CARD_HEIGHT;
        TreasurePane.getChildren().add(copper.getView());
        copper.getView().relocate(offsetX, offsetY);
        copper.getView().setOnMouseClicked(getBuyHandle("copper", copper.getView()));
    }
 
    private void initializeProvince(Stack province) {
        int offsetX = 0;
        int offsetY = 0;
        VictoryPane.getChildren().add(province.getView());
        province.getView().relocate(offsetX, offsetY);
        province.getView().setOnMouseClicked(getBuyHandle("province", province.getView()));
    }
 
    private void initializeDuchy(Stack duchy) {
        int offsetX = 0;
        int offsetY = Card.CARD_HEIGHT;
        VictoryPane.getChildren().add(duchy.getView());
        duchy.getView().relocate(offsetX, offsetY);
        duchy.getView().setOnMouseClicked(getBuyHandle("duchy", duchy.getView()));
    }
 
    private void initializeEstate(Stack estate) {
        int offsetX = 0;
        int offsetY = 2*Card.CARD_HEIGHT;
        VictoryPane.getChildren().add(estate.getView());
        estate.getView().relocate(offsetX, offsetY);
        estate.getView().setOnMouseClicked(getBuyHandle("estate", estate.getView()));
    }
   
    private Stack last_initialized_discardstack;
    private Stack last_initialized_curse;
    private Stack last_initialized_trash;
   
    public void initialize_myenvironment(Stack deck, Stack disc, Stack trash, Stack curse){
        initialize_deck(deck);
        initialize_disc(disc);
        initialize_trash(trash);
        initialize_curse(curse);
    }
    public void initialize_deck(Stack deck){
        HandPane.getChildren().add(deck.getView());
        deck.getView().relocate(760, 120);
    }
    public void initialize_disc(Stack disc){
        last_initialized_discardstack = disc;
        HandPane.getChildren().add(last_initialized_discardstack.getView());
        last_initialized_discardstack.getView().relocate(860, 120);
    }
    public void initialize_trash(Stack trash){
        last_initialized_trash = trash;
        HandPane.getChildren().add(last_initialized_trash.getView());
        trash.getView().relocate(760, 0);
    }
    public void initialize_curse(Stack curse){
        last_initialized_curse = curse;
        HandPane.getChildren().add(curse.getView());
        curse.getView().relocate(860, 0);
    }
    public void reinitialize_disc(Stack disc){
        HandPane.getChildren().remove(last_initialized_discardstack);
        last_initialized_discardstack = disc;
        HandPane.getChildren().add(last_initialized_discardstack);
        last_initialized_discardstack.relocate(860, 120);
    }
    public void reinitialize_trash(Stack trash){
        HandPane.getChildren().remove(last_initialized_trash);
        last_initialized_trash = trash;
        HandPane.getChildren().add(last_initialized_trash);
        last_initialized_trash.relocate(760, 0);
    }
   
    private int tableCardCount = 0;
    private Pair<Integer, Integer> getLocationForNextTableCard(){
       
        int locX = 0;
        int locY = 0;
        // De location function gebaseerd op CARD_WIDTH, CARD_HEIGHT, en HandCardPane.getWidth().
        int cards_per_line = (int) Math.round(Math.floor(TableCardContent.getWidth() / Card.CARD_WIDTH));
        //int used_width_per_line = cards_per_line * CARD_WIDTH;
        locX = (tableCardCount % cards_per_line) * Card.CARD_WIDTH;
        locY = ((int)Math.round((Math.floor(tableCardCount / cards_per_line)))) * Card.CARD_HEIGHT;
       
        //make scrollbar appear if necessary
        if((tableCardCount + 1) % cards_per_line == 0){
            TableCardContent.setPrefHeight(TableCardContent.getPrefHeight() + Card.CARD_HEIGHT);
        }
       
        Pair<Integer, Integer> location = new Pair<>(locX,locY);
        return location;
    }
    
    public void refreshTableCardView(){
        tableCardCount = 0;
       
        TableCardContent.getChildren().clear();
       
        TableCardContent.setPrefHeight(Card.CARD_HEIGHT + 2);
    }
    protected void addCardToTable(ImageView iv){
        Pair<Integer, Integer> location = getLocationForNextTableCard();
        iv.setVisible(false);
        TableCardContent.getChildren().add(iv);
        iv.relocate(location.getKey(), location.getValue());
        iv.setVisible(true);
        tableCardCount ++;
    }

    void reinitialize_curse(Stack curse) {
        HandPane.getChildren().remove(last_initialized_curse);
        last_initialized_curse = curse;
        HandPane.getChildren().add(last_initialized_curse);
        last_initialized_curse.relocate(860, 0);
    }
}