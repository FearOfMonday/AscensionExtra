package AscensionExtra.patches;

import AscensionExtra.AscensionMod;
import AscensionExtra.buttons.AscensionData;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
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
        public static void increment(@ByRef Integer[] level) {
            if (manager.isActive) {
                AscensionData data = manager.getClickedButton();
                if (data != null) {
                    if (manager.prevAscLvl != level[0]) level[0] = manager.prevAscLvl;
                    data.uniqueCounter += 1;
                    data.saveLvl();
                    data.setLvlAndText();
                } else manager.saveOldTxt();
            } else manager.saveOldTxt();
        }

        @SpirePostfixPatch
        public static void secondaryTextUpdateToEnsureThatMyTextWins(@ByRef Integer[] level) {
            if (manager.isActive) {
                AscensionData data = manager.getClickedButton();
                if (data != null) data.setLvlAndText();
                else manager.saveOldTxt();
            } else manager.saveOldTxt();
            manager.prevAscLvl = level[0];
        }
    }

    @SpirePatch2(clz = CharacterOption.class, method = "decrementAscensionLevel")
    public static class DecrementExtraAsc {

        @SpirePrefixPatch
        public static void decrement(@ByRef Integer[] level) {
            if (manager.isActive) {
                AscensionData data = manager.getClickedButton();
                if (data != null) {
                    if (manager.prevAscLvl != level[0]) level[0] = manager.prevAscLvl;
                    data.uniqueCounter -= 1;
                    data.saveLvl();
                    data.setLvlAndText();
                } else manager.saveOldTxt();
            } else manager.saveOldTxt();
        }

        @SpirePostfixPatch
        public static void secondaryTextUpdateToEnsureThatMyTextWins(@ByRef Integer[] level) {
            if (manager.isActive) {
                AscensionData data = manager.getClickedButton();
                if (data != null) data.setLvlAndText();
                else manager.saveOldTxt();
            } else manager.saveOldTxt();
            manager.prevAscLvl = level[0];
        }
    }

    @SpirePatch2(clz = CharacterOption.class, method = "updateHitbox")
    public static class updateForCharacterSwitch {

        public static boolean iAmASwitch = false;

        @SpireInsertPatch(locator = Locator.class)
        public static void preBool(CharacterOption __instance) {
            AscensionMod.p = __instance.c.chosenClass;
            iAmASwitch = true;
        }

        @SpirePostfixPatch
        public static void afterBool() {
            if (iAmASwitch) {
                manager.prevAscLvl = CardCrawlGame.mainMenuScreen.charSelectScreen.ascensionLevel;
                manager.saveOldTxt();
                if (manager.isActive) {
                    manager.loadAllButtons();
                    AscensionData data = manager.getClickedButton();
                    if (data != null) data.setLvlAndText();
                }
                iAmASwitch = false;
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.FieldAccessMatcher matcher = new Matcher.FieldAccessMatcher(CharacterOption.class, "maxAscensionLevel");
                return LineFinder.findInOrder(ctMethodToPatch, matcher);
            }
        }
    }
}
