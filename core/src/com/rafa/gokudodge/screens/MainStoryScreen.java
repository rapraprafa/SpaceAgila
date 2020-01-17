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
import com.rafa.gokudodge.tools.ScrollingBackground;
import com.rafa.gokudodge.tools.ScrollingStory;

import javax.xml.soap.Text;

public class MainStoryScreen implements Screen{

    public static final int BANNER_WIDTH = 350;
    public static final int BANNER_HEIGHT = 100;
    Music gameovermusic;

    public ScrollingStory scrollingStory;

    GokuDodge game;

    int score;

    Texture backtoMenu;

    public MainStoryScreen(GokuDodge game) {
        this.game = game;
        this.score = score;

        //load textures and fonts
        backtoMenu = new Texture("main-menu.png");
        gameovermusic = Gdx.audio.newMusic(Gdx.files.internal("gameovermusic.mp3"));

    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0.5f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();

        game.scrollingStory.updateAndRender(delta,game.batch);

        gameovermusic.play();
        game.batch.end();
    }


    @Override
    public void resize(int width, int height) {
        game.cam.update(width, height);
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
}
