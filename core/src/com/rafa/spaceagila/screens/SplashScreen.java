package com.rafa.spaceagila.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rafa.spaceagila.SpaceAgila;
import com.rafa.spaceagila.tools.GameCamera;

public class SplashScreen implements Screen {
    SpriteBatch batchSplash;
    Texture ttrSplash;
    SpaceAgila game;
    GameCamera cam;
    Music splashmusic;
    public SplashScreen() {
        super();
        batchSplash = new SpriteBatch();
        ttrSplash = new Texture("icon.png");
        cam = new GameCamera(SpaceAgila.WIDTH_DESKTOP, SpaceAgila.HEIGHT_DESKTOP);
        splashmusic = Gdx.audio.newMusic(Gdx.files.internal("startsound.mp3"));
        splashmusic.play();
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batchSplash.begin();
        batchSplash.draw(ttrSplash, SpaceAgila.WIDTH_DESKTOP / 2 - ttrSplash.getWidth() / 2, SpaceAgila.HEIGHT_DESKTOP/2 - ttrSplash.getHeight()/2 , ttrSplash.getWidth(), ttrSplash.getHeight());
        batchSplash.end();
        batchSplash.setProjectionMatrix(cam.combined());

    }

    @Override
    public void hide() { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void show() { }

    @Override
    public void resize(int width, int height) {
        cam.update(width, height);
    }

    @Override
    public void dispose() {
        splashmusic.dispose();
    }
}
