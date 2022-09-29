package AscensionExtra.patches;

import AscensionExtra.buttons.AscensionManager;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.VictoryScreen;

import static com.megacrit.cardcrawl.screens.GameOverScreen.isVictory;

public class SaveProgressPatch {

    @SpirePatch2(clz = CardCrawlGame.class, method = "loadPlayerSave")
    public static class LoadCurrentPlayer {

        @SpirePostfixPatch
        public static void loader(AbstractPlayer p) {
            AscensionManager.p = p.chosenClass;
            AscensionManager.loadAllButtons();
        }
    }

    @SpirePatch2(clz = DeathScreen.class, method = "updateAscensionProgress")
    public static class ForDeathScreen {

        @SpirePostfixPatch
        public static void death() {
            if ((isVictory || AbstractDungeon.actNum >= 4) && AbstractDungeon.isAscensionMode && Settings.isStandardRun()) AscensionManager.saveAfterRun();
        }
    }

    @SpirePatch2(clz = VictoryScreen.class, method = "updateAscensionAndBetaArtProgress")
    public static class ForVictoryScreen {

        @SpirePostfixPatch
        public static void victory() {
            if (AbstractDungeon.isAscensionMode && !Settings.seedSet && !Settings.isTrial) AscensionManager.saveAfterRun();
        }
    }
}
