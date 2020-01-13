package com.rafa.gokudodge.screens;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.AudioDevice;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.rafa.gokudodge.GokuDodge;


public class MainMenuScreen implements Screen {

    private static final int PLAY_BUTTON_WIDTH = 200;
    private static final int PLAY_BUTTON_HEIGHT = 100;
    private static final int EXIT_BUTTON_WIDTH = 200;
    private static final int EXIT_BUTTON_HEIGHT = 100;
    private static final int EXIT_BUTTON_Y = 100;
    private static final int PLAY_BUTTON_Y = 250;

    Music mainmenumusic;


    final GokuDodge game;
    Texture playButtonActive;
    Texture playButtonInactive;
    Texture exitButtonActive;
    Texture exitButtonInactive;
    Texture soundButtonPlay;
    Texture soundButtonMute;
    Texture transparent;


    public MainMenuScreen(final GokuDodge game) {
        this.game = game;
        playButtonActive = new Texture("play-active.png");
        playButtonInactive = new Texture("play-inactive.png");
        exitButtonActive = new Texture("exit-active.png");
        exitButtonInactive = new Texture("exit-inactive.png");
        soundButtonPlay = new Texture("playsound.png");
        soundButtonMute = new Texture("mute.png");
        transparent = new Texture("transpa.png");


        mainmenumusic = Gdx.audio.newMusic(Gdx.files.internal("gamemusic.mp3"));



        final MainMenuScreen mainMenuScreen = this;

        Gdx.input.setInputProcessor(new InputAdapter() {
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                //Exit button
                int xExit = GokuDodge.WIDTH_DESKTOP / 2 - EXIT_BUTTON_WIDTH / 2;
                if (game.cam.getInputInGameWorld().x < xExit + EXIT_BUTTON_WIDTH && game.cam.getInputInGameWorld().x > xExit && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < EXIT_BUTTON_Y + EXIT_BUTTON_HEIGHT && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > EXIT_BUTTON_Y) {
                    mainMenuScreen.dispose();
                    Gdx.app.exit();
                }


                //Play Game button
                int xPlay = GokuDodge.WIDTH_DESKTOP / 2 - PLAY_BUTTON_WIDTH / 2;
                if (game.cam.getInputInGameWorld().x < xPlay + PLAY_BUTTON_WIDTH && game.cam.getInputInGameWorld().x > xPlay && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < PLAY_BUTTON_Y + PLAY_BUTTON_HEIGHT && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > PLAY_BUTTON_Y) {
                    mainMenuScreen.dispose();
                    game.setScreen(new MainGameScreen(game));
                }

                return super.touchDown(screenX, screenY, pointer, button);
            }

        });

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();

        mainmenumusic.setLooping(true);
        mainmenumusic.play();


        game.scrollingBackground.updateAndRender(delta, game.batch);



        int xExit = GokuDodge.WIDTH_DESKTOP / 2 - EXIT_BUTTON_WIDTH / 2;
        int xPlay = GokuDodge.WIDTH_DESKTOP / 2 - PLAY_BUTTON_WIDTH / 2;

        if (game.cam.getInputInGameWorld().x < xExit + EXIT_BUTTON_WIDTH && game.cam.getInputInGameWorld().x > xExit && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < EXIT_BUTTON_Y + EXIT_BUTTON_HEIGHT && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > EXIT_BUTTON_Y) {
            game.batch.draw(exitButtonInactive, xExit, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
        } else {
            game.batch.draw(exitButtonActive, xExit, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
        }

        if (game.cam.getInputInGameWorld().x < xPlay + PLAY_BUTTON_WIDTH && game.cam.getInputInGameWorld().x > xPlay && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < PLAY_BUTTON_Y + PLAY_BUTTON_HEIGHT && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > PLAY_BUTTON_Y) {
            game.batch.draw(playButtonInactive, xPlay, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
        } else {
            game.batch.draw(playButtonActive, xPlay, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
        }
        game.batch.end();

        game.batch.begin();
        //Mute
        if(mainmenumusic.getVolume() == 1f) {
            game.batch.draw(soundButtonPlay, GokuDodge.WIDTH_DESKTOP / 16 - soundButtonPlay.getWidth() / 2, 20, soundButtonPlay.getWidth(), soundButtonPlay.getHeight());
            if (game.cam.getInputInGameWorld().x < GokuDodge.WIDTH_DESKTOP / 16 - soundButtonPlay.getWidth() / 2 + soundButtonPlay.getWidth() && game.cam.getInputInGameWorld().x > GokuDodge.WIDTH_DESKTOP / 16 - soundButtonPlay.getWidth() / 2 && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < 20 + soundButtonPlay.getHeight() && game.cam.getInputInGameWorld().y > 20) {
                if (Gdx.input.justTouched()) {
                    mainmenumusic.setVolume(0f);
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
        else if (mainmenumusic.getVolume() == 0f) {
            game.batch.draw(soundButtonMute, GokuDodge.WIDTH_DESKTOP / 16 - soundButtonMute.getWidth() / 2, 20, soundButtonMute.getWidth(), soundButtonMute.getHeight());
            if (game.cam.getInputInGameWorld().x < GokuDodge.WIDTH_DESKTOP / 16 - soundButtonMute.getWidth() / 2 + soundButtonMute.getWidth() && game.cam.getInputInGameWorld().x > GokuDodge.WIDTH_DESKTOP / 16 - soundButtonMute.getWidth() / 2 && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < 20 + soundButtonMute.getHeight() && game.cam.getInputInGameWorld().y > 20) {
                if (Gdx.input.justTouched()) {
                    mainmenumusic.setVolume(1f);
                    System.out.println("hi3");
                }
            }
        }
//        else{
//            if(Gdx.input.isTouched()) {
//                game.batch.draw(transparent, GokuDodge.WIDTH_DESKTOP / 16 - soundButtonMute.getWidth() / 2, 50, soundButtonMute.getWidth(), soundButtonMute.getHeight());
//                game.batch.draw(soundButtonPlay, GokuDodge.WIDTH_DESKTOP / 16 - soundButtonPlay.getWidth() / 2, 50, soundButtonPlay.getWidth(), soundButtonPlay.getHeight());
//                music.setVolume(1f);
//                System.out.println("hi4");
//            }
//        }
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
        Gdx.input.setInputProcessor(null);
        mainmenumusic.dispose();
    }
}
