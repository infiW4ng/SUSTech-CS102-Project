package xyz.chengzi.aeroplanechess.listener;

public interface GameStateListener extends Listener {
    void onPlayerStartRound(int player, Integer playerWon);

    void onPlayerEndRound(int player);
}
