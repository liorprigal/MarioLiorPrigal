package com.liorprigal.mariobros.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.liorprigal.mariobros.Items.Item;
import com.liorprigal.mariobros.Items.ItemDef;
import com.liorprigal.mariobros.Items.Mushroom;
import com.liorprigal.mariobros.MarioBros;
import com.liorprigal.mariobros.Scenes.Hud;
import com.liorprigal.mariobros.Sprites.Enemy;
import com.liorprigal.mariobros.Sprites.Goomba;
import com.liorprigal.mariobros.Sprites.Mario;
import com.liorprigal.mariobros.Sprites.Turtle;
import com.liorprigal.mariobros.Tools.B2WorldCreator;
import com.liorprigal.mariobros.Tools.worldContactListener;

import java.util.PriorityQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by LiorUser on 20/02/2016.
 */
public class PlayScreen implements Screen{
    private MarioBros game;

    private TextureAtlas atlas;

    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;
    //Tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    //box2d vars
    private World world;
    private Box2DDebugRenderer b2dr; // graphical representation of our fixtures and bodies inside of our box2d world
    private B2WorldCreator creator;
    //sprites
    private Mario player;

    private Music music;

    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    public PlayScreen(MarioBros game)
    {
        atlas = new TextureAtlas("Mario_new.pack");

        this.game = game;
        gamecam = new OrthographicCamera();
        //create cam used to follow mario through cam world
        gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gamecam);
        hud = new Hud(game.batch);
        //Load our map and setup our map renderer
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);
        //initially set our gamcam to be centered correctly at the start of of map
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        //create our Box2D world, setting no gravity in X, -10 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0, -10), true);

        //allows for debug lines of our box2d world.
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        player = new Mario(this); // initialization of Mario class object

        world.setContactListener(new worldContactListener(game));

        music = game.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        //music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }

    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class){
                items.add(new Mushroom(this, idef.position.x, idef.position.y));
            }
        }
    }

    public TextureAtlas getAtlas()
    {
        return atlas;
    }

    @Override
    public void show() {

    }

    public void handleInput(float deltaTime){
        //control our player using immediate impulses
        if(player.currentState != Mario.State.DEAD) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && (player.b2Body.getLinearVelocity().y == 0)) {
                player.b2Body.applyLinearImpulse(new Vector2(0, 4f), player.b2Body.getWorldCenter(), true); // 4f works best in the Y direction can be changed
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2Body.getLinearVelocity().x <= 2) // 2 works best as maximum speed can be changed
            {
                player.b2Body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2Body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2Body.getLinearVelocity().x >= -2) // 2 works best as maximum speed can be changed
            {
                player.b2Body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2Body.getWorldCenter(), true);
            }
        }
    }

    public void update(float deltaTime)
    {//handle user input first
        handleInput(deltaTime);
        handleSpawningItems();

        world.step(1/60f, 6, 2);//time stamp is 60 times every 1 seconds, velocity and position the higher the more percise but takes more time to calculate

        player.update(deltaTime);
        for(Enemy enemy : creator.getEnemies())
        {
            enemy.update(deltaTime);
            if(enemy instanceof Turtle && ((Turtle) enemy).currentState == Turtle.State.DEAD && ((Turtle) enemy).getStateTime() > 3){
                creator.removeTurtle((Turtle) enemy);
            }
            if(enemy.getX() < player.getX() + 224 / MarioBros.PPM) // 224 is 14 blocks * 16 which is the tile size
            {
                enemy.b2body.setActive(true);
            }
        }

        for(Item item : items) {
            item.update(deltaTime);
        }

        hud.update(deltaTime);
        if(player.currentState != Mario.State.DEAD) {
            gamecam.position.x = player.b2Body.getPosition().x;
        }

        //update gamecam with correct coordinates after changes
        gamecam.update();
        //make renderer draw what our camera sees
        renderer.setView(gamecam);
    }

    @Override
    public void render(float delta) {
        // seperate game logic from render
        update(delta);
        //clear the screen with black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();// after clearing the screen
        //renderer our Box2DDebugLine
        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for(Enemy enemy : creator.getEnemies())
        {
            enemy.draw(game.batch);
        }
        for(Item item : items){
            item.draw(game.batch);
        }
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    public boolean gameOver(){
        if(player.currentState == Mario.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return  false;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    public TiledMap getMap(){
        return map;
    }

    public World getWorld(){
        return world;
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
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
