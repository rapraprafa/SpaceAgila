package com.rafa.gokudodge.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rafa.gokudodge.GokuDodge;
import com.rafa.gokudodge.tools.CollisionRect;

public class Comet {

    public static int SPEED = 400;
    public static final int WIDTH = 32;
    public static final int HEIGHT = 32;
    private static Texture texture;

    float x, y;
    CollisionRect rect;
    public boolean remove = false;

    public Comet(float x) {
        this.x = x;
        this.y = GokuDodge.HEIGHT_DESKTOP;

        this.rect = new CollisionRect(x, y, WIDTH, HEIGHT);
        if (texture == null) {
            texture = new Texture("comet_notgif.png");
        }
    }

    public void update(float deltaTime) {
        y -= (SPEED * deltaTime) + (deltaTime/4);
        x -= SPEED * deltaTime - (deltaTime * 2);

        if (x < -WIDTH) {
            remove = true;
        }
        if (y < -HEIGHT) {
            remove = true;
        }

        rect.move(x, y);
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public CollisionRect getCollisionRect() {
        return rect;
    }

}
