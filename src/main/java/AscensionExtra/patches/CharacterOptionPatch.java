package AscensionExtra.patches;

import AscensionExtra.AscensionMod;
import AscensionExtra.buttons.AscensionData;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import javassist.CtBehavior;

@SuppressWarnings("unused")
public class CharacterOptionPatch {

    private static AscensionMod.AscensionManager manager;

    public static void addManager(AscensionMod.AscensionManager man) {
        manager = man;
    }

    @SpirePatch2(clz = CharacterOption.class, method = "incrementAscensionLevel")
    public static class incrementExtraAsc {

        @SpirePrefixPatch
        public static SpireReturn<Void> incrememt(int level) {
            if (manager.isActive) {
                AscensionData data = manager.getClickedButton();
                if (data != null) {
                    manager.setLvl(data, level);
                    return SpireReturn.Return(null);
                } else {
                    manager.regularAscLevel = level;
                    return SpireReturn.Continue();
                }
            } else {
                manager.regularAscLevel = level;
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch2(clz = CharacterOption.class, method = "decrementAscensionLevel")
    public static class DecrementExtraAsc {

        @SpirePrefixPatch
        public static SpireReturn<Void> decrement(int level) {
            if (manager.isActive) {
                AscensionData data = manager.getClickedButton();
                if (data != null) {
                    manager.setLvl(data, level);
                    return SpireReturn.Return(null);
                } else {
                    manager.regularAscLevel = level;
                    return SpireReturn.Continue();
                }
            } else {
                manager.regularAscLevel = level;
                return SpireReturn.Continue();
            }
        }
    }

    @SpirePatch2(clz = CharacterOption.class, method = "updateHitbox")
    public static class updateForCharacterSwitch {
        @SpireInsertPatch(locator = Locator.class, localvars = {"pref"})
        public static SpireReturn<Void> notSorryForThis(CharacterOption __instance) {
            AscensionMod.p = __instance.c.chosenClass;
            manager.regularAscLevel = CardCrawlGame.mainMenuScreen.charSelectScreen.ascensionLevel;
            if (manager.isActive) {
                manager.loadAllButtons();
                AscensionData data = manager.getClickedButton();
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
