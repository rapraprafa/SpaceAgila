package com.rafa.spaceagila.screens;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.rafa.spaceagila.SpaceAgila;
import com.rafa.spaceagila.entities.Asteroid;
import com.rafa.spaceagila.entities.Big_Asteroid;
import com.rafa.spaceagila.entities.BonusLife;
import com.rafa.spaceagila.entities.Comet;
import com.rafa.spaceagila.entities.Explosion;
import com.rafa.spaceagila.entities.MultiplayerShip;
import com.rafa.spaceagila.tools.CollisionRect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.xml.soap.Text;

public class MultiplayerGameScreen1 implements Screen, ApplicationListener{

    private final float UPDATE_TIME = 1/60f;
    float timer = 0;

    public static final float SPEED = 500;
    public static int no_of_players = 0;
    public static final float SHIP_ANIMATION_SPEED = 0.3f;
    public static final int SHIP_WIDTH_PIXEL = 32 / 2;
    public static final int SHIP_HEIGHT_PIXEL = 32 / 2;
    public static final int SHIP_WIDTH = SHIP_WIDTH_PIXEL * 3;
    public static final int SHIP_HEIGHT = SHIP_HEIGHT_PIXEL * 3;

    public static final float MIN_ASTEROID_SPAWN_TIMER_LEVEL = 0.5f;
    public static final float MAX_ASTEROID_SPAWN_TIMER_LEVEL = 1f;

    Animation[] rolls, rolls2;

    MultiplayerShip player;

    TextureRegion[][] playerShip, friendlyShip;
    HashMap<String, MultiplayerShip> friendlyPlayers;


    public static float x;
    public static float y;
    public static float stateTime;
    private float asteroidSpawnTimer;
    private float bonuslife_SpawnTimer;

    private float big_asteroidSpawnTimer;

    float comets_SpawnTimer;

    private int roll, roll2;
    private static boolean paused;
    private boolean dragging, adjustX;
    public boolean ending, endingbackground;

    private static boolean start;
    private static Music ingamemusic;

    private Random random;

    private Vector2 touch;
    private SpaceAgila game;

    Sound explode, life;

    boolean final_level_stage;
    ArrayList<Asteroid> asteroids;
    ArrayList<Big_Asteroid> big_asteroids;
    ArrayList<Comet> comets;
    ArrayList<BonusLife> bonuslifes;
    ArrayList<Explosion> explosions;

    Texture blank;
    private Texture level1, level2, level3, level4, level5, level6, level7, level8, level9, level10, level11, level12, level13, level14, level15, final_level;
    private Texture playingame, pauseingame, pausedbanner, mainMenuBanner, resume;
    private Texture soundButtonPlay, soundButtonMute;
    private Socket socket;
    int temp_players;
    CollisionRect playerRect;
    CollisionRect asteroidRect, cometRect;

    float health = 1f;//0 = dead, 1 = alive

    public static float VOLUME;

    public MultiplayerGameScreen1(SpaceAgila game) {
        connectSocket();
        configSocketEvents();

        this.game = game;
        final_level_stage = false;
        VOLUME = 1.0f;
        start = false;
        adjustX = false;
        endingbackground = false;
        blank = new Texture("white.png");

        explode = Gdx.audio.newSound(Gdx.files.internal("explode.ogg"));
        life = Gdx.audio.newSound(Gdx.files.internal("life.ogg"));

        asteroids = new ArrayList<>();
        big_asteroids = new ArrayList<>();
        explosions = new ArrayList<>();
        comets = new ArrayList<>();
        bonuslifes = new ArrayList<>();

        playerRect = new CollisionRect(0, 0, SHIP_WIDTH, SHIP_HEIGHT);
        asteroidRect = new CollisionRect(0, 0, Asteroid.WIDTH, Asteroid.HEIGHT);
        cometRect = new CollisionRect(0, 0, Big_Asteroid.WIDTH, Big_Asteroid.HEIGHT);

        random = new Random();
        asteroidSpawnTimer = random.nextFloat() * (MAX_ASTEROID_SPAWN_TIMER_LEVEL - MIN_ASTEROID_SPAWN_TIMER_LEVEL) + MIN_ASTEROID_SPAWN_TIMER_LEVEL;

        roll = 1;
        rolls = new Animation[4];
        roll2 = 1;
        rolls2 = new Animation[4];
        playerShip = TextureRegion.split(new Texture("Lightning-edit.png"), SHIP_WIDTH_PIXEL, SHIP_HEIGHT_PIXEL);
        friendlyShip = TextureRegion.split(new Texture("other_players.png"), SHIP_WIDTH_PIXEL, SHIP_HEIGHT_PIXEL);
        friendlyPlayers = new HashMap<String, MultiplayerShip>();
        rolls[roll] = new Animation(SHIP_ANIMATION_SPEED, playerShip[0]);
        rolls2[roll2] = new Animation(SHIP_ANIMATION_SPEED, friendlyShip[0]);


        playingame = new Texture("playingame.png");
        pauseingame = new Texture("pauseingame.png");

        pausedbanner = new Texture("pausedbanner.png");
        mainMenuBanner = new Texture("main-menu.png");

        ingamemusic = Gdx.audio.newMusic(Gdx.files.internal("ingamemusicfinal.mp3"));
        ingamemusic.play();
        ingamemusic.setLooping(true);

        soundButtonPlay = new Texture("playsound.png");
        soundButtonMute = new Texture("mute.png");

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        System.out.println(no_of_players);


        if(friendlyPlayers != null && no_of_players == 2){
            start = true;
        }

        if(start && no_of_players == 1){
            disconnectSocket();
            this.dispose();
            game.setScreen(new VictoryScreen1(game));
            return;
        }
        handleInput(Gdx.graphics.getDeltaTime());
        //level
        generalUpdateLevel1(delta, MIN_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, MAX_ASTEROID_SPAWN_TIMER_LEVEL - 0.3f, 0.3f, 0.7f, 2f, 4f, 5f, 10f);
    }


    //movement code
    public void handleInput(float dt){
        if(player!=null){
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                    x -= SPEED * Gdx.graphics.getDeltaTime();
                }
                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                    x += SPEED * Gdx.graphics.getDeltaTime();

                    if (x + SHIP_WIDTH > SpaceAgila.WIDTH_DESKTOP) {
                        x = SpaceAgila.WIDTH_DESKTOP - SHIP_WIDTH;
                    }
                }
                if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                    y += SPEED * Gdx.graphics.getDeltaTime();

                    if (y + SHIP_HEIGHT > SpaceAgila.HEIGHT_DESKTOP) {
                        y = SpaceAgila.HEIGHT_DESKTOP - SHIP_HEIGHT;
                    }
                }
                if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                    y -= SPEED * Gdx.graphics.getDeltaTime();

                    if (y < 0) {
                        y = 0;
                    }
                }

                if (dragging) {
                    player.setX(game.cam.getInputInGameWorld().x - SHIP_WIDTH / 2);
                    player.setY(((SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y) + SHIP_HEIGHT / 2));
                }
                if (game.cam.getInputInGameWorld().x < player.getX() + SHIP_WIDTH && game.cam.getInputInGameWorld().x > player.getX() && SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y < player.getY() + SHIP_HEIGHT && SpaceAgila.HEIGHT_DESKTOP + game.cam.getInputInGameWorld().y > player.getY()) {
                    if (Gdx.input.isTouched()) {
                        player.setX(game.cam.getInputInGameWorld().x - SHIP_WIDTH / 2);
                        player.setY(((SpaceAgila.HEIGHT_DESKTOP - game.cam.getInputInGameWorld().y) + SHIP_HEIGHT / 2));
                        dragging = true;
                    } else {
                        dragging = false;
                    }
                }
                if (!adjustX) {
                    if (player.getX() < 0)
                        player.setX(0);
                    if (player.getX() + SHIP_WIDTH > SpaceAgila.WIDTH_DESKTOP)
                        player.setX(SpaceAgila.WIDTH_DESKTOP - SHIP_WIDTH);
                } else {
                    if (player.getX() < 25)
                        player.setX(25);
                    if (player.getX() + SHIP_WIDTH > SpaceAgila.WIDTH_DESKTOP - 25)
                        player.setX(SpaceAgila.WIDTH_DESKTOP - SHIP_WIDTH - 25);
                }
                if (player.getY() + SHIP_HEIGHT > SpaceAgila.HEIGHT_DESKTOP / 2) {
                    player.setY(SpaceAgila.HEIGHT_DESKTOP / 2 - SHIP_HEIGHT);
                }
                if (player.getY() < 0) {
                    player.setY(0);
                }
            if(ending){
                player.setY(player.getY()+20);
            }

            //after player moves, update collision rect
            playerRect.move(player.getX(), player.getY());

            updateServer(Gdx.graphics.getDeltaTime());
        }

    }

    public void updateServer(float dt){
        timer += dt;
        if (timer >= UPDATE_TIME && playerShip != null && player.hasMoved()) {
            JSONObject data = new JSONObject();
            try {
                data.put("no_of_players", no_of_players);
                data.put("x", player.getX());
                data.put("y", player.getY());
                data.put("start", start);
                socket.emit("playerMoved", data);
            } catch (JSONException e) {
                Gdx.app.log("SocketIO", "Error sending update data.");
            }
        }

    }

    public void generalUpdateLevel1(float delta, float minAsteroidSpawnTimer, float maxAsteroidSpawnTimer, float big_asteroid_MIN_SpawnTimer, float big_asteroid_MAX_SpawnTimer, float minCometSpawnTimer, float maxCometSpawnTimer, float min_bonuslifeSpawnTimer, float max_bonuslifeSpawnTimer) {
//        if(no_of_players >= 2){
//            start = true;
//        }
        if(start) {
            ArrayList<Asteroid> asteroidsToRemove = new ArrayList<>();
            ArrayList<Big_Asteroid> big_asteroidsToRemove = new ArrayList<>();
            ArrayList<Comet> cometsToRemove = new ArrayList<>();
            ArrayList<BonusLife> bonuslifeToRemove = new ArrayList<>();

            //asteroid spawn code + update
            //asteroid spawn code
            asteroidSpawnTimer -= delta;
            if (asteroidSpawnTimer <= 0) {
                asteroidSpawnTimer = random.nextFloat() * (maxAsteroidSpawnTimer - minAsteroidSpawnTimer) + minAsteroidSpawnTimer;
                asteroids.add(new Asteroid(random.nextInt(SpaceAgila.WIDTH_DESKTOP - Asteroid.WIDTH)));
            }
            //asteroid update code
            for (Asteroid asteroid : asteroids) {
                asteroid.update(delta);
                if (asteroid.remove) {
                    asteroidsToRemove.add(asteroid);
                }
            }

//            if (asteroids != null) {
//                JSONObject data = new JSONObject();
//                try {
//                    for (int i = 0; i < asteroids.size(); i++) {
//                        data.put("asteroid", asteroids.get(i));
//                    }
//                    socket.emit("asteroidSpawned", data);
//                } catch (JSONException e) {
//                    Gdx.app.log("SocketIO", "Error sending update data.");
//                }
//            }

            //big asteroid spawn code + update
            //big asteroid spawn code
            big_asteroidSpawnTimer -= delta;
            if (big_asteroidSpawnTimer <= 0) {
                big_asteroidSpawnTimer = random.nextFloat() * (big_asteroid_MAX_SpawnTimer - big_asteroid_MIN_SpawnTimer) + big_asteroid_MIN_SpawnTimer;
                big_asteroids.add(new Big_Asteroid(random.nextInt(SpaceAgila.WIDTH_DESKTOP - Asteroid.WIDTH)));
            }
            //big asteroid update code
            for (Big_Asteroid big_asteroid : big_asteroids) {
                big_asteroid.update(delta);
                if (big_asteroid.remove) {
                    big_asteroidsToRemove.add(big_asteroid);
                }
            }
            //comet spawn code + update
            //comet spawn code
            comets_SpawnTimer -= delta;
            if (comets_SpawnTimer <= 0) {
                comets_SpawnTimer = random.nextFloat() * (maxCometSpawnTimer - minCometSpawnTimer) + minCometSpawnTimer;
                comets.add(new Comet(random.nextInt(SpaceAgila.WIDTH_DESKTOP - Asteroid.WIDTH)));
            }
            //comet update code
            for (Comet comet : comets) {
                comet.update(delta);
                if (comet.remove) {
                    cometsToRemove.add(comet);
                }
            }
            //bonus life spawn code + update
            //bonus life spawn code
            bonuslife_SpawnTimer -= delta;
            if (bonuslife_SpawnTimer <= 0) {
                bonuslife_SpawnTimer = random.nextFloat() * (max_bonuslifeSpawnTimer - min_bonuslifeSpawnTimer) + min_bonuslifeSpawnTimer;
                bonuslifes.add(new BonusLife(random.nextInt(SpaceAgila.WIDTH_DESKTOP - BonusLife.WIDTH)));
            }
            //bonus life update code
            for (BonusLife bonuslife : bonuslifes) {
                bonuslife.update(delta);
                if (bonuslife.remove) {
                    bonuslifeToRemove.add(bonuslife);
                }
            }

            //Update explosions
            ArrayList<Explosion> explosionsToRemove = new ArrayList<>();
            for (Explosion explosion : explosions) {
                explosion.update(delta);
                if (explosion.remove)
                    explosionsToRemove.add(explosion);
            }
            explosions.removeAll(explosionsToRemove);

            //after all updates, check for collision
            //asteroid collision
            for (Asteroid asteroid : asteroids) {
                for (Comet comet : comets) {
                    if (asteroid.getCollisionRect().collidesWith(comet.getCollisionRect())) {
                        asteroidsToRemove.add(asteroid);
                        explode.play(VOLUME);
                        Gdx.input.vibrate(new long[]{0, 200}, -1);
                        explosions.add(new Explosion(asteroid.getX(), asteroid.getY()));
                    }
                }
                if (asteroid.getCollisionRect().collidesWith(playerRect)) {
                    asteroidsToRemove.add(asteroid);
                    explode.play(VOLUME);
                    Gdx.input.vibrate(new long[]{0, 200}, -1);
                    explosions.add(new Explosion(player.getX(), player.getY()));
                    health -= 0.34f;
                    if (health <= 0) {
                        disconnectSocket();
                        this.dispose();
                        game.setScreen(new GameOverScreen(game,1));
                        return;
                    }
                }
            }
            //asteroids remove code
            asteroids.removeAll(asteroidsToRemove);

            //big asteroid collision
            for (Big_Asteroid big_asteroid : big_asteroids) {
                if (!final_level_stage) {
                    for (Comet comet : comets) {
                        if (big_asteroid.getCollisionRect().collidesWith(comet.getCollisionRect())) {
                            big_asteroidsToRemove.add(big_asteroid);
                            explode.play(VOLUME);
                            Gdx.input.vibrate(new long[]{0, 200}, -1);
                            explosions.add(new Explosion(big_asteroid.getX(), big_asteroid.getY()));
                        }
                    }
                    if (big_asteroid.getCollisionRect().collidesWith(playerRect)) {
                        big_asteroidsToRemove.add(big_asteroid);
                        explode.play(VOLUME);
                        Gdx.input.vibrate(new long[]{0, 200}, -1);
                        explosions.add(new Explosion(player.getX(), player.getY()));
                        health -= 0.5f;
                        if (health <= 0) {
                            disconnectSocket();
                            this.dispose();
                            game.setScreen(new GameOverScreen(game,1));
                            return;
                        }
                    }
                }
            }
            //big asteroids remove code
            big_asteroids.removeAll(big_asteroidsToRemove);

            //comet collision
            for (Comet comet : comets) {
                if (!final_level_stage) {
                    if (comet.getCollisionRect().collidesWith(playerRect)) {
                        cometsToRemove.add(comet);
                        explode.play(VOLUME);
                        Gdx.input.vibrate(new long[]{0, 200}, -1);
                        explosions.add(new Explosion(player.getX(), player.getY()));
                        health -= 9999f;

                        if (health <= 0) {
                            disconnectSocket();
                            this.dispose();
                            game.setScreen(new GameOverScreen(game,1));
                            return;
                        }
                    }
                }
            }
            //comets remove code
            comets.removeAll(cometsToRemove);

            //bonus life remove code
            for (BonusLife bonuslife : bonuslifes) {
                if (bonuslife.getCollisionRect().collidesWith(playerRect)) {
                    life.play(VOLUME);
                    bonuslifeToRemove.add(bonuslife);
                    if (health >= 1) {
                        health = 1;
                        health += 0;
                    } else
                        health += 0.34f;
                }
            }
            bonuslifes.removeAll(bonuslifeToRemove);
        }
        stateTime += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        TextureRegion currentFrame = (TextureRegion) rolls[roll].getKeyFrame(stateTime, true);
        TextureRegion currentFrame1 = (TextureRegion) rolls2[roll2].getKeyFrame(stateTime, true);
        game.batch.begin();

        game.scrollingBackground.updateAndRender(delta, game.batch);

        if(start) {
            for (Asteroid asteroid : asteroids) {
                asteroid.render(game.batch);
            }
            for (Big_Asteroid big_asteroid : big_asteroids) {
                big_asteroid.render(game.batch);
            }
            for (Comet comet : comets) {
                comet.render(game.batch);
            }
            for (BonusLife bonuslife : bonuslifes) {
                bonuslife.render(game.batch);
            }
            for (Explosion explosion : explosions) {
                explosion.render(game.batch);
            }
        }

        if(health <= 0.32f)
            game.batch.setColor(Color.RED);
        else if(health <=0.66f && health > 0.32f)
            game.batch.setColor(Color.ORANGE);
        else
            game.batch.setColor(Color.GREEN);

        game.batch.draw(blank, 0, 0, SpaceAgila.WIDTH_DESKTOP * health, 10);

        game.batch.setColor(Color.WHITE);

        for(HashMap.Entry<String, MultiplayerShip> entry : friendlyPlayers.entrySet()){
            game.batch.draw(entry.getValue(),entry.getValue().getX(),entry.getValue().getY(), SHIP_WIDTH, SHIP_HEIGHT);
        }
        if(player != null) {
            game.batch.draw(currentFrame, player.getX(), player.getY(), SHIP_WIDTH, SHIP_HEIGHT);
        }
        game.batch.end();
    }

    public void connectSocket(){
        try{
            socket = IO.socket("http://192.168.100.12:3000");
            socket.connect();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void disconnectSocket(){
        try{
            socket.disconnect();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public void configSocketEvents(){
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                no_of_players = 1;
                Gdx.app.log("SocketIO", "Connected");
                player = new MultiplayerShip(playerShip);
                Gdx.app.log("SocketIO", "No. of players: " + no_of_players);
            }
        }).on("socketID", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try{
                    String id = data.getString("id");
                    Gdx.app.log("SocketIO", "My ID: " + id);
                }
                catch (JSONException e){
                    Gdx.app.log("SocketIO", "Error getting ID");
                }
            }
        }).on("newPlayer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try{
                    no_of_players += 1;
                    String playerId = data.getString("id");
                    friendlyPlayers.put(playerId, new MultiplayerShip(friendlyShip));
                    Gdx.app.log("SocketIO", "New Player ID: " + playerId);
                    Gdx.app.log("SocketIO", "No. of players: " + no_of_players);
                }
                catch (JSONException e){
                    Gdx.app.log("SocketIO", "Error getting New Player ID");
                }
            }
        }).on("playerDisconnected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try{
                    no_of_players -= 1;
                    String id = data.getString("id");
                    friendlyPlayers.remove(id);
                    Gdx.app.log("SocketIO", "No. of players: " + no_of_players);
                }
                catch (JSONException e){
                    Gdx.app.log("SocketIO", "Error getting New Player ID");
                }
            }
        }).on("playerMoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try{
                    String playerId = data.getString("id");
                    Double x = data.getDouble("x");
                    Double y = data.getDouble("y");
                    start = data.getBoolean("start");
                    no_of_players = data.getInt("no_of_players");
                    if(friendlyPlayers.get(playerId) != null){
                        friendlyPlayers.get(playerId).setPosition(x.floatValue(),y.floatValue());
                    }
                }
                catch (JSONException e){
                    Gdx.app.log("SocketIO", "Error getting New Player ID");
                }
            }
        }).on("asteroidSpawned", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try{
                    System.out.println(data.getString("asteroid"));
                }
                catch (JSONException e){
                    Gdx.app.log("SocketIO", "Error getting asteroid");
                }
            }
        }).on("getPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray objects = (JSONArray) args[0];
                try{
                    for(int i = 0; i < objects.length(); i++){
                        MultiplayerShip coopPlayer = new MultiplayerShip(friendlyShip);
                        Vector2 position = new Vector2();
                        position.x = ((Double) objects.getJSONObject(i).getDouble("x")).floatValue();
                        position.y = ((Double) objects.getJSONObject(i).getDouble("y")).floatValue();
                        coopPlayer.setPosition(position.x, position.y);

                        friendlyPlayers.put(objects.getJSONObject(i).getString("id"), coopPlayer);

                    }
                } catch (JSONException e){

                }
            }
        });
    }

    @Override
    public void create() {

    }

    @Override
    public void resize(int width, int height) {

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
        ingamemusic.dispose();
        explode.dispose();
        disconnectSocket();
    }

}
