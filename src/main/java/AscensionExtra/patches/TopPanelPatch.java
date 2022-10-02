package AscensionExtra.patches;

import AscensionExtra.AscensionManager;
import AscensionExtra.AscensionMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class TopPanelPatch {

    @SpirePatch2(clz = TopPanel.class, method = "setupAscensionMode")
    public static class BuildAdditionalAscInfoBoxes {

        @SpirePostfixPatch
        public static void build() {
            AscensionManager.buildStrings();
        }
    }

    @SpirePatch2(clz = TopPanel.class, method = "renderDungeonInfo")
    public static class RenderPlusOnName {
        public static boolean notFirst = false;

        @SpireInstrumentPatch
        public static ExprEditor plusName() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("renderFontLeftTopAligned")) {
                        if (notFirst) m.replace("if (" + AscensionMod.class.getName() + ".isActivated()) {$proceed($1, $2, " +
                                TopPanelPatch.class.getName() + ".stringIntP(), $4, $5, $6);} else {$proceed($$);}");
                        else notFirst = true;
                    }
                }
            };
        }
    }

    @SpirePatch2(clz = TopPanel.class, method = "updateAscensionHover")
    public static class MakeItPossibleToSwitchTipBox {

        @SpireInsertPatch(locator = Locator.class)
        public static void switcher(TopPanel __instance) {
            if (InputHelper.justClickedLeft && __instance.ascensionHb.hovered && AbstractDungeon.isAscensionMode) __instance.ascensionHb.clickStarted = true;
            if (__instance.ascensionHb.clicked) {
                __instance.ascensionHb.clicked = false;
                AscensionManager.incrementViewIndex();
            }
        }

        @SpireInstrumentPatch
        public static ExprEditor RenderAdditionalBox() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("renderGenericTip")) {
                        m.replace("if (" + AscensionMod.class.getName() + ".isActivated() && " +
                                AscensionManager.class.getName() + ".getIndexes()[2] > 0) {$proceed($1, $2, " +
                                AscensionManager.class.getName() + ".getActiveName(), " +
                                AscensionManager.class.getName() + ".getActiveTexts());} else {$proceed($$);}");
                    }
                }
            };
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.FieldAccessMatcher matcher = new Matcher.FieldAccessMatcher(Hitbox.class, "hovered");
                return LineFinder.findInOrder(ctMethodToPatch, matcher);
            }
        }
    }

    public static String stringIntP() {
        return AbstractDungeon.ascensionLevel + "+";
    }
}
