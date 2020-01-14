package com.rafa.gokudodge.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.rafa.gokudodge.GokuDodge;
import com.rafa.gokudodge.entities.Asteroid;
import com.rafa.gokudodge.tools.CollisionRect;

import java.util.ArrayList;
import java.util.Random;

public class MainGameScreen implements Screen {

    public static final float SPEED = 500;

    public static final float SHIP_ANIMATION_SPEED = 0.5f;
    public static final int SHIP_WIDTH_PIXEL = 32 / 2;
    public static final int SHIP_HEIGHT_PIXEL = 32 / 2;
    public static final int SHIP_WIDTH = SHIP_WIDTH_PIXEL * 3;
    public static final int SHIP_HEIGHT = SHIP_HEIGHT_PIXEL * 3;

    public static final float MIN_ASTEROID_SPAWN_TIMER_LEVEL1 = 0.5f;
    public static final float MAX_ASTEROID_SPAWN_TIMER_LEVEL1 = 1f;

    public static final float MIN_ASTEROID_SPAWN_TIMER_LEVEL2 = 0.3f;
    public static final float MAX_ASTEROID_SPAWN_TIMER_LEVEL2 = 0.7f;

    Animation[] rolls;

    float x;
    float y;
    int roll;
    float stateTime;
    float asteroidSpawnTimer;
    boolean paused = false;
    boolean dragging;


    Music ingamemusic;

    Random random;

    Vector2 touch;
    GokuDodge game;


    ArrayList<Asteroid> asteroids;

    Texture blank;
    private Texture level1, level2;
    private Texture playingame, pauseingame;

    CollisionRect playerRect;

    float health = 1;//0 = dead, 1 = alive

    private State state = State.RUN;

    public MainGameScreen(GokuDodge game) {
        this.game = game;

        y = 15;
        x = GokuDodge.WIDTH_DESKTOP / 2 - SHIP_WIDTH / 2;

        blank = new Texture("white.png");

        asteroids = new ArrayList<Asteroid>();

        playerRect = new CollisionRect(0, 0, SHIP_WIDTH, SHIP_HEIGHT);

        random = new Random();
        asteroidSpawnTimer = random.nextFloat() * (MAX_ASTEROID_SPAWN_TIMER_LEVEL1 - MIN_ASTEROID_SPAWN_TIMER_LEVEL1) + MIN_ASTEROID_SPAWN_TIMER_LEVEL1;

        roll = 1;
        rolls = new Animation[4];

        TextureRegion[][] rollSpriteSheet = TextureRegion.split(new Texture("Lightning.png"), SHIP_WIDTH_PIXEL, SHIP_HEIGHT_PIXEL);
        rolls[roll] = new Animation(SHIP_ANIMATION_SPEED, rollSpriteSheet[0]);

        playingame = new Texture("playingame.png");
        pauseingame = new Texture("pauseingame.png");

        level1 = new Texture("level1.png");
        level2 = new Texture("level2.png");

        ingamemusic = Gdx.audio.newMusic(Gdx.files.internal("ingamemusicfinal.mp3"));
        ingamemusic.play();
        ingamemusic.setLooping(true);

    }

    public void setGameState(State s) {
        this.state = s;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {


        if (paused) {
            game.batch.begin();
            game.batch.draw(playingame, GokuDodge.WIDTH_DESKTOP - playingame.getWidth(), 20, playingame.getWidth(), playingame.getHeight());
            game.batch.end();
            if (game.cam.getInputInGameWorld().x < GokuDodge.WIDTH_DESKTOP - playingame.getWidth() + playingame.getWidth() && game.cam.getInputInGameWorld().x > GokuDodge.WIDTH_DESKTOP - playingame.getWidth() && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < 20 + playingame.getHeight() && game.cam.getInputInGameWorld().y > 20 || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    paused = false;
                    ingamemusic.play();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            game.batch.begin();
            game.batch.draw(pauseingame, GokuDodge.WIDTH_DESKTOP - pauseingame.getWidth(), 20, pauseingame.getWidth(), pauseingame.getHeight());
            game.batch.end();
            if (ingamemusic.getPosition() > 0 && ingamemusic.getPosition() < 5) {
                generalUpdateLevelIntro(delta, level1);
            } else if (ingamemusic.getPosition() > 5 && ingamemusic.getPosition() < 35) {
                generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL1, MAX_ASTEROID_SPAWN_TIMER_LEVEL1);
            } else if (ingamemusic.getPosition() > 35 && ingamemusic.getPosition() < 40) {
                generalUpdateLevelIntro(delta, level2);
            } else if (ingamemusic.getPosition() > 40 && ingamemusic.getPosition() < 70) {
                generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL2, MAX_ASTEROID_SPAWN_TIMER_LEVEL2);
            } else {
                generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL2, MAX_ASTEROID_SPAWN_TIMER_LEVEL2);
            }
        }


    }


    public void generalUpdateLevelIntro(float delta, Texture texture) {
        if (game.cam.getInputInGameWorld().x < GokuDodge.WIDTH_DESKTOP - playingame.getWidth() + playingame.getWidth() && game.cam.getInputInGameWorld().x > GokuDodge.WIDTH_DESKTOP - playingame.getWidth() && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < 20 + playingame.getHeight() && game.cam.getInputInGameWorld().y > 20 || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                paused = true;
                ingamemusic.pause();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= SPEED * Gdx.graphics.getDeltaTime();
            if (x < 0) {
                x = 0;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += SPEED * Gdx.graphics.getDeltaTime();

            if (x + SHIP_WIDTH > GokuDodge.WIDTH_DESKTOP) {
                x = GokuDodge.WIDTH_DESKTOP - SHIP_WIDTH;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            y += SPEED * Gdx.graphics.getDeltaTime();

            if (y + SHIP_HEIGHT > GokuDodge.HEIGHT_DESKTOP) {
                y = GokuDodge.HEIGHT_DESKTOP - SHIP_HEIGHT;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            y -= SPEED * Gdx.graphics.getDeltaTime();

            if (y < 0) {
                y = 0;
            }
        }


        //clicked on sprite
        // do something that vanish the object clicked
        //if(touch.x < GokuDodge.WIDTH_DESKTOP/2 - SHIP_WIDTH/2 + SHIP_WIDTH && touch.x > GokuDodge.WIDTH_DESKTOP/2 - SHIP_WIDTH/2 && GokuDodge.HEIGHT_DESKTOP - touch.y < GokuDodge.HEIGHT_DESKTOP/2 - SHIP_HEIGHT/2 + SHIP_HEIGHT && GokuDodge.HEIGHT_DESKTOP - touch.y > GokuDodge.HEIGHT_DESKTOP/2 - SHIP_HEIGHT/2) {
        //}


        if (dragging) {
            x = game.cam.getInputInGameWorld().x - SHIP_WIDTH / 2;
            y = ((GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y) + SHIP_HEIGHT / 2);
        }


        if (game.cam.getInputInGameWorld().x < x + SHIP_WIDTH && game.cam.getInputInGameWorld().x > x && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < y + SHIP_HEIGHT && GokuDodge.HEIGHT_DESKTOP + game.cam.getInputInGameWorld().y > y) {
            if (Gdx.input.isTouched()) {
                x = game.cam.getInputInGameWorld().x - SHIP_WIDTH / 2;
                y = ((GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y) + SHIP_HEIGHT / 2);
                dragging = true;
            } else {
                dragging = false;
            }
        }


        if (x < 0) {
            x = 0;
        }

        if (x + SHIP_WIDTH > GokuDodge.WIDTH_DESKTOP) {
            x = GokuDodge.WIDTH_DESKTOP - SHIP_WIDTH;
        }

        if (y + SHIP_HEIGHT > GokuDodge.HEIGHT_DESKTOP / 2) {
            y = GokuDodge.HEIGHT_DESKTOP / 2 - SHIP_HEIGHT;
        }

        if (y < 0) {
            y = 0;
        }


        //asteroid spawn code

        //after player moves, update collision rect
        playerRect.move(x, y);

        //asteroid update code
        ArrayList<Asteroid> asteroidsToRemove = new ArrayList<Asteroid>();
        for (Asteroid asteroid : asteroids) {
            asteroid.update(delta);
            if (asteroid.remove) {
                asteroidsToRemove.add(asteroid);
            }
        }


        for (Asteroid asteroid : asteroids) {
            if (asteroid.getCollisionRect().collidesWith(playerRect)) {
                asteroidsToRemove.add(asteroid);
                health -= 1;

                if (health <= 0) {
                    this.dispose();
                    game.setScreen(new GameOverScreen(game, 1));
                    return;
                }
            }
        }
        asteroids.removeAll(asteroidsToRemove);

        stateTime += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        TextureRegion currentFrame = (TextureRegion) rolls[roll].getKeyFrame(stateTime, true);

        game.batch.begin();

        game.scrollingBackground.updateAndRender(delta, game.batch);

        for (Asteroid asteroid : asteroids) {
            asteroid.render(game.batch);
        }

        game.batch.draw(blank, 0, 0, GokuDodge.WIDTH_DESKTOP * health, 5);
        game.batch.draw(currentFrame, x, y, SHIP_WIDTH, SHIP_HEIGHT);
        game.batch.draw(pauseingame, GokuDodge.WIDTH_DESKTOP - pauseingame.getWidth(), 20, pauseingame.getWidth(), pauseingame.getHeight());

        game.batch.draw(texture, GokuDodge.WIDTH_DESKTOP / 2 - level1.getWidth() / 2, 300, level1.getWidth(), level1.getHeight());
        game.batch.end();
    }


    public void generalUpdateLevel1(float delta, float minAsteroidSpawnTimer, float maxAsteroidSpawnTimer) {
        if (game.cam.getInputInGameWorld().x < GokuDodge.WIDTH_DESKTOP - playingame.getWidth() + playingame.getWidth() && game.cam.getInputInGameWorld().x > GokuDodge.WIDTH_DESKTOP - playingame.getWidth() && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < 20 + playingame.getHeight() && game.cam.getInputInGameWorld().y > 20 || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                paused = true;
                ingamemusic.pause();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            x -= SPEED * Gdx.graphics.getDeltaTime();

        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            x += SPEED * Gdx.graphics.getDeltaTime();

            if (x + SHIP_WIDTH > GokuDodge.WIDTH_DESKTOP) {
                x = GokuDodge.WIDTH_DESKTOP - SHIP_WIDTH;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            y += SPEED * Gdx.graphics.getDeltaTime();

            if (y + SHIP_HEIGHT > GokuDodge.HEIGHT_DESKTOP) {
                y = GokuDodge.HEIGHT_DESKTOP - SHIP_HEIGHT;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            y -= SPEED * Gdx.graphics.getDeltaTime();

            if (y < 0) {
                y = 0;
            }
        }


        touch = new Vector2(game.cam.getInputInGameWorld().x, game.cam.getInputInGameWorld().y);

        //clicked on sprite
        // do something that vanish the object clicked
        //if(touch.x < GokuDodge.WIDTH_DESKTOP/2 - SHIP_WIDTH/2 + SHIP_WIDTH && touch.x > GokuDodge.WIDTH_DESKTOP/2 - SHIP_WIDTH/2 && GokuDodge.HEIGHT_DESKTOP - touch.y < GokuDodge.HEIGHT_DESKTOP/2 - SHIP_HEIGHT/2 + SHIP_HEIGHT && GokuDodge.HEIGHT_DESKTOP - touch.y > GokuDodge.HEIGHT_DESKTOP/2 - SHIP_HEIGHT/2) {
        //}


        if (dragging) {
            x = game.cam.getInputInGameWorld().x - SHIP_WIDTH / 2;
            y = ((GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y) + SHIP_HEIGHT / 2);
        }


        if (game.cam.getInputInGameWorld().x < x + SHIP_WIDTH && game.cam.getInputInGameWorld().x > x && GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < y + SHIP_HEIGHT && GokuDodge.HEIGHT_DESKTOP + game.cam.getInputInGameWorld().y > y) {
            if (Gdx.input.isTouched()) {
                x = game.cam.getInputInGameWorld().x - SHIP_WIDTH / 2;
                y = ((GokuDodge.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y) + SHIP_HEIGHT / 2);
                dragging = true;
            } else {
                dragging = false;
            }
        }


        if (x < 0) {
            x = 0;
        }

        if (x + SHIP_WIDTH > GokuDodge.WIDTH_DESKTOP) {
            x = GokuDodge.WIDTH_DESKTOP - SHIP_WIDTH;
        }

        if (y + SHIP_HEIGHT > GokuDodge.HEIGHT_DESKTOP / 2) {
            y = GokuDodge.HEIGHT_DESKTOP / 2 - SHIP_HEIGHT;
        }

        if (y < 0) {
            y = 0;
        }


        //asteroid spawn code
        asteroidSpawnTimer -= delta;
        if (asteroidSpawnTimer <= 0) {
            asteroidSpawnTimer = random.nextFloat() * (maxAsteroidSpawnTimer - minAsteroidSpawnTimer) + minAsteroidSpawnTimer;
            asteroids.add(new Asteroid(random.nextInt(GokuDodge.WIDTH_DESKTOP - Asteroid.WIDTH)));
        }

        //after player moves, update collision rect
        playerRect.move(x, y);

        //asteroid update code
        ArrayList<Asteroid> asteroidsToRemove = new ArrayList<Asteroid>();
        for (Asteroid asteroid : asteroids) {
            asteroid.update(delta);
            if (asteroid.remove) {
                asteroidsToRemove.add(asteroid);
            }
        }


        for (Asteroid asteroid : asteroids) {
            if (asteroid.getCollisionRect().collidesWith(playerRect)) {
                asteroidsToRemove.add(asteroid);
                health -= 1;

                if (health <= 0) {
                    this.dispose();
                    game.setScreen(new GameOverScreen(game, 1));
                    return;
                }
            }
        }
        asteroids.removeAll(asteroidsToRemove);

        stateTime += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        TextureRegion currentFrame = (TextureRegion) rolls[roll].getKeyFrame(stateTime, true);
        game.batch.begin();

        game.scrollingBackground.updateAndRender(delta, game.batch);

        for (Asteroid asteroid : asteroids) {
            asteroid.render(game.batch);
        }

        game.batch.draw(blank, 0, 0, GokuDodge.WIDTH_DESKTOP * health, 5);
        game.batch.draw(currentFrame, x, y, SHIP_WIDTH, SHIP_HEIGHT);
        game.batch.draw(pauseingame, GokuDodge.WIDTH_DESKTOP - pauseingame.getWidth(), 20, pauseingame.getWidth(), pauseingame.getHeight());

        game.batch.end();
    }


    public enum State {
        PAUSE,
        RUN,
        RESUME,
        STOPPED
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        this.state = State.PAUSE;
    }

    @Override
    public void resume() {
        this.state = State.RESUME;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        ingamemusic.dispose();
    }


}
