/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package LixoGame;

import com.golden.gamedev.engine.BaseAudio;
import com.golden.gamedev.object.Timer;
import com.golden.gamedev.util.ImageUtil;
import com.golden.gamedev.util.Utility;
import java.awt.image.BufferedImage;

/**
 *
 * @author Bruno
 */
public class Robot extends SceneObject {
    
    private int health;
    private Timer bulletTimer;
    private Timer moveTimer;
    private Timer deathTimer;
    private int direction;
    private int numFrames;
    private RobotState state;
    double radius;
    private LixoGame game;
    private int oldState = PlayerConstants.DirectionRight;
    private int currentState = PlayerConstants.DirectionRight;
    private BufferedImage[] spriteSheet;
    private BufferedImage[] WalkRight;
    private BufferedImage[] DamageRight;
    private BufferedImage[] WalkLeft;
    private BufferedImage[] DamageLeft;

    public Robot (BufferedImage[] images, double x, double y, Player player, int health, LixoGame game)
    {
       super(images,x,y, player);
       this.health = health;
       this.spriteSheet = images;
       bulletTimer = new Timer(PlayerConstants.BulletTimer);
       bulletTimer.setActive(false);
       moveTimer = new Timer(100);
       moveTimer.setActive(false);
       deathTimer = new Timer(200);
       deathTimer.setActive(false);
       state = RobotState.Searching;
       radius = 150.0;
       this.game = game;

       numFrames = 0;

       //Specifics to two imaged robot
       WalkLeft = new BufferedImage[] { images[0] };
       DamageLeft = new BufferedImage[] { images[1] };
       WalkRight = new BufferedImage[] { ImageUtil.flipHorizontal(images[0]) };
       DamageRight = new BufferedImage[] { ImageUtil.flipHorizontal(images[1]) };
    }

    @Override
    public void update (long elapsedtime)
    {
        switch(state)
        {
            case Searching:
                if (isHeroInRadius())
                {
                    double delta = playerRef.getX() - this.getX();

                    if (delta < 0) //moving left
                    {
                        oldState = currentState;
                        currentState = PlayerConstants.DirectionLeft;

                        if((currentState - oldState) == -1) //was looking right
                        {
                            spriteSheet[0] = WalkLeft[0];
                            spriteSheet[1] = DamageLeft[0];
                        }//if

                        this.moveX(-2.0);

                        if (this.getX() < 0)
                        {
                            this.moveX(2.0);
                        }//if
                    }
                    else
                    {
                        //moving right
                        oldState = currentState;
                        currentState = PlayerConstants.DirectionRight;

                        if (currentState - oldState == 1) //was looking left
                        {
                            spriteSheet[0] = WalkRight[0];
                            spriteSheet[1] = DamageRight[0];
                        }//if

                        this.moveX(2.0);

                        if (this.getX() + this.getWidth() > game.getWidth())
                        {
                            this.moveX(-2.0);
                        }
                    }//if

                    this.state = RobotState.Attacking;
                }
                else // hero is not in radius
                {
                    if( numFrames <= 0 )
                    {
                        // change direction direction
                        direction = Utility.getRandom(0,1);
                        numFrames = Utility.getRandom(30,60);
                    }

                    if (direction == 0)
                    {
                        // Walking left
                        this.moveX(-2.0);

                        //treats collision with left border
                        if (this.getX() < 0)
                        {
                            this.moveX(2.0);
                        }//if

                        oldState = currentState;
                        currentState = PlayerConstants.DirectionLeft;

                        if((currentState - oldState) == -1) //was looking right
                        {
                            spriteSheet[0] = WalkLeft[0];
                            spriteSheet[1] = DamageLeft[0];
                        }//if
                    }
                    else
                    {
                        // Walking right
                        this.moveX(2.0);

                        //Treats collision with right border
                        if (this.getX() + this.getWidth() > game.getWidth())
                        {
                            this.moveX(-2.0);
                        }

                        oldState = currentState;
                        currentState = PlayerConstants.DirectionRight;

                        if (currentState - oldState == 1) //was looking left
                        {
                            spriteSheet[0] = WalkRight[0];
                            spriteSheet[1] = DamageRight[0];
                        }//if
                    }//if

                    numFrames--;
                }//if
                break;

            case Attacking:
                /**@TODO: Implement robot attack*/
                this.state = RobotState.Searching;
                break;
            case Death:
                if(deathTimer.action(elapsedtime))
                    this.setActive(false);
                break;
        }

    }

    @Override
    public void OnCollision ()
    {
        if (!playerRef.isInvincible())
        {
            //System.out.println("Damaging Bananixo with 1");
            playerRef.setHealth(playerRef.getHealth() - 1);
        }
    }

    public void OnHit (int damage)
    {
        health -= damage;

        if(game.GetSoundState())
            game.bsSound.play(SoundStrings.RobotHit, BaseAudio.MULTIPLE);

        if (health <= 0)
        {
            this.state = RobotState.Death;
            
            if(game.GetSoundState())
                game.bsSound.play(SoundStrings.Explosion);

            this.spriteSheet[0] = game.getImage(GameConstants.RobotExplosion);
            this.playerRef.setScore(playerRef.getScore() + GameConstants.RobotValue);
            this.deathTimer.setActive(true);
            this.deathTimer.refresh();

            game.RobotRemover(this);
            //this.setActive(false);
        }//if
        else
        {
            this.setFrame(1);
            this.updateAnimation();
        }
    }

    /**
     * checkBulletTimer checks if bulletTimer is active and if it has elapsed. It is
     * used to check if a new bullet can be created based in PlayerConstants.BulletTimer time
     * for the delay between shots
     * @param elapsedtime Time elapsed since the start of the program
     * @return  true if bulletTime is not active or the time has elapsed
     *          false if you need to wait to shoot
     */
    public boolean checkBulletTimer(long elapsedtime)
    {
        if (!bulletTimer.isActive())
        {
            return true;
        }
        else
        {
            if(bulletTimer.action(elapsedtime))
            {
                return true;
            }
            else
            {
                return false;
            }//if
        }//if
    }

    /**
     * Activates bulletTimer if not already active
     */
    public void setBulletTimer()
    {
        if (!bulletTimer.isActive())
        {
            bulletTimer.setActive(true);
            bulletTimer.refresh();
        }//if
    }

    /**
     * Resets bulletTimer. Used when a shot hit something
     */
    public void resetBulletTimer()
    {
        bulletTimer.setActive(false);
    }

    private boolean isHeroInRadius() {
        if (this.getDistance(playerRef) <= radius)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private enum RobotState
    {
        Searching,
        Attacking,
        Death
    }
}
