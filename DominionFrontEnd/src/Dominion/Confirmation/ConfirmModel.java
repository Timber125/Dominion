/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominion.Confirmation;

import Dominion.DynamicCard.ClientModelService;
import javafx.application.Platform;
import javafx.scene.paint.Color;

/**
 *
 * @author admin
 */
public class ConfirmModel {

    public ConfirmController myControl;
    private ClientModelService callback;
    void intialize(ConfirmController myControl) {
        this.myControl = myControl;
    }
    
    public ConfirmModel(ClientModelService callback){
        this.callback = callback;
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
    void insert_info(final ConfirmInfo information) {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                for(int i = 0; i < 3; i++){
                    myControl.changenickname(i, information.nickname[i]);
                    myControl.changedecksize(i, "Deck size: " + information.decksize[i]);
                    myControl.changediscardsize(i, "Discardpile size: " + information.discardsize[i]);
                    myControl.changehandsize(i, "Hand size: " + information.handsize[i]);
                    myControl.changevictorypoints(i, "Victory points: " + information.victorypoints[i]);
                    myControl.changeblocked(i, information.blocked[i]?(blocked):(passed), information.blocked[i]?(cblocked):(cpassed));
                }
                myControl.changeinfo(information.info);
            }
        });
    }

    void clear_info() {
        //
    }

    void confirmed() {
        // CHECKS!
        // then use callback to make some kind of callback. 
    }
    
}
