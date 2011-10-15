/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package LixoGame;

import com.golden.gamedev.engine.BaseAudio;
import java.awt.image.BufferedImage;

/**
 *
 * @author Bruno
 */
public class Coin extends SceneObject {

    LixoGame game;

    public Coin(BufferedImage[] images, double x, double y, Player player, LixoGame gameRef)
    {
        super (images, x, y, player);
        game = gameRef;
    }

    @Override
    public void OnCollision() {
        try
        {
            playerRef.setScore(playerRef.getScore() + GameConstants.CoinValue);

            if(game.GetSoundState())
                game.bsSound.play(SoundStrings.CoinGet, BaseAudio.MULTIPLE);

            this.setActive(false);

        }
        catch (NullPointerException e)
        {
            System.out.println(e.getMessage() + " Player not set");
        }
    }

}
