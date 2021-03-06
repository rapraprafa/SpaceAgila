package com.rafa.spaceagila.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.rafa.spaceagila.SpaceAgila;

public class PauseScreen implements Screen {

    SpaceAgila game;
    Texture pausedbanner;
    Texture resume;
    boolean endingbackground;

    public PauseScreen(SpaceAgila game){
        this.game = game;
        pausedbanner = new Texture("pausedbanner.png");
        endingbackground = false;
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
        game.batch.draw(pausedbanner, SpaceAgila.WIDTH_DESKTOP / 2 - pausedbanner.getWidth() / 2, 500, pausedbanner.getWidth(), pausedbanner.getHeight());
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)){
            this.dispose();
        }
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

    }
}
