/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import com.golden.gamedev.engine.BaseAudio;
import java.awt.image.BufferedImage;

/**
 *
 * @author Bruno
 */
class Potion extends SceneObject {

    LixoGame game;

    public Potion(BufferedImage[] images, double x, double y, Player player, LixoGame gameRef)
    {
        super (images, x, y, player);
        game = gameRef;
    }

    @Override
    public void OnCollision() {

        if(game.GetSoundState())
            game.bsSound.play(SoundStrings.PotionGet, BaseAudio.MULTIPLE);

        playerRef.setInvincibility(PlayerConstants.InvincibilityPotionTimer);
        playerRef.setInvincibleDueToPotion(true);
        this.setActive(false);
    }

}
