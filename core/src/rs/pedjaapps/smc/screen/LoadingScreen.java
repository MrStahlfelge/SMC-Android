package rs.pedjaapps.smc.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import rs.pedjaapps.smc.assets.Assets;
import rs.pedjaapps.smc.view.HUD;

/**
 * @author Mats Svensson
 */
public class LoadingScreen extends AbstractScreen {
    private final ProgressBar progressBar;
    private final Color color1;
    private final Color color2;
    private final ShapeRenderer renderer;
    private AbstractScreen screenToLoadAfter;
    private boolean resume = false;
    boolean assetsLoaded = false;

    public LoadingScreen(AbstractScreen screenToLoadAfter, boolean resume) {
        super(screenToLoadAfter.game);
        game.assets.manager.finishLoadingAsset(Assets.SKIN_HUD);

        this.screenToLoadAfter = screenToLoadAfter;
        this.resume = resume;

        Skin skin = game.assets.manager.get(Assets.SKIN_HUD, Skin.class);
        progressBar = new ProgressBar(0, 100, 1, false, skin);

        progressBar.setSize(stage.getWidth() * .75f, 30);
        progressBar.setPosition(stage.getWidth() / 2, 20, Align.bottom);
        progressBar.setColor(skin.getColor(Assets.COLOR_EMPH2));
        progressBar.setAnimateDuration(.15f);

        stage.addActor(progressBar);

        Image imGameLogo = MainMenuScreen.createLogoImage(game);
        imGameLogo.setPosition(stage.getWidth() / 2, stage.getHeight() - 10f, Align.top);
        stage.addActor(imGameLogo);

        Label loading = new Label("Loading...", skin, "outline");
        loading.setFontScale(.8f);
        loading.setAlignment(Align.center);
        loading.setPosition(stage.getWidth() / 2, progressBar.getY() + progressBar.getHeight() + 10, Align.bottom);
        loading.addAction(HUD.getForeverFade());
        stage.addActor(loading);

        TextureRegion txtLoadingLogo = game.assets.manager.get(Assets.SKIN_HUD, Skin.class)
                .getAtlas().findRegion(Assets.LOGO_LOADING);
        Image imLoadingLogo = new Image(txtLoadingLogo);
        imLoadingLogo.setSize(imLoadingLogo.getWidth() * .33f, imLoadingLogo.getHeight() * .33f);
        imLoadingLogo.setPosition(stage.getWidth() / 2, loading.getY() + loading.getHeight() + (imGameLogo.getY() -
                loading.getY() - loading.getHeight()) / 2, Align.center);
        stage.addActor(imLoadingLogo);

        color1 = new Color(.117f, 0.705f, .05f, 0f);
        color2 = new Color(0f, 0.392f, 0.039f, 0f);
        renderer = new ShapeRenderer();
    }

    @Override
    public void show() {
        assetsLoaded = false;
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (game.assets.manager.update() && assetsLoaded) {
            // Load some, will return true if done loading
            /*if(!resume)*/
            screenToLoadAfter.onAssetsLoaded();
            if (screenToLoadAfter instanceof GameScreen) {
                ((GameScreen) screenToLoadAfter).resumed = resume;
            }
            game.setScreen(screenToLoadAfter);
        }

        progressBar.setValue(Math.max(assetsLoaded ? game.assets.manager.getProgress() * 100 : 0, 3));

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setProjectionMatrix(stage.getCamera().combined);
        renderer.rect(0, 0, stage.getWidth(), stage.getHeight(), color2, color1, color1, color2);
        renderer.end();

        stage.act();
        stage.draw();

        if (!assetsLoaded) {
            // Auf nächste Ausfhührung posten, damit dieser Bildschirm auf langsamen Geräten schonmal einmal gezeichnet
            // wird
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    screenToLoadAfter.loadAssets();
                    assetsLoaded = true;
                }
            });
        }

        //backgroundColor.render(stage.getCamera(), stage.getSp));

        //async loading is just for show, since loading takes less than a second event for largest levels
        //if debug mode just load it all at once
        //game.assets.manager.finishLoading();
    }

    @Override
    public void loadAssets() {
        //do nothing
    }

    @Override
    public void onAssetsLoaded() {
        //do nothing
    }

    @Override
    public void dispose() {
        renderer.dispose();
        ;
        super.dispose();
    }
}
