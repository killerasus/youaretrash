/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import com.golden.gamedev.Game;
import com.golden.gamedev.GameLoader;

/**
 *
 * @author Bruno
 */
public class AppletLoader extends GameLoader {

    @Override
    protected Game createAppletGame() {
        return new LixoGame();
    }
}
