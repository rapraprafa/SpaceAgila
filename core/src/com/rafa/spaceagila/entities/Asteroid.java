package com.rafa.spaceagila.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.rafa.spaceagila.SpaceAgila;
import com.rafa.spaceagila.tools.CollisionRect;

public class Asteroid {

    public static int SPEED = 300;
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;
    public static  float ASTEROID_ANIMATION_SPEED = 0.5f;
    private static Texture texture;
    private Texture img;
    private TextureRegion[] animationFrames;
    Animation animation;
    float elapsedTime;

    float x, y;

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    CollisionRect rect;
    public boolean remove = false;

    public Asteroid(float x) {
        this.x = x;
        this.y = SpaceAgila.HEIGHT_DESKTOP;

        this.rect = new CollisionRect(x, y, WIDTH, HEIGHT);
        img = new Texture("asteroid.png");

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
//        TextureRegion currentFrame = (TextureRegion) rolls[roll].getKeyFrame(MainGameScreen.stateTime, true);
        batch.draw((TextureRegion)animation.getKeyFrame(elapsedTime,true), x, y);

    }

    public CollisionRect getCollisionRect() {
        return rect;
    }

}
