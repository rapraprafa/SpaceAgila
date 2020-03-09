package com.rafa.spaceagila;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;
import com.rafa.spaceagila.screens.MainMenuScreen;
import com.rafa.spaceagila.screens.SplashScreen;
import com.rafa.spaceagila.tools.GameCamera;
import com.rafa.spaceagila.tools.ScrollingBackground;

public class SpaceAgila extends Game {

    SplashScreen splashScreen;
    public static final int WIDTH_DESKTOP = 480;
    public static final int HEIGHT_DESKTOP = 720;
    public static boolean IS_MOBILE = false;

    public static int no_of_players = 0;

    public SpriteBatch batch;
    public ScrollingBackground scrollingBackground;
    public GameCamera cam;


    private static long SPLASH_MINIMUM_MILLIS = 5000L;

//    MainGameScreen main;

    @Override
    public void create() {
        batch = new SpriteBatch();
        cam = new GameCamera(WIDTH_DESKTOP, HEIGHT_DESKTOP);
        setScreen(new SplashScreen());

        final long splash_start_time = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {

                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {

                        long splash_elapsed_time = System.currentTimeMillis() - splash_start_time;
                        if (splash_elapsed_time < SpaceAgila.SPLASH_MINIMUM_MILLIS) {
                            Timer.schedule(
                                    new Timer.Task() {
                                        @Override
                                        public void run() {
                                            SpaceAgila.this.setScreen(new MainMenuScreen(SpaceAgila.this, 1f));
                                        }
                                    }, (float)(SpaceAgila.SPLASH_MINIMUM_MILLIS - splash_elapsed_time) / 1000f);
                        } else {
                            SpaceAgila.this.setScreen(new MainMenuScreen(SpaceAgila.this, 1f));
                        }
                    }
                });
            }
        }).start();

        if (Gdx.app.getType() == Application.ApplicationType.Android || Gdx.app.getType() == Application.ApplicationType.iOS) {
            IS_MOBILE = true;
        }
        IS_MOBILE = true;

        this.scrollingBackground = new ScrollingBackground();

//        if (main.health == 0){
//            this.setScreen(new GameOverScreen(this,0));
//        }
    }



    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void render() {
        batch.setProjectionMatrix(cam.combined());
        super.render();
    }

    public void resize(int width, int height) {
        this.scrollingBackground.resize(width, height);
        cam.update(width, height);
        super.resize(width, height);
    }

}
