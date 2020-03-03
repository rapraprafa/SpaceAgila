package com.rafa.spaceagila.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.rafa.spaceagila.SpaceAgila;

import java.awt.GradientPaint;
import java.awt.image.BufferedImage;


public class MainMenuScreen implements Screen, ApplicationListener, InputProcessor {

    public static final int BACK_WIDTH = 204;
    public static final int BACK_HEIGHT = 82;

    private static final int PLAY_BUTTON_WIDTH = 175;
    private static final int PLAY_BUTTON_HEIGHT = 87;

    private static final int EXIT_BUTTON_WIDTH = 175;
    private static final int EXIT_BUTTON_HEIGHT = 87;

    private static final int ABOUT_BUTTON_WIDTH = 196;
    private static final int ABOUT_BUTTON_HEIGHT = 98;

    private static final int SPACE_WIDTH = 337;
    private static final int SPACE_HEIGHT = 172;

    private static final int AGILA_WIDTH = 337;
    private static final int AGILA_HEIGHT = 172;


    private static final int PLAY_BUTTON_Y = 255;
    private static final int ABOUT_BUTTON_Y = 150;
    private static final int EXIT_BUTTON_Y = 55;

    Music mainmenumusic;

    boolean showstory;

    final SpaceAgila game;
    Texture playButtonActive,tryc, playButtonInactive, exitButtonActive, exitButtonInactive, soundButtonPlay, soundButtonMute, transparent, about, aboutInactive, space, agila;
    Texture backInactive, back;

    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;

    private GradientPaint gradientPaing;
    private BufferedImage textImage;
    private double textY;
    boolean endingbackground;


    public MainMenuScreen(final SpaceAgila game) {
        this.game = game;
        space = new Texture("space75.png");
        agila = new Texture("agila75.png");
        about = new Texture("about.png");
        aboutInactive = new Texture("aboutInactive.png");
        playButtonActive = new Texture("play-active.png");
        playButtonInactive = new Texture("play-inactive.png");
        exitButtonActive = new Texture("exit-active.png");
        exitButtonInactive = new Texture("exit-inactive.png");
        soundButtonPlay = new Texture("playsound.png");
        soundButtonMute = new Texture("mute.png");
        transparent = new Texture("transpa.png");
        endingbackground = false;
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        showstory = false;

        tryc = new Texture("story.png");
        //load textures and fonts
        backInactive = new Texture("backInactive.png");
        back = new Texture("back.png");

        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);

        mainmenumusic = Gdx.audio.newMusic(Gdx.files.internal("gamemusic.mp3"));

        final MainMenuScreen mainMenuScreen = this;

        Gdx.input.setInputProcessor(new InputAdapter() {
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {


                //About button
                int xAbout = SpaceAgila.WIDTH_DESKTOP / 2 - PLAY_BUTTON_WIDTH / 2;
                if ((game.cam.getInputInGameWorld().x < xAbout + ABOUT_BUTTON_WIDTH && game.cam.getInputInGameWorld().x > xAbout && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < ABOUT_BUTTON_Y + ABOUT_BUTTON_HEIGHT && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > ABOUT_BUTTON_Y)) {
                    showstory = true;
                }
                if (!showstory) {
                    //Exit button
                    int xExit = SpaceAgila.WIDTH_DESKTOP / 2 - EXIT_BUTTON_WIDTH / 2;
                    if ((game.cam.getInputInGameWorld().x < xExit + EXIT_BUTTON_WIDTH && game.cam.getInputInGameWorld().x > xExit && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < EXIT_BUTTON_Y + EXIT_BUTTON_HEIGHT && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > EXIT_BUTTON_Y)) {
                        mainMenuScreen.dispose();
                        game.dispose();
                        Gdx.app.exit();
                    }

                    //Play button
                    int xPlay = SpaceAgila.WIDTH_DESKTOP / 2 - PLAY_BUTTON_WIDTH / 2;
                    if ((game.cam.getInputInGameWorld().x < xPlay + PLAY_BUTTON_WIDTH && game.cam.getInputInGameWorld().x > xPlay && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < PLAY_BUTTON_Y + PLAY_BUTTON_HEIGHT && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > PLAY_BUTTON_Y)) {
                        game.connectSocket();
                        mainMenuScreen.dispose();
                        game.setScreen(new MainGameScreen(game));

                    }
                }
                return super.touchDown(screenX, screenY, pointer, button);
            }

        });

    }



    public void mainStoryScreen(float delta){
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.scrollingBackground.updateAndRender(delta/16, game.batch);

        int xBack = SpaceAgila.WIDTH_DESKTOP - BACK_WIDTH/2 ;
        int yBack = 0;

        if (game.cam.getInputInGameWorld().x < xBack + BACK_WIDTH/2 && game.cam.getInputInGameWorld().x > xBack && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < yBack + BACK_HEIGHT/2 && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > yBack) {
            game.batch.draw(backInactive, xBack, yBack, BACK_WIDTH/2, BACK_HEIGHT/2);
            if (Gdx.input.justTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                showstory = false;
                return;
            }
        } else {
            game.batch.draw(back, xBack, yBack, BACK_WIDTH/2, BACK_HEIGHT/2);
        }
        game.batch.draw(tryc,SpaceAgila.WIDTH_DESKTOP/2 - tryc.getWidth()/2, SpaceAgila.HEIGHT_DESKTOP/2 - tryc.getHeight()/2, tryc.getWidth(), tryc.getHeight());

        mainmenumusic.play();
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();

        mainmenumusic.setLooping(true);
        mainmenumusic.play();

        game.scrollingBackground.updateAndRender(delta, game.batch);

        int xExit = SpaceAgila.WIDTH_DESKTOP / 2 - EXIT_BUTTON_WIDTH / 2;
        int xPlay = SpaceAgila.WIDTH_DESKTOP / 2 - PLAY_BUTTON_WIDTH / 2;
        int xAbout = SpaceAgila.WIDTH_DESKTOP / 2 - ABOUT_BUTTON_WIDTH / 2;
        int xSpace = SpaceAgila.WIDTH_DESKTOP / 2 - SPACE_WIDTH / 2;
        int ySpace = SpaceAgila.HEIGHT_DESKTOP - 200;
        int xAgila = SpaceAgila.WIDTH_DESKTOP / 2 - AGILA_WIDTH / 2;
        int yAgila = SpaceAgila.HEIGHT_DESKTOP - 320;

        game.batch.draw(space, xSpace, ySpace, SPACE_WIDTH, SPACE_HEIGHT);
        game.batch.draw(agila, xAgila, yAgila, AGILA_WIDTH, AGILA_HEIGHT);

        if (game.cam.getInputInGameWorld().x < xExit + EXIT_BUTTON_WIDTH && game.cam.getInputInGameWorld().x > xExit && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < EXIT_BUTTON_Y + EXIT_BUTTON_HEIGHT && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > EXIT_BUTTON_Y) {

            game.batch.draw(exitButtonInactive, xExit, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
        } else {
            game.batch.draw(exitButtonActive, xExit, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
        }
        if (game.cam.getInputInGameWorld().x < xPlay + PLAY_BUTTON_WIDTH && game.cam.getInputInGameWorld().x > xPlay && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < PLAY_BUTTON_Y + PLAY_BUTTON_HEIGHT && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > PLAY_BUTTON_Y) {
            game.batch.draw(playButtonInactive, xPlay, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
        } else {
            game.batch.draw(playButtonActive, xPlay, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
        }
        if (game.cam.getInputInGameWorld().x < xAbout + ABOUT_BUTTON_WIDTH && game.cam.getInputInGameWorld().x > xAbout && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < ABOUT_BUTTON_Y + ABOUT_BUTTON_HEIGHT && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > ABOUT_BUTTON_Y) {
            game.batch.draw(aboutInactive, xAbout, ABOUT_BUTTON_Y, ABOUT_BUTTON_WIDTH, ABOUT_BUTTON_HEIGHT);
        } else {
            game.batch.draw(about, xAbout, ABOUT_BUTTON_Y, ABOUT_BUTTON_WIDTH, ABOUT_BUTTON_HEIGHT);
        }

        //Mute
        if(mainmenumusic.getVolume() == 1f) {
            game.batch.draw(soundButtonPlay, SpaceAgila.WIDTH_DESKTOP / 16 - soundButtonPlay.getWidth() / 2, 20, soundButtonPlay.getWidth(), soundButtonPlay.getHeight());
            if (game.cam.getInputInGameWorld().x < SpaceAgila.WIDTH_DESKTOP / 16 - soundButtonPlay.getWidth() / 2 + soundButtonPlay.getWidth() && game.cam.getInputInGameWorld().x > SpaceAgila.WIDTH_DESKTOP / 16 - soundButtonPlay.getWidth() / 2 && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < 20 + soundButtonPlay.getHeight() && game.cam.getInputInGameWorld().y > 20) {
                if (Gdx.input.justTouched()) {
                    mainmenumusic.setVolume(0f);
                    System.out.println("music muted");
                }
            }
        }

        //Unmute
        else if (mainmenumusic.getVolume() == 0f) {
            game.batch.draw(soundButtonMute, SpaceAgila.WIDTH_DESKTOP / 16 - soundButtonMute.getWidth() / 2, 20, soundButtonMute.getWidth(), soundButtonMute.getHeight());
            if (game.cam.getInputInGameWorld().x < SpaceAgila.WIDTH_DESKTOP / 16 - soundButtonMute.getWidth() / 2 + soundButtonMute.getWidth() && game.cam.getInputInGameWorld().x > SpaceAgila.WIDTH_DESKTOP / 16 - soundButtonMute.getWidth() / 2 && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < 20 + soundButtonMute.getHeight() && game.cam.getInputInGameWorld().y > 20) {
                if (Gdx.input.justTouched()) {
                    mainmenumusic.setVolume(1f);
                    System.out.println("music unmuted");
                }
            }
        }

        if(showstory){
            mainStoryScreen(delta);
        }
        game.batch.end();
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
        Gdx.input.setInputProcessor(null);
        mainmenumusic.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK) {
            Gdx.app.exit();
            return true;
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
}
