package Game;

import com.golden.gamedev.object.AnimatedSprite;
import com.golden.gamedev.object.Sprite;
import java.awt.image.BufferedImage;

/**
 *
 * @author Bruno
 */
public class Bullet extends AnimatedSprite {

    private int damage;
    AnimatedSprite creator;
    LixoGame game;

    public Bullet (BufferedImage[] image, double x, double y, int damage, double speedX, AnimatedSprite creator, LixoGame gameRef)
    {
        super( image, x, y);
        this.damage = damage;
        this.setSpeed(speedX, 0);
        this.creator = creator;
        this.game = gameRef;
    }

    public void OnCollision (Sprite spr)
    {
        if (spr instanceof GlassWall)
        {
            if(creator instanceof Player )
            {
                //System.out.println("And it collided with GlassWall");
                ((GlassWall)spr).OnDamage(damage);
                ((Player)creator).resetBulletTimer();
            }
            else
            {
                if (creator instanceof Robot)
                {
                    ((Robot)creator).resetBulletTimer();
                }
                else
                {
                    System.out.println("So, was there anyone who shot?");
                }//if
            }//if

            this.setActive(false);
        }
        else
        {
            if (spr instanceof Robot)
            {
                if (creator instanceof Robot)
                {
                    ((Robot)creator).resetBulletTimer();
                }
                else
                {
                    if (creator instanceof Player)
                    {
                        ((Robot)spr).OnHit(damage);
                        ((Player)creator).resetBulletTimer();
                    }
                    else
                    {
                        System.out.println("So, was there anyone who shot?");
                    }//if
                }//if

                this.setActive(false);

            }
            else
            {
                if(spr instanceof Spikes)
                {
                    if (creator instanceof Robot)
                    {
                        ((Robot)creator).resetBulletTimer();
                    }
                    else
                    {
                        if (creator instanceof Player)
                        {
                            ((Player)creator).resetBulletTimer();
                        }
                        else
                        {
                            System.out.println("So, was there anyone who shot?");
                        }//if

                    }//if

                    this.setActive(false);

                }
                else
                {
                    if (spr instanceof Player)
                    {
                        if (creator instanceof Robot)
                        {
                            ((Player)spr).setHealth(((Player)spr).getHealth() - damage);
                            ((Robot)creator).resetBulletTimer();

                        }
                        else
                        {
                            if (creator instanceof Player)
                            {
                                ((Player)creator).resetBulletTimer();
                            }
                            else
                            {
                                System.out.println("So, was there anyone who shot?");
                            }//if

                        }//if

                        this.setActive(false);

                    }//if
                }//if
            }//if
        }//if
    }

    @Override
    public void update(long elapsedTime)
    {
       super.update(elapsedTime);

       if (!this.isOnScreen())
       {
           this.setActive(false);

           if(creator instanceof Player)
           {
               ((Player)creator).resetBulletTimer();
           }
           else if (creator instanceof Robot)
           {
               ((Robot)creator).resetBulletTimer();
           }
       }
    }

}
