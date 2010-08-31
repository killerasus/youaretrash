package Game;

/**
 *
 * @author Bruno Baere
 */
public class GameConstants {

    //Game Strings
    static final public String GameWindowTitle = "YOU ARE TRASH!";
    static final public String GameWindowIconPath = "resources/images/character/andando1-2.png";
    static final public String GameInsideMusicPath = "resources/music/Kobalt_loop.mp3";
    static final public String GameMenuMusicPath = "resources/music/Press Start_menumusic.mp3";
    static final public String GameEndMusic = "resources/music/End Game.mp3";
    static final public String Credits = "resources/images/backgrounds/credits.png";
    static final public String EndGameImage = "resources/images/backgrounds/end_game.jpg";
    static final public String InvincibeImage = "resources/images/interface/invincible.png";
    static final public String BackgroundImageGame = "resources/images/backgrounds/TitleBackground800x600.gif";
    static final public String GameAssetStart = "resources/images/blocks/baseStart.png";
    static final public String GameAssetOrganic = "resources/images/blocks/bloco lixo comum 4-4.png";
    static final public String GameAssetPlastic = "resources/images/blocks/bloco plastico 4-4.png";
    static final public String GameAssetMetal = "resources/images/blocks/bloco metal 4-4.png";
    static final public String GameAssetGlass = "resources/images/blocks/bloco vidro 4-4.png";
    static final public String GameAssetMetalBonus = "resources/images/bonus/moeda.png";
    static final public String GameAssetMetalProblem = "resources/images/adversaries/metal - espinho.png";
    static final public String GameAssetPlasticBonus = "resources/images/bonus/arma.png";
    static final public String GameAssetPlasticProblem = "resources/images/adversaries/plastico - inimigo sritesheet_Final.png";
    static final public String GameAssetOrganicBonus = "resources/images/bonus/maca.png";
    static final public String GameAssetOrganicProblem = "resources/images/adversaries/lixo organico - monte de lixo.png";
    static final public String GameAssetGlassBonus = "resources/images/bonus/garrafa.png";
    static final public String GameAssetGlassProblem = "resources/images/adversaries/vidro - barreira de vidro spritesheet.png";
    static final public String GameAssetGlassBroken = "resources/images/adversaries/vidro - quebrado spritesheet.png";
    static final public String PauseImage = "resources/images/interface/paused.png";
    static final public String GameFontName = "Fonte";
    static final public String ComicFont = "resources/fonts/Comic_Andy.ttf";
    static final public float ComicFontSize = (float) 100.0;
    static final public String CartoonFont = "resources/fonts/BD_Cartoon_Shout.ttf";
    static final public float CartoonFontSize = (float) 15.0;
    static final public String GameFontPath = "resources/images/interface/fonte_com_espaco.png";
    static final public String GameFontPath2 = "resources/images/interface/fonte_com_espaco.png";
    static final public String Block = "resources/images/blocks/blocofinal.png";
    static final public String GameDoor = "resources/images/blocks/porta.png";
    static final public String BulletImage = "resources/images/bonus/tiro.png";
    static final public String LifeSprite = "resources/images/interface/vida.png";
    static final public String RobotExplosion = "resources/images/adversaries/boom.png";
    static final public String HealthSprite = "resources/images/interface/saude.png";
    static final public String ClockString = "resources/images/interface/reloginho.png";
    static final public String GoodResult = "resources/images/interface/good.png";
    static final public String BadResult = "resources/images/interface/bad.png";
    static final public String PerfectBonus = "resources/images/interface/bonus.png";
    static final public String Match = "resources/images/interface/match the colors.png";
    static final public String GameramaImage = "resources/images/interface/gameramalogo.png";
    static final public String HiscoreFileString = "hiscore.dat";
    static final public String LivesString = "Lives: ";
    static final public String HealthString = "Health: ";
    static final public String AmmoString = "Ammo: ";
    static final public String Screenshot = "YAT_screen_";
    static final public int EndGameTimer = 13000;
    static final public int EndStage = 42;

    //UI Elements
    static final public int UIElementsStartX = 20;
    static final public int UIElementsStartY = 20;
    static final public int InvincibleX = 225;
    static final public int InvincibleY = 84;
    static final public double ClockPositionX = 340;
    static final public double ClockPositionY = 20;
    static final public int ClockTimer = 10000;
    static final public int GameOverTimer = 4200;
    static final public int CutsceneTimer = 4000;
    static final public int UIRightElement = 420;
    static final public int MatchY = 80;
    static final public int MatchX = 260;
    static final public int ScoresStartingy = 354;
    static final public int ScoreNameX = 30;
    static final public int ScorePointsX = 400;
    static final public int ScoreStageX = 600;
    static final public int HiscoreX = 220;
    static final public int HiscoreY = 280;

    //Asset constants
    static final public double AssetStartX = 0.0;
    static final public double AssetY = 486.0;
    static final public double AssetBlockSize = 150.0;
    static final public double AssetOneX = 105.0;
    static final public double AssetTwoX = AssetOneX + AssetBlockSize;
    static final public double AssetThreeX = AssetTwoX + AssetBlockSize;
    static final public double AssetFourX = AssetThreeX + AssetBlockSize;
    static final public double MaxVelocity = 10.0;
    static final public int CoinValue = 100;
    static final public int RobotValue = 200;
    static final public int Perfect = 500;
    static final int GunPoints = 50;
    static final int GlassWallHealth = 2;
    static final int RobotHealth = 3;
    static final int BulletDamage = 1;
    static final double BulletSpeed = 2.0;
    static final double SpikesVelocity = 0.5;
    static final public double InitialBlockVelocity = 1.4;
    static final public double BlockAcceleration = 0.15;

    //Menu Items
    static final public String BackgroundImageTitle = "resources/images/openingscreen/fundo-inicio.png";
    static final public String MenuItemPlay = "resources/images/openingscreen/play.png";
    static final public String MenuItemHowTo = "resources/images/openingscreen/how-to.png";
    static final public String MenuItemCredits = "resources/images/openingscreen/credits.png";
    static final public String MenuItemHiscore =  "resources/images/openingscreen/high-score.png";
    static final public String VisionLabImage = "resources/images/interface/vl_logo.gif";
    static final public String CTSGSImage = "resources/images/interface/cts game studies.png";
    static final public String Hiscore = "resources/images/interface/high-score.png";
    static final public double MenuItemStartX = 260;
    static final public double MenuItemStartY = 338;
    static final public int SelectedFrame = 0;
    static final public int UnselectedFrame = 1;
    static final public String Off = "OFF";
    static final public String On = "ON";
    static final public String Sound = "Sound";
    static final public String Music = "Music";

    //HowTo Backgrounds
    static final public String FirstHowToBackground = "resources/images/backgrounds/step0.png";
    static final public String SecondHowToBackground = "resources/images/backgrounds/step1.png";
    static final public String ThirdHowToBackground = "resources/images/backgrounds/step2.png";

    //Booleans
    static final public boolean GameFullscreenTrue = true;
    static final public boolean GameFullscreenFalse = false;

    //Educative Images
    static final public String GameOverScreen = "resources/images/backgrounds/game_over.jpg";
    static final public String CutsceneFile = "resources/images/backgrounds/level_cutscene";
    static int Cutscenes = 6; //Number of cutscenes
    static final public int CutsceneStageModulo = 5;
    static int NumberofBlocks = 4;
    static int BeepThreshold = 3;
}
