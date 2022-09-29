package AscensionExtra.patches;

import AscensionExtra.buttons.AscensionManager;
import AscensionExtra.AscensionMod;
import AscensionExtra.buttons.AscensionData;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import javassist.CtBehavior;

public class CharacterOptionPatch {

    @SpirePatch2(clz = CharacterOption.class, method = "incrementAscensionLevel")
    public static class incrementExtraAsc {

        @SpirePrefixPatch
        public static SpireReturn<Void> incrememt(int level) {
            if (AscensionMod.isActivated()) {
                AscensionData data = AscensionManager.getClickedButton();
                if (data != null) {
                    AscensionManager.setLvl(data, level);
                    return SpireReturn.Return(null);
                } else {
                    AscensionManager.regularAscLevel = level;
                    return SpireReturn.Continue();
                }
            } else {
                AscensionManager.regularAscLevel = level;
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch2(clz = CharacterOption.class, method = "decrementAscensionLevel")
    public static class DecrementExtraAsc {

        @SpirePrefixPatch
        public static SpireReturn<Void> decrement(int level) {
            if (AscensionMod.isActivated()) {
                AscensionData data = AscensionManager.getClickedButton();
                if (data != null) {
                    AscensionManager.setLvl(data, level);
                    return SpireReturn.Return(null);
                } else {
                    AscensionManager.regularAscLevel = level;
                    return SpireReturn.Continue();
                }
            } else {
                AscensionManager.regularAscLevel = level;
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch2(clz = CharacterOption.class, method = "updateHitbox")
    public static class updateForCharacterSwitch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"pref"})
        public static SpireReturn<Void> notSorryForThis(CharacterOption __instance) {
            AscensionManager.p = __instance.c.chosenClass;
            AscensionManager.regularAscLevel = CardCrawlGame.mainMenuScreen.charSelectScreen.ascensionLevel;
            if (AscensionMod.isActivated()) {
                AscensionManager.loadAllButtons();
                AscensionData data = AscensionManager.getClickedButton();
                if (data != null) {
                    data.setLvlAndText();
                    return SpireReturn.Return(null);
                } else return SpireReturn.Continue();
            } else {
                return SpireReturn.Continue();
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.FieldAccessMatcher matcher = new Matcher.FieldAccessMatcher(CharacterSelectScreen.class, "A_TEXT");
                return LineFinder.findInOrder(ctMethodToPatch, matcher);
            }
        }
    }
}
