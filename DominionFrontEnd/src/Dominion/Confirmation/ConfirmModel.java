/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominion.Confirmation;

import Client.ConnectionManager;
import Client.JSonFactory;
import Dominion.DynamicCard.Card;
import Dominion.DynamicCard.ClientModelService;
import Dominion.DynamicStack.Stack;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

/**
 *
 * @author admin
 */
public class ConfirmModel implements InvalidationListener{

    public ConfirmController myControl;
    private final ClientModelService callback;
    private ConnectionManager connection;
    private Stage managerStage;
    private int[] number_of_displayed_cards_for_entry;
    
    
    void intialize(ConfirmController myControl) {
        this.myControl = myControl;
        number_of_displayed_cards_for_entry = new int[3];
        for(int i = 0; i < number_of_displayed_cards_for_entry.length; i++){
            number_of_displayed_cards_for_entry[i] = 0;
        }
    }
    
    public ConfirmModel(ClientModelService callback, ConnectionManager connection){
        this.callback = callback;
        this.connection = connection;
    }
    
    
    
    /*
    REQUIREMENTS: 
        3 "Scrollpanes", waarin kaarten komen die selecteerbaar zullen zijn. 
            Er zit al 1 scrollpane in, een scrollpane heeft altijd een anchorpane in zich zitten. 
            de anchorpane in de scrollpane is hetgeen waarop je dingen zet, van de scrollpane trek je je niets aan. 
            Link dus enkel die anchorpane in je controller (@FXML AnchorPane hetpaneel) en niet (@FXML ScrollPane scroll)
        
        1 button "confirm"
    
    
    */
    private final String blocked = "Attack Blocked";
    private final String passed = "Attack Passed";
    private final Color cblocked = Color.RED;
    private final Color cpassed = Color.GREEN;
    public void setStage(Stage s){
        this.managerStage = s;
    }
    void insert_info(final ConfirmInfo information, final ArrayList<Card> handcardsreference) {
        final InvalidationListener that = this;
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                for(int i = 0; i < 3; i++){
                    ArrayList<Long> used_hand_ids = new ArrayList<>();
                    myControl.changenickname(i, information.nickname[i]);
                    myControl.changedecksize(i, "Deck size: " + information.decksize[i]);
                    myControl.changediscardsize(i, "Discardpile size: " + information.discardsize[i]);
                    myControl.changehandsize(i, "Hand size: " + information.handsize[i]);
                    myControl.changevictorypoints(i, "Victory points: " + information.victorypoints[i]);
                    myControl.changeblocked(i, information.blocked[i]?(blocked):(passed), information.blocked[i]?(cblocked):(cpassed));
                    for(int j = 0; j < information.entrycount[i]; j++){
                        
                        // Inject the real long-id that is in client's hand instead of id "hand"
                        if(information.identifiers[i].get(j).equals("hand")){
                            String name = information.cardnames[i].get(j);
                            for(Card c : handcardsreference){
                                if(!used_hand_ids.contains(c.getID())){
                                    if(c.getName().equals(name)){
                                        used_hand_ids.add(c.getID());
                                        information.identifiers[i].remove(j);
                                        information.identifiers[i].add(j, c.getID().toString());
                                        break;
                                    }
                                }
                            }
                        }
                        SelectableCard sc = new SelectableCard(information.cardnames[i].get(j), information.identifiers[i].get(j), i);
                        sc.addListener(that);
                        addToCardView(i, sc);
                        // We need no reference to the selectable card to avoid memleaks:
                        // We clear all cards at once, 
                        // And once the confirmview closes we do a clear of all children in the panes. 
                        // This removes the last hidden references to the selectablecards,
                        // Which makes the garbagecollector deallocate the memory. 
                    }
                }
                System.out.println("The info that should be shown is: ");
                System.out.println(information.info);
                myControl.changeinfo(information.info);
            }
        });
        
        // Test card adding: works.
        
        /*
        Card test = new Card("back");
        Stack teststack = new Stack(test,5);
        teststack.getView().setOnMouseClicked(new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent t) {
                System.out.println("FUCKITALL, FUCKITAAAAAALLLL");
            }
                
        });
        addToCardView(0, teststack);
        
        */
        
        
    }

    void clear_info() {
        //
    }

    void confirmed() {
        connection.write(JSonFactory.JSON.protocol_interaction_confirmrequest());
    }

    
    public void addToCardView(final int index, final StackPane view){
        final Pair<Integer, Integer> location = getLocationForNextCard(index);
        number_of_displayed_cards_for_entry[index] ++;
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                myControl.addToCardPane(index, view, location.getKey(), location.getValue());
            }
        });
    }
    
    public void refreshCardView(final int index){
        number_of_displayed_cards_for_entry[index] = 0;
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
               myControl.clearCardPane(index);
            }
        });
    }
    
    public void refreshAllCardViews(){
        for(int i = 0; i < number_of_displayed_cards_for_entry.length; i++){
            refreshCardView(i);
        }
    }
    private Pair<Integer, Integer> getLocationForNextCard(int for_entry){
        
        int locX;
        int locY;
        // De location function gebaseerd op CARD_WIDTH, CARD_HEIGHT, en HandCardPane.getWidth(). 
        int cards_per_line = 4;
        //int used_width_per_line = cards_per_line * CARD_WIDTH;
        locX = (number_of_displayed_cards_for_entry[for_entry] % cards_per_line) * Card.CARD_WIDTH;
        locY = ((int)Math.round((Math.floor(number_of_displayed_cards_for_entry[for_entry] / cards_per_line)))) * Card.CARD_HEIGHT;
        
        //make scrollbar appear if necessary
        /*if((number_of_displayed_cards_for_entry[for_entry] + 1) % cards_per_line == 0){
            HandCardPane.setPrefHeight(HandCardPane.getPrefHeight() + Card.CARD_HEIGHT);
        }*/
        
        Pair<Integer, Integer> location = new Pair<>(locX,locY);
        return location;
    }

    @Override
    public void invalidated(Observable o) {
        if(o instanceof SelectableCard){
            SelectableCard changed = (SelectableCard) o;
            System.out.println("Clicked: [" + changed.parentindex + "] " + changed.cardname + " - " + changed.identifier);
            connection.write(JSonFactory.JSON.protocol_interaction_changenotification(changed.cardname, changed.identifier, 1, changed.parentindex));
        }
    }
    
    public void hide(){
       Platform.runLater(new Runnable(){

           @Override
           public void run() {
               refreshAllCardViews();
               managerStage.hide();
           }
           
       });
    }
    public void show(){
      Platform.runLater(new Runnable(){

           @Override
           public void run() {
               managerStage.show();
               managerStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

                   @Override
                   public void handle(WindowEvent t) {
                       // Javadocs:
                       // "The eventhandler can prevent the window from closing by consuming the event."
                       t.consume();
                   }
               });
               managerStage.requestFocus();
           }
           
       });
    }
    
    
}
