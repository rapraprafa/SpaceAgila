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

public class EndingScreen implements Screen, ApplicationListener, InputProcessor {

    public static final int BANNER_WIDTH = 350;
    public static final int BANNER_HEIGHT = 100;
    boolean endingbackground;
    Music victorymusic;


    SpaceAgila game;

    int score;

    Texture agilaHas, returned, safely, mainMenu, mainMenuInactive;

    public EndingScreen(SpaceAgila game) {
        this.game = game;

        //load textures and fonts
        agilaHas = new Texture("agila_has.png");
        returned = new Texture("returned.png");
        safely = new Texture("safely.png");
        mainMenu = new Texture("main-menu.png");
        mainMenuInactive = new Texture("main-menu-inactive.png");
        victorymusic = Gdx.audio.newMusic(Gdx.files.internal("victory.mp3"));
        endingbackground = false;

        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();

        game.scrollingBackground.updateAndRender(delta*3,game.batch);

        int xMainMenu = SpaceAgila.WIDTH_DESKTOP / 2 - BANNER_WIDTH / 2 + BANNER_WIDTH/4;
        int yMainMenu = SpaceAgila.HEIGHT_DESKTOP - BANNER_HEIGHT - 500;

        game.batch.draw(agilaHas, SpaceAgila.WIDTH_DESKTOP / 2 - BANNER_WIDTH / 2, SpaceAgila.HEIGHT_DESKTOP - BANNER_HEIGHT - 130, BANNER_WIDTH, BANNER_HEIGHT);
        game.batch.draw(returned, SpaceAgila.WIDTH_DESKTOP / 2 - BANNER_WIDTH / 2, SpaceAgila.HEIGHT_DESKTOP - BANNER_HEIGHT - 215, BANNER_WIDTH, BANNER_HEIGHT);
        game.batch.draw(safely, SpaceAgila.WIDTH_DESKTOP / 2 - BANNER_WIDTH / 2, SpaceAgila.HEIGHT_DESKTOP - BANNER_HEIGHT - 295, BANNER_WIDTH, BANNER_HEIGHT);

        if (game.cam.getInputInGameWorld().x < xMainMenu + BANNER_WIDTH/2 && game.cam.getInputInGameWorld().x > xMainMenu && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < yMainMenu + BANNER_HEIGHT/2 && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > yMainMenu) {
            game.batch.draw(mainMenuInactive, xMainMenu, yMainMenu, BANNER_WIDTH/2, BANNER_HEIGHT/2);
            if (Gdx.input.justTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                this.dispose();
                game.batch.end();
                game.setScreen(new MainMenuScreen(game, victorymusic.getVolume()));
                return;
            }
        } else {
            game.batch.draw(mainMenu, xMainMenu, yMainMenu, BANNER_WIDTH/2, BANNER_HEIGHT/2);
        }

        victorymusic.play();
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
        victorymusic.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK){
            victorymusic.stop();
            game.setScreen(new MainMenuScreen(game, victorymusic.getVolume()));
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
