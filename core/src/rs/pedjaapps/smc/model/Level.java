package rs.pedjaapps.smc.model;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pedja on 1/31/14.
 */
public class Level
{
    private float width;
    private float height;
    private List<GameObject> gameObjects;
    private Vector3 spanPosition;//i think its a camera position
	private Background bg1;
	private Background bg2;
    private BackgroundColor bgColor;
    private Array<String> music;
	
	public static final String[] levels = {"data/"};

	public Level()
	{
		this.gameObjects = new ArrayList<GameObject>();
	}
	
	public void setBg1(Background bg1)
	{
		this.bg1 = bg1;
	}

	public Background getBg1()
	{
		return bg1;
	}

	public void setBg2(Background bg2)
	{
		this.bg2 = bg2;
	}

	public Background getBg2()
	{
		return bg2;
	}

    public float getWidth()
    {
        return width;
    }

    public void setWidth(float width)
    {
        this.width = width;
    }

    public float getHeight()
    {
        return height;
    }

    public void setHeight(float height)
    {
        this.height = height;
    }

    public List<GameObject> getGameObjects()
    {
        return gameObjects;
    }

    public void setGameObjects(List<GameObject> gameObjects)
    {
        this.gameObjects = gameObjects;
    }

    public Vector3 getSpanPosition()
    {
        return spanPosition;
    }

    public void setSpanPosition(Vector3 spanPosition)
    {
        this.spanPosition = spanPosition;
    }

    public BackgroundColor getBgColor()
    {
        return bgColor;
    }

    public void setBgColor(BackgroundColor bgColor)
    {
        this.bgColor = bgColor;
    }

    public Array<String> getMusic()
    {
        return music;
    }

    public void setMusic(Array<String> music)
    {
        this.music = music;
    }
}
