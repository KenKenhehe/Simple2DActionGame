package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.DelayedRemovalArray;

public class MyGdxGame extends ApplicationAdapter {

	SpriteBatch batch;
	boolean gameOver;
	OrthographicCamera camera;
	Prince player ;
	float screenWidth;
	float screenHeight;
	float deltaTime;
	Texture winSign;
	Texture loseSign;
	Sprite sprite;
	DelayedRemovalArray<Enemy> enemys;
	@Override
	public void create () {
		gameOver = false;
		deltaTime = Gdx.graphics.getDeltaTime();
		batch = new SpriteBatch();
		screenWidth = 300;
		screenHeight = 240;
		camera = new OrthographicCamera();
		camera.setToOrtho(false,screenWidth,screenHeight);
		player = new Prince(new Vector2(150,0));
		enemys = new DelayedRemovalArray<Enemy>();
		winSign = new Texture("youWinSign.png");
		loseSign = new Texture("youLoseSign.png");
		enemys.add(new Enemy(new Vector2(50,0)));
	}


	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 2, 20, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(gameOver == false) {
			batch.begin();
			for (Enemy enemy : enemys) {
				enemy.render(batch);
				enemy.update(Gdx.graphics.getDeltaTime());
				enemy.toWardTarget(player, Gdx.graphics.getDeltaTime());
				if (player.hit(enemy)) {
					enemy.health -= Constants.PLAYER_ATTACK_DAMAGE;
					if (enemy.health > 0) {
						enemy.sprite.setColor(Color.RED);
						enemy.beingHit = true;
						if (player.facing == Prince.Facing.right) {
							enemy.moving = Enemy.Moving.idle;
							enemy.attackState = Enemy.AttackState.idle;
							enemy.position.x += .5f;
						} else if (player.facing == Prince.Facing.left) {
							enemy.moving = Enemy.Moving.idle;
							enemy.attackState = Enemy.AttackState.idle;
							enemy.position.x -= .5f;
						}
					} else if (enemy.health <= 0) {
						enemys.removeValue(enemy, false);
						gameOver = true;

					}
				} else {
					enemy.beingHit = false;
					enemy.sprite.setColor(Color.WHITE);
				}
			}
			for (Enemy enemy : enemys) {
				if (enemy.hit(player) && player.mode == Prince.Mode.attackMode) {
					player.sprite.setColor(Color.BLACK);
					player.health -= 1;
				}
				if (enemy.hit(player) && player.mode == Prince.Mode.defendMode) {
					player.sprite.setColor(Color.GOLD);
					player.defendBar -= 1;
				}
			}
			if (player.defendBar >= 0 && player.defendBar < 40) {
				player.sprite.setColor(Color.RED);
				player.mode = Prince.Mode.attackMode;
				Constants.PLAYER_ATTACK_DAMAGE += 0.1;
			}
			if (player.defendBar < 0)
				player.sprite.setColor(Color.BLACK);
			if(player.health < 0){
				gameOver = true;
			}
			player.render(batch);
			player.update(Gdx.graphics.getDeltaTime());
			if(player.rectangle.x > screenWidth - player.idle.getWidth() / 4){
				player.rectangle.x = screenWidth - player.idle.getWidth() / 4;
			}
			if(player.rectangle.x < 0){
				player.rectangle.x = 0;
			}
			batch.setProjectionMatrix(camera.combined);

			batch.end();
		}
		if(gameOver == true && Gdx.input.isKeyJustPressed(Input.Keys.R)){
			gameOver = false;
			enemys.add(new Enemy(new Vector2(50,0)));
			player = new Prince(new Vector2(150,0));
			Constants.PLAYER_ATTACK_DAMAGE = 1;
		}
		if(gameOver == true && player.health <= 0){
			batch.begin();
			sprite = new Sprite(loseSign);
			sprite.setPosition(screenWidth / 2 - (sprite.getTexture().getWidth() / 2),
					screenHeight / 2 - (sprite.getTexture().getHeight() /2));
			sprite.draw(batch);
			batch.end();
		}
		else if(gameOver == true && player.health > 0){
			batch.begin();
			sprite = new Sprite(winSign);
			sprite.setPosition(screenWidth / 2 - (sprite.getTexture().getWidth() / 2),
					screenHeight / 2 - (sprite.getTexture().getHeight() /2));
			sprite.draw(batch);
			sprite.draw(batch);
			batch.end();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		player.dispose();
		for(Enemy enemy : enemys)
			enemy.dispose();
	}
}
