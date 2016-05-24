/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominion.Confirmation;

import Dominion.DynamicCard.Card;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 *
 * @author admin
 */
public class SelectableCard extends StackPane implements Observable{
    protected String cardname;
    protected String identifier;
    protected Card card;
    protected InvalidationListener il;
    protected int parentindex;
    
    public SelectableCard(String cardname, String identifier, int parentindex){
        this.cardname = cardname;
        this.identifier = identifier;
        this.parentindex = parentindex;
        this.card = new Card(cardname);
        
    }
    
    // Only one listener is needed, used easy single-listener implementation. 
    @Override
    public void addListener(final InvalidationListener il) {
        this.il = il;
        // Make 'this' accessible from inner scope -> that
        final Observable that = this;
        this.getChildren().add(this.card.getView());
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                il.invalidated(that);
            }
        });
    }
    
    @Override
    public void removeListener(InvalidationListener il) {
        this.il = null;
        //this.getChildren().remove(this.card.getView());
        this.setOnMouseClicked(null);
    }
}
