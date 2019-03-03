package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import java.util.Random;

/**
 * Created by asus on 2017/6/29.
 */

public class Enemy {
    boolean beingHit;

    public float health;

    Vector2 position;
    Vector2 velocity;
    Sprite sprite;

    Rectangle rectangle;
    Rectangle attackRec;

    Texture walk;
    Texture walkLeft;
    Texture attack;
    Texture attackLeft;
    Texture idle;
    Texture idleLeft;
    Texture damaged;
    Texture damagedLeft;

    Animation walkAnimation;
    Animation walkLeftAnimation;
    Animation attackAnimation;
    Animation attackAnimationLeft;
    Animation idleAnimation;
    Animation idleAnimationLeft;

    Random r1;
    Random r2;

    AttackState attackState;
    Facing facing;
    Moving moving;
    Enemy(Vector2 position){
        health = 100;
        beingHit = false;
        attackState = AttackState.idle;
        facing = Facing.right;
        moving = Moving.idle;
        //Textures
        walk = new Texture("warewolfWalk.png");
        walkLeft = new Texture("wolfWalkLeft128.png");
        attack = new Texture("warewolfAttack128.png");
        attackLeft = new Texture("warewolfAttack128Left.png");
        idle = new Texture("warewolfIdle.png");
        idleLeft = new Texture("warewolfIdleLEft128.png");
        damaged = new Texture("warewolfDamaged128.png");
        damagedLeft = new Texture("warewolfDamagedLeft128.png");

        r1 = new Random();
        r2 = new Random();
        //Animations
        walkAnimation = new Animation(new TextureRegion(walk),4,.5f);
        walkLeftAnimation = new Animation(new TextureRegion(walkLeft),4,.5f);
        idleAnimation = new Animation(new TextureRegion(idle),2,1);
        idleAnimationLeft = new Animation(new TextureRegion(idleLeft),2,1);
        attackAnimation = new Animation(new TextureRegion(attack),4,.7f);
        attackAnimationLeft = new Animation(new TextureRegion(attackLeft),4,.7f);
        //hit boxes
        rectangle = new Rectangle(position.x,position.y,walk.getWidth()/8,walk.getHeight()/4);

        attackRec = new Rectangle(position.x + 32,position.y,walk.getWidth()/8,walk.getHeight()/4);
        if(facing == Facing.left)
            attackRec.setPosition(position.x - 32, position.y);
        this.position = position;

        velocity = new Vector2(0,0);

        sprite = new Sprite(walkAnimation.getFrame());
        sprite.setPosition(position.x,position.y);

        setRecPosition();
    }

    public void update(float delta){

        if(facing == Facing.left)
            attackRec.setPosition(position.x - 32, position.y);
        else if(facing == Facing.right)
            attackRec.setPosition(position.x + 32, position.y);
        if(position.y >= 0){
            velocity.y -= 20 * delta;
            position.y += velocity.y;
        }
        sprite.setPosition(position.x,position.y);
        setRecPosition();
    }

    public void toWardTarget(Prince player,float delta){
        moving = Moving.moving;
        if(position.x + Constants.ENEMY_SPOT_RANGE < player.rectangle.x ||
                position.x - Constants.ENEMY_SPOT_RANGE > player.rectangle.x){
            moving = Moving.idle;
            if(facing == Facing.right){
                sprite.setRegion(idleAnimation.getFrame());
                idleAnimation.update(delta);
            }
            else if(facing == Facing.left){
                sprite.setRegion(idleAnimationLeft.getFrame());
                idleAnimationLeft.update(delta);
            }
        }
        if(position.x < player.rectangle.x - 10){
            moveRight(delta);
        }
        if(position.x > player.rectangle.x + 5){
            moveLeft(delta);
        }
        if(position.x < player.rectangle.x + Constants.ENEMY_ATTACK_RANGE
                && position.x > player.rectangle.x - Constants.ENEMY_ATTACK_RANGE){
            attackState = AttackState.attacking;
            moving = Moving.idle;
        }
        else
            attackState = AttackState.idle;

       determineAni(delta);

    }

    public void setRecPosition(){
        rectangle.x = position.x;
        rectangle.y = position.y;
    }


    public void moveRight(float delta){
        if(! beingHit){
            if(moving == Moving.moving && attackState != AttackState.attacking){
                facing = Facing.right;
                position.x +=  Constants.WOLF_MOVE_SPEED * delta;
                walkAnimation.update(delta);
                sprite.setRegion(walkAnimation.getFrame());
            }
        }
    }

    public void moveLeft(float delta){
    if(!beingHit){
        if(moving == Moving.moving && attackState != AttackState.attacking){
        facing = Facing.left;
        position.x -= Constants.WOLF_MOVE_SPEED * delta;
        walkLeftAnimation.update(delta);
        sprite.setRegion(walkLeftAnimation.getFrame());
        }
        }
    }

    public void idleAttack(float delta){
        if(attackState == AttackState.attacking){
            if(facing == Facing.right){
                attackAnimation.update(delta);
                sprite.setRegion(attackAnimation.getFrame());
            }
            else if(facing == Facing.left){
                attackAnimationLeft.update(delta);
                sprite.setRegion(attackAnimationLeft.getFrame());
            }
        }
    }


    public void determineAni(float delta){
        idleAttack(delta);
    }

    public boolean hit(Prince player){
        if(this.attackRec.overlaps(player.rectangle) && attackState == AttackState.attacking){
            if(facing == Facing.right && attackAnimation.getFrameNum() == 3)
                return true;
            if(facing == Facing.left && attackAnimationLeft.getFrameNum() == 3)
                return true;
        }
        return false;
    }

    public void setPosition(Vector2 position){
        this.position = position;
    }

    public Vector2 getPosition(){
        return this.position;
    }

    public void render(SpriteBatch batch){
        sprite.draw(batch);
    }

    public void dispose(){
        walk.dispose();
        walkLeft.dispose();
        attack.dispose();
        attackLeft.dispose();
        idle.dispose();
        idleLeft.dispose();
        damaged.dispose();
        damagedLeft.dispose();
    }


    enum AttackState{
        attacking,
        idle
    }

    enum Facing{
        right,
        left
    }
    enum Moving{
        moving,
        idle
    }
}
