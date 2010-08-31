package Game;

import com.golden.gamedev.engine.BaseAudio;
import java.awt.image.BufferedImage;

/**
 *
 * @author Bruno
 */
public class Gun extends SceneObject {

    LixoGame game;

    public Gun(BufferedImage[] images, double x, double y, Player player, LixoGame gameRef)
    {
        super (images, x, y, player);
        game = gameRef;
    }

    @Override
    public void OnCollision() {
        playerRef.addGun();
        playerRef.modifyAmmo(5);
        playerRef.setScore(playerRef.getScore() + GameConstants.GunPoints);

        if(game.GetSoundState())
            game.bsSound.play(SoundStrings.GunPickup, BaseAudio.MULTIPLE);
        this.setActive(false);
    }

}
