package AscensionExtra.buttons;

import AscensionExtra.AscensionMod;
import AscensionExtra.util.TexLoader;
import basemod.ClickableUIElement;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import static AscensionExtra.AscensionMod.makePath;

public class PageIncreaseButton extends ClickableUIElement {
    public static Texture arrow = TexLoader.getTexture(makePath("images/arrow.png"));

    protected boolean clicked;
    private boolean hovered;
    private Color drawColor;
    private final AscensionMod.AscensionManager manager;

    public PageIncreaseButton(AscensionMod.AscensionManager manager) {
        super(arrow);
        drawColor = Color.WHITE;
        this.manager = manager;
    }

    @Override
    public void update() {
        super.update();
        if (hovered) drawColor = Color.WHITE;
        else drawColor = Color.LIGHT_GRAY;
        if (clicked) {
            clicked = false;
            manager.pageIndex++;
            if (manager.pageIndex > manager.maxPages) manager.pageIndex = 0;
            manager.disableButtons(null);
            manager.activateAndSetNextPageFirstButton();
        }
    }

    @Override
    protected void onHover() {
        if (isClickable()) hovered = true;
    }

    @Override
    protected void onUnhover() {
        hovered = false;
    }

    @Override
    protected void onClick() {
        clicked = true;
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb, drawColor);
        FontHelper.renderFontRightAligned(sb, FontHelper.cardTitleFont, manager.pageIndex + "/" + manager.maxPages, x, y + 32 * Settings.scale, Color.SKY);
    }
}
