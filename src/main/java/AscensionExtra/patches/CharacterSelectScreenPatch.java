package AscensionExtra.patches;

import AscensionExtra.AscensionMod;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.screens.charSelect.CharacterOption;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import javassist.CannotCompileException;
import javassist.CtBehavior;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

@SuppressWarnings("unused")
public class CharacterSelectScreenPatch {

    private static AscensionMod.AscensionManager manager;

    public static void addManager(AscensionMod.AscensionManager man) {
        manager = man;
    }

    @SpirePatch2(clz = CharacterSelectScreen.class, method = "initialize")
    public static class InitButtons {

        @SpirePostfixPatch
        public static void init() {
            manager.initButtons();
        }
    }

    @SpirePatch2(clz = CharacterSelectScreen.class, method = "updateAscensionToggle")
    public static class MixOfPatches {

        public static boolean extendedClick = false;

        @SpireInsertPatch(locator = Locator.class)
        public static void workAroundForMinty() {
            extendedClick = true;
        }


        @SpirePostfixPatch
        public static void buttonLogic(CharacterSelectScreen __instance) {
            if (extendedClick) additionalUpdate(__instance);
            if (AscensionMod.p != null && manager.hasButtons()) manager.update(__instance);
        }

        private static class Locator extends SpireInsertLocator {
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
                Matcher.FieldAccessMatcher fieldAccessMatcher = new Matcher.FieldAccessMatcher(CharacterSelectScreen.class, "options");
                return new int[] {LineFinder.findAllInOrder(ctMethodToPatch, fieldAccessMatcher)[1]};
            }
        }
    }

    @SpirePatch2(clz = CharacterSelectScreen.class, method = "renderAscensionMode")
    public static class DisplayButtons {

        public static int counter = 3;

        @SpirePrefixPatch
        public static void renderButtons(CharacterSelectScreen __instance, SpriteBatch sb) {
            if (__instance.isAscensionMode) AbstractDungeon.ascensionLevel = CardCrawlGame.mainMenuScreen.charSelectScreen.ascensionLevel;
            if (AscensionMod.p != null && manager.hasButtons()) manager.render(sb);
        }

        @SpirePostfixPatch
        public static void renderArrowAgainBecauseOfMinty(Hitbox ___ascRightHb, SpriteBatch sb) {
            if (getClickedName() != null) {
                if (!___ascRightHb.hovered && !Settings.isControllerMode) {
                    sb.setColor(Color.LIGHT_GRAY);
                } else {
                    sb.setColor(Color.WHITE);
                }
                sb.draw(ImageMaster.CF_RIGHT_ARROW, ___ascRightHb.cX - 24.0F, ___ascRightHb.cY - 24.0F, 24.0F, 24.0F, 48.0F, 48.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 48, 48, false, false);
            }
        }

        @SpireInstrumentPatch
        public static ExprEditor RenderButtonNameInPlaceOfAscension() {
            return new ExprEditor() {
                public void edit(MethodCall m) throws CannotCompileException {
                    if (m.getMethodName().equals("renderFontCentered") && counter > 0) {
                        counter--;
                        if (counter == 0) {
                            m.replace("if (" + CharacterSelectScreenPatch.class.getName() + ".getClickedName() != null) {$proceed($1, $2, "
                                    + CharacterSelectScreenPatch.class.getName() + ".getLvl(), $4, $5, " + Settings.class.getName() + ".BLUE_TEXT_COLOR);} else {$proceed($$);}");
                        } else {
                            m.replace("if (" + CharacterSelectScreenPatch.class.getName() + ".getClickedName() != null) {$proceed($1, $2, " +
                                    CharacterSelectScreenPatch.class.getName() + ".getClickedName(), " +
                                    CharacterSelectScreenPatch.class.getName() + ".getDisplacement(), $5, $6);} else {$proceed($$);}");
                        }
                    }
                }
            };
        }
    }

    @SpirePatch2(clz = CharacterSelectScreen.class, method = "updateButtons")
    public static class StuffToDoWhenCancelOrProgress {

        @SpireInsertPatch(locator = Locator.class)
        public static void hideExButtonWhenCancel() {
            AscensionMod.p = null;
            manager.disableExtraButton();
            manager.disableButtons(null);
            manager.isActive = false;
        }

        @SpireInsertPatch(locator = Locator2.class)
        public static void updateWhenProgress() {
            if (manager.isActive) {
                //Makes sure to disable this check if no extra ascensions are actually above lvl 0
                manager.isActive = manager.isAnyButtonActive();
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
            AscensionMod.p = null;
            manager.disableExtraButton();
            manager.disableButtons(null);
            manager.isActive = false;
        }
    }

    public static String getLvl() {
        return CharacterSelectScreen.TEXT[7] + (manager.getClickedButton() != null ? manager.getClickedButton().uniqueCounter : 0);
    }

    public static String getClickedName() {
        return manager.getClickedButton() != null ? manager.getClickedButton().name : null;
    }

    public static float getDisplacement() {
        return (Settings.WIDTH / 2.0F);
    }

    public static void additionalUpdate(CharacterSelectScreen ch) {
        for (CharacterOption o : ch.options) {
            if (o.selected) {
                o.incrementAscensionLevel(ch.ascensionLevel + 1);
                break;
            }
        }
    }
}