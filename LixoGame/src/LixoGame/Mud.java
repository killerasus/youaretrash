/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package LixoGame;

//import com.golden.gamedev.object.Timer;
import java.awt.image.BufferedImage;

/**
 *
 * @author Bruno
 */
public class Mud extends SceneObject {

    public Mud(BufferedImage[] images, double x, double y, Player player)
    {
        super (images, x, y, player);
    }

    @Override
    public void OnCollision() {
        //System.out.println("Yuck! Bananixo is in mud!");
        playerRef.setMudHindrance();
    }

}
