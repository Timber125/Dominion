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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

/**
 *
 * @author admin
 */
public class ClientControlV2 {
    
    final int CARD_WIDTH = 150;
    final int CARD_HEIGHT = 200;
    
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
    
    private ConnectionManager connection;
    private final String chat_alias;
    private MainV2 main;
    
    public ClientControlV2(String chat_alias, MainV2 main){
        this.chat_alias = chat_alias;
        this.main = main;
    }
    
    
    public void init() {
        ChatButton.setOnAction(displayError());
        /*try {
            // loadImage laadt de kaart uit het programma, niet van op de C schijf.
            CardsHandView.setImage(loadImage("moat"));
        } catch (IOException ex) {
            System.err.println("Could not load image 'moat'.");
        }
        CardsHandView.setOnMouseClicked(onCardPress());*/
        /*
        addCardToHand("moat");
        addCardToHand("lol");
        addCardToHand("adventurer");
        addCardToHand("gold");
        addCardToHand("ghostship");
        addCardToHand("treasuremap");
        addCardToHand("upgrade");
        addCardToHand("university");
        addCardToHand("torturer");
        addCardToHand("torturer");
        addCardToHand("copper");
        addCardToHand("estate");
        */
    }
    
    private void cardSwap(){
        /* 
        
        Ik heb dit in commentaar gezet om 2 redenen (no worries hoor ;p) 
        1 - je code doet nog niet echt iets veranderen, je displayt opnieuw "moat" :p 
            het probleem is, als je wilt checken welke kaart het op dit moment is, dan krijg je een 
            "if-statement", en dat is logica! 
        2 - omdat dit nog steeds een "controller" is, is het niet "netjes" om hier logica te zetten.
            je geeft beter door aan de 'main' dat de user deze actie heeft uitgevoerd. 
            de 'main' kan dan beslissen of er een pakketje naar de server moet worden gezonden,
            of als de actie lokaal kan gebeuren. 
        
        Daarom heb ik cardSwap doen verwijzen naar de 'main', en in de main een functie geschreven
        die een pakketje naar de server zendt, dat aangeeft dat de user een kaart wil trekken. 
        Op de serverside zie je dan al "User [session] draws 1 card". Dat is nog niet het correcte gedrag,
        maar er is wel al op gepaste wijze connectie met de server :)         
        
        jouw code was:
        
        File cardFile = new File ("C:/DominionCards/moat.jpg");
        Image card = new Image(cardFile.toURI().toString());
        CardsHandView.setImage(card);
        */
        
        main.control_card_clicked();
    }
    
    public void setConnection(ConnectionManager manager){
        this.connection = manager;
        ChatButton.setOnAction(sendToServer());
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
    
    /*
        Wat de fuck is dit ding? 
        -> eigenlijk vrij mooi :P 
    
    de functie geeft een "eventhandler" terug, dus eigenlijk :
    de functie geeft nen stuk code terug dat moet uitgevoerd worden als IETS gebeurt. 
        dat IETS dat gebeurt, da is nen mouseclick op de cardview, 
        dus in "initialize()" we roepen dat aan als:
        CardHandsView.setOnMouseClicked(jordyCheckDit()); 
        -> jordyCheckDit geeft dus de volledige actionhandler terug. 
        -> in jordyCheckDit() functie kunt ge het gedrag van die actie aanpassen. 
        
    Private: enkel voor deze klasse
    EventHandler: Betekent dat het een stuk code kan uitvoeren, ALS een event gebeurt. 
            iets da op zichzelf een stuk code kan uitvoeren, is nen Thread
            iets da op zichzelf een stuk code kan uitvoeren ALS een actie gebeurt: EventHandler
    
    ActionEvent: nen hele handige "shortcut" voor een basic mouseclick. 
        Andere mogelijkheden: 
            - MouseEvent : met de muis
            - KeyEvent : met het toetsenbord
            - WindowEvent : als uw window wordt geminimaliseerd, of gesloten, of geresized... 
            - ... 
    
        Al die "general" events kunnen ook gespecifieerd worden: 
            - KeyEvent.KeyReleased = als ge nen toets op uw keyboard loslaat 
            - MouseEvent.onMouseClicked = als ge uw muisknop indrukt 
            - MouseEvent.onMouseReleased = als ge uw muisknop loslaat
            - ... 
    
    als ge der geinteresseerd in zijt, zoek maar eens op. Als ge nen specifieke "event" zoekt, 
    vraag gewoon aan mij ;) 
    
    
    */
    
    private EventHandler onCardPress(){
        // Return een nieuwe "eventhandler", getriggerd door een mouseEvent. 
        return new EventHandler<javafx.scene.input.MouseEvent>(){

            @Override
            public void handle(javafx.scene.input.MouseEvent t) {
                // het enige wat hij moet doen is cardSwap() aanroepen;
                // dan kunt ge daar uw code definieren. 
                // Niet letten op die javafx.scene.input.MouseEvent, 
                // Hij deed wa moeilijk omdak per ongeluk AWT-MouseEvent had geimport. 
                cardSwap();
            }
            
        };
    }
    
    private void print_error(){
        ChatArea.appendText("No connection found!\n");
    }
    
    
    
    // Throws IOException -> Blokkeer niet als je de kaart niet vindt. 
    // Als we alle kaarten goed aanroepen, dan is het de user zijn schuld
    //      dat de kaart niet gevonden wordt -> hij heeft hem dan manueel verplaatst. 
    private Image loadImage(String name) throws IOException {
        Image card = new Image(ClassLoader.getSystemResource( "resources/DominionCards/" + name + ".jpg" ).openStream());
        return card;
    }
    
    /*
        De "styling" van de kaart-display kan hier aangepast worden. 
        Als je overal je imageviews maakt via deze functie, dan is de styling overal gelijk.
    */
    private ImageView createCardDisplay(){
        ImageView iv = new ImageView();
        iv.setFitHeight(CARD_HEIGHT);
        iv.setFitWidth(CARD_WIDTH);
        return iv;
    }
    
    /*
        
        Creëert een imageview aan de hand van de functie hierboven, met opgegeven kaart-naam. 
        Zoekt de kaart, en indien niet gevonden (door typo's, of uitbreidingkaarten) laadt hij de 
        achterkant van een kaart (back.jpg). 
        
        Als ook de achterkant niet gevonden wordt, dan zijn we zeker dat de resource-folder 
        niet goed geinitialiseerd is. Dat zou niet mogen gebeuren. 
    
    */
    private ImageView createCardDisplayFor(String imagename){
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
        ImageView iv = createCardDisplay();
        iv.setImage(card);
        return iv;
    }
    
    
    /* Some functions to add a card to the players hand. Takes an imageview, an image or a cardname-string.
        -> ImageView requires a ready-to-display imageview, with linked image and set sizes. 
        -> Image requires a loaded "image", don't give null images or anything... :)
        -> String requires the name of the card you want to display (in lowercase). 
    */
    protected void addCardToHand(ImageView iv){
        Pair<Integer, Integer> location = getLocationForNextCard();
        iv.setVisible(false);
        HandCardPane.getChildren().add(iv);
        iv.relocate(location.getKey(), location.getValue());
        iv.setVisible(true);
        card_count ++;
    }
    protected void addCardToHand(Image i){
        ImageView iv = createCardDisplay();
        iv.setImage(i);
        addCardToHand(iv);
    }
    public void addCardToHand(String cardname){
        ImageView iv = createCardDisplayFor(cardname);
        addCardToHand(iv);
    }
    
    private Pair<Integer, Integer> getLocationForNextCard(){
        
        int locX = 0;
        int locY = 0;
        // De location function gebaseerd op CARD_WIDTH, CARD_HEIGHT, en HandCardPane.getWidth(). 
        int cards_per_line = (int) Math.round(Math.floor(HandCardPane.getWidth() / CARD_WIDTH));
        //int used_width_per_line = cards_per_line * CARD_WIDTH;
        locX = (card_count % cards_per_line) * CARD_WIDTH;
        locY = ((int)Math.round((Math.floor(card_count / cards_per_line)))) * CARD_HEIGHT;
        
        //make scrollbar appear if necessary
        if((card_count + 1) % cards_per_line == 0){
            HandCardPane.setPrefHeight(HandCardPane.getPrefHeight() + CARD_HEIGHT);
        }
        
        Pair<Integer, Integer> location = new Pair<>(locX,locY);
        return location;
    }
    
    private int card_count = 0;
    
    private void refreshHandView(){
        card_count = 0;
        
        HandCardPane.getChildren().clear();
        
        HandCardPane.setPrefHeight(CARD_HEIGHT);
    }
    
}
