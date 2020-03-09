package com.rafa.spaceagila.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.rafa.spaceagila.SpaceAgila;

public class MainStoryScreen implements Screen, ApplicationListener, InputProcessor {

    public static final int BACK_WIDTH = 204;
    public static final int BACK_HEIGHT = 82;
    public Music gameovermusic;

    SpaceAgila game;

    int score;

    Texture backInactive, back, tryc;

    public MainStoryScreen(SpaceAgila game) {
        this.game = game;
        tryc = new Texture("story.png");
        //load textures and fonts
        backInactive = new Texture("backInactive.png");
        back = new Texture("back.png");
        gameovermusic = Gdx.audio.newMusic(Gdx.files.internal("gameovermusic.mp3"));

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

        int xBack = SpaceAgila.WIDTH_DESKTOP - BACK_WIDTH/2 ;
        int yBack = 0;

        if (game.cam.getInputInGameWorld().x < xBack + BACK_WIDTH/2 && game.cam.getInputInGameWorld().x > xBack && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < yBack + BACK_HEIGHT/2 && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y > yBack) {
            game.batch.draw(backInactive, xBack, yBack, BACK_WIDTH/2, BACK_HEIGHT/2);
            if (Gdx.input.justTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                this.dispose();
                game.batch.end();
                game.setScreen(new MainMenuScreen(game, gameovermusic.getVolume()));
                return;
            }
        } else {
            game.batch.draw(back, xBack, yBack, BACK_WIDTH/2, BACK_HEIGHT/2);
        }
        game.batch.draw(tryc,SpaceAgila.WIDTH_DESKTOP/2 - tryc.getWidth()/2, SpaceAgila.HEIGHT_DESKTOP/2 - tryc.getHeight()/2, tryc.getWidth(), tryc.getHeight());

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
            game.setScreen(new MainMenuScreen(game, gameovermusic.getVolume()));
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
