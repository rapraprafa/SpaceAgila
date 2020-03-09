package com.rafa.spaceagila.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.rafa.spaceagila.SpaceAgila;

public class VictoryScreen1 implements Screen, ApplicationListener, InputProcessor {

    public static final int BANNER_WIDTH = 350;
    public static final int BANNER_HEIGHT = 100;
    boolean endingbackground;
    Music gameovermusic;


    SpaceAgila game;

    int score;

    Texture main_menu, main_menu_inactive, you_win_inactive;

    public VictoryScreen1(SpaceAgila game) {
        this.game = game;
        this.score = score;

        //load textures and fonts
        you_win_inactive = new Texture("you-win-inactive.png");
        main_menu = new Texture("main-menu.png");
        main_menu_inactive = new Texture("main-menu-inactive.png");
        gameovermusic = Gdx.audio.newMusic(Gdx.files.internal("gameovermusic.mp3"));
        endingbackground = false;

        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();

        game.scrollingBackground.updateAndRender(delta,game.batch);

        int xTryAgain = SpaceAgila.WIDTH_DESKTOP / 2 - BANNER_WIDTH / 2 + BANNER_WIDTH/4;

        int yTryAgain = SpaceAgila.HEIGHT_DESKTOP - BANNER_HEIGHT - 250;

        game.batch.draw(you_win_inactive, SpaceAgila.WIDTH_DESKTOP / 2 - BANNER_WIDTH / 2, SpaceAgila.HEIGHT_DESKTOP - BANNER_HEIGHT - 150, BANNER_WIDTH, BANNER_HEIGHT);

        if (game.cam.getInputInGameWorld().x < xTryAgain + BANNER_WIDTH/2 && game.cam.getInputInGameWorld().x > xTryAgain && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < yTryAgain + BANNER_HEIGHT/2 && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > yTryAgain) {
            game.batch.draw(main_menu_inactive, xTryAgain, yTryAgain, BANNER_WIDTH/2, BANNER_HEIGHT/2);
            if (Gdx.input.justTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                this.dispose();
                game.batch.end();
                game.setScreen(new MainMenuScreen(game, gameovermusic.getVolume()));
                return;
            }
        } else {
            game.batch.draw(main_menu, xTryAgain, yTryAgain, BANNER_WIDTH/2, BANNER_HEIGHT/2);
        }

        gameovermusic.play();
        game.batch.end();
    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {
        game.cam.update(width, height);
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
        gameovermusic.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            gameovermusic.stop();
            game.setScreen(new MainMenuScreen(game, gameovermusic.getVolume()) );
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
