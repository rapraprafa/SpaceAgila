package com.rafa.gokudodge.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.rafa.gokudodge.GokuDodge;
import com.rafa.gokudodge.entities.Asteroid;
import com.rafa.gokudodge.tools.CollisionRect;

import java.util.ArrayList;
import java.util.Random;

public class MainGameScreen implements Screen {

    public static final float SPEED = 500;

    public static final float SHIP_ANIMATION_SPEED = 0.5f;
    public static final int SHIP_WIDTH_PIXEL = 32 / 2;
    public static final int SHIP_HEIGHT_PIXEL = 32 / 2;
    public static final int SHIP_WIDTH = SHIP_WIDTH_PIXEL * 3;
    public static final int SHIP_HEIGHT = SHIP_HEIGHT_PIXEL * 3;

    public static final float MIN_ASTEROID_SPAWN_TIMER = 0.2f;
    public static final float MAX_ASTEROID_SPAWN_TIMER = 0.6f;



    Animation[] rolls;

    float x;
    float y;
    int roll;
    float stateTime;
    float asteroidSpawnTimer;
    Music ingamemusic;

    Random random;

    Vector2 touch;
    GokuDodge game;


    ArrayList<Asteroid> asteroids;

    Texture blank;


    CollisionRect playerRect;

    float health = 1;//0 = dead, 1 = alive

    public MainGameScreen(GokuDodge game) {
        this.game = game;

        y = 15;
        x = GokuDodge.WIDTH_DESKTOP / 2 - SHIP_WIDTH / 2;

        blank = new Texture("white.png");

        asteroids = new ArrayList<Asteroid>();

        playerRect = new CollisionRect(0, 0, SHIP_WIDTH, SHIP_HEIGHT);

        random = new Random();
        asteroidSpawnTimer = random.nextFloat() * (MAX_ASTEROID_SPAWN_TIMER - MIN_ASTEROID_SPAWN_TIMER) + MIN_ASTEROID_SPAWN_TIMER;

        roll = 1;
        rolls = new Animation[4];

        TextureRegion[][] rollSpriteSheet = TextureRegion.split(new Texture("Lightning.png"), SHIP_WIDTH_PIXEL, SHIP_HEIGHT_PIXEL);
        rolls[roll] = new Animation(SHIP_ANIMATION_SPEED, rollSpriteSheet[0]);

        ingamemusic = Gdx.audio.newMusic(Gdx.files.internal("ingamemusicfinal.mp3"));

        ingamemusic.play();
        ingamemusic.setLooping(true);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= SPEED * Gdx.graphics.getDeltaTime();

            if (x < 0) {
                x = 0;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += SPEED * Gdx.graphics.getDeltaTime();

            if (x + SHIP_WIDTH > GokuDodge.WIDTH_DESKTOP) {
                x = GokuDodge.WIDTH_DESKTOP - SHIP_WIDTH;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            y += SPEED * Gdx.graphics.getDeltaTime();

            if (y + SHIP_HEIGHT > GokuDodge.HEIGHT_DESKTOP) {
                y = GokuDodge.HEIGHT_DESKTOP - SHIP_HEIGHT;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            y -= SPEED * Gdx.graphics.getDeltaTime();

            if (y < 0) {
                y = 0;
            }
        }

        if (Gdx.input.isTouched()) {

            touch = new Vector2(game.cam.getInputInGameWorld().x, game.cam.getInputInGameWorld().y);

            //clicked on sprite
            // do something that vanish the object clicked
            //if(touch.x < GokuDodge.WIDTH_DESKTOP/2 - SHIP_WIDTH/2 + SHIP_WIDTH && touch.x > GokuDodge.WIDTH_DESKTOP/2 - SHIP_WIDTH/2 && GokuDodge.HEIGHT_DESKTOP - touch.y < GokuDodge.HEIGHT_DESKTOP/2 - SHIP_HEIGHT/2 + SHIP_HEIGHT && GokuDodge.HEIGHT_DESKTOP - touch.y > GokuDodge.HEIGHT_DESKTOP/2 - SHIP_HEIGHT/2) {
            //}

            x = touch.x - SHIP_HEIGHT / 2;
            y = (GokuDodge.HEIGHT_DESKTOP - touch.y) + SHIP_HEIGHT / 2;

            if (x < 0) {
                x = 0;
            }

            if (x + SHIP_WIDTH > GokuDodge.WIDTH_DESKTOP) {
                x = GokuDodge.WIDTH_DESKTOP - SHIP_WIDTH;
            }

            if (y + SHIP_HEIGHT > GokuDodge.HEIGHT_DESKTOP) {
                y = GokuDodge.HEIGHT_DESKTOP - SHIP_HEIGHT;
            }

            if (y < 0) {
                y = 0;
            }
        }

        //asteroid spawn code
        asteroidSpawnTimer -= delta;
        if (asteroidSpawnTimer <= 0) {
            asteroidSpawnTimer = random.nextFloat() * (MAX_ASTEROID_SPAWN_TIMER - MIN_ASTEROID_SPAWN_TIMER) + MIN_ASTEROID_SPAWN_TIMER;
            asteroids.add(new Asteroid(random.nextInt(GokuDodge.WIDTH_DESKTOP - Asteroid.WIDTH)));
        }

        //after player moves, update collision rect
        playerRect.move(x, y);

        //asteroid update code
        ArrayList<Asteroid> asteroidsToRemove = new ArrayList<Asteroid>();
        for (Asteroid asteroid : asteroids) {
            asteroid.update(delta);
            if (asteroid.remove) {
                asteroidsToRemove.add(asteroid);
            }
        }


        for (Asteroid asteroid : asteroids) {
            if (asteroid.getCollisionRect().collidesWith(playerRect)) {
                asteroidsToRemove.add(asteroid);
                health -= 1;

                if (health <= 0){
                    this.dispose();
                    game.setScreen(new GameOverScreen(game, 1));
                    return;
                }
            }
        }
        asteroids.removeAll(asteroidsToRemove);

        stateTime += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        TextureRegion currentFrame = (TextureRegion) rolls[roll].getKeyFrame(stateTime, true);
        game.batch.begin();

        game.scrollingBackground.updateAndRender(delta,game.batch);

        for (Asteroid asteroid : asteroids) {
            asteroid.render(game.batch);
        }

        game.batch.draw(blank, 0, 0, GokuDodge.WIDTH_DESKTOP * health, 5);
        game.batch.draw(currentFrame, x, y, SHIP_WIDTH, SHIP_HEIGHT);


        game.batch.end();


    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        ingamemusic.dispose();
    }
}
