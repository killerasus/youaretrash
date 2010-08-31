/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

/**
 *
 * @author Bruno baere
 */
public class Menu {
    private MenuState state;

    public Menu(MenuState state)
    {
        this.state = state;
    }

    public void rollUp()
    {
        switch(this.state)
        {
            case Play:
                break;
            case HowTo:
                this.state = MenuState.Play;
                break;
            case Credits:
                this.state = MenuState.HowTo;
                break;
            case Hiscore:
                this.state = MenuState.Credits;
                break;
        }
    }

    public void rollDown()
    {
        switch(this.state)
        {
            case Play:
                this.state = MenuState.HowTo;
                break;
            case HowTo:
                this.state = MenuState.Credits;
                break;
            case Credits:
                this.state = MenuState.Hiscore;
                break;
            case Hiscore:
                break;
        }
    }

    public MenuState getState() {
        return state;
    }

    public void setState(MenuState state)
    {
        this.state = state;
    }
}
