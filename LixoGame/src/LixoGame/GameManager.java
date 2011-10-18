package LixoGame;

/**
 * The purpose of this class is to provide a manager for the "game". It manages
 * state changes, such as from Puzzle mode to Platform mode, and performs the
 * loading and unloading operations for each state change. The Game itself doesn't
 * need to know which state the "game" is in, only wether it is running or not.
 * The outside world calls LoadGame and UnloadGame.
 *
 * @author Bruno
 */

import com.golden.gamedev.engine.BaseAudio;
import java.awt.*;
import java.awt.event.*;
import com.golden.gamedev.engine.BaseAudioRenderer;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;
import com.golden.gamedev.object.collision.BasicCollisionGroup;
import com.golden.gamedev.object.collision.CollisionGroup;
import com.golden.gamedev.util.ImageUtil;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


public class GameManager {

    private LixoGame GameRef;

    private InGameState OldState;
    private InGameState GameState;
    private ArrayList<Recyclables> Baskets; //Baskets states
    private RecyclableFeature[] TrashInBaskets; //What will be loaded on PlatformState

    private Player Hero;
    private BufferedImage[] HeroSpriteSheet;
    private Sprite[] Lives;
    private Sprite[] HealthBar;
    private Sprite Pause;
    private Sprite Clock;
    private Sprite Ammo;
    private Sprite MatchColors;
    private ImageBackground GameBackground;
    private Block[] Blocks;

    private int Stage;
    private int ActiveBlock; //Which block is active
    private LinkedList<Column> ColumnOrder; //Order of block appearance
    private boolean[] blocksOk; //Which baskets are ok
    private double dropVelocity;

    private SpriteGroup PlayerGroup;
    private SpriteGroup PlayerShotsGroup;
    private SpriteGroup EnemyShotsGroup;
    private SpriteGroup SceneObjects;
    private SpriteGroup EnemyGroup;
    private SpriteGroup BlocksGroup;
    private SpriteGroup RecyclableBinGroup;
    private SpriteGroup UIElementsGroup;
    private SpriteGroup BlockableGroup;
    private SpriteGroup DoorExitGroup;
    private SpriteGroup ResultGroup;

    private PlayField GameField;

    private TerrainCollision terrainCollisionHandler;
    private AssetCollision assetCollisionHandler;
    private DoorCollision exitCollisionHandler;
    private ProblemCollisionGroup problemCollisionGroup;
    private ProblemCollisionGroup blockableCollisionGroup;
    private ProblemCollisionGroup enemyBlockableCollisionGroup;
    private PlayerShotCollision playerShotCollisionGroup;
    private PlayerShotCollision playerShotBlockableCollisionGroup;
    private EnemyShotCollision enemyShotCollisionGroup;

    private Timer gameOverTimer;
    private Timer gameTimer;
    private Timer cutsceneTimer;
    private Timer endgameTimer;
    private BananaTimer bananaGameTimer;
    private String time;
    private LinkedList<BufferedImage> cutsceneScreens;
    private int cutsceneIndex;
    private Sprite Invincible;
    private Sprite Perfect;

    /**
     * @param Ref Reference to the Game object that called the GameManager. Needed
     * to get info about window size and other stuff
     */
    public GameManager(LixoGame Ref)
    {
        OldState = null;
        GameState = InGameState.PuzzleState;
        GameRef = Ref;
        cutsceneIndex = GameRef.getRandom(0, GameConstants.Cutscenes-1);
        GameBackground = new ImageBackground(GameRef.getImage(GameConstants.BackgroundImageGame),
                    GameRef.getWidth(), GameRef.getHeight());

        Stage = 1;
        time = "";
        dropVelocity = GameConstants.InitialBlockVelocity;

        //gameTimer = new Timer(GameConstants.ClockTimer);
        //gameTimer.setActive(false);
        bananaGameTimer = new BananaTimer(GameConstants.ClockTimer);
        gameOverTimer = new Timer (GameConstants.GameOverTimer);
        gameOverTimer.setActive(false);
        cutsceneTimer = new Timer (GameConstants.CutsceneTimer);
        cutsceneTimer.setActive(false);
        endgameTimer = new Timer (GameConstants.EndGameTimer);
        endgameTimer.setActive(false);

        MatchColors = new Sprite(GameRef.getImage(GameConstants.Match));
        Invincible = new Sprite (GameRef.getImage(GameConstants.InvincibeImage,true));
        Invincible.move(GameConstants.InvincibleX, GameConstants.InvincibleY);
        Perfect = new Sprite(GameRef.getImage(GameConstants.PerfectBonus));
        Perfect.move(GameConstants.InvincibleX, GameConstants.InvincibleY + 170);
        HeroSpriteSheet = LoadPlayerSpriteSheet();
        LoadHero();

        cutsceneScreens = new LinkedList<BufferedImage>();

        for (int i = 0; i < GameConstants.Cutscenes; i++)
        {
            cutsceneScreens.add(ImageUtil.getImage(GameRef.bsIO.getURL(GameConstants.CutsceneFile + (i+1) + ".png")));
        }

        Collections.shuffle(cutsceneScreens);
    }

    public boolean update (long elapsedTime) throws IOException, ClassNotFoundException
    {
        int keyPressed = GameRef.bsInput.getKeyPressed();

        switch (GameState)
        {
            case PuzzleState:

                //System.out.println("DEBUG: Puzzle block state. If everything true, must go to Platform state");
                //System.out.println("blocksOk[0] = " + blocksOk[0] + "\tblocksOk[1] = " + blocksOk[1] +
                //         "\tblocksOk[2] = " + blocksOk[2] + "\tblocksOk[3] = " + blocksOk[3] + "\n");

                boolean allcorrect = true;
                
                if (blocksOk[0] == true && blocksOk[1] == true &&
                        blocksOk[2] == true && blocksOk[3] == true)
                {
                    for (int i = 0; i < TrashInBaskets.length; i++)
                    {
                        if(TrashInBaskets[i] == RecyclableFeature.MetalProblem ||
                                TrashInBaskets[i] == RecyclableFeature.OrganicProblem ||
                                TrashInBaskets[i] == RecyclableFeature.GlassProblem ||
                                TrashInBaskets[i] == RecyclableFeature.PlasticProblem)
                        {
                            allcorrect = false;
                            break;
                        }
                    }

                    if (allcorrect)
                    {
                        Perfect.render(GameRef.bsGraphics.getBackBuffer()); //render PERFECT in backbuffer
                        GameRef.bsGraphics.flip();  //draw back buffer to screen
                        //Updates player score
                        Hero.setScore(Hero.getScore() + GameConstants.Perfect);

                        if(GameRef.GetSoundState())
                        {
                            try{
                                BaseAudioRenderer dummy = GameRef.bsSound.getAudioRenderer(
                                    GameRef.bsSound.play(SoundStrings.AllBlocksCorrect, BaseAudio.SINGLE));

                                /** @TODO: Linux problem */
                                //Wait for sound to play
                                //while (dummy.getStatus() != BaseAudioRenderer.END_OF_SOUND){ /* Empty */ }
                                Thread.sleep(1000L);

                            }
                            catch(ArrayIndexOutOfBoundsException e)
                            {
                                System.out.println("ERROR MESSAGE: Sound in your system may not be supported.");
                                System.out.println("ERROR MESSAGE: This was set by GameManager::update#puzzlestate");
                                System.out.println(e.getMessage());
                            }
                            catch (InterruptedException e)
                            {
                                System.out.println(e.getMessage());
                            }
                            
                        }//if
                        
                        AdvanceStage(allcorrect);
                        break;

                    }//if
                    else
                    {
                        UnloadPuzzleState();
                        LoadPlatformState();
                        GameState = InGameState.PlatformState;
                    }
                }
                else
                {
                    if (keyPressed == KeyEvent.VK_S)
                    {
                        GameRef.ChangeSoundState();
                    }
                    else
                    {
                        if(keyPressed == KeyEvent.VK_M)
                        {
                            GameRef.ChangeMusicState();
                        }//if
                    }//if

                    if (keyPressed == KeyEvent.VK_ESCAPE)
                    {
                        return false;
                    }
                    else if ( keyPressed == KeyEvent.VK_RIGHT)
                    {
                        Blocks[ActiveBlock].RotateClockWise();
                    }
                    else if ( keyPressed == KeyEvent.VK_LEFT)
                    {
                        Blocks[ActiveBlock].RotateCounterClockWise();
                    }
                    else if ( keyPressed == KeyEvent.VK_DOWN )
                    {
                        Blocks[ActiveBlock].setY(GameConstants.AssetY - Blocks[ActiveBlock].getHeight());
                    }
                    else
                    {
                        //GameField.update(elapsedTime);
                    }//if

                    if ( dropVelocity > GameConstants.MaxVelocity)
                    {
                        Blocks[ActiveBlock].moveY(GameConstants.MaxVelocity);
                    }
                    else
                    {
                        Blocks[ActiveBlock].moveY( dropVelocity );
                    }

                    GameField.update(elapsedTime);
                }
                
                break;

            case PlatformState:

                bananaGameTimer.update();

                int leftTime = (int) ((GameConstants.ClockTimer - bananaGameTimer.getTimePast())/1000);
                
                if (bananaGameTimer.checkTimer())
                {
                    Hero.setHealth(0);
                }//if

                if (Hero.getLives() > 0)
                {
                    if (Hero.getHealth() <= 0)
                    {
                        ResetPlayerOnDeath();
                        GameState = InGameState.PuzzleState;
                        
                        UnloadPlatformState();
                        LoadPuzzleState();

                        if (GameRef.GetMusicState())
                            GameRef.bsMusic.play(GameConstants.GameInsideMusicPath, BaseAudio.MULTIPLE);

                        break;
                    }

                    //Updates how many Health Dots should be drawn
                    for (int i = 0; i < HealthBar.length; i++)
                    {
                        if (i < Hero.getHealth())
                        {
                            HealthBar[i].setActive(true);
                        }
                        else
                        {
                            HealthBar[i].setActive(false);
                        }
                    }

                    //Updates how many Life Dots should be drawn
                    for (int i = 0; i < Lives.length; i++)
                    {
                        if (i < Hero.getLives())
                        {
                            Lives[i].setActive(true);
                        }
                        else
                        {
                            Lives[i].setActive(false);
                        }
                    }

                    if (keyPressed == KeyEvent.VK_ESCAPE)
                    {
                        return false;
                    }//if

                    if (keyPressed == KeyEvent.VK_P || keyPressed == KeyEvent.VK_PAUSE )
                    {
                        GameState = InGameState.PausedState;
                        bananaGameTimer.pauseTimer();
                        //timeStop = gameTimer.getCurrentTick();
                        //System.out.println("TimeStop = " + timeStop);
                        if(Hero.isInvincible())
                        {
                            /**@TODO:Create Player::GetInvincibilityTimer()*/
                        }
                        
                        GameRef.bsMusic.setVolume((float)0.0);
                    }
                    else
                    {

                        GameField.update(elapsedTime);

                        if (keyPressed == KeyEvent.VK_CONTROL && Hero.hasGun())
                        {
                            AddBulletToScene ( (Hero.getCurrentState() == PlayerConstants.DirectionRight? Hero.getX() + Hero.getWidth() :
                                Hero.getX()), Hero.getY() + 60,
                                    (Hero.getCurrentState() == PlayerConstants.DirectionRight? GameConstants.BulletSpeed : -GameConstants.BulletSpeed),
                                    elapsedTime, Hero);
                        }//if
                        else
                        {
                            if (keyPressed == KeyEvent.VK_S)
                            {
                                GameRef.ChangeSoundState();
                            }
                            else
                            {
                                if(keyPressed == KeyEvent.VK_M)
                                {
                                    GameRef.ChangeMusicState();
                                }//if
                            }//if
                        }
                    }//if
                }
                else
                {
                    GameState = InGameState.GameOverState;
                    LoadGameOverState();
                }

                if(leftTime <= GameConstants.BeepThreshold)
                {
                    if(GameRef.GetSoundState() && GameState == InGameState.PlatformState)
                    {
                        GameRef.bsSound.play(SoundStrings.Beep, BaseAudio.SINGLE);
                    }
                }

                break;

            case PausedState:

                //keyPressed = GameRef.bsInput.getKeyPressed();
                //gameTimer.setCurrentTick(timeStop);

                if (keyPressed == KeyEvent.VK_P || keyPressed == KeyEvent.VK_PAUSE )
                {
                    //gameTimer.notify();
                    GameState = InGameState.PlatformState;
                    GameRef.bsMusic.setVolume((float)1.0);
                    bananaGameTimer.unpauseTimer();
                    GameField.update(elapsedTime);
                }//if
                else
                {
                    if (keyPressed == KeyEvent.VK_S)
                    {
                        GameRef.ChangeSoundState();
                    }
                    else
                    {
                        if(keyPressed == KeyEvent.VK_M)
                        {
                            GameRef.ChangeMusicState();
                        }//if
                    }//if
                }
                break;

            case GameOverState:
                if (gameOverTimer.action(elapsedTime))
                {
                    GameState = InGameState.PuzzleState;
                    UnloadGameOverState();
                    GameRef.bsMusic.stopAll();
                    RegisterScore();
                    return false;
                }//if

                break;

            case Cutscene:
                if (cutsceneTimer.action(elapsedTime))
                {
                    GameState = InGameState.PuzzleState;
                    LoadPuzzleState();
                    if (GameRef.GetMusicState())
                            GameRef.bsMusic.play(GameConstants.GameInsideMusicPath, BaseAudio.MULTIPLE);
                }//if
                break;

            case EndScene:
                if (endgameTimer.action(elapsedTime))
                {
                    GameRef.bsMusic.stopAll();
                    RegisterScore();
                    return false;
                }
                break;
        }//switch

        return true;
    }

    @SuppressWarnings("empty-statement")
    public void render (Graphics2D g)
    {
        switch (GameState)
        {
            case PausedState:
                GameField.render(g);
                InterfaceRender(g);
                Pause.render(g);
                break;

            case PlatformState:
                GameField.render(g);
                InterfaceRender(g);
                break;

            case PuzzleState:
                GameField.render(g);
                MatchColors.render(g,(int) (0.5*GameRef.getWidth() -
                        0.5*MatchColors.getWidth()), GameConstants.MatchY);
                GameRef.BigFont.drawText(g, "Stage: " + Stage, GameFont.CENTER,
                200, -40, 400, 0, 0);
                ResultGroup.render(g);
                break;

            case GameOverState:
                GameField.render(g);
                break;

            case Cutscene:
                GameField.render(g);
                break;

            case EndScene:
                GameField.render(g);
                break;
        }//switch
    }

    /**
     * Register score prompts user for Name input and updates hiscore file
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void RegisterScore() throws ClassNotFoundException, IOException
    {
        ObjectOutputStream output = null;
        String playerName = JOptionPane.showInputDialog("Register your name in the Hall of Fame!");
        JOptionPane.showMessageDialog(null, String.format("%s, your score was %s and your stage was %s", playerName, Hero.getScore(), this.Stage));

        ScoreTracker current = new ScoreTracker(playerName, (int) Hero.getScore(),
                this.Stage, (this.GameState == InGameState.EndScene? true: false));
        List<ScoreTracker> scores = new ArrayList<ScoreTracker>();

        try {

            String parentPath = null;

            try {
                //Gets the parent directory of the running class (ex: jar directory)
                parentPath = (new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI())).getParent();
            } catch (URISyntaxException ex) {
                Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            ObjectInputStream input = null;

            try{
                input = new ObjectInputStream(new FileInputStream(parentPath + File.separator + GameConstants.HiscoreFileString));

                while (true)
                {
                    scores.add((ScoreTracker)input.readObject());
                }
            }
            catch(EOFException e)
            {
                if(input != null)
                    input.close();
            }
            catch (FileNotFoundException e)
            {
                //Creates an empty hiscore.dat
                Formatter formatter = new Formatter(parentPath + File.separator + GameConstants.HiscoreFileString);
                formatter.close();
            }
            finally
            {
                scores.add(current);
                Collections.sort(scores);
            }

            output = new ObjectOutputStream(new FileOutputStream(parentPath + File.separator + "hiscore.dat"));
            //ObjectOutputStream output = new ObjectOutputStream(getClass().getResource("hiscore.dat").openStream());

            /**
             * @TODO: Write this in a better way for keeping just the top 10 scores
             */
            for (ScoreTracker score : scores)
            {
                //System.out.printf("%s\t%s\t%s\n",score._name,score._score,score._stage);
                output.writeObject(score);
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
        finally
        {
            if (output != null)
                output.close();
        }
    }

    /**
     * This method handles the collision for the Player and the Terrain.
     * @param sprite Sprite that collided with the collision group
     */
//    private void handleCollision (Sprite terrain, Sprite character) {
//        Player spr = (Player) character;
//
//        //spr.forceY(spr.getY() - 2);
//        spr.setY(terrain.getY() - spr.getHeight());
//        spr.setOnGround ();
//    }

    /**
     * This method handles the effects of RecyclableBlock collision with the ground,
     * setting the next active block and checking if all blocks have been set. Must check
     * the block state and set which blocks are incorret.
     * @param sprite
     */
    private void blockCollision (Sprite block){

        //System.out.println("DEBUG: Entered blockCollision");

        Block blk = (Block) block;

        //Calculates penetration from block into terrain
        //double dif = blk.getHeight() + blk.getY() - terrain.getY();

        int basketNumber = blk.GetColumn().ordinal();

        //Moves the block up
        //blk.setY(terrain.getY() - blk.getHeight());
        blk.setY(GameConstants.AssetY - blk.getHeight());

        //Sets basket state to full
        blocksOk[basketNumber] = true;

        //Sets basket feature for Platform level
        if(blk.GetState() == Baskets.get(basketNumber) )
        {
            //In case the trash was thrown in the right basket
            if(GameRef.GetSoundState())
                GameRef.bsSound.play(SoundStrings.BlockMatch, BaseAudio.MULTIPLE);

            /**@TODO:Fix these magic numbers*/
            ResultGroup.add(new Sprite(GameRef.getImage(GameConstants.GoodResult, true),
                    blk.getX() + 33, blk.getY() - 25));

            switch(blk.GetState())
            {
                case Metal:
                    TrashInBaskets[basketNumber] = RecyclableFeature.MetalBonus;
                    break;
                case Glass:
                    TrashInBaskets[basketNumber] = RecyclableFeature.GlassBonus;
                    break;
                case Organic:
                    TrashInBaskets[basketNumber] = RecyclableFeature.OrganicBonus;
                    break;
                case Plastic:
                    TrashInBaskets[basketNumber] = RecyclableFeature.PlasticBonus;
                    break;
            }//switch
        }
        else
        {
            if(GameRef.GetSoundState())
                GameRef.bsSound.play(SoundStrings.BlockMiss, BaseAudio.MULTIPLE);

            ResultGroup.add(new Sprite(GameRef.getImage(GameConstants.BadResult, true),
                    blk.getX() + 41, blk.getY() - 25));

            switch(blk.GetState())
            {
                case Metal:
                    TrashInBaskets[basketNumber] = RecyclableFeature.MetalProblem;
                    break;
                case Glass:
                    TrashInBaskets[basketNumber] = RecyclableFeature.GlassProblem;
                    break;
                case Organic:
                    TrashInBaskets[basketNumber] = RecyclableFeature.OrganicProblem;
                    break;
                case Plastic:
                    TrashInBaskets[basketNumber] = RecyclableFeature.PlasticProblem;
                    break;
            }//switch
        }//if

        //System.out.println("DEBUG: " + Blocks[ActiveBlock].toString());
        //System.out.println("DEBUG: ActiveBlock = " + ActiveBlock + " Number of Blocks = " + GameConstants.NumberofBlocks);

        //Maybe this is what was giving us erros (was less than 3)
        if (ActiveBlock < GameConstants.NumberofBlocks - 1)
        {
            LinkedList<Column> col = new LinkedList<Column>();

            //Setting a list of free columns
            for (int i = 0; i < blocksOk.length; i++)
            {
                if (!blocksOk[i])
                {
                    try
                    {
                        col.add(ConvertIndexToColumn(i));
                    }
                    catch (Exception e)
                    {
                        System.out.println(e.getMessage());
                    }//try
                }//if
            }//for

            Collections.shuffle(col);

            ++ActiveBlock; //Goes to the next block

            if (ActiveBlock >= GameConstants.NumberofBlocks)
            {
                assert(blocksOk[0] == true && blocksOk[1] == true &&
                        blocksOk[2] == true && blocksOk[3] == true);
                return;
            }

            Blocks[ActiveBlock].setActive(true);
            Blocks[ActiveBlock].SetColumn(col.poll());
            Blocks[ActiveBlock].move(ConvertColumnToCoordinate(Blocks[ActiveBlock].GetColumn()) , 0);
        }//if

    }

    /**
     * This method is called from player class to add gun bullets into the scene
     * @param x X position of the bullet
     * @param y Y position of the bullet
     * @param speed X speed of the bullet
     * @param elapsedtime Time elapsed since the start of the simulation
     * @param creator AnimatedSprite that created the bullet. The current implementation
     * takes that creator can be Robot or Player. Should check for a Shooter Interface
     */
    public void AddBulletToScene (double x, double y, double speed, long elapsedtime, AnimatedSprite creator)
    {
        Bullet newBullet;

        if (creator instanceof Player)
        {
            if (((Player)creator).checkBulletTimer(elapsedtime))
            {
                newBullet = new Bullet(ImageUtil.getImages(GameRef.bsIO.getURL(GameConstants.BulletImage),
                        1, 1, Color.WHITE), x, y, GameConstants.BulletDamage, speed, creator, GameRef);
                ((Player)creator).setBulletTimer();
                PlayerShotsGroup.add(newBullet);
                ((Player)creator).modifyAmmo(-1);

                if(GameRef.GetSoundState())
                    GameRef.bsSound.play(SoundStrings.GunShot, BaseAudio.MULTIPLE);
            }//if
        }
        else
        {
            if (creator instanceof Robot)
            {
                if (((Robot)creator).checkBulletTimer(elapsedtime))
                {
                    newBullet = new Bullet(ImageUtil.getImages(GameRef.bsIO.getURL(GameConstants.BulletImage),
                            1, 1), x, y, GameConstants.BulletDamage, speed, creator, this.GameRef);
                    newBullet.setActive(true);
                    ((Robot)creator).setBulletTimer();
                    EnemyShotsGroup.add(newBullet);
                }//if
            }//if
        }//if
    }

    /**
     * Loads the game resources and makes necessairy changes
     */
    public void LoadGame()
    {
        if (GameRef.GetMusicState() && OldState == null)
        {
            GameRef.bsMusic.stopAll();
            GameRef.bsMusic.play(GameConstants.GameInsideMusicPath, BaseAudio.SINGLE_REPLAY);
        }

        switch(GameState)
        {
            case PuzzleState:
                LoadPuzzleState();
                break;
            case PlatformState:
                LoadPlatformState();
                break;
        }//switch
    }

    /**
     * Unloads game resources, depending on which state it is in
     */
    public void UnloadGame()
    {
        switch(GameState)
        {
            case PuzzleState:
                UnloadPuzzleState();
                break;
            case PausedState: //fallthrough desired
            case PlatformState:
                UnloadPlatformState();
                break;
            case GameOverState:
                UnloadGameOverState();
                break;
        }//switch

        FreeResources();
    }

    /**
     * Loads the resources needed to run the platform part of the game
     */
    private void LoadPlatformState()
    {
        GameField = new PlayField();
        //HeroSpriteSheet = LoadPlayerSpriteSheet();

        Lives = new Sprite[PlayerConstants.MaxLife];
        HealthBar = new Sprite[PlayerConstants.MaxHealth];

        Clock = new Sprite(GameRef.bsLoader.getImage(GameConstants.ClockString));
        Ammo = new Sprite(GameRef.bsLoader.getImage(GameConstants.BulletImage));

        Pause = new Sprite(ImageUtil.getImage(GameRef.bsIO.getURL(GameConstants.PauseImage),Color.WHITE));
        Pause.move(GameRef.getWidth()/2 - Pause.getWidth()/2 , GameRef.getHeight()/2 - Pause.getHeight());

        //Creating collision groups
        terrainCollisionHandler = new TerrainCollision();
        assetCollisionHandler = new AssetCollision();
        exitCollisionHandler = new DoorCollision();
        problemCollisionGroup = new ProblemCollisionGroup();
        playerShotCollisionGroup = new PlayerShotCollision();
        enemyShotCollisionGroup = new EnemyShotCollision();
        blockableCollisionGroup = new ProblemCollisionGroup();
        enemyBlockableCollisionGroup = new ProblemCollisionGroup();
        playerShotBlockableCollisionGroup = new PlayerShotCollision();

        if (Hero == null)
        {
            LoadHero();
        }
        else
        {
            Hero.resetMudTimer();//Forgot this =/
            if(PlayerGroup != null)
                PlayerGroup.remove(Hero);
        }

        Hero.setLocation(PlayerConstants.PlayerStartX, GameConstants.AssetY - Hero.getHeight());
        
        Hero.forceX(PlayerConstants.PlayerStartX);
        Hero.forceY(GameConstants.AssetY - Hero.getHeight());

        //Creating groups
        PlayerGroup = new SpriteGroup("Player Group");
        PlayerShotsGroup = new SpriteGroup("Player Shots Group");
        EnemyShotsGroup = new SpriteGroup("Enemy Shots Group");
        SceneObjects = new SpriteGroup("Scene Objects");
        EnemyGroup = new SpriteGroup("Enemy Group");
        UIElementsGroup = new SpriteGroup("UI Elements");
        DoorExitGroup = new SpriteGroup("Exit Door");
        BlockableGroup = new SpriteGroup("Blockable Group");

        Sprite ExitDoor = new Sprite(GameRef.getImage(GameConstants.GameDoor));
        ExitDoor.setLocation(649 + 0.5*GameConstants.AssetBlockSize, GameConstants.AssetY - ExitDoor.getHeight());

        DoorExitGroup.add(ExitDoor);

        PlayerGroup.setBackground(GameBackground);
        PlayerShotsGroup.setBackground(GameBackground);
        EnemyShotsGroup.setBackground(GameBackground);
        SceneObjects.setBackground(GameBackground);
        EnemyGroup.setBackground(GameBackground);
        DoorExitGroup.setBackground(GameBackground);
        UIElementsGroup.setBackground(GameBackground);
        BlockableGroup.setBackground(GameBackground);

        //Adding HeroSprites to player group
        PlayerGroup.add(Hero);
        UIElementsGroup.add(Ammo);
        UIElementsGroup.add(Clock);
        Clock.move(GameConstants.ClockPositionX - Clock.getWidth(), GameConstants.ClockPositionY);
        Ammo.move(GameConstants.UIElementsStartX + GameRef.LittleFont.getWidth(GameConstants.AmmoString),
                GameConstants.UIElementsStartY + 75);

        double XPos = GameRef.LittleFont.getWidth(GameConstants.LivesString) + GameConstants.UIElementsStartX;

        //Populating life bar
        for (int i = 0; i < Lives.length; i++)
        {
            Lives[i] = new Sprite(GameRef.getImage(GameConstants.LifeSprite));
            Lives[i].setImmutable(true);
            Lives[i].move( XPos, GameConstants.UIElementsStartY - 10);
            XPos += Lives[i].getWidth();
            UIElementsGroup.add(Lives[i]);
        }

        XPos = GameRef.LittleFont.getWidth(GameConstants.HealthString) + GameConstants.UIElementsStartX;

        //Populating health bar
        for (int i = 0; i < HealthBar.length; i++)
        {
            HealthBar[i] = new Sprite(GameRef.getImage(GameConstants.HealthSprite));
            HealthBar[i].setImmutable(true);
            HealthBar[i].move(XPos, GameConstants.UIElementsStartY + 33);
            XPos += HealthBar[i].getWidth() + 2;
            UIElementsGroup.add(HealthBar[i]);
        }

        //Adding SceneObjects to SceneObjectsGroup
        for (int i = 0; i < TrashInBaskets.length; i++)
        {
            switch (TrashInBaskets[i])
            {
                case GlassBonus:
                    Potion newPotion = new Potion(
                            ImageUtil.getImages(GameRef.bsIO.getURL(GameConstants.GameAssetGlassBonus),
                            1, 1, Color.WHITE), 0, 0, Hero, this.GameRef);
                    newPotion.setLocation(
                            GameConstants.AssetOneX + (i)*GameConstants.AssetBlockSize + 0.5*GameConstants.AssetBlockSize - newPotion.getWidth(),
                            GameConstants.AssetY - newPotion.getHeight());
                    newPotion.setFrame(0);
                    newPotion.setActive(true);
                    SceneObjects.add(newPotion);
                    break;

                case GlassProblem:
                    GlassWall newGlassWall = new GlassWall(
                            ImageUtil.getImages(GameRef.bsIO.getURL(GameConstants.GameAssetGlassProblem),
                            2, 1, Color.WHITE), 0, 0, Hero, ImageUtil.getImages(GameRef.bsIO.getURL(GameConstants.GameAssetGlassBroken),
                            2, 1, Color.WHITE), this.GameRef);
                    newGlassWall.setLocation(
                            GameConstants.AssetOneX + (i)*GameConstants.AssetBlockSize + 0.5*GameConstants.AssetBlockSize - 0.5*newGlassWall.getWidth(),
                            GameConstants.AssetY - newGlassWall.getHeight());
                    newGlassWall.setFrame(0);
                    newGlassWall.setActive(true);
                    //EnemyGroup.add(newGlassWall);
                    BlockableGroup.add(newGlassWall);
                    break;

                case MetalBonus:
                    Coin newCoin = new Coin(
                            ImageUtil.getImages(GameRef.bsIO.getURL(GameConstants.GameAssetMetalBonus), 
                            1, 1, Color.WHITE), 0, 0, Hero, GameRef);
                    newCoin.setLocation(
                            GameConstants.AssetOneX + (i)*GameConstants.AssetBlockSize + 0.5*GameConstants.AssetBlockSize - 0.5*newCoin.getWidth(),
                            GameConstants.AssetY - newCoin.getHeight());
                    newCoin.setFrame(0);
                    newCoin.setActive(true);
                    SceneObjects.add(newCoin);
                    break;

                case MetalProblem:
                    Spikes newSpike = new Spikes(ImageUtil.getImages(GameRef.bsIO.getURL(GameConstants.GameAssetMetalProblem),
                            1, 1, Color.WHITE),0, 0, Hero);
                    newSpike.setLocation(
                            GameConstants.AssetOneX + (i)*GameConstants.AssetBlockSize + 0.5*GameConstants.AssetBlockSize - 0.5*newSpike.getWidth(),
                            GameConstants.AssetY - newSpike.getHeight());
                    newSpike.setFrame(0);
                    newSpike.setActive(true);
                    //EnemyGroup.add(newSpike);
                    BlockableGroup.add(newSpike);
                    break;

                case OrganicBonus:
                    Apple newApple = new Apple(ImageUtil.getImages(GameRef.bsIO.getURL(GameConstants.GameAssetOrganicBonus),
                            1, 1, Color.WHITE),0, 0, Hero, GameRef);
                    newApple.setLocation(
                            GameConstants.AssetOneX + (i)*GameConstants.AssetBlockSize + 0.5*GameConstants.AssetBlockSize - 0.5*newApple.getWidth(),
                            GameConstants.AssetY - newApple.getHeight());
                    newApple.setFrame(0);
                    newApple.setActive(true);
                    SceneObjects.add(newApple);
                    break;

                case OrganicProblem:
                    Mud newMud = new Mud(
                            ImageUtil.getImages(GameRef.bsIO.getURL(GameConstants.GameAssetOrganicProblem),
                            1, 1, Color.WHITE), 0, 0, Hero);
                    newMud.setLocation(
                            GameConstants.AssetOneX + (i)*GameConstants.AssetBlockSize + 0.5*GameConstants.AssetBlockSize - 0.5*newMud.getWidth(),
                            GameConstants.AssetY - newMud.getHeight());
                    newMud.setFrame(0);
                    newMud.setActive(true);
                    EnemyGroup.add(newMud);
                    break;

                case PlasticBonus:
                    Gun newGun = new Gun (
                            ImageUtil.getImages(GameRef.bsIO.getURL(GameConstants.GameAssetPlasticBonus),
                            1, 1, Color.WHITE), 0, 0, Hero, this.GameRef);
                    newGun.setLocation(
                            GameConstants.AssetOneX + (i)*GameConstants.AssetBlockSize + 0.5*GameConstants.AssetBlockSize - newGun.getWidth(),
                            GameConstants.AssetY - newGun.getHeight());
                    newGun.setFrame(0);
                    newGun.setActive(true);
                    SceneObjects.add(newGun);
                    break;

                case PlasticProblem:
                    Robot newRobot = new Robot(
                            ImageUtil.getImages(GameRef.bsIO.getURL(GameConstants.GameAssetPlasticProblem),
                            2, 1, Color.WHITE), 0, 0, Hero, GameConstants.RobotHealth, this.GameRef);
                    newRobot.setLocation(
                            GameConstants.AssetOneX + (i)*GameConstants.AssetBlockSize + 0.5*GameConstants.AssetBlockSize - newRobot.getWidth(),
                            GameConstants.AssetY - newRobot.getHeight());
                    newRobot.setActive(true);
                    EnemyGroup.add(newRobot);
                    break;

                default:
                    break;
            }//switch
        }//for

        GameField.addCollisionGroup(RecyclableBinGroup, PlayerGroup, terrainCollisionHandler);
        GameField.addCollisionGroup(EnemyGroup, PlayerGroup, problemCollisionGroup);
        GameField.addCollisionGroup(BlockableGroup, PlayerGroup, blockableCollisionGroup);
        GameField.addCollisionGroup(PlayerShotsGroup, EnemyGroup, playerShotCollisionGroup);
        GameField.addCollisionGroup(EnemyShotsGroup, PlayerGroup, enemyShotCollisionGroup);
        GameField.addCollisionGroup(BlockableGroup, EnemyGroup, enemyBlockableCollisionGroup);
        GameField.addCollisionGroup(PlayerShotsGroup, BlockableGroup, playerShotBlockableCollisionGroup);
        GameField.addCollisionGroup(SceneObjects, PlayerGroup, assetCollisionHandler);
        GameField.addCollisionGroup(DoorExitGroup, PlayerGroup, exitCollisionHandler);
        
        GameField.setBackground(GameBackground);

        //This order influences the element drawing order
        GameField.addGroup(PlayerGroup);
        GameField.addGroup(PlayerShotsGroup);
        GameField.addGroup(EnemyShotsGroup);
        GameField.addGroup(BlockableGroup);
        GameField.addGroup(SceneObjects);
        GameField.addGroup(RecyclableBinGroup);
        GameField.addGroup(DoorExitGroup);
        GameField.addGroup(EnemyGroup); //So, enemies are drawn in front of the door
        GameField.addGroup(UIElementsGroup);

        //gameTimer.setActive(true);
        //gameTimer.refresh();
        bananaGameTimer.startTimer();
    }

    /**
     * Releases the resources needed to run the platform part of the game
     */
    private void UnloadPlatformState()
    {
        Pause = null;
        //gameTimer.setActive(false);
        bananaGameTimer.resetTimer();
        OldState = InGameState.PlatformState;

        //As images remain stored until removed, the flip remains as well
        //This is needed to avoid having a flipped image being used after
        //the game was canceled. Using a spritesheet with flipped images and
        //properly using AdvanceSprite should solve this properly.
        //GameRef.bsLoader.removeImages(HeroSpriteSheet);

        Hero.turnOffInvincibility();
        GameField.clearPlayField();
    }
    
    /**
     * Loads the resources needed to run the puzzle part of the game
     */
    private void LoadPuzzleState()
    {
        try
        {
            //Initializes GameField
            GameField = new PlayField(GameBackground);
            
            //Generating random column order
            ColumnOrder = new LinkedList<Column>();
            ColumnOrder.add(Column.First);
            ColumnOrder.add(Column.Second);
            ColumnOrder.add(Column.Third);
            ColumnOrder.add(Column.Fourth);
            Collections.shuffle(ColumnOrder);

            Baskets = GenerateRandomPositions();
            TrashInBaskets = new RecyclableFeature[GameConstants.NumberofBlocks];
            RecyclableBinGroup = new SpriteGroup("RecyclableBins");
            ResultGroup = new SpriteGroup("Results");
            //ResultGroup.setBackground(GameBackground);
            terrainCollisionHandler = new TerrainCollision();

            ActiveBlock = 0;

            //Adding the platforms firstly, for the blocks will appear in front of them
            RecyclableBinGroup.add(new Sprite(GameRef.getImage(GameConstants.GameAssetStart),
                GameConstants.AssetStartX,GameConstants.AssetY));
            RecyclableBinGroup.add(new Sprite(GameRef.getImage(GameConstants.GameAssetStart),
                650,GameConstants.AssetY));

            for (int i = 0; i < Baskets.size(); i++)
            {
                switch (Baskets.get(i))
                {
                    case Glass:
                        RecyclableBinGroup.add(new Sprite(GameRef.getImage(GameConstants.GameAssetGlass),
                            GameConstants.AssetOneX+(i)*GameConstants.AssetBlockSize,GameConstants.AssetY));
                        break;
                    case Metal:
                        RecyclableBinGroup.add(new Sprite(GameRef.getImage(GameConstants.GameAssetMetal),
                            GameConstants.AssetOneX+(i)*GameConstants.AssetBlockSize,GameConstants.AssetY));
                        break;
                    case Organic:
                        RecyclableBinGroup.add(new Sprite(GameRef.getImage(GameConstants.GameAssetOrganic),
                            GameConstants.AssetOneX+(i)*GameConstants.AssetBlockSize,GameConstants.AssetY));
                        break;
                    case Plastic:
                        RecyclableBinGroup.add(new Sprite(GameRef.getImage(GameConstants.GameAssetPlastic),
                            GameConstants.AssetOneX+(i)*GameConstants.AssetBlockSize,GameConstants.AssetY));
                        break;
                }//switch

            }//for

            RecyclableBinGroup.setBackground(GameBackground);
            ResultGroup.setBackground(GameBackground);
            GameField.addGroup(RecyclableBinGroup);
            //GameField.addGroup(ResultGroup);

            //Initializes blocksOk
            blocksOk = new boolean[GameConstants.NumberofBlocks];
            blocksOk[0] = false;
            blocksOk[1] = false;
            blocksOk[2] = false;
            blocksOk[3] = false;

            //Old code is kept here if rotate seems to be inefficient
            //
            //BufferedImage Block1 = new BufferedImage(BlockImage.getWidth(), BlockImage.getHeight(),
            //        BufferedImage.TYPE_4BYTE_ABGR_PRE);
            ////This writes BlockImage to Block1 buffer
            //Block1.createGraphics().drawImage(BlockImage, 0, 0, null);

            BufferedImage Block = GameRef.bsLoader.getImage(GameConstants.Block);

            Blocks = new Block[GameConstants.NumberofBlocks];
            Blocks[0] = new Block(Block);
            Blocks[1] = new Block(Block);
            Blocks[2] = new Block(Block);
            Blocks[3] = new Block(Block);

            Random rand = new Random(); //Initialized with seed = currentTime (as of Java2 SE 1.4.2)
            int rots;

            //Randomizing blocks starting position
            for (int i = 0; i < GameConstants.NumberofBlocks; i++)
            {
                rots = rand.nextInt()%GameConstants.NumberofBlocks;

                //rotates the block rots times
                for(int j = 0; j < rots; j++)
                {
                  Blocks[i].RotateCounterClockWise();
                }
            }

            BlocksGroup = new SpriteGroup("Blocks");
            BlocksGroup.add(Blocks[0]);
            BlocksGroup.add(Blocks[1]);
            BlocksGroup.add(Blocks[2]);
            BlocksGroup.add(Blocks[3]);

            BlocksGroup.setBackground(GameBackground);
            BlocksGroup.setActive(true);

            //Must be set to Immutable, or when setActive to false, will be disposed
            //When Immutable == true, can only be disposed by SpriteGroup
            Blocks[0].setImmutable(true);
            Blocks[1].setImmutable(true);
            Blocks[2].setImmutable(true);
            Blocks[3].setImmutable(true);

            Blocks[0].setActive(false);
            Blocks[1].setActive(false);
            Blocks[2].setActive(false);
            Blocks[3].setActive(false);

            Blocks[ActiveBlock].setActive(true);
            Blocks[ActiveBlock].SetColumn(ColumnOrder.poll());
            Blocks[ActiveBlock].move(ConvertColumnToCoordinate(Blocks[ActiveBlock].GetColumn()), 0);

            GameField.addCollisionGroup(RecyclableBinGroup, BlocksGroup, terrainCollisionHandler);
            GameField.addGroup(BlocksGroup);
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * Releases the resources needed to run the puzzle part of the game
     */
    private void UnloadPuzzleState() {
        //Removes so this isn't cleared by clear playfield
        OldState = InGameState.PuzzleState;
        GameField.removeGroup(RecyclableBinGroup);
        GameField.removeGroup(ResultGroup);
        ResultGroup.clear();
        GameField.clearPlayField();
    }

    /**
     * Generates a list of unique Recyclables in random order
     * @return ArrayList containing the numbers in random order
     */
    private ArrayList<Recyclables> GenerateRandomPositions ()
    {
        ArrayList<Recyclables> baskets = new ArrayList<Recyclables>();

        baskets.add(Recyclables.Glass);
        baskets.add(Recyclables.Metal);
        baskets.add(Recyclables.Organic);
        baskets.add(Recyclables.Plastic);

        Collections.shuffle(baskets);
        return baskets;
    }

    
    /**
     * Convert column enumerate to world coordinates
     * @param col
     * @return
     */
    private double ConvertColumnToCoordinate ( Column col )
    {
        switch(col)
        {
            case First:
                return GameConstants.AssetOneX + 1;

            case Second:
                return GameConstants.AssetOneX + 1 + GameConstants.AssetBlockSize;
                
            case Third:
                return GameConstants.AssetOneX + 1 + 2*GameConstants.AssetBlockSize;
            
            case Fourth:
                return GameConstants.AssetOneX + 1 + 3*GameConstants.AssetBlockSize;
                
            default:
                return GameConstants.AssetOneX;
        }//switch
    }
    
    /**
     * Convert passed index to Column equivalent
     * @param i
     * @return
     * @throws Exception
     */
    private Column ConvertIndexToColumn(int i) throws Exception
    {
        switch (i)
        {
            case 0:
                return Column.First;
            case 1:
                return Column.Second;
            case 2:
                return Column.Third;
            case 3:
                return Column.Fourth;
            default:
                throw new Exception("Invalid index to column convertion");
        }
    }

    private BufferedImage[] LoadPlayerSpriteSheet() {
        BufferedImage[] spritesheet = new BufferedImage[PlayerConstants.SpriteSheetImages];

        for (int i = 0; i < PlayerConstants.SpriteSheetImages; i++)
        {
            spritesheet[i] = GameRef.getImage("resources/images/character/banana" + (i+1) + ".png", true);
        }

        return spritesheet;
    }

    private void ResetPlayerOnDeath()
    {
        Hero.setHealth(PlayerConstants.StartingHealth);
        Hero.removeGun();
    }

    private void LoadGameOverState() {
        try
        {
            GameField = new PlayField(new ImageBackground(
                    GameRef.getImage(GameConstants.GameOverScreen)));

            GameRef.bsMusic.stopAll();

            if(GameRef.GetSoundState())
                GameRef.bsSound.play(SoundStrings.GameOver);

            gameOverTimer.setActive(true);
            gameOverTimer.refresh();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void UnloadGameOverState()
    {
        GameField.clearPlayField();
        gameOverTimer.setActive(false);
    }

    private void InterfaceRender(Graphics2D g) {
        int NextLine = GameRef.LittleFont.drawText(g, GameConstants.LivesString, GameFont.LEFT,
                        GameConstants.UIElementsStartX, GameConstants.UIElementsStartY , GameRef.LittleFont.getWidth(GameConstants.LivesString), 0, 0);
        NextLine = GameRef.LittleFont.drawText(g, GameConstants.HealthString, GameFont.LEFT,
                GameConstants.UIElementsStartX, NextLine, GameRef.LittleFont.getWidth(GameConstants.HealthString), 0, 0);
        GameRef.LittleFont.drawText(g, GameConstants.AmmoString, GameFont.LEFT,
                GameConstants.UIElementsStartX, NextLine, 400, 0, 0);
        GameRef.LittleFont.drawText(g, " x " + Hero.getAmmo(), GameFont.LEFT,
                GameConstants.UIElementsStartX + Ammo.getWidth() +
                GameRef.LittleFont.getWidth(GameConstants.AmmoString), NextLine, 400, 0, 0);

        NextLine = GameRef.LittleFont.drawText(g, "Stage: " + Stage, GameFont.LEFT,
                GameRef.getWidth() - GameRef.LittleFont.getWidth("Stage: " + Stage) - 20, GameConstants.UIElementsStartY, 400, 0, 0);
        NextLine = GameRef.LittleFont.drawText(g, "Score: " + Hero.getScore(), GameFont.LEFT,
                GameRef.getWidth() - GameRef.LittleFont.getWidth("Score: " + Hero.getScore()) - 20, NextLine, 400, 0 ,0);

        time = "" + (int)(((long)GameConstants.ClockTimer - bananaGameTimer.getTimePast())/1000);

        //As time may not be set already when game starts, this could throw an NumberFormatException
        try
        {
            if (Integer.parseInt(time) < 10)
            {
                time = "0" + time;
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

        if (GameState != InGameState.PausedState)
            GameRef.BigFont.drawText(g, "0:" + time, GameFont.CENTER,
                (int)Clock.getX(), (int) (Clock.getY() - 60), 200, 0, 0);

        //Prints INVINCIBLE on screen
        if(Hero.getInvincibleDueToPotion())
        {
            Invincible.render(g);
            //GameRef.BigFont.drawText(g, GameConstants.Invincible, GameFont.LEFT,
            //    GameConstants.InvincibleX, GameConstants.InvincibleY, 400, 0, 0);
            //System.out.println(NextLine+30);
        }
    }

    /**
     * This method is necessary to free resources from JVM. As of 2010/08/23, we're getting
     * numerous memory leaks due to references not being freed
     */
    private void FreeResources() {

        //Before GC
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        System.out.println("free memory: " + freeMemory / 1024);
        System.out.println("allocated memory: " + allocatedMemory / 1024);
        System.out.println("max memory: " + maxMemory /1024);
        System.out.println("total free memory: " + (freeMemory + (maxMemory - allocatedMemory)) / 1024);

        OldState = null;
        GameState = null;

        if (GameField != null)
        {
            GameField.clearCache();
            GameField.clearPlayField();
            GameField = null;
        }

        if (PlayerGroup != null)
        {
            PlayerGroup.clear();
            PlayerGroup = null;
        }

        if (PlayerShotsGroup != null)
        {
            PlayerShotsGroup.clear();
            PlayerShotsGroup = null;
        }

        if (EnemyShotsGroup != null)
        {
            EnemyShotsGroup.clear();
            EnemyShotsGroup = null;
        }

        if (SceneObjects != null)
        {
            SceneObjects.clear();
            SceneObjects = null;
        }

        if (EnemyGroup != null)
        {
            EnemyGroup.clear();
            EnemyGroup = null;
        }

        if (BlocksGroup != null)
        {
            BlocksGroup.clear();
            BlocksGroup = null;
        }

        if (RecyclableBinGroup != null)
        {
            RecyclableBinGroup.clear();
            RecyclableBinGroup = null;
        }

        if (UIElementsGroup != null)
        {
            UIElementsGroup.clear();
            UIElementsGroup = null;
        }

        if (BlockableGroup != null)
        {
            BlockableGroup.clear();
            BlockableGroup = null;
        }

        if (DoorExitGroup != null)
        {
            DoorExitGroup.clear();
            DoorExitGroup = null;
        }

        if (ResultGroup != null)
        {
            ResultGroup.clear();
            ResultGroup = null;
        }


        if (Baskets != null)
        {
            Baskets.clear();
            Baskets = null;
        }

        if (TrashInBaskets != null)
        {
            for(int i = 0; i < TrashInBaskets.length; i++)
                TrashInBaskets[i] = null;
            TrashInBaskets = null;
        }

        Hero = null;

        if (Lives != null)
        {
            for(int i = 0; i < Lives.length; i++)
                Lives[i] = null;
            Lives = null;
        }

        if (HealthBar != null)
        {
            for(int i = 0; i < HealthBar.length; i++)
                HealthBar[i] = null;
            HealthBar = null;
        }

        if (Blocks != null)
        {
            for(int i = 0; i < Blocks.length; i++)
                Blocks[i] = null;
            Blocks = null;
        }

        Pause = null;
        Clock = null;
        Ammo = null;
        MatchColors = null;
        GameBackground = null;

        if(ColumnOrder != null)
        {
            ColumnOrder.clear();
            ColumnOrder = null;
        }

        terrainCollisionHandler = null;
        assetCollisionHandler = null;
        exitCollisionHandler = null;
        problemCollisionGroup = null;
        blockableCollisionGroup = null;
        enemyBlockableCollisionGroup = null;
        playerShotCollisionGroup = null;
        playerShotBlockableCollisionGroup = null;
        enemyShotCollisionGroup = null;

        gameOverTimer = null;
        gameTimer = null;
        cutsceneTimer = null;
        endgameTimer = null;
        bananaGameTimer = null;
        time = null;

        if (cutsceneScreens != null)
        {
            cutsceneScreens.clear();
            cutsceneScreens = null;
        }

        Invincible = null;
        Perfect = null;
    }

    /**
     * Advances one stage, updating GameState and loading assets relative to
     * the next stage. Must unload previous state before calling.
     * @param advancePuzzle True if must advance to a puzzle stage
     */
    private void AdvanceStage(boolean advancePuzzle) {
        ++Stage;
        dropVelocity += GameConstants.BlockAcceleration*Stage*0.15;
        if (Stage > GameConstants.EndStage )
        {
            LoadEndGameState();
            GameState = InGameState.EndScene;
        }
        //Cutscene check, every stage divisible by 5
        else if(Stage % GameConstants.CutsceneStageModulo == 0)
        {
            GameState = InGameState.Cutscene;
            LoadCutsceneState();   
        }
        else if (advancePuzzle)
        {
            GameState = InGameState.PuzzleState;
            LoadPuzzleState();
        }
        else{
            GameState = InGameState.PlatformState;
            LoadPlatformState();
        }
    }

    /**
     * This class provides methods that handle collision between the terrain and something else,
     * for example the character
     */
    private class TerrainCollision extends CollisionGroup {

        public TerrainCollision () {
            // Check on pixels.
            pixelPerfectCollision = true;
        }

        @Override
        public void collided (Sprite one, Sprite two) {
            if (two instanceof Player)
            {
                //handleCollision(one, two);
                if (collisionSide == TOP_BOTTOM_COLLISION)
                {
                    two.moveY(two.getOldY() - two.getY());
                    ((Player)two).setOnGround ();
                }
            }
            else
            {
                if (two instanceof Block)
                {
                    blockCollision(two);
                }//if
            }//if
        }
    }

    private void LoadEndGameState()
    {
        GameField.clearPlayField();
        Stage--; //Just to not write something weird at the score
        GameField.setBackground(new ImageBackground(GameRef.getImage(GameConstants.EndGameImage)));
        GameRef.bsMusic.stopAll();
        if(GameRef.GetMusicState())
            GameRef.bsMusic.play(GameConstants.GameEndMusic);
        endgameTimer.setActive(true);
        endgameTimer.refresh();
    }

    private void LoadCutsceneState() {
        cutsceneTimer.setActive(true);
        cutsceneTimer.refresh();
        cutsceneIndex = ++cutsceneIndex%(GameConstants.Cutscenes-1);
        GameField = new PlayField(new ImageBackground(cutsceneScreens.get(cutsceneIndex)));
    }

    /**
     * Provides methods to handle collison between player and Items
     */
    private class AssetCollision extends BasicCollisionGroup
    {
        public AssetCollision () {
            // Check on pixels.
            pixelPerfectCollision = true;
        }

        @Override
        public void collided(Sprite s1, Sprite s2) {
            if (s2 instanceof Player && s1 instanceof SceneObject)
            {
                //System.out.println("Player collided with SceneObject");
                SceneObject item = (SceneObject)s1;
                item.OnCollision();
                //System.out.println(Hero.getScore());
            }//if
        }

    }

    private class PlayerShotCollision extends BasicCollisionGroup
    {
        public PlayerShotCollision () {
            // Check on pixels.
            pixelPerfectCollision = true;
        }

        @Override
        public void collided(Sprite s1, Sprite s2) {
            if(s1 instanceof Bullet)
            {
                //System.out.println("Bullet collided");
                ((Bullet)s1).OnCollision(s2);
            }
        }
    }

    private class EnemyShotCollision extends BasicCollisionGroup
    {
        public EnemyShotCollision () {
            // Check on pixels.
            pixelPerfectCollision = true;
        }

        @Override
        public void collided(Sprite s1, Sprite s2) {
            if(s1 instanceof Bullet)
            {
                //System.out.println("Bullet collided");
                ((Bullet)s1).OnCollision(s2);
            }
        }
    }

    private class DoorCollision extends BasicCollisionGroup
    {

        public DoorCollision () {
            // Check on pixels.
            pixelPerfectCollision = true;
        }

        @Override
        public void collided(Sprite s1, Sprite s2) {
            if (s2 instanceof Player)
            {
                try
                {
                    int slot;
                    BaseAudioRenderer test = null;
                    if(GameRef.GetSoundState())
                    {
                        slot = GameRef.bsSound.play(SoundStrings.DoorExit, BaseAudio.SINGLE);
                        test = GameRef.bsSound.getAudioRenderer(slot);

                        /**
                         * @TODO: This doesn't work in Linux. Why???
                         * I think it depends on JVM implementation and if System
                         * is 64 or 32 bit
                         */
//                        while (test.getStatus() != BaseAudioRenderer.END_OF_SOUND)
//                        {
//                            //Waits for audio file to finish before loading
//                        }//while
                        
                        // wait for 1 second
                        Thread.sleep(1000L);
                    }//if
                }
                catch (Exception e)
                {
                    System.out.println(e.getMessage());
                }

                GameField.removeGroup(PlayerGroup);
                UnloadPlatformState();

                AdvanceStage(true);
            }//if
        }
    }

    /**
     * Group collision that deals with Recyclables Problems collision
     */
    private class ProblemCollisionGroup extends CollisionGroup
    {

        public ProblemCollisionGroup() {
            pixelPerfectCollision = true;
        }


        @Override
        public void collided(Sprite s1, Sprite s2) {
            if(s2 instanceof Player || s2 instanceof Robot)
            {
                //System.out.println("Player collided");
                if (!(s1 instanceof Mud))
                {
                    switch (collisionSide)
                    {
                        case BOTTOM_TOP_COLLISION:
                            //Not tested
                            //System.out.println("BOTTOM TOP COLLISION");
                            //s2.moveY((collisionY1 + s1.getHeight() - collisionY2));
                            s2.moveY(s2.getOldY() + s2.getY());
                            break;
                            
                        case TOP_BOTTOM_COLLISION:
                            //PORTAL!!! s2.moveY(-(collisionY2 + s2.getHeight() + collisionY1));
                            //s2.moveY(-(collisionY2 + s2.getHeight() - collisionY1));
                            s2.moveY(s2.getOldY() - s2.getY());

                            if(s2 instanceof Player)
                            {
                                ((Player)s2).setOnGround();
                                
                                if (s1 instanceof GlassWall)
                                {
                                    //System.out.println(collisionSide);
                                    ((GlassWall)s1).OnCollision();
                                }
                                else if (s1 instanceof Robot)
                                {
                                    ((Robot)s1).OnCollision();
                                }
                                else if (s1 instanceof Spikes)
                                {
                                    ((Spikes)s1).OnCollision();
                                }
                            }

                            break;

                        case LEFT_RIGHT_COLLISION:
                            //s2.moveX((collisionX1 - (collisionX2 + s2.getWidth())));

                            if (s1 instanceof Spikes && s2 instanceof Player)
                                s2.setX(s2.getOldX());
                            else
                                s2.moveX((collisionX1 - (collisionX2 + s2.getWidth())));
                            
                            //Treats collision with left border
                            if (s2.getX() < 0)
                            {
                                s2.moveX(-s2.getX()); //I think this is unaccurate
                            }//if

                            if(s2 instanceof Player)
                            {
                                if (s1 instanceof Robot)
                                {
                                    ((Robot)s1).OnCollision();
                                }
                            }

                            break;

                        case RIGHT_LEFT_COLLISION:
                            //OK
                            //System.out.println("RIGHT LEFT COLLISION");
                            //s2.moveX(-(collisionX2 - s1.getWidth() - collisionX1));
                            if (s1 instanceof Spikes && s2 instanceof Player)
                                s2.setX(s2.getOldX());
                            else
                                s2.moveX(-(collisionX2 - s1.getWidth() - collisionX1));

                            //treats collision with screen right border
                            if (s2.getX() + s2.getWidth() > GameRef.getWidth())
                            {
                                s2.moveX(GameRef.getWidth() - (s2.getX() + s2.getWidth()));
                            }//if

                            if(s2 instanceof Player)
                            {
                                if (s1 instanceof Robot)
                                {
                                    ((Robot)s1).OnCollision();
                                }
                            }

                            break;
                    }//switch
                }
                else
                {
                    if (s2 instanceof Player)
                    {
                        //System.out.println("Bananixo collided with Mud");
                        ((Mud)s1).OnCollision();
                    }
                }//if
            }//if
        }
    }

    private void LoadHero()
    {
        Hero = new Player(HeroSpriteSheet,PlayerConstants.PlayerStartX, PlayerConstants.PlayerStartY,
                    PlayerConstants.StartingLives, PlayerConstants.StartingHealth, PlayerConstants.StartingAmmo, PlayerConstants.StartWithGun,
                    GameRef);
        Hero.setImmutable(true);
        Hero.setAnimationFrame(PlayerConstants.WalkAnimation);
        Hero.getAnimationTimer().setDelay(300); //In milliseconds
        Hero.setAnimate(true);
        Hero.setLoopAnim(true);
    }

    public void RemoveRobotFromCollision(Robot myself)
    {
        EnemyGroup.remove(myself);
        UIElementsGroup.add(myself);//Well, this is ugly, but may work
    }
}

