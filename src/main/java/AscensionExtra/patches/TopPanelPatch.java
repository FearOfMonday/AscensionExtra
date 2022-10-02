package AscensionExtra.patches;

import AscensionExtra.AscensionMod;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.ui.panels.TopPanel;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SuppressWarnings("unused")
public class TopPanelPatch {

    private static AscensionMod.AscensionManager manager;

    public static void addManager(AscensionMod.AscensionManager man) {
        manager = man;
    }

    @SpirePatch2(clz = TopPanel.class, method = "setupAscensionMode")
    public static class BuildAdditionalAscInfoBoxes {

        @SpirePostfixPatch
        public static void build(@ByRef String[] ___ascensionString) {
            ___ascensionString[0] += CardCrawlGame.languagePack.getUIString("ascensionmanager:AscensionPrefix").TEXT[6];
            manager.buildStrings();
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
                        if (notFirst) m.replace("if (" + TopPanelPatch.class.getName() + ".getActive()) {$proceed($1, $2, " +
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
                manager.incrementViewIndex();
            }
        }

        @SpireInstrumentPatch
        public static ExprEditor RenderAdditionalBox() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("renderGenericTip")) {
                        m.replace("if (" + TopPanelPatch.class.getName() + ".getActive() && " +
                                TopPanelPatch.class.getName() + ".getActiveIndex() > 0) {$proceed($1, $2, " +
                                TopPanelPatch.class.getName() + ".getActiveName(), " +
                                TopPanelPatch.class.getName() + ".getActiveTexts());} else {$proceed($$);}");
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

    public static boolean getActive() {
        return manager.isActive;
    }

    public static int getActiveIndex() {
        return manager.viewIndex;
    }

    public static String getActiveName() {
        return manager.data.get(manager.viewIndex - 1).name;
    }

    public static String getActiveTexts() {
        return manager.ascTexts[manager.viewIndex - 1];
    }

    public static String stringIntP() {
        return AbstractDungeon.ascensionLevel + "+";
    }
}
