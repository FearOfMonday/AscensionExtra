package AscensionExtra.patches;

import AscensionExtra.AscensionMod;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.VictoryScreen;

import static com.megacrit.cardcrawl.screens.GameOverScreen.isVictory;

@SuppressWarnings("unused")
public class SaveProgressPatch {

    private static AscensionMod.AscensionManager manager;

    public static void addManager(AscensionMod.AscensionManager man) {
        manager = man;
    }

    @SpirePatch2(clz = CardCrawlGame.class, method = "loadPlayerSave")
    public static class LoadCurrentPlayer {

        @SpirePostfixPatch
        public static void loader(AbstractPlayer p) {
            AscensionMod.p = p.chosenClass;
            manager.loadAllButtons();
        }
    }

    @SpirePatch2(clz = DeathScreen.class, method = "updateAscensionProgress")
    public static class ForDeathScreen {

        @SpirePostfixPatch
        public static void death() {
            if ((isVictory || AbstractDungeon.actNum >= 4) && AbstractDungeon.isAscensionMode && Settings.isStandardRun()) manager.saveAfterRun();
        }
    }

    @SpirePatch2(clz = VictoryScreen.class, method = "updateAscensionAndBetaArtProgress")
    public static class ForVictoryScreen {

        @SpirePostfixPatch
        public static void victory() {
            if (AbstractDungeon.isAscensionMode && !Settings.seedSet && !Settings.isTrial) manager.saveAfterRun();
        }
    }
}
