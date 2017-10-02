package rs.pedjaapps.smc.view;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import rs.pedjaapps.smc.MaryoGame;
import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.assets.FontAwesome;
import rs.pedjaapps.smc.object.World;
import rs.pedjaapps.smc.screen.GameScreen;
import rs.pedjaapps.smc.utility.GameSave;
import rs.pedjaapps.smc.utility.HUDTimeText;
import rs.pedjaapps.smc.utility.MyMathUtils;
import rs.pedjaapps.smc.utility.NAHudText;
import rs.pedjaapps.smc.utility.NATypeConverter;
import rs.pedjaapps.smc.utility.PrefsManager;

import static com.badlogic.gdx.Gdx.gl;

public class HUD {
    private static final float UPDATE_FREQ = .15f;
    private final NATypeConverter<Integer> coins = new NATypeConverter<>();
    private final NAHudText<Integer> lives = new NAHudText<>(null, "x");
    private final HUDTimeText time = new HUDTimeText();
    public Stage stage;
    public boolean updateTimer = true;
    public boolean jumpPressed;
    public boolean firePressed;
    public boolean upPressed, downPressed, rightPressed, leftPressed;
    private float noUpdateDuration;
    private World world;
    private GameScreen gameScreen;
    private TextButton pauseButton, play, musicButton;
    private Button fire, jump;
    private Touchpad touchpad;
    private Texture itemBox, maryoL, goldM;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private Table buttonsTable;
    private float stateTime;
    private int points;
    private String pointsText;
    private Label ttsLabel;
    private Label pauseLabel;
    private Label scoreLabel;
    private Label coinsLabel;
    private Label timeLabel;
    private Label livesLabel;
    private Image imItemBox;
    private Image imWaffles;
    private Image imMaryoL;
    public HUD(World world, GameScreen gameScreen) {
        this.world = world;
        this.gameScreen = gameScreen;
        batch = new SpriteBatch();
        stage = new Stage(new FitViewport(MaryoGame.NATIVE_WIDTH, MaryoGame.NATIVE_HEIGHT));
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void loadAssets() {
        world.screen.game.assets.manager.load("data/sounds/item/live_up_2.mp3", Sound.class);

        world.screen.game.assets.manager.load("data/game/itembox.png", Texture.class, world.screen.game.assets
                .textureParameter);
        world.screen.game.assets.manager.load("data/game/maryo_l.png", Texture.class, world.screen.game.assets
				.textureParameter);
        world.screen.game.assets.manager.load("data/game/gold_m.png", Texture.class, world.screen.game.assets
				.textureParameter);
        world.screen.game.assets.manager.load("data/game/game_over.png", Texture.class, world.screen.game.assets
				.textureParameter);
    }

    public void initAssets() {
        // already initialized
        if (pauseLabel != null)
            return;

        Skin skin = world.screen.game.assets.manager.get(Assets.SKIN_HUD, Skin.class);
        float padX = stage.getWidth() * 0.03f;
        float ibSize = MaryoGame.NATIVE_WIDTH / 14;

        fire = new Button(skin, "fire");
        fire.setSize(MaryoGame.NATIVE_HEIGHT / 7f, MaryoGame.NATIVE_HEIGHT / 7f);
        fire.setPosition(MaryoGame.NATIVE_WIDTH - fire.getWidth() * 1.5f, MaryoGame.NATIVE_HEIGHT * .35f);
        stage.addActor(fire);
        fire.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (firePressed != fire.getClickListener().isPressed()) {
                    firePressed = fire.getClickListener().isPressed();

                    if (firePressed)
                        world.maryo.firePressed();
                    else
                        world.maryo.fireReleased();
                }

                return false;
            }
        });

        jump = new Button(skin, "jump");
        jump.setSize(fire.getWidth(), fire.getHeight());
        jump.setPosition(fire.getX() - fire.getWidth() * 1.5f, fire.getY() - fire.getHeight() * 1.5f);
        stage.addActor(jump);
        jump.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (jumpPressed != jump.getClickListener().isPressed()) {
                    jumpPressed = jump.getClickListener().isPressed();

                    if (jumpPressed)
                        world.maryo.jumpPressed();
                    else
                        world.maryo.jumpReleased();
                }

                return false;
            }
        });

        touchpad = new Touchpad(0, skin);
        touchpad.setSize(MaryoGame.NATIVE_HEIGHT * .4f, MaryoGame.NATIVE_HEIGHT * .4f);
        touchpad.setPosition(MaryoGame.NATIVE_HEIGHT / 15f, MaryoGame.NATIVE_HEIGHT / 15f);
        stage.addActor(touchpad);
        touchpad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (upPressed != touchpad.getKnobPercentY() > .33f) {
                    upPressed = touchpad.getKnobPercentY() > .33f;
                    if (upPressed)
                        world.maryo.upPressed();
                    else
                        world.maryo.upReleased();
                }

                if (downPressed != touchpad.getKnobPercentY() < -.33f) {
                    downPressed = touchpad.getKnobPercentY() < -.33f;
                    if (downPressed)
                        world.maryo.downPressed();
                    else
                        world.maryo.downReleased();
                }

                if (rightPressed != touchpad.getKnobPercentX() > .33f) {
                    rightPressed = touchpad.getKnobPercentX() > .33f;
                    if (rightPressed)
                        world.maryo.rightPressed();
                    else
                        world.maryo.rightReleased();
                }

                if (leftPressed != touchpad.getKnobPercentX() < -.33f) {
                    leftPressed = touchpad.getKnobPercentX() < -.33f;
                    if (leftPressed)
                        world.maryo.leftPressed();
                    else
                        world.maryo.leftReleased();
                }

            }
        });

        itemBox = world.screen.game.assets.manager.get("data/game/itembox.png");
        maryoL = world.screen.game.assets.manager.get("data/game/maryo_l.png");
        goldM = world.screen.game.assets.manager.get("data/game/gold_m.png");

        Texture.TextureFilter filter = Texture.TextureFilter.Linear;
        itemBox.setFilter(filter, filter);
        maryoL.setFilter(filter, filter);
        goldM.setFilter(filter, filter);

        ttsLabel = new Label("TOUCH ANYWHERE TO START", skin, Assets.LABEL_BORDER60);
        ttsLabel.setPosition(stage.getWidth() / 2, stage.getHeight() / 2, Align.center);
        ttsLabel.addAction(Actions.forever(Actions.sequence(Actions.alpha(.3f, 1f), Actions.fadeIn(1f))));
        stage.addActor(ttsLabel);

        pauseLabel = new Label("PAUSE", skin, Assets.LABEL_BORDER60);
        pauseLabel.setPosition(stage.getWidth() / 2, stage.getHeight() / 2 + padX / 2, Align.bottom);
        pauseLabel.addAction(Actions.forever(Actions.sequence(Actions.alpha(.3f, 1f), Actions.fadeIn(1f))));
        stage.addActor(pauseLabel);

        TextButton cancelButton = new TextButton(FontAwesome.MISC_CROSS, skin, Assets.BUTTON_FA);
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.exitToMenu();
            }
        });

        play = new TextButton("Resume", skin, Assets.BUTTON_SMALL);
        play.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.setGameState(GameScreen.GAME_STATE.GAME_RUNNING);
            }
        });

        musicButton = new MusicButton(skin, world.screen.game.assets.manager.get("data/sounds/audio_on.mp3", Sound
				.class)) {
            @Override
            protected Music getMusic() {
                return gameScreen.getMusic();
            }
        };

        buttonsTable = new Table();
        buttonsTable.defaults().uniform().pad(padX / 2).fill();
        buttonsTable.add(cancelButton);
        buttonsTable.add(play).uniform(false, true);
        buttonsTable.add(musicButton);
        buttonsTable.setPosition(stage.getWidth() / 2, stage.getHeight() / 2 - padX * 2, Align.top);
        stage.addActor(buttonsTable);

        imItemBox = new Image(itemBox);
        imItemBox.setPosition(MaryoGame.NATIVE_WIDTH / 2 - ibSize, MaryoGame.NATIVE_HEIGHT - ibSize - ibSize / 5);
        imItemBox.setSize(ibSize, ibSize);
        stage.addActor(imItemBox);

        pauseButton = new TextButton(FontAwesome.BIG_PAUSE, skin, Assets.BUTTON_FA);
        pauseButton.getLabel().setFontScale(.5f);
        pauseButton.setSize(MaryoGame.NATIVE_HEIGHT / 10f, MaryoGame.NATIVE_HEIGHT / 10f);
        pauseButton.setPosition(MaryoGame.NATIVE_WIDTH - padX / 2 - pauseButton.getWidth(),
                imItemBox.getY() + imItemBox.getHeight() - pauseButton.getHeight());
        stage.addActor(pauseButton);
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.setGameState(GameScreen.GAME_STATE.GAME_PAUSED);
            }
        });

        imMaryoL = new Image(maryoL);
        imMaryoL.setSize(ibSize / 1.25f, ibSize / 2.5f);
        imMaryoL.setPosition(pauseButton.getX() - imMaryoL.getHeight() * 3, imItemBox.getY() + imItemBox.getHeight()
				- imMaryoL.getHeight() - imMaryoL.getHeight() / 2);
        stage.addActor(imMaryoL);

        scoreLabel = new Label(formatPointsString(0), skin, Assets.LABEL_BORDER25);
        scoreLabel.setPosition(padX, imMaryoL.getY());
        stage.addActor(scoreLabel);

        imWaffles = new Image(goldM);
        imWaffles.setPosition(padX * 2 + scoreLabel.getWidth(), scoreLabel.getY());
        stage.addActor(imWaffles);

        coinsLabel = new Label(" ", skin, Assets.LABEL_BORDER25);
        coinsLabel.setPosition(imWaffles.getX() + imWaffles.getWidth(), scoreLabel.getY());
        stage.addActor(coinsLabel);

        livesLabel = new Label("0x", skin, Assets.LABEL_BORDER25);
        livesLabel.setPosition(imMaryoL.getX(), scoreLabel.getY(), Align.bottomRight);
        stage.addActor(livesLabel);

        time.update(0);
        timeLabel = new Label(new String(time.getChars()), skin, Assets.LABEL_BORDER25);
        timeLabel.setPosition(livesLabel.getX() - padX, scoreLabel.getY(), Align.bottomRight);
        stage.addActor(timeLabel);

        //TODO popuptextbox

        onGameStateChange();
    }

    public void onGameStateChange() {
        GameScreen.GAME_STATE gameState = gameScreen.getGameState();

        ttsLabel.setVisible(gameState == GameScreen.GAME_STATE.GAME_READY);
        pauseLabel.setVisible(gameState == GameScreen.GAME_STATE.GAME_PAUSED);

        boolean isInGame = !(gameState == GameScreen.GAME_STATE.GAME_READY
                || gameState == GameScreen.GAME_STATE.GAME_PAUSED);
        scoreLabel.setVisible(isInGame);
        imItemBox.setVisible(isInGame);
        coinsLabel.setVisible(isInGame);
        imWaffles.setVisible(isInGame);
        timeLabel.setVisible(isInGame);
        imMaryoL.setVisible(isInGame);
        livesLabel.setVisible(isInGame);
        buttonsTable.setVisible(gameState == GameScreen.GAME_STATE.GAME_PAUSED);
        pauseButton.setVisible(isInGame);
        jump.setVisible(isInGame && MaryoGame.showOnScreenControls());
        fire.setVisible(jump.isVisible());
        touchpad.setVisible(jump.isVisible());
    }

    public void render(GameScreen.GAME_STATE gameState, float deltaTime) {
        if (gameState == GameScreen.GAME_STATE.GAME_PAUSED)
            drawPauseOverlay();

        else {
            if (updateTimer) stateTime += deltaTime;
            batch.setProjectionMatrix(stage.getCamera().combined);
            batch.begin();

            //if(GameSave.getItem() != null)
            //	batch.setColor(Color.RED);

            noUpdateDuration = noUpdateDuration + deltaTime;

            if (noUpdateDuration >= UPDATE_FREQ) {
                noUpdateDuration = 0;
                // points
                //TODO nicht jedes Mal ändern
                pointsText = formatPointsString(GameSave.save.points);
                scoreLabel.setText(pointsText);

                //coins
                //TODO nicht jedes Mal ändern
                String coins = this.coins.toString(GameSave.getCoins());
                coinsLabel.setText(coins);

                //time
                //TODO nicht jedes Mal ändern und sowieso besser!
                time.update(stateTime);
                timeLabel.setText(new String(time.getChars()));

                //lives
                //TODO nicht jedes Mal ändern und sowieso besser!
                livesLabel.setText(this.lives.toString(MyMathUtils.max(GameSave.save.lifes, 0)));
            }

            //draw item if any
            if (GameSave.getItem() != null) {
                float w = imItemBox.getWidth() * 0.5f;
                float h = imItemBox.getHeight() * 0.5f;
                float x = imItemBox.getX() + w * .5f;
                float y = imItemBox.getY() + h * .5f;
                batch.draw(GameSave.getItem().texture, x, y, w, h);
            }

            batch.end();
        }
        if (PrefsManager.isDebug()) drawDebug();

        stage.getViewport().apply();
        stage.act(deltaTime);
        stage.draw();
    }

    private void drawDebug() {
    }

    private String formatPointsString(int points) {
        if (pointsText != null && this.points == points) {
            return pointsText;
        } else {
            this.points = points;
            String pointsPrefix = "Points ";
            String pointsString = points + "";
            int zeroCount = 8 - pointsString.length();
            for (int i = 0; i < zeroCount; i++) {
                pointsPrefix += "0";
            }
            return (pointsText = pointsPrefix + pointsString);
        }
    }

    private void drawPauseOverlay() {
        gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL20.GL_BLEND);

        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.5f);
        shapeRenderer.rect(0, 0, MaryoGame.NATIVE_WIDTH, MaryoGame.NATIVE_HEIGHT);
        shapeRenderer.end();

    }

    public void dispose() {
        stage.dispose();
    }

    public enum Key {
        none, pause, fire, jump, left, right, up, down, play, sound, music
    }

}
