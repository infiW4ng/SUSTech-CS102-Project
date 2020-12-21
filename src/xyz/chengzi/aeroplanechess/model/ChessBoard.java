package xyz.chengzi.aeroplanechess.model;

import xyz.chengzi.aeroplanechess.listener.ChessBoardListener;
import xyz.chengzi.aeroplanechess.listener.Listenable;

import java.util.*;

public class ChessBoard implements Listenable<ChessBoardListener> {
    private final List<ChessBoardListener> listenerList = new ArrayList<>();
    private final Square[][] grid;
    private final LinkedList<ChessPiece>[] hangar;
    private final Square[] runway;
    private final int dimension, endDimension;
    private final int shortcutJmp = 4, shortcutDest = shortcutJmp + 3, shortcutKick = 15;

    public ChessBoard(int dimension, int endDimension) {
        this.grid = new Square[4][dimension + endDimension];
        this.hangar = new LinkedList[4];
        this.runway = new Square[4];
        for (int i = 0; i < 4; i++) {
            hangar[i] = new LinkedList<>();
            runway[i] = new Square(new ChessBoardLocation(i, -1));
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
        for (int i = 0; i < 4; i++)
            setChessPieceAt(new ChessBoardLocation(i, 0), hangar[i].pop());
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
        setChessPieceAt(new ChessBoardLocation(0, 15), hangar[0].pop());
        setChessPieceAt(new ChessBoardLocation(1, 15), hangar[1].pop());
        setChessPieceAt(new ChessBoardLocation(2, 15), hangar[2].pop());
        setChessPieceAt(new ChessBoardLocation(3, 15), hangar[3].pop());
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
        ChessBoardLocation dest = src;
        int color = getChessPieceAt(src).getPlayer();

        // FIXME: This just naively move the chess forward without checking anything
        for (int i = 0; i < steps; i++) {
            dest = nextLocationNorm(dest, color);
        }
        if(getChessPieceAt(dest) != null) {
            if (getChessPieceAt(dest).getPlayer() == color) {
                setChessPieceAt(dest, new ChessPiece(color, removeChessPieceAt(src).getStack() + getChessPieceAt(dest).getStack()));
            } else {
                kickChessPieceAt(dest);
                setChessPieceAt(dest, removeChessPieceAt(src));
            }
        } else {
                setChessPieceAt(dest, removeChessPieceAt(src));
        }
        if(dest.getColor() == color) {
            if (dest.getIndex() == shortcutJmp) {
                kickChessPieceAt(new ChessBoardLocation((color + 2) % 4, shortcutKick));
                setChessPieceAt(new ChessBoardLocation(color, shortcutDest), removeChessPieceAt(dest));
                recur = false;
            }
            if (recur) {
                if (dest.getIndex() < dimension - 1) {
                    moveChessPiece(dest, 4, false);
                } else {
                    moveChessPiece(dest, 1, false);
                }
            }
        }
    }

    public ChessBoardLocation nextLocation(ChessBoardLocation location, int color, boolean direction) {
        return location.getIndex() <= (dimension - 1) ? nextLocationNorm(location, color)
                                                      : nextLocationEnd(location, color, direction);
    }

    public ChessBoardLocation nextLocationNorm(ChessBoardLocation location, int color) {
        return location.getIndex() <= (dimension - 1) ? location.getIndex() < (dimension - 1) ? location.getIndex() < 3  ? new ChessBoardLocation((location.getColor() + 1) % 4, location.getIndex() + 10)
                                                                                                                         : new ChessBoardLocation((location.getColor() + 1) % 4, location.getIndex() - 3)
                                                                                              : color == location.getColor() ? new ChessBoardLocation(location.getColor(), location.getIndex() + 1)
                                                                                                                             : new ChessBoardLocation((location.getColor() + 1) % 4, location.getIndex() - 3)
                                                      : new ChessBoardLocation(location.getColor(), location.getIndex() + 1);
    }

    public ChessBoardLocation nextLocationEnd(ChessBoardLocation location, int color, boolean direction) {
        return direction ? new ChessBoardLocation(location.getColor(), location.getIndex() + 1)
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
