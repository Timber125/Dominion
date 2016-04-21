/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Dominion.DynamicCard;

import java.io.IOException;
import javafx.event.EventHandler;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author admin
 */
public class Card {
    // Static id-counter
    static Long nextID = 0L;
    
    public static int CARD_WIDTH = 150;
    public static int CARD_HEIGHT = 200;
    
    private final long id;
    private final String name;
    private ImageView iv;
    public Card(String name){
        this.name = name;
        this.id = nextID++; // oneliner to make it synchronized in bytecode
        this.iv = createCardDisplayFor(name);
        makeUnclickable();
    }
    
    public String getName(){
        return name;
    }
    
    public Long getID(){
        return id;
    }
    
    public ImageView getView(){
        return iv;
    }
    
    public void makeClickable(){
        Glow glow = new Glow(1.0);
        iv.setEffect(glow);
        iv.setOnMouseClicked(ClientControlV2.enableClick(this));
    }
    
    public void makeUnclickable(){
        Glow glow = new Glow(0.0);
        iv.setEffect(glow);
        iv.setOnMouseClicked(null); // Voor gebruiksvriendelijkheid later een boodschap laten weergeven?
    }
    
    
    // Throws IOException -> Blokkeer niet als je de kaart niet vindt. 
    // Als we alle kaarten goed aanroepen, dan is het de user zijn schuld
    //      dat de kaart niet gevonden wordt -> hij heeft hem dan manueel verplaatst. 
    public static Image loadImage(String name) throws IOException {
        Image card = new Image(ClassLoader.getSystemResource( "resources/DominionCards/" + name + ".jpg" ).openStream());
        return card;
    }
    
    /*
        De "styling" van de kaart-display kan hier aangepast worden. 
        Als je overal je imageviews maakt via deze functie, dan is de styling overal gelijk.
    */
    public static ImageView createCardDisplay(){
        ImageView ivi = new ImageView();
        ivi.setFitHeight(CARD_HEIGHT);
        ivi.setFitWidth(CARD_WIDTH);
        return ivi;
    }
    
    /*
        
        Creëert een imageview aan de hand van de functie hierboven, met opgegeven kaart-naam. 
        Zoekt de kaart, en indien niet gevonden (door typo's, of uitbreidingkaarten) laadt hij de 
        achterkant van een kaart (back.jpg). 
        
        Als ook de achterkant niet gevonden wordt, dan zijn we zeker dat de resource-folder 
        niet goed geinitialiseerd is. Dat zou niet mogen gebeuren. 
    
    */
    public static ImageView createCardDisplayFor(String imagename){
        Image card;
        try {
            card = loadImage(imagename);
        } catch (IOException ex) {
            System.err.println("[-] error loading " + imagename + ".jpg !");
            try {
                card = loadImage("back");
            } catch (IOException ex2){
                System.err.println("[-][-] Resources folder not initialized correctly!");
                // Return an empty imageview with the right size, so layout is not 'fucked'. 
                return createCardDisplay();
            }
        } catch (NullPointerException typoerror){
            System.err.println("[-] " + imagename + " is a typo, or not available in the resources folder.");
            try {
                card = loadImage("back");
            } catch (IOException ex2){
                System.err.println("[-][-] Resources folder not initialized correctly!");
                // Return an empty imageview with the right size, so layout is not 'fucked'. 
                return createCardDisplay();
            }
        }
        ImageView ivi = createCardDisplay();
        ivi.setImage(card);
        return ivi;
    }
   
    
    
    
    
}
