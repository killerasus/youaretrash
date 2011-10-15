package LixoGame;

import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.util.ImageUtil;
import java.awt.image.BufferedImage;

/**
 * This class represents a falling block object
 * @author Bruno
 */
public class Block extends Sprite {
    private Column column; //This represents where in the 4 columns the block is
    private Recyclables state; //This represents the state of the block (which face is pointing downwards)

    Block(Recyclables initState, Column initCol)
    {
        super();
        column = initCol;
        state = initState;
    }

    Block(BufferedImage block) {
        super(block);
        column = Column.First;
        state = Recyclables.Metal;
    }

    Block(BufferedImage block, Recyclables initState, Column initCol)
    {
        super(block);
        column = initCol;
        state = initState;
    }

    public void PushLeft()
    {
        this.moveX(-GameConstants.AssetBlockSize);
    }

    public void PushRight()
    {
        this.moveX(GameConstants.AssetBlockSize);
    }

    public void RotateCounterClockWise()
    {
        this.setImage(ImageUtil.rotate(this.getImage(), -90));
        switch(state)
        {
            case Metal:
                state = Recyclables.Plastic;
                break;
            case Glass:
                state = Recyclables.Metal;
                break;
            case Organic:
                state = Recyclables.Glass;
                break;
            case Plastic:
                state = Recyclables.Organic;
                break;
        }//switch
    }

    public void RotateClockWise()
    {
        this.setImage(ImageUtil.rotate(this.getImage(), 90));
        switch(state)
        {
            case Metal:
                state = Recyclables.Glass;
                break;
            case Glass:
                state = Recyclables.Organic;
                break;
            case Organic:
                state = Recyclables.Plastic;
                break;
            case Plastic:
                state = Recyclables.Metal;
                break;
        }//switch
    }

    public Recyclables GetState()
    {
        return state;
    }

    public void SetState(Recyclables newState)
    {
        state = newState;
    }

    public Column GetColumn()
    {
        return column;
    }

    public void SetColumn(Column newCol)
    {
        column = newCol;
    }

}