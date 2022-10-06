package AscensionExtra;

import AscensionExtra.buttons.AscensionData;
import AscensionExtra.buttons.AscensionExtraButton;
import AscensionExtra.buttons.DisableAllButton;
import AscensionExtra.buttons.PageIncreaseButton;
import AscensionExtra.patches.CharacterOptionPatch;
import AscensionExtra.patches.CharacterSelectScreenPatch;
import AscensionExtra.patches.SaveProgressPatch;
import AscensionExtra.patches.TopPanelPatch;
import AscensionExtra.util.TexLoader;
import basemod.*;
import basemod.abstracts.CustomSavable;
import basemod.interfaces.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.localization.*;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;

@SuppressWarnings({"unused", "WeakerAccess"})
@SpireInitializer
public class AscensionMod implements EditStringsSubscriber, PostInitializeSubscriber {
    public static final Logger LOGGER = LogManager.getLogger(AscensionMod.class.getName());
    public static final String MOD_ID = "ascensionmanager";
    public static final String AUTHOR = "FearOfMonday";
    public static final String DESCRIPTION =
            "Allows for multiple custom Ascension to be registered and used simultaneously without conflict, similar to Hades' heat system.";

    private static final int MAX_PER_SIDE = 12;
    private static final ArrayList<String> unLockables = new ArrayList<>();

    public static String makePath(String resourcePath) {
        return MOD_ID + "Resources/" + resourcePath;
    }

    public static AbstractPlayer.PlayerClass p = null;
    public static String[] TEXT;

    private static float loopLabelTime = 0.0F;
    private static int loopLabelIndex = 0;
    private static Texture FRONT;
    private static AscensionMod.AscensionManager manager;

    public AscensionMod() {
        BaseMod.subscribe(this);
    }

    public static void initialize() {
        new AscensionMod();
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(UIStrings.class, MOD_ID + "Resources/localization/eng/UIstrings.json");
    }

    @Override
    public void receivePostInitialize() {
        UIStrings uiStrings = CardCrawlGame.languagePack.getUIString("ascensionmanager:AscensionPrefix");
        TEXT  = uiStrings.TEXT;
        FRONT = TexLoader.getTexture(makePath("images/front.png"));
        manager = new AscensionManager();
        BaseMod.addSaveField("ascensionmanager:isActivated", manager);
        CharacterOptionPatch.addManager(manager);
        CharacterSelectScreenPatch.addManager(manager);
        SaveProgressPatch.addManager(manager);
        TopPanelPatch.addManager(manager);
        Texture badgeTexture = TexLoader.getTexture(makePath("images/Badge.png"));
        ModPanel settingsPanel = new ModPanel();
        float width = FontHelper.getWidth(FontHelper.charDescFont,
                CardCrawlGame.languagePack.getUIString("ascensionmanager:ModSettings").TEXT[0], 1.0F / Settings.scale);
        ModLabel loopLabel = new ModLabel(getLoopString(), 475.0F + width, 530.0F, Settings.GREEN_TEXT_COLOR,
                FontHelper.charDescFont, settingsPanel, label -> {
            loopLabelTime -= Gdx.graphics.getDeltaTime();
            if (loopLabelTime < 0.0F) {
                label.text = getLoopString();
                loopLabelTime += 1.5F;
            }
        });
        settingsPanel.addUIElement(loopLabel);
        ModLabel labelest = new ModLabel(CardCrawlGame.languagePack.getUIString("ascensionmanager:ModSettings").TEXT[0],
                465.0F, 530.0F, FontHelper.charDescFont, settingsPanel, label -> {});
        settingsPanel.addUIElement(labelest);
        ModButton moderButton = new ModButton(350.0f, 475.0F, settingsPanel, button -> {
            for (String s : unLockables) {
                unlockAscension(s);
            }
        });
        settingsPanel.addUIElement(moderButton);
        BaseMod.registerModBadge(badgeTexture, MOD_ID, AUTHOR, DESCRIPTION, settingsPanel);
    }

    private String getLoopString() {
        if (!unLockables.isEmpty()) {
            loopLabelIndex = loopLabelIndex < unLockables.size() - 1 ? loopLabelIndex + 1 : 0;
            String s = unLockables.get(loopLabelIndex);
            for (AscensionData data : manager.data) {
                if (data.id.equals(s)) {
                    return data.name + ".";
                }
            }
        }
        return CardCrawlGame.languagePack.getUIString("ascensionmanager:ModSettings").TEXT[1];
    }

    //Commands for other users
    public static void addAscensionData(String id, String name, String[] ascInfo) {
        addAscensionData(null, id, name, ascInfo);
    }

    public static void addAscensionData(String imgPath, String id, String name, String[] ascInfo) {
        addAscensionData(imgPath, id, name, ascInfo, false);
    }

    public static void addAscensionData(String imgPath, String id, String name, String[] ascensionText, boolean locked) {
        try {
            Paths.get(id.replace(":", ""));
        } catch (InvalidPathException | NullPointerException ex) {
            LOGGER.info("Following ID cannot be used to make a file on your OS, thus your data cannot be saved!!!");
            LOGGER.info("It is adviced to change it in order to avoid issues.");
        }
        for (AscensionData d : manager.data) {
            if (d.id.equals(id)) {
                LOGGER.info("Following ID is already registered: " + id);
                LOGGER.info("It is adviced to change it in order to avoid issues.");
            }
        }
        manager.data.add(new AscensionData(imgPath, id, name, ascensionText, locked, manager));
    }

    public static AscensionData getAscensionData(String id) {
        if (manager.isActive) {
            for (AscensionData d : manager.data) {
                if (d.id.equals(id)) return d;
            }
        }
        return null;
    }

    public static int getAscensionLvl(String id) {
        AscensionData d = getAscensionData(id);
        if (d != null) return d.uniqueCounter;
        else return 0;
    }

    public static boolean isLvlActive(String id, int lvl) {
        return getAscensionLvl(id) >= lvl;
    }

    public static void unlockAscension(String id) {
        for (AscensionData d : manager.data) {
            if (d.id.equals(id)) d.unlock();
        }
    }

    public static void registerAsUnlockable(String id) {
        if (!unLockables.contains(id)) unLockables.add(id);
    }

    public static class AscensionManager implements CustomSavable<Boolean> {
        public boolean isActive;
        public ArrayList<AscensionData> data;
        public int pageIndex;
        public int maxPages;
        public int viewIndex;
        public String[] ascTexts;

        public String previousAscensionMsg;
        public int prevAscLvl;

        private final AscensionExtraButton exButton;
        private final PageIncreaseButton pIncreaseButton;
        private final DisableAllButton resetButton;

        private AscensionManager() {
            exButton = new AscensionExtraButton(this);
            pIncreaseButton = new PageIncreaseButton(this);
            resetButton = new DisableAllButton(this);
            isActive = false;
            pageIndex = 0;
            maxPages = 0;
            viewIndex = 0;
            data = new ArrayList<>();
            previousAscensionMsg = "";
            prevAscLvl = 0;
        }

        public void initButtons() {
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

        public void saveOldTxt() {
            previousAscensionMsg = CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString;
        }

        public void resetTxtNLvl() {
            CardCrawlGame.mainMenuScreen.charSelectScreen.ascLevelInfoString = previousAscensionMsg;
        }

        public void setLvl(AscensionData d, int mod) {
            d.uniqueCounter += mod;
            d.saveLvl();
        }

        public void buildStrings() {
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

        public void incrementViewIndex() {
            viewIndex++;
            if (viewIndex > getSize()) {
                viewIndex = 0;
                return;
            }
            if (data.get(viewIndex - 1).uniqueCounter <= 0) incrementViewIndex();
        }

        public void incrementPageIndex() {
            pageIndex++;
            if (pageIndex > maxPages) pageIndex = 0;
        }

        public void activateAndSetNextPageFirstButton() {
            data.get(pageIndex * MAX_PER_SIDE).clicked = true;
            data.get(pageIndex * MAX_PER_SIDE).saveLvl();
            data.get(pageIndex * MAX_PER_SIDE).setLvlAndText();
        }

        public boolean isAnyButtonActive() {
            for (AscensionData d : data) {
                if (d.uniqueCounter > 0) return true;
            }
            return false;
        }

        public AscensionData getClickedButton() {
            for (AscensionData d : data) {
                if (d.clicked) return d;
            }
            return null;
        }

        public boolean hasButtons() {
            return !data.isEmpty();
        }

        public void disableButtons(AscensionData exception) {
            for (AscensionData d : data) {
                if (d != exception) {
                    d.clicked = false;
                    d.update();
                }
            }
        }

        public void disableExtraButton() {
            exButton.clicked = false;
            exButton.update();
            resetTxtNLvl();
        }

        public void loadAllButtons() {
            for (AscensionData d : data) {
                d.loadData();
            }
        }

        public void saveAfterRun() {
            if (isActive) {
                for (AscensionData d : data) {
                    d.updateUnlockable();
                }
            }
        }

        public void update(CharacterSelectScreen ch) {
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

        public void render(SpriteBatch sb) {
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
                sb.draw(FRONT, Settings.WIDTH / 2.0F + 400.0F * Settings.scale, 270 * Settings.scale, FRONT.getWidth() * Settings.scale, FRONT.getHeight() * Settings.scale);
                if (getSize() > MAX_PER_SIDE) pIncreaseButton.render(sb);
            }
        }

        public int getSize() {
            return data.size();
        }

        @Override
        public Boolean onSave() {
            return isActive;
        }

        @Override
        public void onLoad(Boolean bool) {
            if (bool != null) isActive = bool;
            else isActive = false;
        }
    }
}

