package Game;

import com.golden.gamedev.engine.BaseAudio;
import com.golden.gamedev.engine.BaseAudioRenderer;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import com.golden.gamedev.util.ImageUtil;
import com.golden.gamedev.object.Timer;
import com.golden.gamedev.object.sprite.AdvanceSprite;

/**
 *
 * @author Bruno Baere
 */
public class Player extends AdvanceSprite {

    //Player attributes
    private int lives;
    private int health;
    private int ammoAmount;
    private long score;
    private boolean hasGun;
    private boolean on_ground;
    private double gravity = PlayerConstants.Gravity;
    private LixoGame game;
    private int oldState = PlayerConstants.DirectionRight;
    private int currentState = PlayerConstants.DirectionRight;
    private BufferedImage[] playerSpritesheet;
    private BufferedImage[] hulkSpritesheet;
    private double velocity;
    private boolean jumping;
    private boolean invincibility;
    private boolean invincibleDueToPotion;
    private boolean inMud;
    private Timer invincibilityTimer;
    private Timer mudTimer;
    private Timer bulletTimer;

    private BufferedImage[] WalkRight;
    private BufferedImage[] JumpRight;
    private BufferedImage[] WalkWithGunRight;
    private BufferedImage[] JumpWithGunRight;
    private BufferedImage[] WalkLeft;
    private BufferedImage[] JumpLeft;
    private BufferedImage[] WalkWithGunLeft;
    private BufferedImage[] JumpWithGunLeft;

    private BufferedImage[] WalkRightHulk;
    private BufferedImage[] JumpRightHulk;
    private BufferedImage[] WalkWithGunRightHulk;
    private BufferedImage[] JumpWithGunRightHulk;
    private BufferedImage[] WalkLeftHulk;
    private BufferedImage[] JumpLeftHulk;
    private BufferedImage[] WalkWithGunLeftHulk;
    private BufferedImage[] JumpWithGunLeftHulk;


   /**
    * This constructor for Player class receives parameters
    * @param spriteSheet - An array of BufferedImage containing the character spritesheet
    * @param x - A double representing character's starting position in x axis
    * @param y - A double representing character's starting position in y axis
    * @param lives - Number of starting lives
    * @param ammo - Number of starting ammo amount
    * @param gun - True if player starts with a gun
    */
    public Player(BufferedImage[] spriteSheet, double x, double y, int lives, int health, int ammo, boolean gun,
            LixoGame game)
    {
        super(spriteSheet,x,y);
        this.playerSpritesheet = spriteSheet;
        this.lives = lives;
        this.health = health;
        this.ammoAmount = ammo;
        this.hasGun = gun;
        this.on_ground = false;
        this.game = game;
        this.hulkSpritesheet = LoadHulkSpriteSheet();
        this.score = 0;
        this.bulletTimer = new Timer (PlayerConstants.BulletTimer);
        this.bulletTimer.setActive(false);
        this.mudTimer = new Timer (PlayerConstants.MudTimer);
        this.mudTimer.setActive(false);
        this.invincibilityTimer = new Timer(PlayerConstants.InvincibilityTimer);
        this.invincibilityTimer.setActive(false);
        this.invincibility = false;
        this.invincibleDueToPotion = false;
        this.inMud = false;

        WalkRight = new BufferedImage[] { spriteSheet[0], spriteSheet[1] };
        JumpRight = new BufferedImage[] { spriteSheet[4] };
        WalkWithGunRight = new BufferedImage[] { spriteSheet[13], spriteSheet[14] };
        JumpWithGunRight = new BufferedImage[] { spriteSheet[17] };
        WalkLeft = new BufferedImage[] { ImageUtil.flip (spriteSheet[0]), ImageUtil.flip(spriteSheet[1]) };
        JumpLeft = new BufferedImage[] { ImageUtil.flip (spriteSheet[4]) };
        WalkWithGunLeft = new BufferedImage[] { ImageUtil.flip (spriteSheet[13]), ImageUtil.flip (spriteSheet[14]) };
        JumpWithGunLeft = new BufferedImage[] { ImageUtil.flip (spriteSheet[17]) };

        WalkRightHulk = new BufferedImage[] { hulkSpritesheet[0], hulkSpritesheet[1] };
        JumpRightHulk = new BufferedImage[] { hulkSpritesheet[4] };
        WalkWithGunRightHulk = new BufferedImage[] { hulkSpritesheet[13], hulkSpritesheet[14] };
        JumpWithGunRightHulk = new BufferedImage[] { hulkSpritesheet[17] };
        WalkLeftHulk = new BufferedImage[] { ImageUtil.flip (hulkSpritesheet[0]), ImageUtil.flip(hulkSpritesheet[1]) };
        JumpLeftHulk = new BufferedImage[] { ImageUtil.flip (hulkSpritesheet[4]) };
        WalkWithGunLeftHulk = new BufferedImage[] { ImageUtil.flip (hulkSpritesheet[13]), ImageUtil.flip (hulkSpritesheet[14]) };
        JumpWithGunLeftHulk = new BufferedImage[] { ImageUtil.flip (hulkSpritesheet[17]) };
        
    }

    /**
     * Modifies character's ammo amount. If adjusted amount falls below zero,
     * it is set to zero
     * @param amount - An integer greater than zero if ammo is being added,
     * lower than zero if it's being reduced
     */
    public void modifyAmmo(int amount)
    {
        this.ammoAmount += amount;

        if (this.ammoAmount <= 0)
        {
            this.ammoAmount = 0;
            removeGun();
        }
        else if (this.ammoAmount > PlayerConstants.AmmoMax)
        {
            this.ammoAmount = PlayerConstants.AmmoMax;
        }
    }

    @Override
    public void update(long elapsedTime)
    {
        super.update(elapsedTime);

        if (invincibilityTimer != null)
        {
            if(invincibilityTimer.isActive())
            {
                if(invincibilityTimer.action(elapsedTime))
                {
                    turnOffInvincibility();
                }//if
            }//if
        }//if

        if (game.keyDown(KeyEvent.VK_RIGHT))
        {
            oldState = currentState;
            currentState = PlayerConstants.DirectionRight;

            if((currentState - oldState) == 1)
            {
                if (getInvincibleDueToPotion())
                {
                    this.playerSpritesheet[0] = WalkRightHulk[0];
                    this.playerSpritesheet[1] = WalkRightHulk[1];
                    this.playerSpritesheet[4] = JumpRightHulk[0];
                    this.playerSpritesheet[13] = WalkWithGunRightHulk[0];
                    this.playerSpritesheet[14] = WalkWithGunRightHulk[1];
                    this.playerSpritesheet[17] = JumpWithGunRightHulk[0];
                }
                else
                {
                    this.playerSpritesheet[0] = WalkRight[0];
                    this.playerSpritesheet[1] = WalkRight[1];
                    this.playerSpritesheet[4] = JumpRight[0];
                    this.playerSpritesheet[13] = WalkWithGunRight[0];
                    this.playerSpritesheet[14] = WalkWithGunRight[1];
                    this.playerSpritesheet[17] = JumpWithGunRight[0];
                }
            }

            if(!inMud)
            {
                this.moveX(PlayerConstants.PlayerDeltaX);
            }
            else
            {
                this.moveX(PlayerConstants.MudVelocity);
            }

            //Treats collision with right border
            if (this.getX() + this.getWidth() > game.getWidth())
            {
                this.moveX(-PlayerConstants.PlayerDeltaX);
            }
        }

        if (game.keyDown(KeyEvent.VK_LEFT))
        {
            oldState = currentState;
            currentState = PlayerConstants.DirectionLeft;

            if((currentState - oldState) == -1)
            {
                if (!getInvincibleDueToPotion())
                {
                    this.playerSpritesheet[0] = WalkLeft[0];
                    this.playerSpritesheet[1] = WalkLeft[1];
                    this.playerSpritesheet[4] = JumpLeft[0];
                    this.playerSpritesheet[13] = WalkWithGunLeft[0];
                    this.playerSpritesheet[14] = WalkWithGunLeft[1];
                    this.playerSpritesheet[17] = JumpWithGunLeft[0];
                }
                else
                {
                    this.playerSpritesheet[0] = WalkLeftHulk[0];
                    this.playerSpritesheet[1] = WalkLeftHulk[1];
                    this.playerSpritesheet[4] = JumpLeftHulk[0];
                    this.playerSpritesheet[13] = WalkWithGunLeftHulk[0];
                    this.playerSpritesheet[14] = WalkWithGunLeftHulk[1];
                    this.playerSpritesheet[17] = JumpWithGunLeftHulk[0];
                }
            }//if

            if(!inMud)
            {
                this.moveX(-PlayerConstants.PlayerDeltaX);
            }
            else
            {
                this.moveX(-PlayerConstants.MudVelocity);
            }//if

            //Treats collision with left border
            if (this.getX() < 0)
            {
                this.moveX(PlayerConstants.PlayerDeltaX);
            }//if
        }//if

        if (game.keyDown(KeyEvent.VK_SPACE) || game.keyDown(KeyEvent.VK_UP))
        {
            if (!jumping)
            {
                jumping = true;

                if (!inMud)
                    velocity = PlayerConstants.FreeFallVelocity;
                else
                    velocity = 0.5*PlayerConstants.FreeFallVelocity; //Mud hindrance on jump

                this.setNoGround();

                if(game.GetSoundState())
                    game.bsSound.play(SoundStrings.Jump, BaseAudio.MULTIPLE);
            }//if
        }//if

        if(!on_ground) {
            velocity += PlayerConstants.Gravity; //Handle Gravity
        }
        else{
            velocity += PlayerConstants.Gravity;
            jumping = true; //setting jump to true makes impossible to jump in middle of a fall
        }//if

        //Corrector factor to evade bouncing
        //if(!(velocity > 0.3 ) && !(velocity < -0.3) && jumping == false)
        //{
        //    velocity = 0;
        //}

        this.moveY(velocity);

        if (mudTimer.isActive())
        {
            if(mudTimer.action(elapsedTime))
            {
                //System.out.println("Mud time elapsed");
                inMud = false;
                mudTimer.setActive(false);
            }//if
        }//if
    }

    public void setOnGround() {
        on_ground = true;
        jumping = false;
        velocity = 0;
        //this.getAnimationTimer().setDelay(300);
        if (!hasGun)
        {
            this.setAnimationFrame(PlayerConstants.WalkAnimation);
        }
        else
        {
            this.setAnimationFrame(PlayerConstants.WalkGunAnimation);
        }
        this.setAnimate(true);
        this.setLoopAnim(true);
    }

    public void setNoGround() {
        on_ground = false;
        jumping = true;

        if (!hasGun)
        {
            this.setAnimationFrame(PlayerConstants.JumpStance);
        }
        else
        {
            this.setAnimationFrame(PlayerConstants.JumpGunStance);
        }
        //this.updateAnimation();
    }
    
    public boolean isOnGround() {
        return on_ground;
    }
    public double getVelocity() {
        return velocity;
    }
    public double getGravity() {
        return gravity;
    }
    public void setGravity(double newGrav) {
        gravity = newGrav;
    }

    public void setScore(long newScore)
    {
        score = newScore;
    }

    public long getScore()
    {
        return score;
    }

    public int getCurrentState()
    {
        return currentState;
    }

    public int getHealth()
    {
        return health;
    }

    public void setHealth(int newHealth)
    {
        if (newHealth > PlayerConstants.MaxHealth)
        {
            health = PlayerConstants.MaxHealth;
        }
        else
        {
            if (newHealth <= 0)
            {
                health = 0;
                lives--;

                if(this.currentState == PlayerConstants.DirectionRight)
                {
                    this.setAnimationFrame(PlayerConstants.DeathAnimationRight);
                }
                else{
                    this.setAnimationFrame(PlayerConstants.DeathAnimationLeft);
                }

                game.render(game.bsGraphics.getBackBuffer());
                game.bsGraphics.flip(); //Needed to update screen

                if(this.currentState == PlayerConstants.DirectionRight)
                {
                }

                game.bsMusic.stopAll();
                BaseAudioRenderer dummy;

                if (game.GetSoundState())
                {
                    try{
                        dummy = game.bsSound.getAudioRenderer(
                                game.bsSound.play(SoundStrings.DeathSound, BaseAudio.SINGLE));

                        /** @TODO: Linux problem */
                        //Wait for sound to play
                        while (dummy.getStatus() != BaseAudioRenderer.END_OF_SOUND){ /* Empty */ }

                    }catch(ArrayIndexOutOfBoundsException e)
                    {
                        System.out.println("ERROR MESSAGE: Sound in your system may not be supported");
                        System.out.println("ERROR MESSAGE: This was set by Player::setHealth");
                        System.out.println(e.getMessage());
                    }//try
                }//if
            }
            else
            {
                if (newHealth - health < 0)
                {
                    //System.out.println("Bananixo says: Ouch!");
                    //When character gets hurt, invincibility is set
                    //as in Megaman
                    if (game.GetSoundState())
                        game.bsSound.play(SoundStrings.HurtAudioString);

                    this.setInvincibility(PlayerConstants.InvincibilityTimer);
                }//if

                health = newHealth;
            }//if
        }//if
    }
    
    public void addGun ()
    {
        if (!hasGun)
        {
            hasGun = true;
            if (on_ground)
            {
                this.setAnimationFrame(PlayerConstants.WalkGunAnimation);
            }
            else
            {
                this.setAnimationFrame(PlayerConstants.JumpGunAnimation);
            }
            this.updateAnimation();
        }
    }

    public void removeGun()
    {
        hasGun = false;
        ammoAmount = 0;

        if ( on_ground )
        {
            this.setAnimationFrame(PlayerConstants.WalkAnimation);
        }
        else
        {
            this.setAnimationFrame(PlayerConstants.JumpAnimation);
        }
        this.updateAnimation();
    }

    public boolean hasGun()
    {
        return hasGun;
    }

    public void setInvincibility(int miliseconds) {
        //System.out.println("Invincibility timer on");
        invincibility = true;
        invincibilityTimer = new Timer(miliseconds);
        invincibilityTimer.setActive(true);
        invincibilityTimer.refresh();
    }

    public void setInvincibleDueToPotion(boolean state)
    {
        invincibleDueToPotion = state;

        if (currentState == PlayerConstants.DirectionRight)
        {
            this.playerSpritesheet[0] = WalkRightHulk[0];
            this.playerSpritesheet[1] = WalkRightHulk[1];
            this.playerSpritesheet[4] = JumpRightHulk[0];
            this.playerSpritesheet[13] = WalkWithGunRightHulk[0];
            this.playerSpritesheet[14] = WalkWithGunRightHulk[1];
            this.playerSpritesheet[17] = JumpWithGunRightHulk[0];
        }
        else
        {
            this.playerSpritesheet[0] = WalkLeftHulk[0];
            this.playerSpritesheet[1] = WalkLeftHulk[1];
            this.playerSpritesheet[4] = JumpLeftHulk[0];
            this.playerSpritesheet[13] = WalkWithGunLeftHulk[0];
            this.playerSpritesheet[14] = WalkWithGunLeftHulk[1];
            this.playerSpritesheet[17] = JumpWithGunLeftHulk[0];
        }

        updateAnimation();

    }

    public boolean getInvincibleDueToPotion()
    {
        return invincibleDueToPotion;
    }

    public void turnOffInvincibility()
    {
        if (invincibleDueToPotion)
        {
            if (currentState == PlayerConstants.DirectionRight)
            {
                this.playerSpritesheet[0] = WalkRight[0];
                this.playerSpritesheet[1] = WalkRight[1];
                this.playerSpritesheet[4] = JumpRight[0];
                this.playerSpritesheet[13] = WalkWithGunRight[0];
                this.playerSpritesheet[14] = WalkWithGunRight[1];
                this.playerSpritesheet[17] = JumpWithGunRight[0];
            }
            else
            {
                this.playerSpritesheet[0] = WalkLeft[0];
                this.playerSpritesheet[1] = WalkLeft[1];
                this.playerSpritesheet[4] = JumpLeft[0];
                this.playerSpritesheet[13] = WalkWithGunLeft[0];
                this.playerSpritesheet[14] = WalkWithGunLeft[1];
                this.playerSpritesheet[17] = JumpWithGunLeft[0];
            }

            updateAnimation();
        }

        invincibility = false;
        invincibleDueToPotion = false;
        invincibilityTimer.setActive(false);
        //System.out.println("Invincibility timer off");
    }

    /**
     * Returns whether player is invincible
     * @return
     */
    boolean isInvincible() {
        return invincibility;
    }

    /**
     * This method is called for reducing player speed while in mud, activating
     */
    void setMudHindrance() {
        if ( !mudTimer.isActive() )
        {
            inMud = true;
            mudTimer.setActive(true);
            mudTimer.refresh();
        }//if

        mudTimer.refresh();
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

    public void resetMudTimer()
    {
        mudTimer.setActive(false);
        inMud = false;
    }

    /**
     * Resets bulletTimer. Used when a shot hit something
     */
    public void resetBulletTimer()
    {
        bulletTimer.setActive(false);
    }

    public int getLives() {
        return lives;
    }

    public int getAmmo() {
        return ammoAmount;
    }

    private BufferedImage[] LoadHulkSpriteSheet() {
        BufferedImage[] spritesheet = new BufferedImage[18];

        for (int i = 0; i < 18; i++)
        {
            spritesheet[i] = game.getImage("resources/images/character/hulk/banana" + (i+1) + ".png", true);
        }

        return spritesheet;
    }

    public Timer GetInvincibiliyyTimer()
    {
        return invincibilityTimer;
    }
}
