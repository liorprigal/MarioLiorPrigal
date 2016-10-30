package com.liorprigal.mariobros.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.liorprigal.mariobros.Items.ItemDef;
import com.liorprigal.mariobros.Items.Mushroom;
import com.liorprigal.mariobros.MarioBros;
import com.liorprigal.mariobros.Scenes.Hud;
import com.liorprigal.mariobros.Screens.PlayScreen;

/**
 * Created by LiorUser on 22/02/2016.
 */
public class Coin extends InteractiveTileObject{
    private  static TiledMapTileSet tileSet;
    private  boolean wasHit;
    private  final int BLANK_COIN = 28; //the unique id was 27 so we add 1 to that number
    // we can right click in the tile program to find unique id of the selected tile, tiled starts counting from 0 index and libgdx tileset starts from 1
    public Coin(PlayScreen screen, MapObject object)
    {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
        wasHit = false;
    }

    @Override
    public void onHeadHit(Mario mario, MarioBros game) {
        Gdx.app.log("Coin", "Collision");
        if(getCell().getTile().getId() == BLANK_COIN){
            game.manager.get("audio/sounds/bump.wav", Sound.class).play();
        }
        else {
            if(object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PPM),
                        Mushroom.class));
                game.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            }
            else {
                game.manager.get("audio/sounds/coin.wav", Sound.class).play();
            }
        }
        if(!wasHit) {
            getCell().setTile(tileSet.getTile(BLANK_COIN));
            wasHit = true;
            Hud.addScore(200);
        }
    }
}
