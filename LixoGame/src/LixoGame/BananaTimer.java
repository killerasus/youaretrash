/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package LixoGame;

/**
 *
 * @author Bruno
 */
public class BananaTimer {

    long timetick;
    long timestart;
    long timepast;
    long timepaused;
    long delta;

    BananaTimer(long newtime)
    {
        timetick = newtime;
        timepast = 0;
        delta = 0;
    }

    long getTimeTick()
    {
        return timetick;
    }

    void setTimeTick(long time)
    {
        timetick = time;
    }

    void startTimer()
    {
        timestart = System.currentTimeMillis();
        //System.out.println("Timer started at: " + timestart);
    }

    void resetTimer()
    {
        delta = 0;
        timepast = 0;
    }

    void pauseTimer()
    {
        timepaused = System.currentTimeMillis();
        //System.out.println("Timer paused at: "+ timepaused);
    }

    void unpauseTimer()
    {
        delta += System.currentTimeMillis() - timepaused;
        //System.out.println("Timer unpaused at: "+ timepaused + delta);
    }

    void update ()
    {
        timepast = System.currentTimeMillis() - timestart - delta;
    }

    long getTimePast()
    {
        return timepast;
    }

    boolean checkTimer()
    {
        if (timepast >= timetick)
            return true;
        else
            return false;
    }
}
