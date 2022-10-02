package AscensionExtra.buttons;

import AscensionExtra.AscensionMod;
import basemod.ClickableUIElement;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.TipHelper;

public class AscensionExtraButton extends ClickableUIElement {
    public static float width = FontHelper.getSmartWidth(FontHelper.cardTitleFont, AscensionMod.TEXT[2], 9999.0F, 0.0F);

    public boolean clicked;
    private boolean hovered;
    private Color textColor;
    private final Texture check;
    private final AscensionMod.AscensionManager manager;

    public AscensionExtraButton(AscensionMod.AscensionManager manager) {
        super(ImageMaster.OPTION_TOGGLE, 0.0F, 0.0F, ImageMaster.OPTION_TOGGLE.getWidth() * 4.0F, ImageMaster.OPTION_TOGGLE.getHeight() * 2.0F);
        textColor = Settings.GREEN_TEXT_COLOR;
        check = ImageMaster.OPTION_TOGGLE_ON;
        this.manager = manager;
    }

    @Override
    public void update() {
        super.update();
        if (hovered) textColor = Settings.GREEN_TEXT_COLOR;
        else textColor = Settings.GOLD_COLOR;
        if (!clicked || !isClickable()) {
            clicked = false;
            manager.disableButtons(null);
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
        clicked = !clicked;
        manager.isActive = clicked;
        if (clicked) {
            manager.loadAllButtons();
            manager.activateAndSetNextPageFirstButton();
        }
        else manager.resetTxtNLvl();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        float halfWidth;
        float halfHeight;
        if (image != null) {
            halfWidth = (float)image.getWidth() / 2.0F;
            halfHeight = (float)image.getHeight() / 2.0F;
            sb.draw(image, x - halfWidth + halfWidth * Settings.scale, y - halfHeight + (hb_h / 2.0F), halfWidth, halfHeight, (float)image.getWidth(), (float)image.getHeight(), Settings.scale, Settings.scale, angle, 0, 0, image.getWidth(), image.getHeight(), false, false);
        }
        if (check != null && clicked) {
            halfWidth = (float)check.getWidth() / 2.0F;
            halfHeight = (float)check.getHeight() / 2.0F;
            sb.draw(check, x - halfWidth + halfWidth * Settings.scale, y - halfHeight + (hb_h / 2.0F), halfWidth, halfHeight, (float)check.getWidth(), (float)check.getHeight(), Settings.scale, Settings.scale, angle, 0, 0, check.getWidth(), check.getHeight(), false, false);
        }
        FontHelper.renderFontCentered(sb, FontHelper.cardTitleFont, AscensionMod.TEXT[2], x + width, y + (hb_h / 2.0F), textColor);
        if (hovered) TipHelper.renderGenericTip(x - 140.0F * Settings.scale, y + 340.0F * Settings.scale, AscensionMod.TEXT[3], AscensionMod.TEXT[4]);
        renderHitbox(sb);
    }
}
