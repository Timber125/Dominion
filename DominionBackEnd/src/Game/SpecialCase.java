/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

/**
 *
 * @author admin
 */
public abstract class SpecialCase {
    private boolean locally_finished = false;
    private Player victim;
    public SpecialCase(Player victim){
        start();
        this.victim = victim;
    }
    public Player getVictim(){
        return victim;
    }
    public void setVictim(Player p){
        this.victim = p;
    }
    public void finish(){
        locally_finished = true;
    }
    public void start(){
        locally_finished = false;
    }
    public boolean isFinished(){
        return locally_finished;
    }
}
