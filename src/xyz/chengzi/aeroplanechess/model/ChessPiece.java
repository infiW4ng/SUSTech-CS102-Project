package xyz.chengzi.aeroplanechess.model;

public class ChessPiece {
    private final int player;
    private final int stack;

    public ChessPiece(int player, int stack) {
        this.player = player;
        this.stack = stack;
    }
    public ChessPiece(int player) {
        this.player = player;
        this.stack = 1;
    }

    public int getPlayer() {
        return player;
    }
    public int getStack() {
        return stack;
    }

    public ChessPiece merge(ChessPiece cp) {
        return player == cp.player ? new ChessPiece(player, stack + cp.stack) : null;
    }
}
