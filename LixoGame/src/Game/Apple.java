package Game;

import com.golden.gamedev.Game;
import com.golden.gamedev.engine.BaseAudio;
import java.awt.image.BufferedImage;

/**
 *
 * @author Bruno
 */
public class Apple extends SceneObject {
    
    LixoGame game;

    public Apple(BufferedImage[] images, double x, double y, Player player, LixoGame gameRef)
    {
        super (images, x, y, player);
        game = gameRef;
    }

    @Override
    public void OnCollision() {
        if (playerRef.getHealth() < PlayerConstants.MaxHealth)
            playerRef.setHealth(playerRef.getHealth()+1);

        if(game.GetSoundState())
            game.bsSound.play(SoundStrings.FoodGet, BaseAudio.MULTIPLE);

        this.setActive(false);
    }

}
