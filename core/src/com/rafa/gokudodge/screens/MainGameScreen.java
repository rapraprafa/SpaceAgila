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
import com.badlogic.gdx.math.Vector2;
import com.rafa.gokudodge.GokuDodge;
import com.rafa.gokudodge.entities.Asteroid;
import com.rafa.gokudodge.entities.Big_Asteroid;
import com.rafa.gokudodge.entities.BonusLife;
import com.rafa.gokudodge.entities.Comet;
import com.rafa.gokudodge.tools.CollisionRect;
import com.sun.org.apache.xpath.internal.operations.And;

import java.util.ArrayList;
import java.util.Random;


public class MainGameScreen implements Screen, ApplicationListener, InputProcessor{

    public static final float SPEED = 500;

    public static final float SHIP_ANIMATION_SPEED = 0.5f;
    public static final int SHIP_WIDTH_PIXEL = 32 / 2;
    public static final int SHIP_HEIGHT_PIXEL = 32 / 2;
    public static final int SHIP_WIDTH = SHIP_WIDTH_PIXEL * 3;
    public static final int SHIP_HEIGHT = SHIP_HEIGHT_PIXEL * 3;

    public static final float MIN_ASTEROID_SPAWN_TIMER_LEVEL = 0.5f;
    public static final float MAX_ASTEROID_SPAWN_TIMER_LEVEL = 1f;

    Animation[] rolls;

    float x;
    float y;
    int roll;
    float stateTime;
    float asteroidSpawnTimer;
    float bonuslife_SpawnTimer;

    float big_asteroidSpawnTimer;

    float comets_SpawnTimer;
    static boolean paused;
    boolean dragging;

    boolean adjustX, big_asteroid_enable, normal_asteroid_enable;
    boolean enable_comet;
    boolean enable_bonuslife;

    Music ingamemusic;

    Random random;

    Vector2 touch;
    GokuDodge game;


    ArrayList<Asteroid> asteroids;
    ArrayList<Big_Asteroid> big_asteroids;
    ArrayList<Comet> comets;
    ArrayList<BonusLife> bonuslifes;

    Texture blank;
    private Texture level1, level2, level3, level4, level5, level6, level7, level8, level9, level10, level11, level12, level13, level14, level15, final_level;
    private Texture playingame, pauseingame, pausedbanner, mainMenuBanner, resume;
    private Texture soundButtonPlay, soundButtonMute;

    CollisionRect playerRect;
    CollisionRect asteroidRect, cometRect;

    float health = 1f;//0 = dead, 1 = alive

    State state;

    public MainGameScreen(GokuDodge game) {
        this.game = game;

        y = 15;
        x = GokuDodge.WIDTH_DESKTOP / 2 - SHIP_WIDTH / 2;

        adjustX = false;
        normal_asteroid_enable = false;
        big_asteroid_enable = false;
        enable_comet = false;
        enable_bonuslife = false;
        blank = new Texture("white.png");

        asteroids = new ArrayList<>();
        big_asteroids = new ArrayList<>();
        comets = new ArrayList<>();
        bonuslifes = new ArrayList<>();

        playerRect = new CollisionRect(0, 0, SHIP_WIDTH, SHIP_HEIGHT);
        asteroidRect = new CollisionRect(0, 0, Asteroid.WIDTH, Asteroid.HEIGHT);
        cometRect = new CollisionRect(0, 0, Big_Asteroid.WIDTH, Big_Asteroid.HEIGHT);

        random = new Random();
        asteroidSpawnTimer = random.nextFloat() * (MAX_ASTEROID_SPAWN_TIMER_LEVEL - MIN_ASTEROID_SPAWN_TIMER_LEVEL) + MIN_ASTEROID_SPAWN_TIMER_LEVEL;

        roll = 1;
        rolls = new Animation[4];

        TextureRegion[][] rollSpriteSheet = TextureRegion.split(new Texture("Lightning-edit.png"), SHIP_WIDTH_PIXEL, SHIP_HEIGHT_PIXEL);
        rolls[roll] = new Animation(SHIP_ANIMATION_SPEED, rollSpriteSheet[0]);

        playingame = new Texture("playingame.png");
        pauseingame = new Texture("pauseingame.png");

        pausedbanner = new Texture("pausedbanner.png");
        mainMenuBanner = new Texture("main-menu.png");
        resume = new Texture("resume.png");

        level1 = new Texture("level1.png");
        level2 = new Texture("level2.png");
        level3 = new Texture("level3.png");
        level4 = new Texture("level4.png");
        level5 = new Texture("level5.png");
        level6 = new Texture("level6.png");
        level7 = new Texture("level7.png");
        level8 = new Texture("level8.png");
        level9 = new Texture("level9.png");


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
                //level1
                if (ingamemusic.getPosition() > 0 && ingamemusic.getPosition() < 5) {
                    generalUpdateLevelIntro(delta, level1);
                }
                else if (ingamemusic.getPosition() > 5 && ingamemusic.getPosition() < 35) {
                    normal_asteroid_enable = true;
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL, MAX_ASTEROID_SPAWN_TIMER_LEVEL, 0, 0);
                }

                //level2
                else if (ingamemusic.getPosition() > 35 && ingamemusic.getPosition() < 40) {
                    generalUpdateLevelIntro(delta, level2);
                }
                else if (ingamemusic.getPosition() > 40 && ingamemusic.getPosition() < 70) {
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0, 0);
                }

                //level3
                else if (ingamemusic.getPosition() > 70 && ingamemusic.getPosition() < 75){
                    generalUpdateLevelIntro(delta, level3);
                }
                else if (ingamemusic.getPosition() > 75 && ingamemusic.getPosition() < 105) {
                    Asteroid.SPEED = 400;
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0, 0);
                }

                //level4
                else if (ingamemusic.getPosition() > 105 && ingamemusic.getPosition() < 110){
                    adjustX = true;
                    generalUpdateLevelIntro(delta, level4);
                }
                else if (ingamemusic.getPosition() > 110 && ingamemusic.getPosition() < 140) {
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0, 0);
                }

                //level5
                else if (ingamemusic.getPosition() > 140 && ingamemusic.getPosition() < 145){
                    generalUpdateLevelIntro(delta, level5);
                }
                else if (ingamemusic.getPosition() > 145 && ingamemusic.getPosition() < 175) {
                    big_asteroid_enable = true;
                    Asteroid.SPEED = 300;
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0.5f, 1f);
                }

                //level6
                else if (ingamemusic.getPosition() > 175 && ingamemusic.getPosition() < 180){
                    generalUpdateLevelIntro(delta, level6);
                }
                else if (ingamemusic.getPosition() > 180 && ingamemusic.getPosition() < 210) {
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0.3f, 0.7f);
                }

                //level7
                else if (ingamemusic.getPosition() > 210 && ingamemusic.getPosition() < 215){
                    generalUpdateLevelIntro(delta, level7);
                }
                else if (ingamemusic.getPosition() > 215 && ingamemusic.getPosition() < 245) {
                    Big_Asteroid.SPEED = 400;
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0.3f, 0.7f);
                }

                //level8
                else if (ingamemusic.getPosition() > 245 && ingamemusic.getPosition() < 250){
                    generalUpdateLevelIntro(delta, level8);
                }
                else if (ingamemusic.getPosition() > 250 && ingamemusic.getPosition() < 260) {
                    enable_bonuslife = true;
                    big_asteroid_enable = false;
                    normal_asteroid_enable = false;
                    generalUpdateLevel1(delta, 0, 0, 0, 0);
                }

                //level9
                else if (ingamemusic.getPosition() > 260 && ingamemusic.getPosition() < 265){
                    generalUpdateLevelIntro(delta, level9);
                }
                else if (ingamemusic.getPosition() > 265 && ingamemusic.getPosition() < 295) {
                    enable_bonuslife = false;
                    enable_comet = true;
                    big_asteroid_enable = true;
                    normal_asteroid_enable = true;
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.35f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.35f, 0.3f, 0.7f);
                }

                else{
                    enable_bonuslife = false;
                }
                break;
            case PAUSE:
                paused();
                break;
            case DONOTHING:
                ;
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

        //after player moves, update collision rect
        playerRect.move(x, y);

        //asteroid update code
        if(normal_asteroid_enable) {
            ArrayList<Asteroid> asteroidsToRemove = new ArrayList<>();
            for (Asteroid asteroid : asteroids) {
                asteroid.update(delta);
                if (asteroid.remove) {
                    asteroidsToRemove.add(asteroid);
                }
            }
            //remove asteroids
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
                if (asteroid.getCollisionRect().collidesWith(cometRect)) {
                    asteroidsToRemove.add(asteroid);
                }
            }
            asteroids.removeAll(asteroidsToRemove);
        }
        if(big_asteroid_enable){
            //asteroid update code
            ArrayList<Big_Asteroid> big_asteroidsToRemove = new ArrayList<>();
            for (Big_Asteroid big_asteroid : big_asteroids) {
                big_asteroid.update(delta);
                if (big_asteroid.remove) {
                    big_asteroidsToRemove.add(big_asteroid);
                }
            }
            //remove asteroids
            for (Big_Asteroid big_asteroid : big_asteroids) {
                if (big_asteroid.getCollisionRect().collidesWith(playerRect)) {
                    big_asteroidsToRemove.add(big_asteroid);
                    health -= 0.5f;

                    if (health <= 0) {
                        this.dispose();
                        game.setScreen(new GameOverScreen(game, 1));
                        return;
                    }
                }
                if (big_asteroid.getCollisionRect().collidesWith(cometRect)) {
                    big_asteroidsToRemove.add(big_asteroid);
                }
            }
            big_asteroids.removeAll(big_asteroidsToRemove);
        }
        if(enable_comet){
            ArrayList<Comet> cometsToRemove= new ArrayList<>();
            for (Comet comet: comets) {
                comet.update(delta);
                if (comet.remove) {
                    cometsToRemove.add(comet);
                }
            }
            //remove asteroids
            for (Comet comet: comets) {
                if (comet.getCollisionRect().collidesWith(playerRect)) {
                    cometsToRemove.add(comet);
                    health -= 1f;

                    if (health <= 0) {
                        this.dispose();
                        game.setScreen(new GameOverScreen(game, 1));
                        return;
                    }
                }
            }
            comets.removeAll(cometsToRemove);
        }
        if(enable_bonuslife){
            //asteroid update code
            ArrayList<BonusLife> bonuslifeToRemove= new ArrayList<>();
            for (BonusLife bonuslife: bonuslifes) {
                bonuslife.update(delta);
                if (bonuslife.remove) {
                    bonuslifeToRemove.add(bonuslife);
                }
            }
            //remove asteroids
            for (BonusLife bonuslife: bonuslifes) {
                if (bonuslife.getCollisionRect().collidesWith(playerRect)) {
                    bonuslifeToRemove.add(bonuslife);
                    if (health >= 1)
                        health += 0;
                    else
                        health += 0.34f;
                }
            }
            bonuslifes.removeAll(bonuslifeToRemove);
        }

        stateTime += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        TextureRegion currentFrame = (TextureRegion) rolls[roll].getKeyFrame(stateTime, true);

        game.batch.begin();

        if (normal_asteroid_enable) {
            for (Asteroid asteroid : asteroids) {
                asteroid.render(game.batch);
            }
        }
        if (big_asteroid_enable){
            for (Big_Asteroid big_asteroid : big_asteroids) {
                big_asteroid.render(game.batch);
            }
        }
        if (enable_comet){
            for (Comet comet: comets) {
                comet.render(game.batch);
            }
        }
        if (enable_bonuslife){
            for (BonusLife bonuslife: bonuslifes) {
                bonuslife.render(game.batch);
            }
        }

        game.scrollingBackground.updateAndRender(delta, game.batch);
        game.batch.draw(blank, 0, 0, GokuDodge.WIDTH_DESKTOP * health, 5);
        game.batch.draw(currentFrame, x, y, SHIP_WIDTH, SHIP_HEIGHT);
        game.batch.draw(pauseingame, GokuDodge.WIDTH_DESKTOP - pauseingame.getWidth(), 20, pauseingame.getWidth(), pauseingame.getHeight());
        game.batch.draw(texture, GokuDodge.WIDTH_DESKTOP / 2 - level1.getWidth() / 2, 300, level1.getWidth(), level1.getHeight());

        game.batch.end();
    }


    public void generalUpdateLevel1(float delta, float minAsteroidSpawnTimer, float maxAsteroidSpawnTimer, float big_asteroid_MIN_SpawnTimer, float big_asteroid_MAX_SpawnTimer) {

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
        if (!adjustX){
            if (x < 0)
                x = 0;
            if (x + SHIP_WIDTH > GokuDodge.WIDTH_DESKTOP)
                x = GokuDodge.WIDTH_DESKTOP - SHIP_WIDTH;
        }
        else {
            if (x < 25)
                x = 25;
            if (x + SHIP_WIDTH > GokuDodge.WIDTH_DESKTOP-25)
                x = GokuDodge.WIDTH_DESKTOP - SHIP_WIDTH-25;
        }
        if (y + SHIP_HEIGHT > GokuDodge.HEIGHT_DESKTOP / 2) {
            y = GokuDodge.HEIGHT_DESKTOP / 2 - SHIP_HEIGHT;
        }
        if (y < 0) {
            y = 0;
        }

        //after player moves, update collision rect
        playerRect.move(x, y);

        //asteroid spawn code
        if(normal_asteroid_enable) {
            asteroidSpawnTimer -= delta;
            if (asteroidSpawnTimer <= 0) {
                asteroidSpawnTimer = random.nextFloat() * (maxAsteroidSpawnTimer - minAsteroidSpawnTimer) + minAsteroidSpawnTimer;
                asteroids.add(new Asteroid(random.nextInt(GokuDodge.WIDTH_DESKTOP - Asteroid.WIDTH)));
            }
            //asteroid update code
            ArrayList<Asteroid> asteroidsToRemove = new ArrayList<Asteroid>();
            for (Asteroid asteroid : asteroids) {
                asteroid.update(delta);
                if (asteroid.remove) {
                    asteroidsToRemove.add(asteroid);
                }
            }
            //remove asteroids
            for (Asteroid asteroid : asteroids) {
                if (asteroid.getCollisionRect().collidesWith(playerRect)) {
                    asteroidsToRemove.add(asteroid);
                    health -= 0.34f;
                    if (health <= 0) {
                        this.dispose();
                        game.setScreen(new GameOverScreen(game, 1));
                        return;
                    }
                }
                if (asteroid.getCollisionRect().collidesWith(cometRect)) {
                    asteroidsToRemove.add(asteroid);
                }
            }
            asteroids.removeAll(asteroidsToRemove);
        }
        if(big_asteroid_enable){
            //asteroid spawn code
            big_asteroidSpawnTimer -= delta;
            if (big_asteroidSpawnTimer <= 0) {
                big_asteroidSpawnTimer = random.nextFloat() * (big_asteroid_MAX_SpawnTimer - big_asteroid_MIN_SpawnTimer) + big_asteroid_MIN_SpawnTimer;
                big_asteroids.add(new Big_Asteroid(random.nextInt(GokuDodge.WIDTH_DESKTOP - Asteroid.WIDTH)));
            }
            //asteroid update code
            ArrayList<Big_Asteroid> big_asteroidsToRemove = new ArrayList<>();
            for (Big_Asteroid big_asteroid : big_asteroids) {
                big_asteroid.update(delta);
                if (big_asteroid.remove) {
                    big_asteroidsToRemove.add(big_asteroid);
                }
            }
            //remove asteroids
            for (Big_Asteroid big_asteroid : big_asteroids) {
                if (big_asteroid.getCollisionRect().collidesWith(playerRect)) {
                    big_asteroidsToRemove.add(big_asteroid);
                    health -= 0.5f;

                    if (health <= 0) {
                        this.dispose();
                        game.setScreen(new GameOverScreen(game, 1));
                        return;
                    }
                }
                if (big_asteroid.getCollisionRect().collidesWith(cometRect)) {
                    big_asteroidsToRemove.add(big_asteroid);
                }
            }
            big_asteroids.removeAll(big_asteroidsToRemove);
        }
        if(enable_comet){
            //comet spawn code
            comets_SpawnTimer -= delta;
            if (comets_SpawnTimer <= 0) {
                comets_SpawnTimer= random.nextFloat() * (maxAsteroidSpawnTimer - minAsteroidSpawnTimer) + minAsteroidSpawnTimer;
                comets.add(new Comet(random.nextInt(GokuDodge.WIDTH_DESKTOP - Asteroid.WIDTH)));
            }
            //asteroid update code
            ArrayList<Comet> cometsToRemove= new ArrayList<>();
            for (Comet comet: comets) {
                comet.update(delta);
                if (comet.remove) {
                    cometsToRemove.add(comet);
                }
            }
            //remove asteroids
            for (Comet comet: comets) {
                if (comet.getCollisionRect().collidesWith(playerRect)) {
                    cometsToRemove.add(comet);
                    health -= 1f;

                    if (health <= 0) {
                        this.dispose();
                        game.setScreen(new GameOverScreen(game, 1));
                        return;
                    }
                }
            }
            comets.removeAll(cometsToRemove);
        }
        if(enable_bonuslife){
            //comet spawn code
            bonuslife_SpawnTimer -= delta;
            if (bonuslife_SpawnTimer <= 0) {
                bonuslife_SpawnTimer= random.nextFloat() * (0.2f - 0.1f) + 0.1f;
                bonuslifes.add(new BonusLife(random.nextInt(GokuDodge.WIDTH_DESKTOP - BonusLife.WIDTH)));
            }
            //asteroid update code
            ArrayList<BonusLife> bonuslifeToRemove= new ArrayList<>();
            for (BonusLife bonuslife: bonuslifes) {
                bonuslife.update(delta);
                if (bonuslife.remove) {
                    bonuslifeToRemove.add(bonuslife);
                }
            }
            //remove asteroids
            for (BonusLife bonuslife: bonuslifes) {
                if (bonuslife.getCollisionRect().collidesWith(playerRect)) {
                    bonuslifeToRemove.add(bonuslife);
                    if (health >= 1)
                        health += 0;
                    else
                        health += 0.34f;
                }
            }
            bonuslifes.removeAll(bonuslifeToRemove);
        }

        stateTime += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        TextureRegion currentFrame = (TextureRegion) rolls[roll].getKeyFrame(stateTime, true);
        game.batch.begin();

        game.scrollingBackground.updateAndRender(delta, game.batch);

        if(normal_asteroid_enable) {
            for (Asteroid asteroid : asteroids) {
                asteroid.render(game.batch);
            }
        }
        if (big_asteroid_enable){
            for (Big_Asteroid big_asteroid : big_asteroids) {
                big_asteroid.render(game.batch);
            }
        }
        if (enable_comet){
            for (Comet comet: comets) {
                comet.render(game.batch);
            }
        }
        if (enable_bonuslife){
            for (BonusLife bonuslife: bonuslifes) {
                bonuslife.render(game.batch);
            }
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
        RUN,
        DONOTHING
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
