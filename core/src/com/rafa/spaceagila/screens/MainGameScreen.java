package com.rafa.spaceagila.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.rafa.spaceagila.SpaceAgila;
import com.rafa.spaceagila.entities.Asteroid;
import com.rafa.spaceagila.entities.Big_Asteroid;
import com.rafa.spaceagila.entities.BonusLife;
import com.rafa.spaceagila.entities.Comet;
import com.rafa.spaceagila.entities.Explosion;
import com.rafa.spaceagila.tools.CollisionRect;

import java.util.ArrayList;
import java.util.Random;



public class MainGameScreen implements Screen, ApplicationListener, InputProcessor{

    public static final float SPEED = 500;

    public static final float SHIP_ANIMATION_SPEED = 0.3f;
    public static final int SHIP_WIDTH_PIXEL = 32 / 2;
    public static final int SHIP_HEIGHT_PIXEL = 32 / 2;
    public static final int SHIP_WIDTH = SHIP_WIDTH_PIXEL * 3;
    public static final int SHIP_HEIGHT = SHIP_HEIGHT_PIXEL * 3;

    public static final float MIN_ASTEROID_SPAWN_TIMER_LEVEL = 0.5f;
    public static final float MAX_ASTEROID_SPAWN_TIMER_LEVEL = 1f;

    Animation[] rolls;

    public static float x;
    public static float y;
    public static float stateTime;
    private float asteroidSpawnTimer;
    private float bonuslife_SpawnTimer;

    private float big_asteroidSpawnTimer;

    float comets_SpawnTimer;



    private int roll;
    private static boolean paused;
    private boolean dragging, adjustX, big_asteroid_enable, normal_asteroid_enable, enable_comet, enable_bonuslife, controls_enable;
    public boolean ending, endingbackground;

    private static Music ingamemusic;

    private Random random;

    private Vector2 touch;
    private SpaceAgila game;

    Sound explode, life;

    boolean final_level_stage;
    ArrayList<Asteroid> asteroids;
    ArrayList<Big_Asteroid> big_asteroids;
    ArrayList<Comet> comets;
    ArrayList<BonusLife> bonuslifes;
    ArrayList<Explosion> explosions;

    Texture blank;
    private Texture level1, level2, level3, level4, level5, level6, level7, level8, level9, level10, level11, level12, level13, level14, level15, final_level;
    private Texture playingame, pauseingame, pausedbanner, mainMenuBanner, resume;
    private Texture soundButtonPlay, soundButtonMute;

    CollisionRect playerRect;
    CollisionRect asteroidRect, cometRect;

    float health = 1f;//0 = dead, 1 = alive

    State state;
    public static float VOLUME;

    public MainGameScreen(SpaceAgila game) {
        this.game = game;
        final_level_stage = false;
        VOLUME = 1.0f;
        y = 15;
        x = SpaceAgila.WIDTH_DESKTOP / 2 - SHIP_WIDTH / 2;

        adjustX = false;
        normal_asteroid_enable = false;
        big_asteroid_enable = false;
        enable_comet = false;
        enable_bonuslife = false;
        controls_enable = false;
        ending = false;
        endingbackground = false;
        blank = new Texture("white.png");

        explode = Gdx.audio.newSound(Gdx.files.internal("explode.ogg"));
        life = Gdx.audio.newSound(Gdx.files.internal("life.ogg"));

        asteroids = new ArrayList<>();
        big_asteroids = new ArrayList<>();
        explosions = new ArrayList<>();
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
        level10 = new Texture("level10.png");
        level11 = new Texture("level11.png");
        level12 = new Texture("level12.png");
        level13 = new Texture("level13.png");
        level14 = new Texture("level14.png");
        level15 = new Texture("level15.png");
        final_level = new Texture("final_level.png");

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
        System.out.println(ingamemusic.getPosition());
        switch(state){
            case RUN:
                game.batch.begin();
                game.batch.draw(pauseingame, SpaceAgila.WIDTH_DESKTOP - pauseingame.getWidth(), 20, pauseingame.getWidth(), pauseingame.getHeight());
                game.batch.end();
                //level1
                if (ingamemusic.getPosition() >= 0 && ingamemusic.getPosition() < 5) {
                    controls_enable = true;
                    generalUpdateLevelIntro(delta, level1);
                }
                else if (ingamemusic.getPosition() >= 5 && ingamemusic.getPosition() < 35) {
                    normal_asteroid_enable = true;
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL, MAX_ASTEROID_SPAWN_TIMER_LEVEL , 0.3f, 0.7f,3f,5f,0,0);
                }

                //level2
                else if (ingamemusic.getPosition() >= 35 && ingamemusic.getPosition() < 40) {
                    generalUpdateLevelIntro(delta, level2);
                }
                else if (ingamemusic.getPosition() >= 40 && ingamemusic.getPosition() < 70) {
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0, 0,0,0, 0 ,0);
                }

                //level3
                else if (ingamemusic.getPosition() >= 70 && ingamemusic.getPosition() < 75){
                    generalUpdateLevelIntro(delta, level3);
                }
                else if (ingamemusic.getPosition() >= 75 && ingamemusic.getPosition() < 105) {
                    Asteroid.SPEED = 350;
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0, 0,0,0, 0,0);
                }

                //level4
                else if (ingamemusic.getPosition() >= 105 && ingamemusic.getPosition() < 110){
                    adjustX = true;
                    generalUpdateLevelIntro(delta, level4);
                }
                else if (ingamemusic.getPosition() >= 110 && ingamemusic.getPosition() < 140) {
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0, 0,0,0,0,0);
                }

                //level5
                else if (ingamemusic.getPosition() >= 140 && ingamemusic.getPosition() < 145){
                    generalUpdateLevelIntro(delta, level5);
                }
                else if (ingamemusic.getPosition() >= 145 && ingamemusic.getPosition() < 175) {
                    big_asteroid_enable = true;
                    Asteroid.SPEED = 250;
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0.5f, 1f,0,0, 0, 0);
                }

                //level6
                else if (ingamemusic.getPosition() >= 175 && ingamemusic.getPosition() < 180){
                    generalUpdateLevelIntro(delta, level6);
                }
                else if (ingamemusic.getPosition() >= 180 && ingamemusic.getPosition() < 210) {
                    Asteroid.SPEED = 350;
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0.3f, 0.7f,0,0, 0, 0);
                }

                //level7
                else if (ingamemusic.getPosition() >= 210 && ingamemusic.getPosition() < 215){
                    generalUpdateLevelIntro(delta, level7);
                }
                else if (ingamemusic.getPosition() >= 215 && ingamemusic.getPosition() < 245) {
                    Big_Asteroid.SPEED = 350;
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0.3f, 0.7f,0,0, 0, 0);
                }

                //level8
                else if (ingamemusic.getPosition() >= 245 && ingamemusic.getPosition() < 250){
                    generalUpdateLevelIntro(delta, level8);
                }
                else if (ingamemusic.getPosition() >= 250 && ingamemusic.getPosition() < 260) {
                    enable_bonuslife = true;
                    big_asteroid_enable = false;
                    normal_asteroid_enable = false;
                    Asteroid.SPEED = 250;
                    Big_Asteroid.SPEED = 250;
                    generalUpdateLevel1(delta, 0, 0, 0, 0,0,0, 0.1f, 0.2f);
                }

                //level9
                else if (ingamemusic.getPosition() >= 260 && ingamemusic.getPosition() < 265){
                    generalUpdateLevelIntro(delta, level9);
                }
                else if (ingamemusic.getPosition() >= 265 && ingamemusic.getPosition() < 295) {
                    enable_comet = true;
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0.3f, 0.7f,0.5f,1f, 5f, 10f);
                }

                //level10
                else if (ingamemusic.getPosition() >= 295 && ingamemusic.getPosition() < 300){
                    generalUpdateLevelIntro(delta, level10);
                }
                else if (ingamemusic.getPosition() >= 300 && ingamemusic.getPosition() < 330) {
                    enable_comet = true;
                    normal_asteroid_enable = true;
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL, MAX_ASTEROID_SPAWN_TIMER_LEVEL, 0.3f, 0.7f,2f,4f,5f,10f);
                }

                //level11
                else if (ingamemusic.getPosition() >= 330 && ingamemusic.getPosition() < 335){
                    generalUpdateLevelIntro(delta, level11);
                }
                else if (ingamemusic.getPosition() >= 335 && ingamemusic.getPosition() < 365) {
                    big_asteroid_enable = true;
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL, MAX_ASTEROID_SPAWN_TIMER_LEVEL , 0.5f, 1f,2f,4f,5f,10f);
                }

                //level12
                else if (ingamemusic.getPosition() >= 365 && ingamemusic.getPosition() < 370){
                    generalUpdateLevelIntro(delta, level12);
                }
                else if (ingamemusic.getPosition() >= 370 && ingamemusic.getPosition() < 400) {
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0.5f, 1f,2f,4f,5f,10f);
                }

                //level13
                else if (ingamemusic.getPosition() >= 400 && ingamemusic.getPosition() < 405){
                    generalUpdateLevelIntro(delta, level13);
                }
                else if (ingamemusic.getPosition() >= 405 && ingamemusic.getPosition() < 435) {
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0.3f, 0.7f,2f,4f,5f,10f);
                }

                //level14
                else if (ingamemusic.getPosition() >= 435 && ingamemusic.getPosition() < 440){
                    generalUpdateLevelIntro(delta, level14);
                }
                else if (ingamemusic.getPosition() >= 440 && ingamemusic.getPosition() < 470) {
                    Asteroid.SPEED = 350;
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0.3f, 0.7f,2f,4f,5f,10f);
                }

                //level15
                else if (ingamemusic.getPosition() >=470 && ingamemusic.getPosition() < 475){
                    generalUpdateLevelIntro(delta, level15);
                }
                else if (ingamemusic.getPosition() >= 475 && ingamemusic.getPosition() < 505) {
                    Big_Asteroid.SPEED = 350;
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0.3f, 0.7f,2f,4f,5f,10f);
                }

                //level16
                else if (ingamemusic.getPosition() >= 505 && ingamemusic.getPosition() < 510){
                    generalUpdateLevelIntro(delta, final_level);
                }
                else if (ingamemusic.getPosition() >= 510 && ingamemusic.getPosition() < 540) {
                    Asteroid.SPEED = 375;
                    Big_Asteroid.SPEED = 400;
                    enable_bonuslife = false;
                    generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.4f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.4f, 0.3f, 0.55f,1f,3f, 0, 0);
                }
                else if (ingamemusic.getPosition() >= 540 && ingamemusic.getPosition() <= 542) {
                    final_level_stage = true;
                    Asteroid.SPEED = 250;
                    Big_Asteroid.SPEED = 250;
                    enable_bonuslife = false;
                    enable_comet = false;
                    normal_asteroid_enable = false;
                    big_asteroid_enable = false;
                    controls_enable = false;
                    ending = true;
                    game.batch.begin();
                    game.scrollingBackground.updateAndRender(delta*2, game.batch);
                    game.batch.end();
                    generalUpdateLevel1(delta, 0 ,0, 0, 0, 0,0,0,0);
                }
                //ending
                else{
                    ingamemusic.stop();
                    game.setScreen(new EndingScreen(game));
                }
                break;
            case PAUSE:
                paused(delta);
                break;
            case DONOTHING:
                ;
                break;
            default:
                break;
        }
    }

    public void paused(float delta){
        int xTryAgain = SpaceAgila.WIDTH_DESKTOP / 2 - GameOverScreen.BANNER_WIDTH / 2 + GameOverScreen.BANNER_WIDTH/4;
        int yTryAgain = SpaceAgila.HEIGHT_DESKTOP - GameOverScreen.BANNER_HEIGHT - 250;

        ingamemusic.pause();
        Gdx.gl.glClearColor(0f, 0.0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();

        game.scrollingBackground.updateAndRender(delta,game.batch);
        game.batch.draw(pausedbanner, SpaceAgila.WIDTH_DESKTOP / 2 - pausedbanner.getWidth() / 2, 500, pausedbanner.getWidth(), pausedbanner.getHeight());
        game.batch.draw(resume, xTryAgain, yTryAgain, GameOverScreen.BANNER_WIDTH/2, GameOverScreen.BANNER_HEIGHT/2);
        game.batch.draw(mainMenuBanner, xTryAgain, yTryAgain - 100, GameOverScreen.BANNER_WIDTH/2, GameOverScreen.BANNER_HEIGHT/2);


        if (game.cam.getInputInGameWorld().x < xTryAgain + GameOverScreen.BANNER_WIDTH/2 && game.cam.getInputInGameWorld().x > xTryAgain && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < yTryAgain + GameOverScreen.BANNER_HEIGHT/2 && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > yTryAgain) {
            if (Gdx.input.justTouched()) {
                state = State.RUN;
                ingamemusic.play();
            }
        }

        if (game.cam.getInputInGameWorld().x < xTryAgain + GameOverScreen.BANNER_WIDTH/2 && game.cam.getInputInGameWorld().x > xTryAgain && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < yTryAgain - 100 + GameOverScreen.BANNER_HEIGHT/2 && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > yTryAgain - 100) {
            if (Gdx.input.justTouched()) {
                this.dispose();
                game.batch.end();
                game.setScreen(new MainMenuScreen(game));
                return;
            }
        }

        //Mute
        if(ingamemusic.getVolume() == 1f) {
            game.batch.draw(soundButtonPlay, SpaceAgila.WIDTH_DESKTOP / 16 - soundButtonPlay.getWidth() / 2, 20, soundButtonPlay.getWidth(), soundButtonPlay.getHeight());
            if (game.cam.getInputInGameWorld().x < SpaceAgila.WIDTH_DESKTOP / 16 - soundButtonPlay.getWidth() / 2 + soundButtonPlay.getWidth() && game.cam.getInputInGameWorld().x > SpaceAgila.WIDTH_DESKTOP / 16 - soundButtonPlay.getWidth() / 2 && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < 20 + soundButtonPlay.getHeight() && game.cam.getInputInGameWorld().y > 20) {
                if (Gdx.input.justTouched()) {
                    ingamemusic.setVolume(0f);
                    muteFX();
                    System.out.println("hi1");
                }
            }
        }

        //Unmute
        else if (ingamemusic.getVolume() == 0f) {
            game.batch.draw(soundButtonMute, SpaceAgila.WIDTH_DESKTOP / 16 - soundButtonMute.getWidth() / 2, 20, soundButtonMute.getWidth(), soundButtonMute.getHeight());
            if (game.cam.getInputInGameWorld().x < SpaceAgila.WIDTH_DESKTOP / 16 - soundButtonMute.getWidth() / 2 + soundButtonMute.getWidth() && game.cam.getInputInGameWorld().x > SpaceAgila.WIDTH_DESKTOP / 16 - soundButtonMute.getWidth() / 2 && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < 20 + soundButtonMute.getHeight() && game.cam.getInputInGameWorld().y > 20) {
                if (Gdx.input.justTouched()) {
                    ingamemusic.setVolume(1f);
                    normalizeFX();
                    System.out.println("hi3");
                }
            }
        }

        game.batch.end();
    }

    public static void muteFX(){
        VOLUME = 0.0f;
    }
    public static void normalizeFX(){
        VOLUME = 1.0f;
    }

    public void generalUpdateLevelIntro(float delta, Texture texture) {

        ArrayList<Asteroid> asteroidsToRemove = new ArrayList<>();
        ArrayList<Big_Asteroid> big_asteroidsToRemove = new ArrayList<>();
        ArrayList<Comet> cometsToRemove= new ArrayList<>();
        ArrayList<BonusLife> bonuslifeToRemove= new ArrayList<>();

        //asteroid update
        if(normal_asteroid_enable) {
            //asteroid update code
            for (Asteroid asteroid : asteroids) {
                asteroid.update(delta);
                if (asteroid.remove) {
                    asteroidsToRemove.add(asteroid);
                }
            }

        }
        //big asteroid update
        if(big_asteroid_enable){
            //big asteroid update code
            for (Big_Asteroid big_asteroid : big_asteroids) {
                big_asteroid.update(delta);
                if (big_asteroid.remove) {
                    big_asteroidsToRemove.add(big_asteroid);
                }
            }
        }
        //comet update
        if(enable_comet){
            //comet update code
            for (Comet comet: comets) {
                comet.update(delta);
                if (comet.remove) {
                    cometsToRemove.add(comet);
                }
            }

        }
        //bonus life update
        if(enable_bonuslife){
            //bonus life update code
            for (BonusLife bonuslife: bonuslifes) {
                bonuslife.update(delta);
                if (bonuslife.remove) {
                    bonuslifeToRemove.add(bonuslife);
                }
            }
        }

        //Update explosions
        ArrayList<Explosion> explosionsToRemove = new ArrayList<>();
        for (Explosion explosion : explosions) {
            explosion.update(delta);
            if (explosion.remove)
                explosionsToRemove.add(explosion);
        }
        explosions.removeAll(explosionsToRemove);

        //movement code


        if(controls_enable) {
            if (game.cam.getInputInGameWorld().x < SpaceAgila.WIDTH_DESKTOP - playingame.getWidth() + playingame.getWidth() && game.cam.getInputInGameWorld().x > SpaceAgila.WIDTH_DESKTOP - playingame.getWidth() && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < 20 + playingame.getHeight() && game.cam.getInputInGameWorld().y > 20 || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
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

                if (x + SHIP_WIDTH > SpaceAgila.WIDTH_DESKTOP) {
                    x = SpaceAgila.WIDTH_DESKTOP - SHIP_WIDTH;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                y += SPEED * Gdx.graphics.getDeltaTime();

                if (y + SHIP_HEIGHT > SpaceAgila.HEIGHT_DESKTOP) {
                    y = SpaceAgila.HEIGHT_DESKTOP - SHIP_HEIGHT;
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
                y = ((SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y) + SHIP_HEIGHT / 2);
            }
            if (game.cam.getInputInGameWorld().x < x + SHIP_WIDTH && game.cam.getInputInGameWorld().x > x && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < y + SHIP_HEIGHT && SpaceAgila.HEIGHT_DESKTOP + game.cam.getInputInGameWorld().y > y) {
                if (Gdx.input.isTouched()) {
                    x = game.cam.getInputInGameWorld().x - SHIP_WIDTH / 2;
                    y = ((SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y) + SHIP_HEIGHT / 2);
                    dragging = true;
                } else {
                    dragging = false;
                }
            }
            if (!adjustX) {
                if (x < 0)
                    x = 0;
                if (x + SHIP_WIDTH > SpaceAgila.WIDTH_DESKTOP)
                    x = SpaceAgila.WIDTH_DESKTOP - SHIP_WIDTH;
            } else {
                if (x < 25)
                    x = 25;
                if (x + SHIP_WIDTH > SpaceAgila.WIDTH_DESKTOP - 25)
                    x = SpaceAgila.WIDTH_DESKTOP - SHIP_WIDTH - 25;
            }
            if (y + SHIP_HEIGHT > SpaceAgila.HEIGHT_DESKTOP / 2) {
                y = SpaceAgila.HEIGHT_DESKTOP / 2 - SHIP_HEIGHT;
            }
            if (y < 0) {
                y = 0;
            }
        }
        if(ending){
            y += 20;
        }

        //after player moves, update collision rect
        playerRect.move(x, y);

        //after all updates, check for collision
        //asteroid collision

        if (!final_level_stage) {
            for (Asteroid asteroid : asteroids) {
                for (Comet comet: comets){
                    if (asteroid.getCollisionRect().collidesWith(comet.getCollisionRect())) {
                        asteroidsToRemove.add(asteroid);
                        explode.play(VOLUME);
                        Gdx.input.vibrate(new long[] { 0, 200}, -1);
                        explosions.add(new Explosion(asteroid.getX(), asteroid.getY()));
                    }
                }
                if (asteroid.getCollisionRect().collidesWith(playerRect)) {
                    asteroidsToRemove.add(asteroid);
                    explode.play(VOLUME);
                    Gdx.input.vibrate(new long[] { 0, 200}, -1);
                    explosions.add(new Explosion(x, y));
                    health -= 0.34f;
                    if (health <= 0) {
                        this.dispose();
                        game.setScreen(new GameOverScreen(game, 1));
                        return;
                    }
                }

            }
            //asteroids remove code
            asteroids.removeAll(asteroidsToRemove);

            //big asteroid collision
            for (Big_Asteroid big_asteroid : big_asteroids) {
                for (Comet comet : comets) {
                    if (big_asteroid.getCollisionRect().collidesWith(comet.getCollisionRect())) {
                        big_asteroidsToRemove.add(big_asteroid);
                        explode.play(VOLUME);
                        Gdx.input.vibrate(new long[]{0, 200}, -1);
                        explosions.add(new Explosion(big_asteroid.getX(), big_asteroid.getY()));
                    }
                }
                if (big_asteroid.getCollisionRect().collidesWith(playerRect)) {
                    big_asteroidsToRemove.add(big_asteroid);
                    explode.play(VOLUME);
                    Gdx.input.vibrate(new long[]{0, 200}, -1);
                    explosions.add(new Explosion(x, y));
                    health -= 0.5f;
                    if (health <= 0) {
                        this.dispose();
                        game.setScreen(new GameOverScreen(game, 1));
                        return;
                    }
                }
            }
            //big asteroids remove code
            big_asteroids.removeAll(big_asteroidsToRemove);

            //comet collision
            for (Comet comet : comets) {
                if (comet.getCollisionRect().collidesWith(playerRect)) {
                    cometsToRemove.add(comet);
                    explode.play(VOLUME);
                    Gdx.input.vibrate(new long[]{0, 200}, -1);
                    explosions.add(new Explosion(x, y));
                    health -= 9999f;

                    if (health <= 0) {
                        this.dispose();
                        game.setScreen(new GameOverScreen(game, 1));
                        return;
                    }
                }
            }
            //comets remove code
            comets.removeAll(cometsToRemove);

            //bonus life remove code
            for (BonusLife bonuslife : bonuslifes) {
                if (bonuslife.getCollisionRect().collidesWith(playerRect)) {
                    life.play(VOLUME);
                    bonuslifeToRemove.add(bonuslife);
                    if (health >= 1) {
                        health = 1;
                        health += 0;
                    } else
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

        if (!final_level_stage) {
            if (normal_asteroid_enable) {
                for (Asteroid asteroid : asteroids) {
                    asteroid.render(game.batch);
                }
            }
            if (big_asteroid_enable) {
                for (Big_Asteroid big_asteroid : big_asteroids) {
                    big_asteroid.render(game.batch);
                }
            }
            if (enable_bonuslife) {
                for (BonusLife bonuslife : bonuslifes) {
                    bonuslife.render(game.batch);
                }
            }
            if (enable_comet) {
                for (Comet comet : comets) {
                    comet.render(game.batch);
                }
            }
            for (Explosion explosion : explosions) {
                explosion.render(game.batch);
            }
        }


        if(health <= 0.32f)
            game.batch.setColor(Color.RED);
        else if(health <=0.66f && health > 0.32f)
            game.batch.setColor(Color.ORANGE);
        else
            game.batch.setColor(Color.GREEN);

        game.batch.draw(blank, 0, 0, SpaceAgila.WIDTH_DESKTOP * health, 10);
        game.batch.setColor(Color.WHITE);
        game.batch.draw(currentFrame, x, y, SHIP_WIDTH, SHIP_HEIGHT);
        game.batch.draw(pauseingame, SpaceAgila.WIDTH_DESKTOP - pauseingame.getWidth(), 20, pauseingame.getWidth(), pauseingame.getHeight());
        game.batch.draw(texture, SpaceAgila.WIDTH_DESKTOP / 2 - level1.getWidth() / 2, 300, level1.getWidth(), level1.getHeight());

        game.batch.end();
    }

    public void generalUpdateLevel1(float delta, float minAsteroidSpawnTimer, float maxAsteroidSpawnTimer, float big_asteroid_MIN_SpawnTimer, float big_asteroid_MAX_SpawnTimer, float minCometSpawnTimer, float maxCometSpawnTimer, float min_bonuslifeSpawnTimer, float max_bonuslifeSpawnTimer) {

        ArrayList<Asteroid> asteroidsToRemove = new ArrayList<>();
        ArrayList<Big_Asteroid> big_asteroidsToRemove = new ArrayList<>();
        ArrayList<Comet> cometsToRemove= new ArrayList<>();
        ArrayList<BonusLife> bonuslifeToRemove= new ArrayList<>();

        //asteroid spawn code + update
        if(normal_asteroid_enable) {
            //asteroid spawn code
            asteroidSpawnTimer -= delta;
            if (asteroidSpawnTimer <= 0) {
                asteroidSpawnTimer = random.nextFloat() * (maxAsteroidSpawnTimer - minAsteroidSpawnTimer) + minAsteroidSpawnTimer;
                asteroids.add(new Asteroid(random.nextInt(SpaceAgila.WIDTH_DESKTOP - Asteroid.WIDTH)));
            }
            //asteroid update code
            for (Asteroid asteroid : asteroids) {
                asteroid.update(delta);
                if (asteroid.remove) {
                    asteroidsToRemove.add(asteroid);
                }
            }

        }
        //big asteroid spawn code + update
        if(big_asteroid_enable){
            //big asteroid spawn code
            big_asteroidSpawnTimer -= delta;
            if (big_asteroidSpawnTimer <= 0) {
                big_asteroidSpawnTimer = random.nextFloat() * (big_asteroid_MAX_SpawnTimer - big_asteroid_MIN_SpawnTimer) + big_asteroid_MIN_SpawnTimer;
                big_asteroids.add(new Big_Asteroid(random.nextInt(SpaceAgila.WIDTH_DESKTOP - Asteroid.WIDTH)));
            }
            //big asteroid update code
            for (Big_Asteroid big_asteroid : big_asteroids) {
                big_asteroid.update(delta);
                if (big_asteroid.remove) {
                    big_asteroidsToRemove.add(big_asteroid);
                }
            }
        }
        //comet spawn code + update
        if(enable_comet){
            //comet spawn code
            comets_SpawnTimer -= delta;
            if (comets_SpawnTimer <= 0) {
                comets_SpawnTimer= random.nextFloat() * (maxCometSpawnTimer - minCometSpawnTimer) + minCometSpawnTimer;
                comets.add(new Comet(random.nextInt(SpaceAgila.WIDTH_DESKTOP - Asteroid.WIDTH)));
            }
            //comet update code
            for (Comet comet: comets) {
                comet.update(delta);
                if (comet.remove) {
                    cometsToRemove.add(comet);
                }
            }

        }
        //bonus life spawn code + update
        if(enable_bonuslife){
            //bonus life spawn code
            bonuslife_SpawnTimer -= delta;
            if (bonuslife_SpawnTimer <= 0) {
                bonuslife_SpawnTimer= random.nextFloat() * (max_bonuslifeSpawnTimer - min_bonuslifeSpawnTimer) + min_bonuslifeSpawnTimer;
                bonuslifes.add(new BonusLife(random.nextInt(SpaceAgila.WIDTH_DESKTOP - BonusLife.WIDTH)));
            }
            //bonus life update code
            for (BonusLife bonuslife: bonuslifes) {
                bonuslife.update(delta);
                if (bonuslife.remove) {
                    bonuslifeToRemove.add(bonuslife);
                }
            }
        }

        //Update explosions
        ArrayList<Explosion> explosionsToRemove = new ArrayList<>();
        for (Explosion explosion : explosions) {
            explosion.update(delta);
            if (explosion.remove)
                explosionsToRemove.add(explosion);
        }
        explosions.removeAll(explosionsToRemove);

        //movement code

        if(controls_enable) {
            if (game.cam.getInputInGameWorld().x < SpaceAgila.WIDTH_DESKTOP - playingame.getWidth() + playingame.getWidth() && game.cam.getInputInGameWorld().x > SpaceAgila.WIDTH_DESKTOP - playingame.getWidth() && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < 20 + playingame.getHeight() && game.cam.getInputInGameWorld().y > 20 || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
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

                if (x + SHIP_WIDTH > SpaceAgila.WIDTH_DESKTOP) {
                    x = SpaceAgila.WIDTH_DESKTOP - SHIP_WIDTH;
                }
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                y += SPEED * Gdx.graphics.getDeltaTime();

                if (y + SHIP_HEIGHT > SpaceAgila.HEIGHT_DESKTOP) {
                    y = SpaceAgila.HEIGHT_DESKTOP - SHIP_HEIGHT;
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
                y = ((SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y) + SHIP_HEIGHT / 2);
            }
            if (game.cam.getInputInGameWorld().x < x + SHIP_WIDTH && game.cam.getInputInGameWorld().x > x && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < y + SHIP_HEIGHT && SpaceAgila.HEIGHT_DESKTOP + game.cam.getInputInGameWorld().y > y) {
                if (Gdx.input.isTouched()) {
                    x = game.cam.getInputInGameWorld().x - SHIP_WIDTH / 2;
                    y = ((SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y) + SHIP_HEIGHT / 2);
                    dragging = true;
                } else {
                    dragging = false;
                }
            }
            if (!adjustX) {
                if (x < 0)
                    x = 0;
                if (x + SHIP_WIDTH > SpaceAgila.WIDTH_DESKTOP)
                    x = SpaceAgila.WIDTH_DESKTOP - SHIP_WIDTH;
            } else {
                if (x < 25)
                    x = 25;
                if (x + SHIP_WIDTH > SpaceAgila.WIDTH_DESKTOP - 25)
                    x = SpaceAgila.WIDTH_DESKTOP - SHIP_WIDTH - 25;
            }
            if (y + SHIP_HEIGHT > SpaceAgila.HEIGHT_DESKTOP / 2) {
                y = SpaceAgila.HEIGHT_DESKTOP / 2 - SHIP_HEIGHT;
            }
            if (y < 0) {
                y = 0;
            }
        }
        if(ending){
            y += 20;
        }

        //after player moves, update collision rect
        playerRect.move(x, y);

        //after all updates, check for collision
        //asteroid collision
        if (!final_level_stage) {
            for (Asteroid asteroid : asteroids) {
                for (Comet comet : comets) {
                    if (asteroid.getCollisionRect().collidesWith(comet.getCollisionRect())) {
                        asteroidsToRemove.add(asteroid);
                        explode.play(VOLUME);
                        Gdx.input.vibrate(new long[]{0, 200}, -1);
                        explosions.add(new Explosion(asteroid.getX(), asteroid.getY()));
                    }
                }
                if (asteroid.getCollisionRect().collidesWith(playerRect)) {
                    asteroidsToRemove.add(asteroid);
                    explode.play(VOLUME);
                    Gdx.input.vibrate(new long[]{0, 200}, -1);
                    explosions.add(new Explosion(x, y));
                    health -= 0.34f;
                    if (health <= 0) {
                        this.dispose();
                        game.setScreen(new GameOverScreen(game, 1));
                        return;
                    }
                }
            }
            //asteroids remove code
            asteroids.removeAll(asteroidsToRemove);

            //big asteroid collision
            for (Big_Asteroid big_asteroid : big_asteroids) {
                if (!final_level_stage) {
                    for (Comet comet : comets) {
                        if (big_asteroid.getCollisionRect().collidesWith(comet.getCollisionRect())) {
                            big_asteroidsToRemove.add(big_asteroid);
                            explode.play(VOLUME);
                            Gdx.input.vibrate(new long[]{0, 200}, -1);
                            explosions.add(new Explosion(big_asteroid.getX(), big_asteroid.getY()));
                        }
                    }
                    if (big_asteroid.getCollisionRect().collidesWith(playerRect)) {
                        big_asteroidsToRemove.add(big_asteroid);
                        explode.play(VOLUME);
                        Gdx.input.vibrate(new long[]{0, 200}, -1);
                        explosions.add(new Explosion(x, y));
                        health -= 0.5f;
                        if (health <= 0) {
                            this.dispose();
                            game.setScreen(new GameOverScreen(game, 1));
                            return;
                        }
                    }
                }
            }
            //big asteroids remove code
            big_asteroids.removeAll(big_asteroidsToRemove);

            //comet collision
            for (Comet comet : comets) {
                if (!final_level_stage) {
                    if (comet.getCollisionRect().collidesWith(playerRect)) {
                        cometsToRemove.add(comet);
                        explode.play(VOLUME);
                        Gdx.input.vibrate(new long[]{0, 200}, -1);
                        explosions.add(new Explosion(x, y));
                        health -= 9999f;

                        if (health <= 0) {
                            this.dispose();
                            game.setScreen(new GameOverScreen(game, 1));
                            return;
                        }
                    }
                }
            }
            //comets remove code
            comets.removeAll(cometsToRemove);

            //bonus life remove code
            for (BonusLife bonuslife : bonuslifes) {
                if (bonuslife.getCollisionRect().collidesWith(playerRect)) {
                    life.play(VOLUME);
                    bonuslifeToRemove.add(bonuslife);
                    if (health >= 1) {
                        health = 1;
                        health += 0;
                    } else
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

        if(!final_level_stage) {

            if (normal_asteroid_enable) {
                for (Asteroid asteroid : asteroids) {
                    asteroid.render(game.batch);
                }
            }
            if (big_asteroid_enable) {
                for (Big_Asteroid big_asteroid : big_asteroids) {
                    big_asteroid.render(game.batch);
                }
            }
            if (enable_comet) {
                for (Comet comet : comets) {
                    comet.render(game.batch);
                }
            }
            if (enable_bonuslife) {
                for (BonusLife bonuslife : bonuslifes) {
                    bonuslife.render(game.batch);
                }
            }
            for (Explosion explosion : explosions) {
                explosion.render(game.batch);
            }
        }


        if(health <= 0.32f)
            game.batch.setColor(Color.RED);
        else if(health <=0.66f && health > 0.32f)
            game.batch.setColor(Color.ORANGE);
        else
            game.batch.setColor(Color.GREEN);

        game.batch.draw(blank, 0, 0, SpaceAgila.WIDTH_DESKTOP * health, 10);

        game.batch.setColor(Color.WHITE);

        game.batch.draw(currentFrame, x, y, SHIP_WIDTH, SHIP_HEIGHT);
        game.batch.draw(pauseingame, SpaceAgila.WIDTH_DESKTOP - pauseingame.getWidth(), 20, pauseingame.getWidth(), pauseingame.getHeight());

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
        explode.dispose();
    }


}
