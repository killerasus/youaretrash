/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package LixoGame;

import com.golden.gamedev.object.AnimatedSprite;
import java.awt.image.BufferedImage;

/**
 *
 * @author Bruno
 */
public abstract class SceneObject extends AnimatedSprite{
    protected Player playerRef;

    public SceneObject()
    {
        super();
        playerRef = null;
    }


    public SceneObject(BufferedImage[] images, double x, double y, Player player)
    {
       super(images,x,y);
       playerRef = player;
    }

    public abstract void OnCollision();
}
