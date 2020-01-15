package com.rafa.gokudodge.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
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

import javax.swing.text.View;

public class MainGameScreen implements Screen, ApplicationListener, InputProcessor{

    public static final float SPEED = 500;

    public static final float SHIP_ANIMATION_SPEED = 0.5f;
    public static final int SHIP_WIDTH_PIXEL = 32 / 2;
    public static final int SHIP_HEIGHT_PIXEL = 32 / 2;
    public static final int SHIP_WIDTH = SHIP_WIDTH_PIXEL * 3;
    public static final int SHIP_HEIGHT = SHIP_HEIGHT_PIXEL * 3;

    public static final float MIN_ASTEROID_SPAWN_TIMER_LEVEL1 = 0.5f;
    public static final float MAX_ASTEROID_SPAWN_TIMER_LEVEL1 = 1f;

    public static final float MIN_ASTEROID_SPAWN_TIMER_LEVEL2 = 0.3f;
    public static final float MAX_ASTEROID_SPAWN_TIMER_LEVEL2 = 0.7f;

    Animation[] rolls;

    float x;
    float y;
    int roll;
    float stateTime;
    float asteroidSpawnTimer;
    static boolean paused;
    boolean dragging;


    Music ingamemusic;

    Random random;

    Vector2 touch;
    GokuDodge game;


    ArrayList<Asteroid> asteroids;

    Texture blank;
    private Texture level1, level2;
    private Texture playingame, pauseingame, pausedbanner, mainMenuBanner, resume;
    private Texture soundButtonPlay, soundButtonMute;

    CollisionRect playerRect;

    float health = 1;//0 = dead, 1 = alive

    State state;

    public MainGameScreen(GokuDodge game) {
        this.game = game;

        y = 15;
        x = GokuDodge.WIDTH_DESKTOP / 2 - SHIP_WIDTH / 2;

        blank = new Texture("white.png");

        asteroids = new ArrayList<Asteroid>();

        playerRect = new CollisionRect(0, 0, SHIP_WIDTH, SHIP_HEIGHT);

        random = new Random();
        asteroidSpawnTimer = random.nextFloat() * (MAX_ASTEROID_SPAWN_TIMER_LEVEL1 - MIN_ASTEROID_SPAWN_TIMER_LEVEL1) + MIN_ASTEROID_SPAWN_TIMER_LEVEL1;

        roll = 1;
        rolls = new Animation[4];

        TextureRegion[][] rollSpriteSheet = TextureRegion.split(new Texture("Lightning.png"), SHIP_WIDTH_PIXEL, SHIP_HEIGHT_PIXEL);
        rolls[roll] = new Animation(SHIP_ANIMATION_SPEED, rollSpriteSheet[0]);

        playingame = new Texture("playingame.png");
        pauseingame = new Texture("pauseingame.png");

        pausedbanner = new Texture("pausedbanner.png");
        mainMenuBanner = new Texture("main-menu.png");
        resume = new Texture("resume.png");

        level1 = new Texture("level1.png");
        level2 = new Texture("level2.png");


        ingamemusic = Gdx.audio.newMusic(Gdx.files.internal("ingamemusicfinal.mp3"));
        ingamemusic.play();
        ingamemusic.setLooping(true);
        state = State.RUN;

        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        soundButtonPlay = new Texture("playsound.png");
        soundButtonMute = new Texture("mute.png");

    }

    public void setGameState(State s) {
        this.state = s;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        switch(state){
            case RUN:
                game.batch.begin();
                game.batch.draw(pauseingame, GokuDodge.WIDTH_DESKTOP - pauseingame.getWidth(), 20, pauseingame.getWidth(), pauseingame.getHeight());
                game.batch.end();
                if (ingamemusic.getPosition() > 0 && ingamemusic.getPosition() < 5) {
                    generalUpdateLevelIntro(delta, level1);
                } else if (ingamemusic.getPosition() > 5 && ingamemusic.getPosition() < 35) {
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL1, MAX_ASTEROID_SPAWN_TIMER_LEVEL1);
                } else if (ingamemusic.getPosition() > 35 && ingamemusic.getPosition() < 40) {
                    generalUpdateLevelIntro(delta, level2);
                } else if (ingamemusic.getPosition() > 40 && ingamemusic.getPosition() < 70) {
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL2, MAX_ASTEROID_SPAWN_TIMER_LEVEL2);
                } else {
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL2, MAX_ASTEROID_SPAWN_TIMER_LEVEL2);
                }
                break;
            case PAUSE:
                paused();
                break;
            default:
                break;
        }


    }

    public void paused(){
        int xTryAgain = GokuDodge.WIDTH_DESKTOP / 2 - GameOverScreen.BANNER_WIDTH / 2 + GameOverScreen.BANNER_WIDTH/4;
        int yTryAgain = GokuDodge.HEIGHT_DESKTOP - GameOverScreen.BANNER_HEIGHT - 250;

        ingamemusic.pause();
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();

        game.batch.draw(pausedbanner, GokuDodge.WIDTH_DESKTOP / 2 - pausedbanner.getWidth() / 2, 500, pausedbanner.getWidth(), pausedbanner.getHeight());
        game.batch.draw(resume, xTryAgain, yTryAgain, GameOverScreen.BANNER_WIDTH/2, GameOverScreen.BANNER_HEIGHT/2);
        game.batch.draw(mainMenuBanner, xTryAgain, yTryAgain - 100, GameOverScreen.BANNER_WIDTH/2, GameOverScreen.BANNER_HEIGHT/2);


        if (game.cam.getInputInGameWorld().x < xTryAgain + GameOverScreen.BANNER_WIDTH/2 && game.cam.getInputInGameWorld().x > xTryAgain && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < yTryAgain + GameOverScreen.BANNER_HEIGHT/2 && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > yTryAgain) {
            if (Gdx.input.justTouched()) {
                state = State.RUN;
                ingamemusic.play();
            }
        }

        if (game.cam.getInputInGameWorld().x < xTryAgain + GameOverScreen.BANNER_WIDTH/2 && game.cam.getInputInGameWorld().x > xTryAgain && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < yTryAgain - 100 + GameOverScreen.BANNER_HEIGHT/2 && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > yTryAgain - 100) {
            if (Gdx.input.justTouched()) {
                this.dispose();
                game.batch.end();
                game.setScreen(new MainMenuScreen(game));
                return;
            }
        }

        //Mute
        if(ingamemusic.getVolume() == 1f) {
            game.batch.draw(soundButtonPlay, GokuDodge.WIDTH_DESKTOP / 16 - soundButtonPlay.getWidth() / 2, 20, soundButtonPlay.getWidth(), soundButtonPlay.getHeight());
            if (game.cam.getInputInGameWorld().x < GokuDodge.WIDTH_DESKTOP / 16 - soundButtonPlay.getWidth() / 2 + soundButtonPlay.getWidth() && game.cam.getInputInGameWorld().x > GokuDodge.WIDTH_DESKTOP / 16 - soundButtonPlay.getWidth() / 2 && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < 20 + soundButtonPlay.getHeight() && game.cam.getInputInGameWorld().y > 20) {
                if (Gdx.input.justTouched()) {
                    ingamemusic.setVolume(0f);
                    System.out.println("hi1");
                }
            }
        }
//            else {
//                if(Gdx.input.isTouched()) {
//                    game.batch.draw(transparent, GokuDodge.WIDTH_DESKTOP / 16 - soundButtonMute.getWidth() / 2, 50, soundButtonMute.getWidth(), soundButtonMute.getHeight());
//                    game.batch.draw(soundButtonPlay, GokuDodge.WIDTH_DESKTOP / 16 - soundButtonPlay.getWidth() / 2, 50, soundButtonPlay.getWidth(), soundButtonPlay.getHeight());
//                    music.setVolume(1f);
//                    System.out.println("hi2");
//                }
//            }


        //Unmute
        else if (ingamemusic.getVolume() == 0f) {
            game.batch.draw(soundButtonMute, GokuDodge.WIDTH_DESKTOP / 16 - soundButtonMute.getWidth() / 2, 20, soundButtonMute.getWidth(), soundButtonMute.getHeight());
            if (game.cam.getInputInGameWorld().x < GokuDodge.WIDTH_DESKTOP / 16 - soundButtonMute.getWidth() / 2 + soundButtonMute.getWidth() && game.cam.getInputInGameWorld().x > GokuDodge.WIDTH_DESKTOP / 16 - soundButtonMute.getWidth() / 2 && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < 20 + soundButtonMute.getHeight() && game.cam.getInputInGameWorld().y > 20) {
                if (Gdx.input.justTouched()) {
                    ingamemusic.setVolume(1f);
                    System.out.println("hi3");
                }
            }
        }

        game.batch.end();

    }


    public void generalUpdateLevelIntro(float delta, Texture texture) {
        if (game.cam.getInputInGameWorld().x < GokuDodge.WIDTH_DESKTOP - playingame.getWidth() + playingame.getWidth() && game.cam.getInputInGameWorld().x > GokuDodge.WIDTH_DESKTOP - playingame.getWidth() && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < 20 + playingame.getHeight() && game.cam.getInputInGameWorld().y > 20 || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.HOME)) {
//                paused = true;
                state = State.PAUSE;
                ingamemusic.pause();
            }
        }

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


        //clicked on sprite
        // do something that vanish the object clicked
        //if(touch.x < GokuDodge.WIDTH_DESKTOP/2 - SHIP_WIDTH/2 + SHIP_WIDTH && touch.x > GokuDodge.WIDTH_DESKTOP/2 - SHIP_WIDTH/2 && GokuDodge.HEIGHT_DESKTOP - touch.y < GokuDodge.HEIGHT_DESKTOP/2 - SHIP_HEIGHT/2 + SHIP_HEIGHT && GokuDodge.HEIGHT_DESKTOP - touch.y > GokuDodge.HEIGHT_DESKTOP/2 - SHIP_HEIGHT/2) {
        //}


        if (dragging) {
            x = game.cam.getInputInGameWorld().x - SHIP_WIDTH / 2;
            y = ((GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y) + SHIP_HEIGHT / 2);
        }


        if (game.cam.getInputInGameWorld().x < x + SHIP_WIDTH && game.cam.getInputInGameWorld().x > x && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < y + SHIP_HEIGHT && GokuDodge.HEIGHT_DESKTOP + game.cam.getInputInGameWorld().y > y) {
            if (Gdx.input.isTouched()) {
                x = game.cam.getInputInGameWorld().x - SHIP_WIDTH / 2;
                y = ((GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y) + SHIP_HEIGHT / 2);
                dragging = true;
            } else {
                dragging = false;
            }
        }


        if (x < 0) {
            x = 0;
        }

        if (x + SHIP_WIDTH > GokuDodge.WIDTH_DESKTOP) {
            x = GokuDodge.WIDTH_DESKTOP - SHIP_WIDTH;
        }

        if (y + SHIP_HEIGHT > GokuDodge.HEIGHT_DESKTOP / 2) {
            y = GokuDodge.HEIGHT_DESKTOP / 2 - SHIP_HEIGHT;
        }

        if (y < 0) {
            y = 0;
        }


        //asteroid spawn code

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

                if (health <= 0) {
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

        game.scrollingBackground.updateAndRender(delta, game.batch);

        for (Asteroid asteroid : asteroids) {
            asteroid.render(game.batch);
        }

        game.batch.draw(blank, 0, 0, GokuDodge.WIDTH_DESKTOP * health, 5);
        game.batch.draw(currentFrame, x, y, SHIP_WIDTH, SHIP_HEIGHT);
        game.batch.draw(pauseingame, GokuDodge.WIDTH_DESKTOP - pauseingame.getWidth(), 20, pauseingame.getWidth(), pauseingame.getHeight());

        game.batch.draw(texture, GokuDodge.WIDTH_DESKTOP / 2 - level1.getWidth() / 2, 300, level1.getWidth(), level1.getHeight());
        game.batch.end();
    }


    public void generalUpdateLevel1(float delta, float minAsteroidSpawnTimer, float maxAsteroidSpawnTimer) {
        if (game.cam.getInputInGameWorld().x < GokuDodge.WIDTH_DESKTOP - playingame.getWidth() + playingame.getWidth() && game.cam.getInputInGameWorld().x > GokuDodge.WIDTH_DESKTOP - playingame.getWidth() && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < 20 + playingame.getHeight() && game.cam.getInputInGameWorld().y > 20 || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.HOME)) {
//                paused = true;
                state = State.PAUSE;
                ingamemusic.pause();
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= SPEED * Gdx.graphics.getDeltaTime();

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


        touch = new Vector2(game.cam.getInputInGameWorld().x, game.cam.getInputInGameWorld().y);

        //clicked on sprite
        // do something that vanish the object clicked
        //if(touch.x < GokuDodge.WIDTH_DESKTOP/2 - SHIP_WIDTH/2 + SHIP_WIDTH && touch.x > GokuDodge.WIDTH_DESKTOP/2 - SHIP_WIDTH/2 && GokuDodge.HEIGHT_DESKTOP - touch.y < GokuDodge.HEIGHT_DESKTOP/2 - SHIP_HEIGHT/2 + SHIP_HEIGHT && GokuDodge.HEIGHT_DESKTOP - touch.y > GokuDodge.HEIGHT_DESKTOP/2 - SHIP_HEIGHT/2) {
        //}


        if (dragging) {
            x = game.cam.getInputInGameWorld().x - SHIP_WIDTH / 2;
            y = ((GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y) + SHIP_HEIGHT / 2);
        }


        if (game.cam.getInputInGameWorld().x < x + SHIP_WIDTH && game.cam.getInputInGameWorld().x > x && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < y + SHIP_HEIGHT && GokuDodge.HEIGHT_DESKTOP + game.cam.getInputInGameWorld().y > y) {
            if (Gdx.input.isTouched()) {
                x = game.cam.getInputInGameWorld().x - SHIP_WIDTH / 2;
                y = ((GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y) + SHIP_HEIGHT / 2);
                dragging = true;
            } else {
                dragging = false;
            }
        }


        if (x < 0) {
            x = 0;
        }

        if (x + SHIP_WIDTH > GokuDodge.WIDTH_DESKTOP) {
            x = GokuDodge.WIDTH_DESKTOP - SHIP_WIDTH;
        }

        if (y + SHIP_HEIGHT > GokuDodge.HEIGHT_DESKTOP / 2) {
            y = GokuDodge.HEIGHT_DESKTOP / 2 - SHIP_HEIGHT;
        }

        if (y < 0) {
            y = 0;
        }


        //asteroid spawn code
        asteroidSpawnTimer -= delta;
        if (asteroidSpawnTimer <= 0) {
            asteroidSpawnTimer = random.nextFloat() * (maxAsteroidSpawnTimer - minAsteroidSpawnTimer) + minAsteroidSpawnTimer;
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

                if (health <= 0) {
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

        game.scrollingBackground.updateAndRender(delta, game.batch);

        for (Asteroid asteroid : asteroids) {
            asteroid.render(game.batch);
        }

        game.batch.draw(blank, 0, 0, GokuDodge.WIDTH_DESKTOP * health, 5);
        game.batch.draw(currentFrame, x, y, SHIP_WIDTH, SHIP_HEIGHT);
        game.batch.draw(pauseingame, GokuDodge.WIDTH_DESKTOP - pauseingame.getWidth(), 20, pauseingame.getWidth(), pauseingame.getHeight());

        game.batch.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            if(state == State.RUN) {
                state = State.PAUSE;
                return true;
            }
            else if (state == State.PAUSE){
                game.setScreen(new MainMenuScreen(game));
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }


    public enum State {
        PAUSE,
        RUN
    }


    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {

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
