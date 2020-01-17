package com.rafa.gokudodge.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rafa.gokudodge.GokuDodge;
import com.rafa.gokudodge.tools.CollisionRect;

public class BonusLife{

    public static int SPEED = 500;
    public static final int WIDTH = 48;
    public static final int HEIGHT = 48;
    private static Texture texture;

    float x, y;
    CollisionRect rect;
    public boolean remove = false;

    public BonusLife(float x) {
        this.x = x;
        this.y = GokuDodge.HEIGHT_DESKTOP;

        this.rect = new CollisionRect(x, y, WIDTH, HEIGHT);
        if (texture == null) {
            texture = new Texture("bonuslife.png");
        }
    }

    public void update(float deltaTime) {
        y -= SPEED * deltaTime;
//        x -= SPEED * deltaTime;
        if (y < -HEIGHT) {
            remove = true;
        }
//        if (x < -WIDTH) {
//            remove = true;
//        }

        rect.move(x, y);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public CollisionRect getCollisionRect() {
        return rect;
    }

}