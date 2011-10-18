package LixoGame;

import com.golden.gamedev.engine.BaseAudio;
import java.awt.*;
import java.awt.event.*;
import com.golden.gamedev.*;
import com.golden.gamedev.Game;
import com.golden.gamedev.engine.audio.JavaLayerMp3Renderer;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;
import com.golden.gamedev.util.*;
import com.golden.gamedev.util.FontUtil;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bruno Baere
 */
public class LixoGame extends Game {

    //Private attributes
    private GameState oldState;
    private GameState gameState;
    private Menu gameMenu;
    private HowToState howTo;

    private ImageBackground HowToFirstBackground;
    private ImageBackground HowToSecondBackground;
    private ImageBackground HowToThirdBackground;
    private ImageBackground HowToCurrent;

    private BufferedImage[] MenuPlaySheet;
    private BufferedImage[] MenuHowToSheet;
    private BufferedImage[] MenuCreditsSheet;
    private BufferedImage[] MenuHiscoreSheet;

    public GameFont BigFont;
    public GameFont LittleFont;

    private AnimatedSprite MenuPlaySprite;
    private AnimatedSprite MenuHowToSprite;
    private AnimatedSprite MenuCreditsSprite;
    private AnimatedSprite MenuHiscoreSprite;

    private ImageBackground MenuBackground;
    private Dimension ScreenDimension;
    private SpriteGroup MenuGroup;
    private PlayField MenuField;
    protected GameManager InGameManager;

    private boolean sound = true;
    private boolean music = true;

    private String soundState = GameConstants.On;
    private String musicState = GameConstants.On;

    List<ScoreTracker> scoreTable = null;

    private ImageBackground CreditsBackground;
    private Sprite Hiscore;
    private Sprite Gamerama;

    @Override
    public void initResources() {

        try
        {
            //Setting initial game state
            gameState = GameState.MenuState;
            oldState = null;

            ScreenDimension = bsGraphics.getSize();

            MenuBackground = new ImageBackground(getImage(GameConstants.BackgroundImageTitle),
                    ScreenDimension.width, ScreenDimension.height);

            CreditsBackground = new ImageBackground(getImage(GameConstants.Credits),
                    ScreenDimension.width, ScreenDimension.height);

            //Setting game window properties
            bsGraphics.setWindowTitle(GameConstants.GameWindowTitle);
            bsGraphics.setWindowIcon(ImageUtil.getImage(bsIO.getURL(GameConstants.GameWindowIconPath),Color.GRAY));

            bsMusic.setBaseRenderer(new JavaLayerMp3Renderer());

            //Sets menu music
            if (GetMusicState())
                bsMusic.play(GameConstants.GameMenuMusicPath, BaseAudio.SINGLE_REPLAY);

            //Setting Font
            //BigFont = fontManager.getFont(LoadBigFontSheet());
            BigFont = (GameFont) fontManager.getFont(FontUtil.createTrueTypeFont(bsIO.getURL(GameConstants.ComicFont),Font.PLAIN,GameConstants.ComicFontSize), Color.BLACK);
            //LittleFont = fontManager.getFont(LoadSmallFontSheet());
            LittleFont = (GameFont) fontManager.getFont(FontUtil.createTrueTypeFont(bsIO.getURL(GameConstants.CartoonFont),Font.PLAIN,GameConstants.CartoonFontSize), Color.BLACK);

//            VisionLab = new Sprite(getImage(GameConstants.VisionLabImage), 550, 320);
//            CTS = new Sprite(getImage(GameConstants.CTSGSImage), 550, VisionLab.getY() + VisionLab.getHeight());
            Gamerama = new Sprite(getImage("resources/images/gamerama_lesser_bwa.png"), 678, 511);
            Hiscore = new Sprite(getImage(GameConstants.Hiscore));
            Hiscore.move(GameConstants.HiscoreX,GameConstants.HiscoreY);

            //Setting menu group
            LoadMenu();
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(long elapsedTime) {

        int keyPressed;

        //Checks game state
        switch(gameState)
        {
            case MenuState:

                keyPressed = bsInput.getKeyPressed();
                //If we are in the MenuState, we can just type up or down
                //to navigate the menu.
                if( keyPressed == KeyEvent.VK_UP )
                {
                    gameMenu.rollUp();
                }
                else
                {
                    if( keyPressed == KeyEvent.VK_DOWN )
                    {
                        gameMenu.rollDown();
                    }
                    else
                    {
                        if ( keyPressed == KeyEvent.VK_ENTER )
                        {
                            GameProcessMenu(gameMenu.getState());
                            return;
                        }
                        else
                        {
                            if (keyPressed == KeyEvent.VK_ESCAPE
                                 || keyPressed == (KeyEvent.ALT_DOWN_MASK & KeyEvent.VK_F4 ) ) //This isn't working
                            {
                                //Exit game
                                UnloadMenu();
                                this.finish();
                            }
                            else
                            {
                                if (keyPressed == KeyEvent.VK_S)
                                {
                                    ChangeSoundState();
                                }
                                else
                                {
                                    if(keyPressed == KeyEvent.VK_M)
                                    {
                                        ChangeMusicState();
                                    }//if
                                }//if
                            }//if
                        }//if
                    }//if
                }//if

                MenuField.update(elapsedTime);

                break;

            case HowToState:

                keyPressed = bsInput.getKeyPressed();

                switch (howTo)
                {
                    case FirstScreen:

                        if (keyPressed == KeyEvent.VK_ENTER || keyPressed == KeyEvent.VK_RIGHT )
                        {
                            howTo = HowToState.SecondScreen;
                            HowToCurrent = HowToSecondBackground;
                        }
                        else if ( keyPressed == KeyEvent.VK_ESCAPE )
                        {
                            oldState = GameState.HowToState;
                            gameState = GameState.MenuState;
                            LoadMenu();
                        }
                        else
                        {
                            if (keyPressed == KeyEvent.VK_S)
                            {
                                ChangeSoundState();
                            }
                            else
                            {
                                if(keyPressed == KeyEvent.VK_M)
                                {
                                    ChangeMusicState();
                                }//if
                            }//if
                        }//if

                        break;

                    case SecondScreen:

                        if (keyPressed == KeyEvent.VK_ENTER || keyPressed == KeyEvent.VK_RIGHT )
                        {
                            howTo = HowToState.ThirdScreen;
                            HowToCurrent = HowToThirdBackground;
                        }
                        else if ( keyPressed == KeyEvent.VK_LEFT )
                        {
                            howTo = HowToState.FirstScreen;
                            HowToCurrent = HowToFirstBackground;
                        }
                        else if ( keyPressed == KeyEvent.VK_ESCAPE )
                        {
                            //howTo = HowToState.FirstScreen;
                            oldState = GameState.HowToState;
                            gameState = GameState.MenuState;
                            LoadMenu();
                        }
                        else
                        {
                            if (keyPressed == KeyEvent.VK_S)
                            {
                                ChangeSoundState();
                            }
                            else
                            {
                                if(keyPressed == KeyEvent.VK_M)
                                {
                                    ChangeMusicState();
                                }//if
                            }//if
                        }//if

                        break;

                    case ThirdScreen:
                        
                        if (keyPressed == KeyEvent.VK_LEFT )
                        {
                            howTo = HowToState.SecondScreen;
                            HowToCurrent = HowToSecondBackground;
                        }
                        else if ( keyPressed == KeyEvent.VK_ESCAPE || keyPressed == KeyEvent.VK_ENTER )
                        {
                            //howTo = HowToState.FirstScreen;
                            oldState = GameState.HowToState;
                            gameState = GameState.MenuState;
                            LoadMenu();
                        }
                        else
                        {
                            if (keyPressed == KeyEvent.VK_S)
                            {
                                ChangeSoundState();
                            }
                            else
                            {
                                if(keyPressed == KeyEvent.VK_M)
                                {
                                    ChangeMusicState();
                                }//if
                            }//if
                        }

                        break;
                }//switch

                break;

            case GamePlayingState:

                boolean mantain = false;

                try {
                    mantain = InGameManager.update(elapsedTime);
                } catch (IOException ex) {
                    Logger.getLogger(LixoGame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(LixoGame.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (!mantain)
                {
                    InGameManager.UnloadGame();
                    oldState = GameState.GamePlayingState;
                    gameState = GameState.MenuState;
                    
                    if(music)
                        bsMusic.play(GameConstants.GameMenuMusicPath, BaseAudio.SINGLE_REPLAY);

                    LoadMenu();
                }//if

                break;

            case CreditsState:

                keyPressed = bsInput.getKeyPressed();

                if (keyPressed == KeyEvent.VK_ESCAPE || keyPressed == KeyEvent.VK_ENTER)
                {
                    oldState = GameState.CreditsState;
                    gameState = GameState.MenuState;
                    LoadMenu();
                }
                else
                {
                    if (keyPressed == KeyEvent.VK_S)
                    {
                        ChangeSoundState();
                    }
                    else
                    {
                        if(keyPressed == KeyEvent.VK_M)
                        {
                            ChangeMusicState();
                        }//if
                    }//if
                }//if

                break;

            case HiscoreState:

                keyPressed = bsInput.getKeyPressed();

                if (keyPressed == KeyEvent.VK_ESCAPE || keyPressed == KeyEvent.VK_ENTER)
                {
                    oldState = gameState.HiscoreState;
                    gameState = GameState.MenuState;
                    LoadMenu();
                }
                else
                {
                    if (keyPressed == KeyEvent.VK_S)
                    {
                        ChangeSoundState();
                    }
                    else
                    {
                        if(keyPressed == KeyEvent.VK_M)
                        {
                            ChangeMusicState();
                        }//if
                    }//if
                }//if

                break;
        }//switch

    }

    @Override
    public void render(Graphics2D g) {
        //render to the screen
        
        //background.render(g);
        //PlayerGroup.render(g);

        int nexty;

        switch(gameState)
        {
            case MenuState:
                MenuGroup.setActive(true);

                switch(gameMenu.getState())
                {
                    case Play:
                        MenuPlaySprite.setFrame(GameConstants.SelectedFrame);
                        MenuHowToSprite.setFrame(GameConstants.UnselectedFrame);
                        MenuCreditsSprite.setFrame(GameConstants.UnselectedFrame);
                        MenuHiscoreSprite.setFrame(GameConstants.UnselectedFrame);
                        break;
                    case HowTo:
                        MenuPlaySprite.setFrame(GameConstants.UnselectedFrame);
                        MenuHowToSprite.setFrame(GameConstants.SelectedFrame);
                        MenuCreditsSprite.setFrame(GameConstants.UnselectedFrame);
                        MenuHiscoreSprite.setFrame(GameConstants.UnselectedFrame);
                        break;
                    case Credits:
                        MenuPlaySprite.setFrame(GameConstants.UnselectedFrame);
                        MenuHowToSprite.setFrame(GameConstants.UnselectedFrame);
                        MenuCreditsSprite.setFrame(GameConstants.SelectedFrame);
                        MenuHiscoreSprite.setFrame(GameConstants.UnselectedFrame);
                        break;
                    case Hiscore:
                        MenuPlaySprite.setFrame(GameConstants.UnselectedFrame);
                        MenuHowToSprite.setFrame(GameConstants.UnselectedFrame);
                        MenuCreditsSprite.setFrame(GameConstants.UnselectedFrame);
                        MenuHiscoreSprite.setFrame(GameConstants.SelectedFrame);
                        break;
                }//switch

                MenuField.render(g);

                int nextx = LittleFont.drawString(g, GameConstants.Sound, 10, 580);
                LittleFont.drawString(g, ": " + soundState, nextx + 2, 580);
                nextx = LittleFont.drawString(g, GameConstants.Music, 680, 580);
                LittleFont.drawString(g, ": " + musicState, nextx + 2, 580);
                
                break;

            case GamePlayingState:
                InGameManager.render(g);
                break;

            case HowToState:
                HowToCurrent.render(g);
                break;

            case CreditsState:
                CreditsBackground.render(g);
//                VisionLab.render(g);
//                CTS.render(g);
                Gamerama.render(g);
//
//                nexty = BigFont.drawText(g, "CREDITS", GameFont.LEFT, 10, 320, 620, 0, 20);
//                nexty = LittleFont.drawText(g, "DOCUMENTADOR - ARTHUR PROTASIO", GameFont.LEFT, 10, nexty + 10, 620, 10, 20);
//                nexty = LittleFont.drawText(g, "PROGRAMADOR - BRUNO BAERE", GameFont.LEFT, 10, nexty, 620, 0, 20);
//                nexty = LittleFont.drawText(g, "DIRETOR DE ARTES E DESIGN - ROMULO MATTEONI", GameFont.LEFT, 10, nexty, 700, 0, 20);
//                nexty = LittleFont.drawText(g, "DIRETOR DE SOM - RODRIGO COELHO", GameFont.LEFT, 10, nexty, 620, 0, 20);
//                nexty = LittleFont.drawText(g, "ANIMACAO E ARTE - BRUNA SADDY", GameFont.LEFT, 10, nexty, 620, 0, 20);
               
                break;

            case HiscoreState:
                MenuBackground.render(g);
                Hiscore.render(g);

                if (scoreTable!=null)
                {
                    if (!scoreTable.isEmpty()){
                        LittleFont.drawText(g, "NAME", GameFont.LEFT, GameConstants.ScoreNameX, GameConstants.ScoresStartingy, 620, 10, 20);
                        LittleFont.drawText(g, "SCORE" , GameFont.LEFT, GameConstants.ScorePointsX, 354, GameConstants.ScoresStartingy, 10, 20);
                        nexty = LittleFont.drawText(g, "STAGE", GameFont.LEFT, GameConstants.ScoreStageX, GameConstants.ScoresStartingy, 620, 10, 20);

                        for(int i = 0; i < scoreTable.size() && i < 5; i++)
                        {
                            ScoreTracker score = scoreTable.get(i);
                            LittleFont.drawText(g, score.getName(), GameFont.LEFT, GameConstants.ScoreNameX, nexty + 10, 620, 10, 20);
                            LittleFont.drawText(g, String.valueOf(score._score) , GameFont.LEFT, GameConstants.ScorePointsX, nexty + 10, 620, 10, 20);
                            nexty = LittleFont.drawText(g, (score.getFinalized()? "+":"") + String.valueOf(score._stage), GameFont.LEFT, GameConstants.ScoreStageX, nexty + 10, 620, 10, 20);
                        }//for
                    }//if
                }//if

                break;
        }//switch
    }

    private void GameProcessMenu(MenuState state) {
        switch(state)
        {
            case Play:
                UnloadMenu();
                InGameManager = new GameManager(this);
                InGameManager.LoadGame();
                gameState = GameState.GamePlayingState;
                break;
            case HowTo:
                UnloadMenu();
                LoadHowTo();
                gameState = GameState.HowToState;
                break;
            case Credits:
                UnloadMenu();
                gameState = GameState.CreditsState;
                break;
            case Hiscore:
                UnloadMenu();
                LoadScore();
                gameState = GameState.HiscoreState;
                break;
        }//switch
    }

    private void LoadHowTo() {
        
        if(HowToFirstBackground == null)
        {
            HowToFirstBackground = new ImageBackground(getImage(GameConstants.FirstHowToBackground));
        }//if
        if (HowToSecondBackground == null)
        {
            HowToSecondBackground = new ImageBackground(getImage(GameConstants.SecondHowToBackground));
        }//if
        if (HowToThirdBackground == null)
        {
            HowToThirdBackground = new ImageBackground(getImage(GameConstants.ThirdHowToBackground));
        }//if
        
        HowToCurrent = HowToFirstBackground;
        howTo = HowToState.FirstScreen;
    }

    private void LoadMenu()
    {
        MenuGroup = new SpriteGroup("Menu Group");
        MenuField = new PlayField(MenuBackground);
        //MenuGroup.setBackground(MenuBackground);
        MenuPlaySheet = getImages(GameConstants.MenuItemPlay, 2, 1);
        MenuPlaySprite = new AnimatedSprite(MenuPlaySheet, GameConstants.MenuItemStartX,
                GameConstants.MenuItemStartY);
        MenuGroup.add(MenuPlaySprite);
        MenuHowToSheet = getImages(GameConstants.MenuItemHowTo, 2, 1);
        MenuHowToSprite = new AnimatedSprite(MenuHowToSheet,
                GameConstants.MenuItemStartX,
                MenuPlaySprite.getY() + MenuPlaySprite.getHeight());
        MenuGroup.add(MenuHowToSprite);
        MenuCreditsSheet = getImages(GameConstants.MenuItemCredits, 2, 1);
        MenuCreditsSprite = new AnimatedSprite(MenuCreditsSheet,
                GameConstants.MenuItemStartX,
                MenuHowToSprite.getY() + MenuHowToSprite.getHeight());
        MenuGroup.add(MenuCreditsSprite);
        MenuHiscoreSheet = getImages(GameConstants.MenuItemHiscore, 2, 1);
        MenuHiscoreSprite = new AnimatedSprite(MenuHiscoreSheet,
                GameConstants.MenuItemStartX,
                MenuCreditsSprite.getY() + MenuCreditsSprite.getHeight());
        MenuGroup.add(MenuHiscoreSprite);
        MenuField.addGroup(MenuGroup);

        //Menu state must be set before starting game
        gameMenu = new Menu(MenuState.Play);

        MenuGroup.setActive(true);
    }

    private void UnloadMenu()
    {
        oldState = GameState.MenuState;
        MenuGroup.setActive(false);
    }

    /**
     *
     * @return
     */
    private BufferedImage[] LoadBigFontSheet() {
        BufferedImage[] spritesheet = new BufferedImage[94];

        for (int i = 0; i < 94; i++)
        {
            spritesheet[i] = getImage("resources/fonts/big/fonte_grande_" + (i) + ".png", true);
        }

        return spritesheet;
    }

    /**
     *
     * @return
     */
    private BufferedImage[] LoadSmallFontSheet() {
        BufferedImage[] spritesheet = new BufferedImage[94];

        for (int i = 0; i < 94; i++)
        {
            spritesheet[i] = getImage("resources/fonts/small/fonte_pequena_" + (i) + ".png", true);
        }

        return spritesheet;
    }

    /**
     * CheckScore opens hiscore file and returns a ScoreTracker list sorted
     * @return Sorted scores list
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private List<ScoreTracker> CheckScore() throws IOException, ClassNotFoundException, URISyntaxException
    {
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
                    scores.add((ScoreTracker) input.readObject());
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
                if (input != null)
                    input.close();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GameManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            Collections.sort(scores);
            return scores;
        }
    }

    /**
     *
     */
    private void LoadScore()
    {
        try {
            scoreTable = CheckScore();
        } catch (IOException ex) {
            Logger.getLogger(LixoGame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LixoGame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(LixoGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main (String[] Args)
    {
        GameLoader game = new GameLoader();

        //Setup the game, instancing Game derived class, passing a screen
        //Dimension and wheter it is fullscreen or not
        game.setup(new LixoGame(), new Dimension(800, 600),
                GameConstants.GameFullscreenFalse);
        //Starts the game
        game.start();
    }

    public void ChangeSoundState() {
        sound = sound?false:true;
        if(!sound)
            soundState = GameConstants.Off;
        else
            soundState = GameConstants.On;
    }
    
    public void ChangeMusicState(){
        music = music?false:true;
        if (!music)
        {
            bsMusic.stopAll();
            musicState = GameConstants.Off;
        }
        else
        {
            if(this.gameState == GameState.GamePlayingState)
                bsMusic.play(GameConstants.GameInsideMusicPath, BaseAudio.SINGLE_REPLAY);
            else
                bsMusic.play(GameConstants.GameMenuMusicPath, BaseAudio.SINGLE_REPLAY);

            musicState = GameConstants.On;
        }
    }
    
    public boolean GetSoundState()
    {
        return sound;
    }
    
    public boolean GetMusicState()
    {
        return music;
    }

    void RobotRemover(Robot aThis) {
        InGameManager.RemoveRobotFromCollision(aThis);
    }

    // Uncomment this line for GTGE logo and removing FPS
    { distribute = true; }

}
