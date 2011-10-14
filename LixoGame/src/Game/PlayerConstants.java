/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

/**
 *
 * @author Bruno Baere
 */
public class PlayerConstants {

    //static final public String PlayerSpriteSheet = "resources/images/character/sprite_ajeitadinho.png";
    static final public int PlayerSpriteSheetColumns = 18;
    static final public int PlayerSpriteSheetLines = 1;
    static final public int StartingLives = 1;
    static final public int StartingHealth = 3;
    static final public int MaxLife = 3;
    static final public int MaxHealth = 3;
    static final public int StartingAmmo = 0;
    static final public int AmmoMax = 30;
    static final public int DirectionRight = 1;
    static final public int DirectionLeft = 0;
    static final public int InvincibilityTimer = 800; //In miliseconds
    static final public int InvincibilityPotionTimer = 3000; //In miliseconds
    static final public int MudTimer = 500;
    static final public int BulletTimer = 50;
    static final public double MudVelocity = 0.5;
    static final public boolean StartWithGun = false;
    static final public double PlayerDeltaX = 2.8;
    static final public double PlayerDeltaY = 2;
    static final public double Gravity = 0.5;
    static final public double PlayerStartX = 0.0;
    static final public double PlayerStartY = 0.0;
    static final public double FreeFallVelocity = -12.0;

    static final public int[] JumpStance = {4};
    static final public int[] JumpGunStance = {17};

    //Animation arrays
    static final public int[] WalkAnimation = {0,1};
    static final public int[] JumpAnimation = {2,3,4,3};
    static final public int[] HitAnimation = {5,6};
    static final public int[] DyingAnimation = {10,11,12};
    static final public int[] WalkGunAnimation = {13,14};
    static final public int[] JumpGunAnimation = {15,16,17,16};
    static final public int[] DeathAnimationRight = {10};
    static final public int[] DeathAnimationLeft = {18};
    
    static int SpriteSheetImages = 19;

}
