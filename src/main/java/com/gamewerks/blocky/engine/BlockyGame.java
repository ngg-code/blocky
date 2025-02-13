package com.gamewerks.blocky.engine;

import java.util.Random;

import com.gamewerks.blocky.util.Constants;
import com.gamewerks.blocky.util.Position;

public class BlockyGame {
    private static final int LOCK_DELAY_LIMIT = 30;

    private Board board;
    private Piece activePiece;
    private Direction movement;

    private int lockCounter;

    private PieceKind[] NewPiece;

    public BlockyGame() {
        board = new Board();
        movement = Direction.NONE;
        lockCounter = 0;
        trySpawnBlock();
    }

    public void swap(int[] a){
        int temporaryA = a[0];
        a[0] = a[1];
        a[1] = temporaryA;
    }

     public int shuffle(){
        int[] index = {0, 1, 2, 3, 4, 5, 6};
        Random rand = new Random();
        int Length = index.length;
        int[] a = new int[2];
        int currentIndex = rand.nextInt(Length);
        a[0] = currentIndex;
        a[1] = index[Length - 1];
        swap(a);
        Length--;
        return currentIndex;
    }

    private void trySpawnBlock() {
        if (activePiece == null) {
            activePiece = new Piece(PieceKind.I,
                    new Position(Constants.BOARD_HEIGHT - 1, Constants.BOARD_WIDTH / 2 - 2));
            if (board.collides(activePiece)) {
                System.exit(0);
            }
        }
    }

    private void processMovement() {
        Position nextPos;
        switch (movement) {
            case NONE:
                nextPos = activePiece.getPosition();
                break;
            case LEFT:
                nextPos = activePiece.getPosition().add(0, -1);
                break;
            case RIGHT:
                nextPos = activePiece.getPosition().add(0, 1);
                break;
            default:
                throw new IllegalStateException("Unrecognized direction: " + movement.name());
        }
        if (!board.collides(activePiece.getLayout(), nextPos)) {
            activePiece.moveTo(nextPos);
        }
    }

    private void processGravity() {
        Position nextPos = activePiece.getPosition().add(-1, 0);
        if (!board.collides(activePiece.getLayout(), nextPos)) {
            lockCounter = 0;
            activePiece.moveTo(nextPos);
        } else {
            if (lockCounter < LOCK_DELAY_LIMIT) {
                lockCounter += 1;
            } else {
                board.addToWell(activePiece);
                lockCounter = 0;
                activePiece = null;
            }
        }
    trySpawnBlock();
    }

    private void processClearedLines() {
        board.deleteRows(board.getCompletedRows());
    }

    public void step() {
        trySpawnBlock();
        processGravity();
        processClearedLines();
        processMovement();
    }

    public boolean[][] getWell() {
        return board.getWell();
    }

    public Piece getActivePiece() {
        return activePiece;
    }

    public void setDirection(Direction movement) {
        this.movement = movement;
    }

    public void rotatePiece(boolean dir) {
        if (activePiece != null) {activePiece.rotate(dir);}
    }
}
