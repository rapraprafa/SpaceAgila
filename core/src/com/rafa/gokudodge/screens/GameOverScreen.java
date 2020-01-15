package com.rafa.gokudodge.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.rafa.gokudodge.GokuDodge;

import javax.xml.soap.Text;

public class GameOverScreen implements Screen, ApplicationListener, InputProcessor {

    public static final int BANNER_WIDTH = 350;
    public static final int BANNER_HEIGHT = 100;
    Music gameovermusic;


    GokuDodge game;

    int score;

    Texture gameOverBanner, tryAgainBanner, mainMenuBanner, tryAgainInactive, mainMenuInactive;

    public GameOverScreen(GokuDodge game, int score) {
        this.game = game;
        this.score = score;

        //load textures and fonts
        gameOverBanner = new Texture("game-over.png");
        tryAgainBanner = new Texture("try-again.png");
        mainMenuBanner = new Texture("main-menu.png");
        tryAgainInactive = new Texture("try-again-inactive.png");
        mainMenuInactive = new Texture("main-menu-inactive.png");
        gameovermusic = Gdx.audio.newMusic(Gdx.files.internal("gameovermusic.mp3"));

        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0.15f, 0.15f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();

        game.scrollingBackground.updateAndRender(delta,game.batch);

        int xTryAgain = GokuDodge.WIDTH_DESKTOP / 2 - BANNER_WIDTH / 2 + BANNER_WIDTH/4;
        int xMainMenu = GokuDodge.WIDTH_DESKTOP / 2 - BANNER_WIDTH / 2 + BANNER_WIDTH/4;

        int yTryAgain = GokuDodge.HEIGHT_DESKTOP - BANNER_HEIGHT - 250;
//        int yMainMenu = GokuDodge.HEIGHT_DESKTOP - BANNER_HEIGHT - 350;

        game.batch.draw(gameOverBanner, GokuDodge.WIDTH_DESKTOP / 2 - BANNER_WIDTH / 2, GokuDodge.HEIGHT_DESKTOP - BANNER_HEIGHT - 150, BANNER_WIDTH, BANNER_HEIGHT);
        //game.batch.draw(tryAgainBanner, xTryAgain, yTryAgain, BANNER_WIDTH/2, BANNER_HEIGHT/2);
        //game.batch.draw(mainMenuBanner, xMainMenu, yMainMenu, BANNER_WIDTH/2, BANNER_HEIGHT/2);

        if (game.cam.getInputInGameWorld().x < xTryAgain + BANNER_WIDTH/2 && game.cam.getInputInGameWorld().x > xTryAgain && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < yTryAgain + BANNER_HEIGHT/2 && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > yTryAgain) {
            game.batch.draw(mainMenuInactive, xTryAgain, yTryAgain, BANNER_WIDTH/2, BANNER_HEIGHT/2);
            if (Gdx.input.justTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                this.dispose();
                game.batch.end();
                game.setScreen(new MainMenuScreen(game));
                return;
            }
        } else {
            game.batch.draw(mainMenuBanner, xTryAgain, yTryAgain, BANNER_WIDTH/2, BANNER_HEIGHT/2);
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
            game.setScreen(new MainMenuScreen(game));
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
