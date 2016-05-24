/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominion.Confirmation;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;



/**
 *
 * @author admin
 */
public class ConfirmController {
    
    @FXML
    Label nickname_0;
    @FXML
    Label nickname_1;
    @FXML
    Label nickname_2;
    
    @FXML
    Label decksize_0;
    @FXML
    Label decksize_1;
    @FXML
    Label decksize_2;
    
    @FXML
    Label discardsize_0;
    @FXML
    Label discardsize_1;
    @FXML
    Label discardsize_2;
    
    @FXML
    Label handsize_0;
    @FXML
    Label handsize_1;
    @FXML
    Label handsize_2;
    
    @FXML
    Label victorypoints_0;
    @FXML
    Label victorypoints_1;
    @FXML
    Label victorypoints_2;
    
    @FXML
    Label blocked_0;
    @FXML
    Label blocked_1;
    @FXML
    Label blocked_2;
    
    @FXML
    AnchorPane cardpane_0;
    @FXML
    AnchorPane cardpane_1;
    @FXML
    AnchorPane cardpane_2;
    
    @FXML
    protected Label info;
    @FXML
    Button confirmbutton;
    
    
    
    public ConfirmModel model;
    public ConfirmController(ConfirmModel myModel){
        model = myModel;
    }

    void startup() {
        confirmbutton.setOnAction(new EventHandler<ActionEvent>(){

            @Override
            public void handle(ActionEvent t) {
                model.confirmed();
                // We vragen hier niet om success: het model checkt of confirm wel mag: 
                //      Als confirm toegelaten is, stuurt het een reactie door naar de server, en sluit het scherm. 
                //      Als confirm niet toegelaten is, blijft het scherm open en dropt het model de request. 
                //          Zo lijkt het alsof confirm "niet werkt" tot confirm toegelaten is. 
                //          Het model zou kunnen een errormessage laten printen, maar daar is (nog) geen label voor voorzien.
            }
            
        });
    }
    
    public void changenickname(int index, String nickname){
        switch(index){
            case(0):{
                nickname_0.setText(nickname);
                break;
            }
            case(1):{
                nickname_1.setText(nickname);
                break;
            }
            case(2):{
                nickname_2.setText(nickname);
                break;
            }
            default:{
                System.err.println("confimcontroller - changenickname index [" + index + "] not recognized");
            }
        }
    }
    
    public void changedecksize(int index, String decksize){
        switch(index){
            case(0):{
                decksize_0.setText(decksize);
                break;
            }
            case(1):{
                decksize_1.setText(decksize);
                break;
            }
            case(2):{
                decksize_2.setText(decksize);
                break;
            }
            default:{
                System.err.println("confimcontroller - changedecksize index [" + index + "] not recognized");
            }
        }
    }
    
    public void changediscardsize(int index, String discardsize){
        switch(index){
            case(0):{
                discardsize_0.setText(discardsize);
                break;
            }
            case(1):{
                discardsize_1.setText(discardsize);
                break;
            }
            case(2):{
                discardsize_2.setText(discardsize);
                break;
            }
            default:{
                System.err.println("confimcontroller - discardsize index [" + index + "] not recognized");
            }
        }
    }
    
    public void changehandsize(int index, String handsize){
        switch(index){
            case(0):{
                handsize_0.setText(handsize);
                break;
            }
            case(1):{
                handsize_1.setText(handsize);
                break;
            }
            case(2):{
                handsize_2.setText(handsize);
                break;
            }
            default:{
                System.err.println("confimcontroller - handsize index [" + index + "] not recognized");
            }
        }
    }
    
    public void changevictorypoints(int index, String victorypoints){
        switch(index){
            case(0):{
                victorypoints_0.setText(victorypoints);
                break;
            }
            case(1):{
                victorypoints_1.setText(victorypoints);
                break;
            }
            case(2):{
                victorypoints_2.setText(victorypoints);
                break;
            }
            default:{
                System.err.println("confimcontroller - victorypoints index [" + index + "] not recognized");
            }
        }
    }
    
    public void changeblocked(int index, String blocked, Color display){
        switch(index){
            case(0):{
                blocked_0.setText("");
                blocked_0.setTextFill(display);
                blocked_0.setText(blocked);
                break;
            }
            case(1):{
                blocked_1.setText("");
                blocked_1.setTextFill(display);
                blocked_1.setText(blocked);
                break;
            }
            case(2):{
                blocked_2.setText("");
                blocked_2.setTextFill(display);
                blocked_2.setText(blocked);
                break;
            }
            default:{
                System.err.println("confimcontroller - blocked index [" + index + "] not recognized");
            }
        }
    }
    
    public void changeinfo(final String infoS){
        
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                info.setText("");
                info.setText(infoS);
                info.setTextFill(Color.WHITE);
                info.setStyle("-fx-text-fill:WHITE;");
                info.setText(infoS);
            }
        });
        
    }
    
    public void clearCardPane(int index){
        switch(index){
            case(0):{
                cardpane_0.getChildren().clear();
                cardpane_0.setPrefHeight(200);
                break;
            }
            case(1):{
                cardpane_1.getChildren().clear();
                cardpane_1.setPrefHeight(200);
                break;
            }
            case(2):{
                cardpane_2.getChildren().clear();
                cardpane_2.setPrefHeight(200);
                break;
            }
            default:{
                System.err.println("confimcontroller - cardpane index [" + index + "] not recognized");
            }
        }
    }
    public void addToCardPane(int index, StackPane theview, int locationX, int locationY){
        switch(index){
            case(0):{
                cardpane_0.getChildren().add(theview);
                theview.relocate(locationX, locationY);
                cardpane_0.setPrefHeight(locationY + 200);
                break;
            }
            case(1):{
                cardpane_1.getChildren().add(theview);
                theview.relocate(locationX, locationY);
                cardpane_1.setPrefHeight(locationY + 200);
                break;
            }
            case(2):{
                cardpane_2.getChildren().add(theview);
                theview.relocate(locationX, locationY);
                cardpane_2.setPrefHeight(locationY + 200);
                break;
            }
            default:{
                System.err.println("confimcontroller - addtocardpane index [" + index + "] not recognized");
            }
        }
    }
    
}
