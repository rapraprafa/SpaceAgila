package com.rafa.gokudodge.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rafa.gokudodge.GokuDodge;
import com.rafa.gokudodge.tools.GameCamera;

public class SplashScreen implements Screen {
    SpriteBatch batchSplash;
    Texture ttrSplash;
    GokuDodge game;
    GameCamera cam;
    Music splashmusic;
    public SplashScreen() {
        super();
        batchSplash = new SpriteBatch();
        ttrSplash = new Texture("splashlogo.png");
        cam = new GameCamera(GokuDodge.WIDTH_DESKTOP, GokuDodge.HEIGHT_DESKTOP);
        splashmusic = Gdx.audio.newMusic(Gdx.files.internal("startsound.mp3"));

        splashmusic.play();
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batchSplash.begin();
        batchSplash.draw(ttrSplash, GokuDodge.WIDTH_DESKTOP / 2 - ttrSplash.getWidth() / 2, GokuDodge.HEIGHT_DESKTOP/2, ttrSplash.getWidth(), ttrSplash.getHeight());
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
