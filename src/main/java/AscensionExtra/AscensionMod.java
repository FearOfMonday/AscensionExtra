package AscensionExtra;

import AscensionExtra.buttons.AscensionManager;
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
    public static final String DESCRIPTION = "Allows for multiple custom Ascension to be registered and used simutaniously without conflict, similar to Hades' heat system.";

    public static String makePath(String resourcePath) {
        return MOD_ID + "Resources/" + resourcePath;
    }
    public static final String BADGE_IMAGE = makePath("images/Badge.png");

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
        BaseMod.registerModBadge(badgeTexture, MOD_ID, AUTHOR, DESCRIPTION, new ModPanel());
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
