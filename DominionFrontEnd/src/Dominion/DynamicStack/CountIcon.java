/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominion.DynamicStack;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author admin
 */
public class CountIcon extends Circle{
    public CountIcon(){
        super(20, Color.web("0xc1af00"));
        this.strokeWidthProperty().setValue(2);
        this.strokeProperty().setValue(Color.BLACK);
    }
}
