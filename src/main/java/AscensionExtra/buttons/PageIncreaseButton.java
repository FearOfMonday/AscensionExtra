package AscensionExtra.buttons;

import AscensionExtra.AscensionManager;
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

    public PageIncreaseButton() {
        super(arrow);
        drawColor = Color.WHITE;
    }

    @Override
    public void update() {
        super.update();
        if (hovered) drawColor = Color.WHITE;
        else drawColor = Color.LIGHT_GRAY;
        if (clicked) {
            clicked = false;
            AscensionManager.disableButtons(null);
            AscensionManager.incrementPageIndex();
            AscensionManager.activateAndSetNextPageFirstButton();
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
        FontHelper.renderFontRightAligned(sb, FontHelper.cardTitleFont, AscensionManager.getIndexes()[0] + "/" + AscensionManager.getIndexes()[1], x, y + 32 * Settings.scale, Color.SKY);
    }
}
