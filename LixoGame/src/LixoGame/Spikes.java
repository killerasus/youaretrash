
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package LixoGame;

import java.awt.image.BufferedImage;

/**
 *
 * @author Bruno
 */
public class Spikes extends SceneObject {

    double velocity = GameConstants.SpikesVelocity;

    public Spikes(BufferedImage[] images, double x, double y, Player player)
    {
        super (images, x, y, player);
    }

    @Override
    public void OnCollision() {
        //System.out.println("Spikes");
        if (!playerRef.isInvincible())
        {
            //System.out.println("Reducing Bananixo health to " + (playerRef.getHealth() - 1));
            playerRef.setHealth(playerRef.getHealth() - 1);
        }//if
   }

//    @Override
//    public void update(long elapsedTime)
//    {
//        if (this.getY() > GameConstants.AssetY)
//            velocity *= -1;
//        else if (this.getY() < GameConstants.AssetY - this.getHeight())
//            velocity *= -1;
//
//        this.moveY(velocity);
//    }

}
