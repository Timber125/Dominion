/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominion.DynamicStack;

import Dominion.DynamicCard.Card;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author admin
 */
public class Stack extends StackPane{
    private ImageView card;
    private CountIcon icon;
    private CountLabel label;
    public Card c;
    public int count;
    public Stack(Card c, int initial_count){
        super();
        this.prefHeight(Card.CARD_HEIGHT);
        this.prefWidth(Card.CARD_WIDTH);
        card = c.getView();
        this.c = c;
        update(initial_count);
        this.count = initial_count;
    }
    
    private void construct(int count){
        card = c.getView();
        icon = new CountIcon();
        label = new CountLabel(count);
    }
    private void build(){
        this.getChildren().add(card);
        this.getChildren().add(icon);
        int centerX = (Card.CARD_WIDTH/2) - 24;
        int centerY = (Card.CARD_HEIGHT/2) - 24;
        icon.relocate(centerX,centerY);
        this.getChildren().add(label);
        label.relocate(centerX, centerY);
    }
    public void test(Stage s){
        AnchorPane root = new AnchorPane();
        Scene sc = new Scene(root, 300, 300);
        root.getChildren().add(this);
        s.setScene(sc);
    }
    public void update(int count){
        this.getChildren().clear();
        construct(count);
        build();
        this.count = count;
    }
    public Card getCard(){
        return this.c;
    }
    public StackPane getView(){
        return this;
    }
    
    
}
