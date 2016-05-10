/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominion.DynamicStack;

import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *
 * @author admin
 */
public class CountLabel extends Label{
    public CountLabel(Integer num){
        super(num.toString());
        this.setFont(Font.font ("Arial", FontWeight.BOLD, 16));
    }
}
