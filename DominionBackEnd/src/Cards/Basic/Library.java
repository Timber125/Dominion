/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cards.Basic;

import Cards.Components.ActionCard;
import Cards.Components.Card;
import Game.Environment;
import Game.InteractionCase;
import Game.Player;
import Game.SpecialCase;
import Server.JSONUtilities;
import java.util.ArrayList;

/**
 *
 * @author Jordy School
 */
public class Library extends ActionCard {
    
    
    private boolean interactionFinished = false;
    public Library() {
        super("library", 5);
    }
    
    @Override
    public boolean hasSpecial(){
        return true;
    }
    
    @Override
    public SpecialCase special(Player victim, Player initiator, Environment env){
        if(victim.getSession().equals(initiator.getSession())){
            InteractionCase ic = new InteractionCase(victim, initiator);
            // If we already have 7 cards, return null.
            int victimcards = victim.hand.size();
            if(victimcards >= 7) return null;
            else{
                    ic.setMaxAmount(1);
                    ic.setMinAmount(0);
                    ic.setMaxCost(100);
                    ic.setMinCost(0);
                    Card curr = victim.takeCardFromDeck();
                    if(curr == null) return null; // YOU HAVE NO MORE CARDS!
                    ic.preloadedCards.add(curr);
                    ic.enableEnvActions();
                    if(curr instanceof ActionCard){
                        ic.setMaxAmount(1);
                        ic.allowedIds.add("interaction");
                    }else{
                        ic.setMaxAmount(0);
                        ic.allowedIds.add("none");
                    }
                    ic.setFinishBehaviour(JSONUtilities.JSON.library_notfinished_finishbehaviour());
                    ic.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_library(victim, initiator, ic));
                    
                    return ic;
            }
        }
        else {
            return null;
        }
    }
    
    @Override
    public InteractionCase future_futureSpecial(Player victim, Player initiator, Environment env, InteractionCase prev){
        //if(interactionFinished) return null;
        if(victim.getSession().equals(initiator.getSession())){
            InteractionCase ic = new InteractionCase(victim, initiator);
            // If we already have 7 cards, return null.
            System.out.println("Creating future interaction, data:");
            System.out.println("Preloadedcard previous size : " + prev.preloadedCards.size());
            // Inject the previous case
            boolean excludedAlready = false;
            for(Card c : prev.preloadedCards){
                boolean excludethis = false;
                if(!excludedAlready){
                    for(Card excluded : prev.selectedSpecials){
                        if(c.getName().equals(excluded.getName())){
                            excludedAlready = true;
                            excludethis = true;
                            System.out.println("Not adding " + excluded.getName() + " since it got selected");
                        }
                    }
                }
                if(!excludethis){
                    System.out.println("Adding to preloadedcards: " + c.getName() + " - " + "interaction");
                    ic.preloadedCards.add(c);
                    ic.allowedIds.add("interaction");
                }
            }
            // Remove the actioncard that might have been discarded.
            System.out.println("Preloadedcards ic is now: " + ic.preloadedCards.size());
            for(Card c : prev.selectedSpecials){
                System.out.println("Removing " + c.getName() + " from preloadedcards");
                System.out.println("success: " + ic.preloadedCards.remove(c));
                ic.allowedIds.remove("interaction");
                // Before losing reference, add it to the discardpile. Update happens on library ending.
                victim.deck.used.add(c);
            }
            
            int victimcards = victim.hand.size() + ic.preloadedCards.size();
            System.out.println("We now have " + victimcards + " victimcards");
            if(victimcards >= 7) {
                    ic.setMinAmount(prev.preloadedCards.size());
                    ic.setMaxAmount(prev.preloadedCards.size());
                    for(Card c : prev.preloadedCards) ic.manually_add_selectedspecial(c, "deck");
                    ic.setMaxCost(100);
                    ic.setMinCost(0);
                    
                    ic.setFinishBehaviour(JSONUtilities.JSON.library_finished_finishbehaviour());
                    ic.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_library_finish(victim, initiator, ic));
                    //interactionFinished = true; // This was the last interaction
                    victim.setIterativeInteractionFactory(null); // Stop iteration
                    return ic;
            }
            else{
                    ic.setMinAmount(0);
                    ic.setMaxCost(100);
                    ic.setMinCost(0);
                    Card curr = victim.takeCardFromDeck();
                    if(curr == null) return null; // YOU HAVE NO MORE CARDS!
                    ic.preloadedCards.add(curr);
                    
                    ic.enableEnvActions();
                    if(curr instanceof ActionCard){
                        ic.setMaxAmount(1);
                        ic.allowedIds.add("interaction");
                    }else{
                        ic.setMaxAmount(0);
                        ic.allowedIds.add("none");
                    }
                    ic.setFinishBehaviour(JSONUtilities.JSON.library_notfinished_finishbehaviour());
                    ic.setStartBehaviour(JSONUtilities.JSON.make_client_confirmation_library(victim, initiator, ic));
                    return ic;
            }
        }else{
            return null;
        }
    }
}
