/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Server;

import Cards.Components.Card;

/**
 *
 * @author admin
 */
public class RTIUtilities {
    public RTIUtilities(){
        
    }
    /* FOR TESTING PURPOSE */
    /*
    public static void main (String[] args){
        // TEST!!
        Card c = getCardByName("Village");
        System.out.println(c.name + " costs " + c.cost);
        // Works if method "getCardByName" is static! 
        // If not called static, should work fine. 
    }
    */
    public Card getCardByName(String cardname){
        /* Replace first character with uppercase format */
        String firstchar = cardname.substring(0,1);
        String trailer = cardname.substring(1);
        firstchar = firstchar.toUpperCase();
        cardname = firstchar + trailer;
        try{
            return (Card) (Class.forName("Cards.Basic." + cardname).newInstance());
        } catch(final InstantiationException e){
            throw new IllegalStateException(e);
        } catch(final IllegalAccessException e){
            throw new IllegalStateException(e);
        } catch(final ClassNotFoundException e){
            throw new IllegalStateException(e);
        }
    }
}
