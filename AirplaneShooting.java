import java.util.ArrayList;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import javax.sound.sampled.*;

import java.io.File;
import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AirplaneShooting extends Application {
	// variables
	private static final Random RAND = new Random();
	private static final int WIDTH = 400;
	private static final int HEIGHT = 800;
	private static final int PLAYER_SIZE = 40;
	static final Image PLAYER_IMG = new Image(
			"file:/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/images/player.png");
	static final Image EXPLOSION_IMG = new Image(
			"file:/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/images/explosion.png");
	static final Image BULLET_IMG = new Image(
			"file:/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/images/bullet.png");
	static final int EXPLOSION_W = 128;
	static final int EXPLOSION_ROWS = 3;
	static final int EXPLOSION_COL = 3;
	static final int EXPLOSION_H = 128;
	static final int EXPLOSION_STEPS = 15;

	static final Image BOMBS_IMG[] = {
			new Image("file:/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/images/1.png"),
			new Image("file:/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/images/2.png"),
			new Image("file:/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/images/3.png"),
			new Image("file:/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/images/4.png"),
			new Image("file:/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/images/5.png"),
			new Image("file:/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/images/6.png"),
			new Image("file:/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/images/7.png"),
			new Image("file:/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/images/8.png"),
			new Image("file:/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/images/9.png"),
			new Image("file:/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/images/10.png") };

	final int MAX_BOMBS = 5;
	final int MAX_ENEMY_SHOT = 10;
	boolean gameOver = false;
	private GraphicsContext gc;

	Rocket player;
	List<Universe> univ;
	List<Shot> shots;
	List<Shot> enemyShots;
	List<Bomb> Bombs;

	private static int speed = 8;
	private boolean right, left, up, down;
	private int score = 0;

	public void start(Stage primaryStage) throws Exception {
		Canvas canvas = new Canvas(WIDTH, HEIGHT);
		gc = canvas.getGraphicsContext2D();
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(25), e -> {
			try {
				run(gc);
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
		canvas.setFocusTraversable(true);
		canvas.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case D:
					right = true;
					return;
				case A:
					left = true;
					return;
				case W:
					up = true;
					return;
				case S:
					down = true;
					return;
				case J:
					shots.add(player.shoot());
					try {
						SoundEffect soo = new SoundEffect ("/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/audio files/Gunfire And Voices.wav");
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return;
				case ENTER:
					if (gameOver) {
						gameOver = false;
						setup();
					}
				}
			}
		});
		canvas.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case D:
					right = false;
					return;
				case A:
					left = false;
					return;
				case W:
					up = false;
					return;
				case S:
					down = false;
					return;
				}
			}
		});
		StackPane stackpane = new StackPane(canvas);
		Scene scene = new Scene(stackpane);
		setup();
		primaryStage.setScene(scene);
		primaryStage.setTitle("纪登程的飞机游戏");
		primaryStage.show();
	}

	
	// setup the game
	private void setup() {
		univ = new ArrayList<>();
		shots = new ArrayList<>();
		enemyShots = new ArrayList<>();
		player = new Rocket(WIDTH / 2, HEIGHT - PLAYER_SIZE, PLAYER_SIZE, PLAYER_IMG);
		Bombs = new ArrayList<>();
		IntStream.range(0, MAX_BOMBS).mapToObj(i -> newBomb()).forEach(Bombs::add);
		score = 0;
	}

	// run Graphics78
	private void run(GraphicsContext gc) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, WIDTH, HEIGHT);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setFont(Font.font(20));
		gc.setFill(Color.WHITE);
		gc.fillText("Score: " + score * 100, 60, 20);
		
		// dispaly gamover message
		if (gameOver) {
			gc.setFont(Font.font(35));
			gc.setFill(Color.GREENYELLOW);
			gc.fillText("Game Over\n Press Enter to Play Again", WIDTH / 2, HEIGHT / 2);
		}

		univ.forEach(Universe::draw);
		player.move();
		player.update();
		player.draw();

		Bombs.stream().peek(Rocket::update).peek(Rocket::draw).forEach(e -> {
			if (player.colide(e) && !e.exploding && !player.exploding) {
				player.explode();
				try {
					SoundEffect boom = new SoundEffect("/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/audio files/Big Explosion Cut Off.wav");
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.explode();
				try {
					SoundEffect boom1 = new SoundEffect("/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/audio files/Big Explosion Cut Off.wav");
				} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (enemyShots.size() < 5)
				enemyShots.add(e.shoot());
			
		});
		
		if (score > 25) {
			enemyShots.stream().peek(Shot::update).peek(Shot::draw).forEach(e -> {
				if (player.colide(e) && !player.exploding) {
					player.explode();
					try {
						SoundEffect boom1 = new SoundEffect("/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/audio files/Big Explosion Cut Off.wav");
					} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					enemyShots.remove(enemyShots.indexOf(e));
				}
				System.out.println("boom");
			});
		}
		if (!gameOver) {
			for (int i = shots.size() - 1; i >= 0; i--) {
				Shot shot = shots.get(i);
				//SoundEffect soo = new SoundEffect ("/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/audio files/Gunfire And Voices.wav");
				if (shot.posY < 0 || shot.toRemove) {
					shots.remove(i);
					continue;
				}
				shot.update();
				shot.draw();
				for (Bomb bomb : Bombs) {
					if (shot.colide(bomb) && !bomb.exploding) {
						bomb.explode();
						SoundEffect boom = new SoundEffect("/Users/jidengcheng/eclipse-workspace/Airplane Shooting Game/audio files/Big Explosion Cut Off.wav");
						shot.toRemove = true;
						score ++;
					}
				}
			}
		}

		for (int i = Bombs.size() - 1; i >= 0; i--) {
			if (Bombs.get(i).destroyed) {
				Bombs.set(i, newBomb());
				for (int j = enemyShots.size() - 1; j >= 0; j--) {
					if (enemyShots.get(i).destroyed)
						enemyShots.set(j, Bombs.get(i).shoot());
				}
			}
		}
		gameOver = player.destroyed;

		if (RAND.nextInt(10) > 2) {
			univ.add(new Universe());
		}
		for (int i = 0; i < univ.size(); i++) {
			if (univ.get(i).posY > HEIGHT)
				univ.remove(i);
		}
	}

	// player
	public class Rocket {
		int posX, posY, size;
		boolean exploding, destroyed;
		boolean isEnemy;
		Image img;
		int explosionStep = 0;
		int explosionSize = size * 2;

		// constructor
		public Rocket(int posX, int posY, int size, Image image) {
			this.posX = posX;
			this.posY = posY;
			this.size = size;
			img = image;
			isEnemy = false;
		}

		public Shot shoot() {
			// positioning the new bullet
			return new Shot(posX + size / 2 - Shot.size / 2, posY - Shot.size, false);
		}

		public void update() {
			if (exploding)
				explosionStep++;
			destroyed = explosionStep > EXPLOSION_STEPS;
		}

		public void draw() {
			if (exploding) {
				gc.drawImage(EXPLOSION_IMG, explosionStep % EXPLOSION_COL * EXPLOSION_W,
						(explosionStep / EXPLOSION_ROWS) * EXPLOSION_H + 1, EXPLOSION_W, EXPLOSION_H,
						posX - (explosionSize - size) / 2 - 30, posY - ((explosionSize - size) / 2), size * 2,
						size * 2);
			} else {
				gc.drawImage(img, posX, posY, size, size);
			}
		}

		public boolean colide(Rocket other) {
			int d = distance(this.posX + size / 2, this.posY + size / 2, other.posX + other.size / 2,
					other.posY + other.size / 2);
			return d < other.size / 2 + this.size / 2;
		}

		public boolean colide(Shot other) {
			int d = distance(this.posX + size / 2, this.posY + size / 2, other.posX + other.size / 2,
					other.posY + other.size / 2);
			return d < other.size / 2 + this.size / 2;
		}

		public void explode() {
			exploding = true;
			explosionStep = -1;
		}

		public void move() {
			if (!gameOver) {
				if (right && posX <= WIDTH - PLAYER_SIZE)
					posX += speed;
				if (left && posX >= 0)
					posX -= speed;
				if (up && posY >= 0)
					posY -= speed;
				if (down && posY <= HEIGHT - PLAYER_SIZE)
					posY += speed;
			}
		}
	}

	// enemy
	public class Bomb extends Rocket {
		public int SPEED = RAND.nextInt(speed / 2) + speed / 4;

		public Bomb(int posX, int posY, int size, Image image) {
			super(posX, posY, size, image);
			isEnemy = true;
		}

		public void update() {
			super.update();
			if (!exploding && !destroyed) {
				posY += SPEED;
			}
			if (posY > HEIGHT)
				destroyed = true; // if enemy is going out of bound
		}

		// **
		public Shot shoot() {
			// positioning the new bullet
			return new Shot(posX + size / 2 - Shot.size / 2, posY + size / 2 + Shot.size, true);
		}

		public int getSPEED() {
			return this.SPEED;
		}
		
		public int getPosX() {
			return this.posX;
		}
		
		public int getPoxY() {
			return this.posY;
		}
	}

	// bullets
	public class Shot {
		public boolean toRemove;
		int posX, posY, speed = 20, enemyShotSpeedY = newBomb().getSPEED() + RAND.nextInt(6) + 3, enemyShotSpeedX = RAND.nextInt(3) + 1;
		static final int size = 6, enemySize = 10;
		private int randomNumber = RAND.nextInt(1000) % 3;
		// **
		public boolean isEnemy;
		public boolean destroyed;

		public Shot(int posX, int posY, boolean isEnemy) {
			this.posX = posX;
			this.posY = posY;
			this.isEnemy = isEnemy;
		}

		public void update() {
			if (isEnemy) {
				if (score > 50) {
					if (randomNumber == 0)
						this.posX += enemyShotSpeedX;
					if (randomNumber == 1)
						this.posX -= enemyShotSpeedX;
				}
				this.posY += enemyShotSpeedY;
			} else {
				this.posY -= speed;
			}

			if (this.posY > HEIGHT || this.posY < 0 || this.posX > WIDTH || this.posX < 0)
				destroyed = true;
		}

		public void draw() {
			if (isEnemy) {
				gc.setFill(Color.RED);
				gc.fillOval(posX, posY, enemySize, enemySize);
			} else {
				gc.setFill(Color.WHITE);
				if(score > 30) {
					gc.fillRect(posX, posY, size*5, size);
				}else {
					gc.fillOval(posX, posY, size, size);
				}
			}
		}

		public boolean colide(Rocket other) {
			int d = distance(this.posX + size / 2, this.posY + size / 2, other.posX + other.size / 2,
					other.posY + other.size / 2);
			return d < other.size / 2 + this.size / 2;
		}
	}

	// environment
	public class Universe {
		int posX, posY;
		private int h, w, r, g, b;
		private double opacity;

		public Universe() {
			posX = RAND.nextInt(WIDTH);
			posY = 0;

			w = RAND.nextInt(5) + 1;
			h = RAND.nextInt(5) + 1;
			r = RAND.nextInt(100) + 150;
			g = RAND.nextInt(100) + 150;
			b = RAND.nextInt(100) + 150;
			opacity = RAND.nextFloat();

			if (opacity < 0)
				opacity *= -1;
			if (opacity > 0.5)
				opacity = 0.5;
		}

		public void draw() {
			if (opacity > 0.8)
				opacity -= 0.01;
			if (opacity < 0.1)
				opacity += 0.01;
			gc.setFill(Color.rgb(r, g, b, opacity));
			gc.fillOval(posX, posY, w, h);
			posY += 5;
		}
	}

	Bomb newBomb() {
		return new Bomb(RAND.nextInt(WIDTH - PLAYER_SIZE / 2), 0, PLAYER_SIZE,
				BOMBS_IMG[RAND.nextInt(BOMBS_IMG.length)]);
	}

	int distance(int x1, int y1, int x2, int y2) {
		return (int) Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
	}

	public class SoundEffect {
		public SoundEffect(String audioFile) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
			 File file = new File(audioFile);
			Clip clip = AudioSystem.getClip();
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
			clip.open(audioStream);
			clip.start();
		}
	}

	//	}
	public static void main(String[] args){
		launch();
	}
}
