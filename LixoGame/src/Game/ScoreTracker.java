/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import java.io.Serializable;

/**
 *
 * @author Bruno
 */
public class ScoreTracker implements Serializable, Comparable<ScoreTracker> {

    String _name;
    int _score;
    int _stage;
    boolean _finalized;

    ScoreTracker (String name, int score, int stage, boolean finalized)
    {
        _name = name;
        _score = score;
        _stage = stage;
        _finalized = finalized;
    }

    ScoreTracker() {
        this("null",-1,-1, false);
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public int getScore() {
        return _score;
    }

    public void setScore(int _score) {
        this._score = _score;
    }

    public int getStage() {
        return _stage;
    }

    public void setStage(int _stage) {
        this._stage = _stage;
    }

    public boolean getFinalized()
    {
        return _finalized;
    }

    public void setFinalized(boolean finalized)
    {
        _finalized = finalized;
    }

    @Override
    public int compareTo(ScoreTracker o) {
        if (this._score < o._score)
            return 1;
        else if (this._score > o._score)
            return -1;
        else if (this._stage < o._stage)
            return 1;
        else if (this._stage > o._stage)
            return -1;
        else if (!this._finalized && !o._finalized)
            return -1;
        else if (this._finalized && !o._finalized)
            return 1;
        else return 0;
    }
}