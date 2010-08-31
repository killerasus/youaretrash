package Game;

import com.golden.gamedev.engine.BaseAudio;
import java.awt.image.BufferedImage;

public class GlassWall extends SceneObject{

    private int health;
    private boolean broken;
    private BufferedImage[] brokenPieces;
    private LixoGame game;

    public GlassWall(BufferedImage[] images, double x, double y, Player player, BufferedImage[] brokenGlass, LixoGame ref)
    {
        super (images, x, y, player);
        health = GameConstants.GlassWallHealth;
        broken = false;
        brokenPieces = brokenGlass;
        game = ref;
    }

    @Override
    public void OnCollision() {
        if (broken)
        {
            //System.out.println("Broken glass");
            if (!playerRef.isInvincible())
            {
                //System.out.println("Reducing Bananixo health to " + (playerRef.getHealth() - 1));
                playerRef.setHealth(playerRef.getHealth() - 1);
            }
        }
    }

    /**
     * This method checks for amount of damage dealt to the glass wall, exchanging it's
     * spritesheet if much damage is caused.
     * @param ammount How much damage was dealt (positive number)
     */
    public void OnDamage(int ammount)
    {
        if (!broken)
        {
            health -= ammount;

            if (health < GameConstants.GlassWallHealth && health > 0)
            {
                this.setFrame(1);

                if (game.GetSoundState())
                    game.bsSound.play(SoundStrings.BreakingGlass, BaseAudio.MULTIPLE);
            }
            else
            {
                if (health <= 0)
                {
                    if (game.GetSoundState())
                        game.bsSound.play(SoundStrings.ExplodingGlass, BaseAudio.MULTIPLE);
                    //System.out.println("GlassWall broke");
                    this.broken = true;
                    this.setImages(brokenPieces);
                    this.setFrame(0);
                    this.updateAnimation();
                    playerRef.setScore(playerRef.getScore() + 50);
                }//if
            }//if
        }//if
    }

}
