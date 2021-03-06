/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cards.Basic;

import Cards.Components.ActionCard;
import Game.Environment;
import Game.Player;
import Game.RewardCase;
import Game.SpecialCase;

/**
 *
 * @author Jordy School
 */
public class Councilroom extends ActionCard {
    
    public Councilroom() {
        super("councilroom", 5);
    }
    
    @Override
    public int cardgain(){
        return 4;
    }
    
    @Override
    public int purchasegain(){
        return 1;
    }
    
    //todo: all other players gain 1 card
    @Override
    public boolean hasSpecial(){
        return true;
    }
    @Override
    public SpecialCase special(Player victim, Player initiator, Environment env){
        if(victim.getSession().equals(initiator.getSession())) return null; // Dont treat initiator as a victim.
        RewardCase council_rewardcase = new RewardCase(victim);
        council_rewardcase.setCardGain(1);
        return council_rewardcase;
    }
    /* happens automatically now, if card detects that special is not null.
    @Override
    public boolean hasSpecial(){
        return true;
    }*/
}
