package com.liorprigal.mariobros.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.liorprigal.mariobros.MarioBros;
import com.liorprigal.mariobros.Screens.PlayScreen;

/**
 * Created by LiorUser on 24/02/2016.
 */
public abstract class Enemy extends Sprite {

    protected World world;
    protected PlayScreen screen;
    public Body b2body;
    public Vector2 velocity;
    public boolean wasHit;

    public Enemy(PlayScreen screen, float x, float y){
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(-1, -2);
        b2body.setActive(false);
        wasHit = false;
    }

    protected  abstract void defineEnemy();
    public abstract void hitOnHead(Mario mario,MarioBros game);
    public abstract void update(float dt);
    public abstract void onEnemyHit(Enemy enemy);

    public boolean getWasHit(){
        return  wasHit;
    }

    public void reverseVelocity(boolean x, boolean y)
    {
        if(x)
        {
            velocity.x = -velocity.x;
        }
        if(y)
        {
            velocity.y = -velocity.y;
        }
    }
}
