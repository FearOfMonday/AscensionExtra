package AscensionExtra.patches;

import AscensionExtra.AscensionManager;
import AscensionExtra.AscensionMod;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class CharacterSelectScreenPatch {

    @SpirePatch2(clz = CharacterSelectScreen.class, method = "initialize")
    public static class InitButtons {

        @SpirePostfixPatch
        public static void init() {
            AscensionManager.initButtons();
        }
    }

    @SpirePatch2(clz = CharacterSelectScreen.class, method = "updateAscensionToggle")
    public static class UpdateButtons {

        @SpirePostfixPatch
        public static void buttona(CharacterSelectScreen __instance) {
            if (AscensionManager.p != null && AscensionManager.hasButtons()) {
                AscensionManager.update(__instance);
            }
        }
    }

    @SpirePatch2(clz = CharacterSelectScreen.class, method = "renderAscensionMode")
    public static class DisplayButtons {

        public static int counter = 2;

        @SpirePostfixPatch
        public static void buttona(@ByRef SpriteBatch[] sb) {
            if (AscensionManager.p != null && AscensionManager.hasButtons()) {
                AscensionManager.render(sb[0]);
            }
        }

        @SpireInstrumentPatch
        public static ExprEditor RenderButtonNameInPlaceOfAscension() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("renderFontCentered") && counter > 0) {
                        counter--;
                        m.replace("if (" + CharacterSelectScreenPatch.class.getName() + ".getClickedName() != null) {$proceed($1, $2, " +
                                CharacterSelectScreenPatch.class.getName() + ".getClickedName(), " +
                                CharacterSelectScreenPatch.class.getName() + ".getDisplacement(), $5, $6);} else {$proceed($$);}");
                    }
                }
            };
        }
    }

    @SpirePatch2(clz = CharacterSelectScreen.class, method = "updateButtons")
    public static class StuffToDoWhenCancelOrProgress {

        @SpireInsertPatch(locator = Locator.class)
        public static void hideExButtonWhenCancel() {
            AscensionManager.p = null;
            AscensionManager.disableAll();
            AscensionMod.setBool(false);
        }

        @SpireInsertPatch(locator = Locator2.class)
        public static void restoreAscensionLevelWhenProgress() {
            AscensionManager.resetTxtNLvl();
            AscensionManager.disableAll();
            if (AscensionMod.isActivated()) {
                //Makes sure to disable this check if no extra ascensions are actually above lvl 0
                AscensionMod.setBool(AscensionManager.isAnyButtonActive());
            }
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.FieldAccessMatcher matcher = new Matcher.FieldAccessMatcher(MainMenuScreen.class, "superDarken");
                return LineFinder.findInOrder(ctMethodToPatch, matcher);
            }
        }

        private static class Locator2 extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.FieldAccessMatcher matcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "isAscensionMode");
                return LineFinder.findInOrder(ctMethodToPatch, matcher);
            }
        }
    }

    @SpirePatch2(clz = CharacterSelectScreen.class, method = "open")
    public static class SetPlayerToNullAndDisableExtra {

        @SpirePostfixPatch
        public static void dailyButton() {
            AscensionManager.p = null;
            AscensionMod.setBool(false);
        }
    }

    public static String getClickedName() {
        return AscensionManager.getClickedButton() != null ? AscensionManager.getClickedButton().name : null;
    }

    public static float getDisplacement() {
        return (Settings.WIDTH / 2.0F);
    }

}