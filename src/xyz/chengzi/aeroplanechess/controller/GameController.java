package xyz.chengzi.aeroplanechess.controller;

import xyz.chengzi.aeroplanechess.listener.GameStateListener;
import xyz.chengzi.aeroplanechess.listener.InputListener;
import xyz.chengzi.aeroplanechess.listener.Listenable;
import xyz.chengzi.aeroplanechess.model.ChessBoard;
import xyz.chengzi.aeroplanechess.model.ChessBoardLocation;
import xyz.chengzi.aeroplanechess.model.ChessPiece;
import xyz.chengzi.aeroplanechess.util.RandomUtil;
import xyz.chengzi.aeroplanechess.view.ChessBoardComponent;
import xyz.chengzi.aeroplanechess.view.ChessComponent;
import xyz.chengzi.aeroplanechess.view.SquareComponent;

import java.util.ArrayList;
import java.util.List;

public class GameController implements InputListener, Listenable<GameStateListener> {
    private final List<GameStateListener> listenerList = new ArrayList<>();
    private final ChessBoardComponent view;
    private final ChessBoard model;

    private Integer[] diceStat;
    private int playerCurrent;
    private Integer playerWon;

    public GameController(ChessBoardComponent chessBoardComponent, ChessBoard chessBoard) {
        this.view = chessBoardComponent;
        this.model = chessBoard;
        this.diceStat = null; this.playerWon = null;

        view.registerListener(this);
        model.registerListener(view);
    }

    public ChessBoardComponent getView() {
        return view;
    }

    public ChessBoard getModel() {
        return model;
    }

    public int getPlayerCurrent() {
        return playerCurrent;
    }

    public Integer getPlayerWon() { return playerWon; }

    public void initializeGame() {
        model.placeInitialPieces();
        diceStat = null; playerWon = null; playerCurrent = 0;
        listenerList.forEach(listener -> listener.onPlayerStartRound(playerCurrent, null));
    }

    public Integer[] rollDice() {
        if (diceStat == null) {
            diceStat = new Integer[7];
            diceStat[0] = null;
            diceStat[1] = RandomUtil.nextInt(1, 6);
            diceStat[2] = RandomUtil.nextInt(1, 6);
            diceStat[3] = this.diceStat[1] + this.diceStat[2];
            diceStat[4] = this.diceStat[1] - this.diceStat[2];
            diceStat[5] = (this.diceStat[1] * this.diceStat[2] <= 12 ? this.diceStat[1] * this.diceStat[2] : 12);
            diceStat[6] = ((this.diceStat[1]%this.diceStat[2]) == 0 ? (this.diceStat[1]/this.diceStat[2]) : null);
            return diceStat;
        } else {
            return null;
        }
    }

    public boolean opDice(int op) {
        if (diceStat != null && diceStat[op] != null) {
            diceStat[0] = diceStat[op];
            return true;
        } else {
            return false;
        }
    }

    public Integer[] getDice() {
        return diceStat;
    }

    public int nextPlayer() {
        diceStat = null;
        return playerCurrent = (playerCurrent + 1) % 4;
    }

    public void setDice(int i) {
        diceStat = new Integer[1];
        diceStat[0] = i;
    }

    @Override
    public void onPlayerClickSquare(ChessBoardLocation location, SquareComponent component) {
        System.out.println("clicked " + location.getColor() + "," + location.getIndex());
    }

    @Override
    public void onPlayerClickChessPiece(ChessBoardLocation location, ChessComponent component) {
        System.out.println("clicked " + location.getColor() + "," + location.getIndex());
        if (diceStat != null && diceStat[0] != null && location.getIndex() <= -1 + model.getDimension() + model.getEndDimension() - 1) {
            ChessPiece piece = model.getChessPieceAt(location);
            if (piece.getPlayer() == playerCurrent) {
                model.moveChessPiece(location, diceStat[0], true);
                listenerList.forEach(listener -> listener.onPlayerEndRound(playerCurrent));
                for(int i = 0; i < 4; i++)
                    if (model.getChessPieceAt(new ChessBoardLocation(i, -1 + model.getDimension() + model.getEndDimension())).getStack() >= 4) { playerWon = (Integer) i; break; }
                nextPlayer();
                listenerList.forEach(listener -> listener.onPlayerStartRound(playerCurrent, playerWon));
            }
        }
    }

    @Override
    public void registerListener(GameStateListener listener) {
        listenerList.add(listener);
    }

    @Override
    public void unregisterListener(GameStateListener listener) {
        listenerList.remove(listener);
    }
}
