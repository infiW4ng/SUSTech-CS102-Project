package xyz.chengzi.aeroplanechess.model;

import xyz.chengzi.aeroplanechess.listener.ChessBoardListener;
import xyz.chengzi.aeroplanechess.listener.Listenable;

import java.util.*;

public class ChessBoard implements Listenable<ChessBoardListener> {
    private final List<ChessBoardListener> listenerList = new ArrayList<>();
    private final Square[][] grid;
    private final LinkedList<ChessPiece>[] hangar;
    private final int[] landing;
    private final int dimension, endDimension;
    private final int shortcutJmp = 5, shortcutDest = shortcutJmp + 3, shortcutKick = 16;

    public ChessBoard(int dimension, int endDimension) {
        this.grid = new Square[4][dimension + endDimension];
        this.hangar = new LinkedList[4];
        this.landing = new int[4];
        for (int i = 0; i < 4; i++) {
            hangar[i] = new LinkedList<>();
        }
        this.dimension = dimension;
        this.endDimension = endDimension;

        initGrid();
    }

    private void initGrid() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < dimension + endDimension; j++) {
                grid[i][j] = new Square(new ChessBoardLocation(i, j));
            }
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                hangar[i].push(new ChessPiece(i));
            }
        }
    }

    public void placeInitialPieces() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < dimension + endDimension; j++) {
                grid[i][j].setPiece(null);
            }
        }
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                hangar[i].push(new ChessPiece(i));

        // FIXME: Demo implementation
        for (int i = 0; i < 4; i++) {
            setChessPieceAt(new ChessBoardLocation(i, 0), hangar[i].pop());
            setChessPieceAt(new ChessBoardLocation(i, -1 + dimension + endDimension), new ChessPiece(i, 0));
        }
//        grid[1][10].setPiece(tarmac[0].pop());
//        grid[2][7].setPiece(tarmac[0].pop());
//        grid[3][4].setPiece(tarmac[0].pop());

//        grid[0][1].setPiece(hangar[1].pop());
//        grid[1][1].setPiece(hangar[2].pop());
//        grid[2][1].setPiece(hangar[3].pop());
//        grid[3][1].setPiece(hangar[0].pop());
//        grid[0][2].setPiece(hangar[1].pop());
//        grid[1][2].setPiece(hangar[2].pop());
//        grid[2][2].setPiece(hangar[3].pop());
//        grid[3][2].setPiece(hangar[0].pop());

//        for (int i = 0; i < 4; i++)
//            grid[i][1].setPiece(tarmac[i].pop());
//        for (int i = 0; i < 4; i++)
//            grid[i][2].setPiece(tarmac[i].pop());
//        for (int i = 0; i < 4; i++)
//            grid[i][3].setPiece(tarmac[i].pop());
//        grid[0][15].setPiece(hangar[0].pop());
//        grid[1][15].setPiece(hangar[1].pop());
//        grid[2][15].setPiece(hangar[2].pop());
//        grid[3][15].setPiece(hangar[3].pop());
        setChessPieceAt(new ChessBoardLocation(0, 16), hangar[0].pop());
        setChessPieceAt(new ChessBoardLocation(0, 16), new ChessPiece(0, 4));
        setChessPieceAt(new ChessBoardLocation(1, 16), hangar[1].pop());
        setChessPieceAt(new ChessBoardLocation(2, 16), hangar[2].pop());
        setChessPieceAt(new ChessBoardLocation(3, 16), hangar[3].pop());
        setChessPieceAt(new ChessBoardLocation(0, 12), hangar[0].pop());
        setChessPieceAt(new ChessBoardLocation(1, 12), hangar[1].pop());
        setChessPieceAt(new ChessBoardLocation(2, 12), hangar[2].pop());
        setChessPieceAt(new ChessBoardLocation(3, 12), hangar[3].pop());
        listenerList.forEach(listener -> listener.onChessBoardReload(this));
    }

    public Square getGridAt(ChessBoardLocation location) {
        return grid[location.getColor()][location.getIndex()];
    }

    public int getDimension() {
        return dimension;
    }

    public int getEndDimension() {
        return endDimension;
    }

    public ChessPiece getChessPieceAt(ChessBoardLocation location) {
        return getGridAt(location).getPiece();
    }

    public void setChessPieceAt(ChessBoardLocation location, ChessPiece piece) {
        getGridAt(location).setPiece(piece);
        listenerList.forEach(listener -> listener.onChessPiecePlace(location, piece));
    }

    public ChessPiece removeChessPieceAt(ChessBoardLocation location) {
        ChessPiece piece = getGridAt(location).getPiece(); if (piece == null) return null;
        getGridAt(location).setPiece(null);
        listenerList.forEach(listener -> listener.onChessPieceRemove(location));
        return piece;
    }

    public ChessPiece kickChessPieceAt(ChessBoardLocation location) {
        ChessPiece piece = getGridAt(location).getPiece(); if (piece == null) return null;
        removeChessPieceAt(location);
        for (int i = 0; i < piece.getStack(); i++)
            hangar[piece.getPlayer()].push(new ChessPiece(piece.getPlayer()));
        return piece;
    }

    public void moveChessPiece(ChessBoardLocation src, int steps, boolean recur) {
        ChessBoardLocation s = src;
        ChessBoardLocation dest = src;
        int player = getChessPieceAt(src).getPlayer();

        // FIXME: This just naively move the chess forward without checking anything
        do {
            boolean[] flagDir = new boolean[1]; flagDir[0] = (steps >= 0);
            for (int i = 0; i < Math.abs(steps); i++) {
                dest = dest.getIndex() < dimension - 1 ? (flagDir[0] ? nextLocationVice(dest, player) : nextLocationVersa(dest, player))
                                                       : nextLocationEnd(dest, player, flagDir);
            }
        } while(false);

        if (dest.getColor() == s.getColor() && dest.getIndex() == s.getIndex()) return;
        if(getChessPieceAt(dest) != null) {
            if (getChessPieceAt(dest).getPlayer() == player) {
                setChessPieceAt(dest, removeChessPieceAt(src).merge(getChessPieceAt(dest)));
            } else {
                kickChessPieceAt(dest);
                setChessPieceAt(dest, removeChessPieceAt(src));
            }
        } else {
                setChessPieceAt(dest, removeChessPieceAt(src));
        }
        if(dest.getColor() == player) {
            if (dest.getIndex() == shortcutJmp) {
                kickChessPieceAt(new ChessBoardLocation((player + 2) % 4, shortcutKick));
                setChessPieceAt(new ChessBoardLocation(player, shortcutDest), removeChessPieceAt(dest));
                recur = false;
            }
            if (recur && dest.getIndex() < dimension - 1) moveChessPiece(dest, 4, false);
        }
    }

    public int getCountOn(int player) {
        return 4 - hangar[player].size();
    }

//    public ChessBoardLocation nextLocation(ChessBoardLocation location, int color, int steps, boolean[] flagDir) {
//        return location.getIndex() <= (dimension - 1) ? nextLocationNorm(location, color)
//                                                      : nextLocationEnd(location, color, flagDir);
//    }

    public ChessBoardLocation nextLocationVice(ChessBoardLocation location, int color) {
        return location.getIndex() <= 0 ? new ChessBoardLocation(color, location.getIndex() + 1)
                                        : location.getIndex() <= 3  ? new ChessBoardLocation((location.getColor() + 1) % 4, location.getIndex() + 10)
                                                                    : new ChessBoardLocation((location.getColor() + 1) % 4, location.getIndex() - 3);
    }

    public ChessBoardLocation nextLocationVersa(ChessBoardLocation location, int color) {
        return location.getIndex() >= 11  ? new ChessBoardLocation((location.getColor() + 3) % 4, location.getIndex() - 10)
                                          : new ChessBoardLocation((location.getColor() + 3) % 4, location.getIndex() + 3);
    }

    public ChessBoardLocation nextLocationEnd(ChessBoardLocation location, int color, boolean[] flagDir) {
        if (location.getIndex() <= dimension && !flagDir[0]) flagDir[0] = !flagDir[0];
        if (location.getIndex() >= -1 + dimension + endDimension && flagDir[0]) flagDir[0] = !flagDir[0];
        return flagDir[0] ? new ChessBoardLocation(location.getColor(), location.getIndex() + 1)
                          : new ChessBoardLocation(location.getColor(), location.getIndex() - 1);
    }

    public int deltaPos(int colorA, int colorB)
    {
        int abs = Math.abs(colorA - colorB);
        return (13 * abs);
    }

    @Override
    public void registerListener(ChessBoardListener listener) {
        listenerList.add(listener);
    }

    @Override
    public void unregisterListener(ChessBoardListener listener) {
        listenerList.remove(listener);
    }
}
