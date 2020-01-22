package com.rafa.spaceagila.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.rafa.spaceagila.SpaceAgila;
import com.rafa.spaceagila.tools.CollisionRect;

public class BonusLife {

    public static int SPEED = 300;
    public static final int WIDTH = 28;
    public static final int HEIGHT = 58;
    public static  float ASTEROID_ANIMATION_SPEED = 0.5f;
    private static Texture texture;
    private Texture img;
    private TextureRegion[] animationFrames;
    Animation animation;
    float elapsedTime;

    float x, y;
    CollisionRect rect;
    public boolean remove = false;

    public BonusLife(float x) {
        this.x = x;
        this.y = SpaceAgila.HEIGHT_DESKTOP;

        this.rect = new CollisionRect(x, y, WIDTH, HEIGHT);
        img = new Texture("bonuslife_gif.png");

        TextureRegion[][] tmpFrames = TextureRegion.split(img, WIDTH, HEIGHT);

        animationFrames = new TextureRegion[4];
        int index = 0;

        for (int i = 0; i < 2; i++){
            for(int j = 0; j < 2; j++) {
                animationFrames[index++] = tmpFrames[j][i];
            }
        }

        animation = new Animation(1f/4f,animationFrames);
    }

    public void update(float deltaTime) {
        y -= SPEED * deltaTime;
        if (y < -HEIGHT) {
            remove = true;
        }

        rect.move(x, y);
    }

    public void render(SpriteBatch batch) {
        elapsedTime += Gdx.graphics.getDeltaTime();
        batch.draw((TextureRegion)animation.getKeyFrame(elapsedTime,true), x, y);

    }

    public CollisionRect getCollisionRect() {
        return rect;
    }

}
