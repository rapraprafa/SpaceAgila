package com.rafa.spaceagila.tools;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rafa.spaceagila.SpaceAgila;
import com.rafa.spaceagila.screens.MainGameScreen;

public class ScrollingBackground {

    public static int DEFAULT_SPEED = 200;
    public static int ACCELERATION = 200;
    public static int GOAL_REACH_ACCELERATION = 300;

    Texture image;
    float y1 = 0, y2;
    public int speed, goalSpeed;
    float imageScale;
    boolean speedFixed;

    public ScrollingBackground() {
        image = new Texture("stars_background.png");
        y2 = image.getHeight();
        imageScale = 0;
        goalSpeed = DEFAULT_SPEED;
        speedFixed = false;
    }

    public void updateAndRender(float deltaTime, SpriteBatch batch) {
        //speed adjustment to reach goal
        if (speed < goalSpeed) {
            speed += GOAL_REACH_ACCELERATION * deltaTime;
            if (speed > goalSpeed) {
                speed = goalSpeed;
            }
        } else if (speed > goalSpeed) {
            speed -= GOAL_REACH_ACCELERATION * deltaTime;
            if (speed < goalSpeed) {
                speed = goalSpeed;
            }

        }
        if (!speedFixed && MainGameScreen.y > SpaceAgila.HEIGHT_DESKTOP) {
            speed += ACCELERATION * deltaTime;
        }

        y1 -= speed * deltaTime;
        y2 -= speed * deltaTime;

        //if image reached the bottom of screen and is not visible, put it back on top
        if (y1 + image.getHeight() * imageScale <= 0) {
            y1 = y2 + image.getHeight() * imageScale;
        }
        if (y2 + image.getHeight() * imageScale <= 0) {
            y2 = y1 + image.getHeight() * imageScale;
        }

        batch.draw(image, 0, y1, SpaceAgila.WIDTH_DESKTOP, image.getHeight() * imageScale);
        batch.draw(image, 0, y2, SpaceAgila.WIDTH_DESKTOP, image.getHeight() * imageScale);
    }

    public void resize(int width, int height) {
        imageScale = width / image.getWidth();
    }

    public void setSpeed(int goalSpeed) {
        this.goalSpeed = goalSpeed;
    }

    public void setSpeedFixed(boolean speedFixed) {
        this.speedFixed = speedFixed;
    }
}
