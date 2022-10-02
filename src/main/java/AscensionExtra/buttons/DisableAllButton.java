package AscensionExtra.buttons;

import AscensionExtra.AscensionMod;
import basemod.ClickableUIElement;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class DisableAllButton extends ClickableUIElement {
    protected boolean clicked;
    private boolean hovered;
    private Color textColor;
    private Color drawColor;
    private final AscensionMod.AscensionManager manager;

    public DisableAllButton(AscensionMod.AscensionManager manager) {
        super(ImageMaster.OPTION_YES);
        textColor = Settings.CREAM_COLOR;
        drawColor = Color.WHITE;
        this.manager = manager;
    }

    @Override
    public void update() {
        super.update();
        if (hovered) {
            textColor = Settings.GOLD_COLOR;
            drawColor = Color.WHITE;
        }
        else  {
            textColor = Settings.CREAM_COLOR;
            drawColor = Color.LIGHT_GRAY;
        }
        if (clicked) {
            clicked = false;
            manager.isActive = false;
            for (AscensionData d : manager.data) {
                d.clicked = false;
                d.uniqueCounter = 0;
                d.saveLvl();
                d.update();
            }
            manager.resetTxtNLvl();
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
        FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, AscensionMod.TEXT[5], x + ((image.getWidth() / 2.0F) - 12) * Settings.scale, y + (image.getHeight() / 2.0F) * Settings.scale, textColor);
    }
}
