/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cards.Basic;

import Cards.Components.ActionCard;
import Cards.Components.Card;
import Cards.Components.TreasureCard;
import Game.Environment;
import Game.InteractionCase;
import Game.Player;
import Game.SpecialCase;
import Server.JSONUtilities;

/**
 *
 * @author Jordy School
 */
public class Adventurer extends ActionCard {
    
    public Adventurer() {
        super("adventurer", 6);
    }
    
    // reveal cards from your deck until you reveal 2 treasurecards. 
    // Discard the rest, take the 2 treasures in hand. 
    
    @Override
    public boolean hasSpecial(){
        return true;
    }
    
    @Override
    public SpecialCase special(Player victim, Player initiator, Environment env){
        if(victim.getSession().equals(initiator.getSession())){
            InteractionCase onself = new InteractionCase(victim, initiator);
            onself.setMinAmount(2);
            onself.setMaxAmount(2);
            onself.setMinCost(0);
            onself.setMaxCost(100);
            // Allow nothing
            // But keep adding cards until 2 treasures
            Card[] treasures = new Card[2];
            int treasuresFound = 0;
            
            while(treasuresFound < 2){
                Card c = victim.takeCardFromDeck();
                if(c instanceof TreasureCard){
                    treasures[treasuresFound] = c;
                    treasuresFound ++;
                    onself.manually_add_selectedspecial(c, "other");
                }
                onself.allowedIds.add("deck");
                onself.preloadedCards.add(c);
                // Redirect the other cards directly to the discardpile. 
                victim.deck.used.add(c);
                
            }
            onself.setFinishBehaviour(JSONUtilities.JSON.adventurer_finishbehaviour());
            onself.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_model_adventurer(victim, initiator, onself));
            return onself;
        }else return null;
    }
}
