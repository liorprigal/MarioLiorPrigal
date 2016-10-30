package com.liorprigal.mariobros.Items;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by LiorUser on 25/02/2016.
 */
public class ItemDef {
    public Vector2 position;
    public Class<?> type;

    public ItemDef(Vector2 position, Class<?> type) {
        this.position = position;
        this.type = type;
    }
}
