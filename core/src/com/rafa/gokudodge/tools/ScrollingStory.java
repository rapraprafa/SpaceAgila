package com.rafa.gokudodge.tools;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rafa.gokudodge.GokuDodge;

public class ScrollingStory {

    public static final int DEFAULT_SPEED = 150;
    public static final int ACCELERATION = 200;
    public static final int GOAL_REACH_ACCELERATION = 200;

    Texture image;
    float y1 = 0, y2;
    int speed, goalSpeed;
    float imageScale;
    boolean speedFixed;

    public ScrollingStory() {
        image = new Texture("story.png");

        y2 = image.getHeight();
        imageScale = 0;
        goalSpeed=DEFAULT_SPEED;
        speedFixed=true;
    }

    public void updateAndRender(float deltaTime, SpriteBatch batch){
        //speed adjustment to reach goal
        if (speed<goalSpeed){
            speed+=GOAL_REACH_ACCELERATION*deltaTime;
            if(speed>goalSpeed){
                speed=goalSpeed;
            }
        } else if (speed>goalSpeed){
            speed-=GOAL_REACH_ACCELERATION*deltaTime;
            if(speed<goalSpeed){
                speed=goalSpeed;
            }
        }

        if (!speedFixed){
            speed+=ACCELERATION*deltaTime;
        }

//        while (y1 != GokuDodge.HEIGHT_DESKTOP){

            y1 += speed*deltaTime;
            y2 += speed*deltaTime;
//        }

//        if image reached the bottom of screen and is not visible, put it back on top


        if (y1-image.getHeight()*imageScale>=0){
            y1=y2-image.getHeight()*imageScale;
        }
        if (y2-image.getHeight()*imageScale>=0){
            y2=y1-image.getHeight()*imageScale;
        }

//        if(y1 != 651){
//            y1 = GokuDodge.HEIGHT_DESKTOP*2;
//        }

        batch.draw(image, 0, y1, GokuDodge.WIDTH_DESKTOP, image.getHeight()*imageScale);
        batch.draw(image, 0, y2, GokuDodge.WIDTH_DESKTOP, image.getHeight()*imageScale);
    }

    public void resize (int width, int height){
        imageScale = width/image.getWidth();
    }

    public void setSpeed(int goalSpeed){
        this.goalSpeed=goalSpeed;
    }

    public void setSpeedFixed(boolean speedFixed){
        this.speedFixed=speedFixed;
    }
}
