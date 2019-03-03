package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by asus on 2017/6/20.
 */
public class Prince {

    int health;
    int defendBar;
    Rectangle attackRec;
    Rectangle rectangle;
    Sprite sprite;

    Vector2 position;
    Vector2 velocity;


    JumpState jumpState;
    AttackState attackState;

    Animation knightAnimation;
    Animation knightAnimationLeft;
    Animation knightIdle;
    Animation knightIdleLeft;
    Animation knightAttack;
    Animation knightAttackLeft;
    Animation TransformAni;
    Animation TransformAniLeft;
    Animation defendForwardAni;
    Animation defendForwardAniLeft;
    Animation defendBackAni;
    Animation defendBackLeftAni;


    Texture walk;
    Texture idle;
    Texture idleLeft;
    Texture walkLeft;
    Texture attack;
    Texture attackLeft;
    Texture Transform;
    Texture TransformLeft;
    Texture defendIdle;
    Texture defendIdleLeft;
    Texture defendWalkForward;
    Texture defendWalkForwardLeft;
    Texture defendWalkBack;
    Texture defendWalkBackLeft;

    Facing facing;
    MoveState moveState;
    Mode mode;

    public Prince(Vector2 position){
        //All the Texture image
        attackLeft = new Texture("KnightAttackSimpleLeft128.png");
        idle = new Texture("KnightIdle128.png");
        walkLeft = new Texture("KnightWalkSwordDownLeft128.png");
        walk = new Texture("KnightWalkSwordDown128.png");
        idleLeft = new Texture("KnightIdleLeft128.png");
        attack = new Texture("KnightAttackSimple128.png");
        Transform = new Texture("KnightTransform128.png");
        TransformLeft = new Texture("KnightTransformLeft128.png");
        defendIdle = new Texture("KnightDefendIdle128.png");
        defendIdleLeft = new Texture("knightDefendIdleLeft.png");
        defendWalkForward = new Texture("KnightDefendWalkForward128.png");
        defendWalkForwardLeft = new Texture("KnightDefendWalkForwardLeft128.png");
        defendWalkBack = new Texture("KnightDefendWalkBack128.png");
        defendWalkBackLeft = new Texture("KnightDefendWalkLeft128.png");
        //all the animation
        knightAnimation = new Animation(new TextureRegion(walk),8,1f);
        knightAnimationLeft = new Animation(new TextureRegion(walkLeft),8, 1f);
        knightIdle = new Animation(new TextureRegion(idle),2,1.2f);
        knightIdleLeft = new Animation(new TextureRegion(idleLeft),2,1.2f);
        knightAttack = new Animation(new TextureRegion(attack),7,.4f);
        knightAttackLeft = new Animation(new TextureRegion(attackLeft),7,.4f);
        TransformAni = new Animation(new TextureRegion(Transform),5,1);
        TransformAniLeft = new Animation(new TextureRegion(TransformLeft),5,1);
        defendForwardAni = new Animation(new TextureRegion(defendWalkForward),8,1f);
        defendForwardAniLeft = new Animation(new TextureRegion(defendWalkForwardLeft),8,1f);
        defendBackAni = new Animation(new TextureRegion(defendWalkBack),8,1);
        defendBackLeftAni = new Animation(new TextureRegion(defendWalkBackLeft),8,1);

        velocity = new Vector2(0,0);
        sprite = new Sprite(knightIdle.getFrame());
        this.position = position;
        sprite.setPosition(position.x,position.y);

        jumpState = JumpState.landed;
        attackState = AttackState.idle;
        facing = Facing.right;
        moveState = MoveState.idle;
        mode = Mode.attackMode;

        //a rectangle for the body to detect if it is damaged
        rectangle = new Rectangle(position.x,position.y,sprite.getWidth(),sprite.getHeight());
        setPosition();
        // a rectangle for detecting weather it is hitting a enemy
        attackRec = new Rectangle(position.x + 20,position.y, 45, 64);
        // attack will be blocked if it is greater than 0
        defendBar = 100;
        health = 100;
    }
    //make the rectangle always goes where the character goes
    public void setPosition(){
        position.x = rectangle.x;
        position.y = rectangle.y;

    }
    public void update(float delta) {
        sprite.setPosition(position.x, position.y);
        setPosition();
        //if it is attack mode, see which way he is facing,
        // and determine which texture to use and what action to perform
        if(mode == Mode.attackMode){
            if (facing == Facing.left) {
                //use the facing left texture and vice versa
                knightIdleLeft.update(delta);
                sprite = new Sprite(knightIdleLeft.getFrame());
                attackRec.setPosition(rectangle.x - 35,rectangle.y);
            } else if (facing == Facing.right) {
                knightIdle.update(delta);
                sprite = new Sprite(knightIdle.getFrame());
                attackRec.setPosition(rectangle.x + 60, rectangle.y);
            }
            if (attackState == AttackState.idle) {
                if (facing == Facing.left) {
                    knightIdleLeft.update(delta);
                    sprite = new Sprite(knightIdleLeft.getFrame());
                } else if (facing == Facing.right) {
                    knightIdle.update(delta);
                    sprite = new Sprite(knightIdle.getFrame());
                }
                sprite.setPosition(rectangle.x, rectangle.y);
            }
        }
        //if he is in defend mode, use the defending texture
       else if(mode == Mode.defendMode){
            if(facing == Facing.right){
                sprite = new Sprite(defendIdle);
            }
            else if(facing == Facing.left){
                sprite.setRegion(defendIdleLeft);
            }
            sprite.setPosition(rectangle.x,rectangle.y);
        }
        //make sure he does not fall down the screen
        if(position.y >= 0){
        velocity.y -= Constants.FALL_VELOCITY * delta;
        rectangle.y += velocity.y;
        }

        if (rectangle.y <= 0) {
            rectangle.y = 0;
            velocity.y = 0;
            jumpState = JumpState.landed;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.J) &&
                jumpState != JumpState.Jumping &&
                mode != Mode.defendMode) {
            attackState = AttackState.attacking;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && jumpState == JumpState.landed
                && (attackState == AttackState.idle|| moveState == MoveState.moving))
            jump(Gdx.graphics.getDeltaTime());

        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            moveState = MoveState.moving;
            moveLeft(delta);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.J))
            moveState = MoveState.idle;

        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            moveState = MoveState.moving;
            moveRight(delta);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.L) && mode == Mode.attackMode){
            mode = Mode.defendMode;
        }

        else if(Gdx.input.isKeyJustPressed(Input.Keys.L) && mode == Mode.defendMode)
            mode = Mode.attackMode;

        determineAni(delta);

    }
    public void moveRight(float delta){
        //action that perform when moving right
        if(jumpState != JumpState.Jumping &&
                moveState == MoveState.moving &&
                attackState != AttackState.attacking&&
                mode == Mode.attackMode){
            facing = Facing.right;
            knightAnimation.update(delta);
            sprite = new Sprite(knightAnimation.getFrame());
            sprite.setPosition(rectangle.x,rectangle.y);
            rectangle.x += delta * Constants.MOVE_SPEED;
            attackRec.x += delta * Constants.MOVE_SPEED;
        }
        if(jumpState == JumpState.Jumping){
            rectangle.x += delta * Constants.MOVE_SPEED;
            attackRec.x += delta * Constants.MOVE_SPEED;
        }
        if(jumpState != JumpState.Jumping &&
                moveState == MoveState.moving &&
                attackState == AttackState.attacking)
            rectangle.x += Constants.ATTACKING_SPEED * delta;
        //action performed when defending and facing right
        if(mode == Mode.defendMode && facing == Facing.right){
            defendForwardAni.update(delta);
            sprite = new Sprite(defendForwardAni.getFrame());
            sprite.setPosition(rectangle.x,rectangle.y);
            rectangle.x += delta * Constants.DEFEND_MOVE_SPEED;
            attackRec.x += delta * Constants.DEFEND_MOVE_SPEED;
        }
        //action performed when defending and facing left
        if(mode == Mode.defendMode && facing == Facing.left){
            defendBackAni.update(delta);
            sprite = new Sprite(defendBackAni.getFrame());
            sprite.setPosition(rectangle.x,rectangle.y);
            rectangle.x += delta * Constants.DEFEND_MOVE_SPEED;
            attackRec.x += delta * Constants.DEFEND_MOVE_SPEED;
        }
    }

    public void moveLeft(float delta){
        //actions that performed when moving left
        if(jumpState != JumpState.Jumping &&
                moveState == MoveState.moving &&
                attackState != AttackState.attacking&&
                mode == Mode.attackMode) {
            facing = Facing.left;
            knightAnimationLeft.update(delta);
            sprite = new Sprite(knightAnimationLeft.getFrame());
            sprite.setPosition(rectangle.x ,rectangle.y );
            rectangle.x -= delta * Constants.MOVE_SPEED;
            attackRec.x -= delta * Constants.MOVE_SPEED;
        }
        if(jumpState == JumpState.Jumping){
            attackRec.x -= delta * Constants.MOVE_SPEED;
            rectangle.x -= delta * Constants.MOVE_SPEED;
        }
        if(jumpState != JumpState.Jumping && moveState == MoveState.moving && attackState == AttackState.attacking)
            rectangle.x -= Constants.ATTACKING_SPEED * delta;
        //action performed when defending and facing left
        if(mode == Mode.defendMode && facing == Facing.left){
            defendForwardAniLeft.update(delta);
            sprite = new Sprite(defendForwardAniLeft.getFrame());
            sprite.setPosition(rectangle.x, rectangle.y);
            rectangle.x -= delta * Constants.DEFEND_MOVE_SPEED;
            attackRec.x -= delta * Constants.DEFEND_MOVE_SPEED;
        }
        //action performed when defending and facing right
         if(mode == Mode.defendMode && facing == Facing.right){
            defendBackLeftAni.update(delta);
            sprite = new Sprite(defendBackLeftAni.getFrame());
             sprite.setPosition(rectangle.x,rectangle.y);
            rectangle.x -= delta * Constants.DEFEND_MOVE_SPEED;
            attackRec.x -= delta * Constants.DEFEND_MOVE_SPEED;
        }
    }

    public void jump(float delta){
        velocity.y += 400 * delta;
        jumpState = JumpState.Jumping;
    }

    public void determineAni(float delta){
        idleAttack(delta);
    }
    public void idleAttack(float delta){
        if(attackState == AttackState.attacking
                && jumpState != JumpState.Jumping ){
            //if it is already the 6th picture at the animation, halt and switch back to idle depending on facing
            if(facing == Facing.right ){
            knightAttack.update(delta);
            sprite.setRegion(knightAttack.getFrame());
            sprite.setPosition(rectangle.x, rectangle.y);
                if (knightAttack.getFrame() == knightAttack.getRegion().get(6)){
                    knightAttack.setFrameNum(0);
                    attackState = AttackState.idle;
                }
            }
            else if(facing == Facing.left ){
                knightAttackLeft.update(delta);
                sprite.setRegion(knightAttackLeft.getFrame());
                sprite.setPosition(rectangle.x - 64, rectangle.y);
                if (knightAttackLeft.getFrame() == knightAttackLeft.getRegion().get(6)){
                    knightAttackLeft.setFrameNum(0);
                    attackState = AttackState.idle;
                }
            }
        }
    }

    //true if player hits a enemy
    public boolean hit(Enemy enemy){
        if(attackState == AttackState.attacking &&
                (knightAttack.getFrameNum() >= 2 || knightAttackLeft.getFrameNum() >= 2)){
            if(attackRec.overlaps(enemy.rectangle)){
                if(enemy.facing == Enemy.Facing.right)
                    enemy.sprite.setRegion(enemy.damaged);
                if(enemy.facing == Enemy.Facing.left)
                    enemy.sprite.setRegion(enemy.damagedLeft);
                enemy.attackState = Enemy.AttackState.idle;
                return true;
            }
        }
            return false;
    }


    public void render(SpriteBatch batch){
        sprite.draw(batch);
    }

    public void dispose(){
        walk.dispose();
        idle.dispose();
        idleLeft.dispose();
        walkLeft.dispose();
        attack.dispose();
        attackLeft.dispose();
        defendWalkForwardLeft.dispose();
        defendIdle.dispose();
        defendWalkForward.dispose();
        defendWalkBackLeft.dispose();
        defendWalkBack.dispose();
    }

    enum JumpState {
        Jumping,
        landed
    }

    enum AttackState{
        attacking,
        idle
    }

    enum Facing{
        right,
        left
    }

    enum MoveState{
        idle,
        moving
    }

    enum Mode{
        attackMode,
        defendMode
    }

}
