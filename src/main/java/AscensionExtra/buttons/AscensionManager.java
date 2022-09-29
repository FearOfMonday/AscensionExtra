package AscensionExtra.buttons;

import AscensionExtra.AscensionMod;
import AscensionExtra.util.TexLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

import static AscensionExtra.AscensionMod.*;

public class AscensionManager {
    public static final Logger LOGGER = LogManager.getLogger(AscensionManager.class.getName());
    private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("ascensionmanager:AscensionPrefix");
    public static final String[] TEXT = uiStrings.TEXT;
    private static final int MAX_PER_SIDE = 12;
    private static ArrayList<AscensionData> data = new ArrayList<>();
    private static Texture front = TexLoader.getTexture(makePath("images/front.png"));
    public static AbstractPlayer.PlayerClass p = null;
    private static AscensionExtraButton exButton;
    private static PageIncreaseButton pIncreaseButton;
    private static DisableAllButton resetButton;
    public static int regularAscLevel = 0;
    private static String[] ascTexts;
    private static int pageIndex = 0;
    private static int maxPages = 0;
    private static int viewIndex = 0;

    public static void init() {
        exButton = new AscensionExtraButton();
        pIncreaseButton = new PageIncreaseButton();
        resetButton = new DisableAllButton();
    }

    public static void initButtons() {
        maxPages = data.size() / MAX_PER_SIDE;
        exButton.setX(Settings.WIDTH / 2.0F + 400.0F * Settings.scale);
        exButton.setY(40.0F * Settings.scale);
        resetButton.setX(Settings.WIDTH / 2.0F + 398.0F * Settings.scale);
        resetButton.setY(Settings.HEIGHT / 2.0F - 205 * Settings.scale);
        pIncreaseButton.setX(Settings.WIDTH / 2.0F + 710.0F * Settings.scale);
        pIncreaseButton.setY(Settings.HEIGHT / 2.0F - 200 * Settings.scale);
        for (int i = 0; i < data.size(); i++) {
            data.get(i).setX(Settings.WIDTH / 2.0F + 400 * Settings.scale);
            data.get(i).setY(Settings.HEIGHT - 100 * Settings.scale - (i % MAX_PER_SIDE) * 50 * Settings.scale);
        }
    }

    public static int getSize() {
        return data.size();
    }

    public static void resetTxtNLvl() {
        if (regularAscLevel > 20) regularAscLevel = 20;
        if (regularAscLevel < 1) regularAscLevel = 1;
        CardCrawlGame.mainMenuScreen.charSelectScreen.ascensionLevel = regularAscLevel;
        CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString = CharacterSelectScreen.A_TEXT[regularAscLevel - 1];
    }

    public static void setLvl(AscensionData d, int lvl) {
        d.uniqueCounter = lvl;
        d.saveLvl();
        d.setLvlAndText();
    }

    public static void buildStrings() {
        viewIndex = 0;
        StringBuilder sb = new StringBuilder();
        ascTexts = new String[getSize()];
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < data.get(i).uniqueCounter; j++) {
                sb.append(data.get(i).ascInfo[j]);
                sb.append(" NL ");
            }
            sb.setLength(Math.max(sb.length() - 4, 0));
            ascTexts[i] = sb.toString();
            sb.setLength(0);
        }
    }

    public static void incrementViewIndex() {
        viewIndex++;
        if (viewIndex > getSize()) {
            viewIndex = 0;
            return;
        }
        if (data.get(viewIndex - 1).uniqueCounter <= 0) incrementViewIndex();
    }

    public static void incrementPageIndex() {
        pageIndex++;
        if (pageIndex > maxPages) pageIndex = 0;
    }

    public static void activateAndSetNextPageFirstButton() {
        data.get(pageIndex * MAX_PER_SIDE).clicked = true;
        data.get(pageIndex * MAX_PER_SIDE).saveLvl();
        data.get(pageIndex * MAX_PER_SIDE).setLvlAndText();
    }

    public static String getActiveName() {
        return data.get(viewIndex - 1).name;
    }

    public static String getActiveTexts() {
        return ascTexts[viewIndex - 1];
    }

    public static boolean isAnyButtonActive() {
        for (AscensionData d : data) {
            if (d.uniqueCounter > 0) return true;
        }
        return false;
    }

    public static AscensionData getClickedButton() {
       for (AscensionData d : data) {
           if (d.clicked) return d;
       }
       return null;
    }

    public static boolean hasButtons() {
        return !data.isEmpty();
    }

    public static int[] getIndexes() {
        return new int[]{pageIndex, maxPages, viewIndex};
    }

    public static void disableAll() {
        exButton.clicked = false;
        AscensionManager.disableButtons(null);
    }

    public static void disableButtons(AscensionData exception) {
        for (AscensionData d : data) {
            if (d != exception) {
                d.clicked = false;
                d.update();
            }
        }
    }

    public static void resetButtons() {
        for (AscensionData d : data) {
            d.clicked = false;
            d.uniqueCounter = 0;
            d.saveLvl();
            d.update();
        }
    }

    public static void loadAllButtons() {
        for (AscensionData d : data) {
            d.loadData();
        }
    }

    public static void saveAfterRun() {
        if (AscensionMod.isActivated()) {
            for (AscensionData d : data) {
                d.updateUnlockable();
            }
        }
    }

    public static void update(CharacterSelectScreen ch) {
        exButton.setClickable(ch.isAscensionMode);
        exButton.update();
        if (exButton.clicked) {
            for (int i = MAX_PER_SIDE * pageIndex; i < getSize(); i++) {
                if (i < MAX_PER_SIDE * (1 + pageIndex)) data.get(i).update();
                else break;
            }
            resetButton.update();
            if (getSize() > MAX_PER_SIDE) pIncreaseButton.update();
        }
    }

    public static void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE);
        exButton.render(sb);
        if (exButton.clicked) {
            sb.draw(ImageMaster.OPTION_CONFIRM, Settings.WIDTH / 2.0F + 396.0F * Settings.scale, 270 * Settings.scale, 404 * Settings.scale, 810 * Settings.scale);
            for (int i = MAX_PER_SIDE * pageIndex; i < getSize(); i++) {
                if (i < MAX_PER_SIDE * (1 + pageIndex)) data.get(i).render(sb);
                else break;
            }
            resetButton.render(sb);
            sb.setColor(Color.WHITE);
            sb.draw(front, Settings.WIDTH / 2.0F + 400.0F * Settings.scale, 270 * Settings.scale, front.getWidth() * Settings.scale, front.getHeight() * Settings.scale);
            if (getSize() > MAX_PER_SIDE) pIncreaseButton.render(sb);
        }
    }

    public static void addAscensionData(String id, String name, String[] ascInfo) {
        addAscensionData(null, id, name, ascInfo);
    }

    public static void addAscensionData(Texture img, String id, String name, String[] ascInfo) {
        addAscensionData(img, id, name, ascInfo, false);
    }

    public static void addAscensionData(Texture img, String id, String name, String[] ascInfo, boolean locked) {
        for (AscensionData d : data) {
            if (d.id.equals(id)) {
                LOGGER.info("Following ID is already registered: " + id);
                LOGGER.info("It is adviced to change it in order to avoid issues.");
            }
        }
        data.add(new AscensionData(img, id, name, ascInfo, locked));
    }

    public static int getAscensionLvl(String id) {
        if (AscensionMod.isActivated()) {
            for (AscensionData d : data) {
                if (d.id.equals(id)) return d.uniqueCounter;
            }
            LOGGER.info("Could not find data for ID: " + id);
        }
        return 0;
    }
}
