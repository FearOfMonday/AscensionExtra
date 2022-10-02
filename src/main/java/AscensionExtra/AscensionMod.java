package AscensionExtra;

import AscensionExtra.util.TexLoader;
import basemod.*;
import basemod.abstracts.CustomSavable;
import basemod.interfaces.*;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.megacrit.cardcrawl.localization.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({"unused", "WeakerAccess"})
@SpireInitializer
public class AscensionMod implements EditStringsSubscriber, PostInitializeSubscriber, CustomSavable<Boolean> {

    public static final Logger LOGGER = LogManager.getLogger(AscensionMod.class.getName());
    public static final String MOD_ID = "ascensionmanager";
    public static final String AUTHOR = "FearOfMonday";
    public static final String DESCRIPTION = "Allows for multiple custom Ascension to be registered and used simultaneously without conflict, similar to Hades' heat system.";

    public static String makePath(String resourcePath) {
        return MOD_ID + "Resources/" + resourcePath;
    }
    public static final String BADGE_IMAGE = makePath("images/Badge.png");

    public static ModLabel label;

    private static AscensionMod mod;
    private boolean ascenionExtraActivated;

    public AscensionMod() {
        BaseMod.subscribe(this);
        ascenionExtraActivated = false;
    }

    public static void initialize() {
        mod = new AscensionMod();
    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(UIStrings.class, MOD_ID + "Resources/localization/eng/UIstrings.json");
    }

    @Override
    public void receivePostInitialize() {
        AscensionManager.init();

        BaseMod.addSaveField("ascensionmanager:isActivated", this);
        Texture badgeTexture = TexLoader.getTexture(BADGE_IMAGE);
        ModPanel settingsPanel = new ModPanel();

        label = new ModLabel("", 450.0F, 680.0F, settingsPanel, me -> {
            if (me.parent.waitingOnEvent) {
                me.text = "Press key";
            } else {
                me.text = "Change console hotkey";
            }
        });
        settingsPanel.addUIElement(label);
        ModButton moderButton = new ModButton(350.0f, 650.0F,
                settingsPanel, button -> AscensionManager.unlockAllRegistered());
        settingsPanel.addUIElement(moderButton);



        BaseMod.registerModBadge(badgeTexture, MOD_ID, AUTHOR, DESCRIPTION, settingsPanel);
    }

    @Override
    public Boolean onSave() {
        return ascenionExtraActivated;
    }

    @Override
    public void onLoad(Boolean bool) {
        if (bool != null) ascenionExtraActivated = bool;
        else ascenionExtraActivated = false;
    }

    public static void setBool(boolean bool) {
        mod.ascenionExtraActivated = bool;
    }

    public static boolean isActivated() {
        return mod.ascenionExtraActivated;
    }
}
