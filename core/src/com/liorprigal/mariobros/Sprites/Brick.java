package com.liorprigal.mariobros.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.liorprigal.mariobros.MarioBros;
import com.liorprigal.mariobros.Scenes.Hud;
import com.liorprigal.mariobros.Screens.PlayScreen;

/**
 * Created by LiorUser on 22/02/2016.
 */
public class Brick extends InteractiveTileObject{
    public Brick(PlayScreen screen, MapObject object)
    {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario, MarioBros game) {
        Gdx.app.log("Brick", "Collision");
        if(mario.isBig()) {
            setCategoryFilter(MarioBros.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(100);
            game.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
        else{
            game.manager.get("audio/sounds/bump.wav", Sound.class).play();
        }
    }
}
