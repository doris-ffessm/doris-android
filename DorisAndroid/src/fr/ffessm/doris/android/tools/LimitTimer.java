package fr.ffessm.doris.android.tools;

/**
 * Timer qui se remet à zero qu'au bout du temps défini.
 * Utile pour limiter les notification à l'IHM 
 *
 */
public class LimitTimer {

	// timer utilisé pour déclencher un refresh que toutes les x nano
    long notifyUITimer = System.nanoTime();
    long timer_length;
    static long MILISECOND = 1000000; 
   
    public LimitTimer(long nbMilisecond){
    	timer_length = nbMilisecond * MILISECOND;
    }
    
	public boolean hasTimerElapsed(){
    	long currentTime = System.nanoTime();
    	if(currentTime - notifyUITimer > timer_length) {
    		notifyUITimer = currentTime;
    		return true;
    	}
    	return false;
    }
}
