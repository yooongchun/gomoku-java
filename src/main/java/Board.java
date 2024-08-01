import lombok.Data;
import zoz.cool.ai.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
public class Board {
    private Integer size;
    private ChessEnum[][] board;
    private RoleEnum firstRole;
    private RoleEnum currentRole;
    private List<OpHistory> history;
    private ZobristCache cache;
    private Cache winnerCache;
    private Cache gameOverCache;
    private Cache evaluationCache;
    private Cache valuableMoveCache;
    private Duration evaluationTime;
    private Evaluation evaluation;

    public Board(Integer size, RoleEnum firstRole) {
        this.size = size;
        this.firstRole = firstRole;
        this.currentRole = firstRole;
        this.board = new ChessEnum[size][size];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                board[x][y] = ChessEnum.EMPTY;
            }
        }
        this.history = new ArrayList<>();
        this.cache = new ZobristCache(size);
        this.winnerCache = new Cache(size);
        this.gameOverCache = new Cache(size);
        this.evaluationCache = new Cache(size);
        this.valuableMoveCache = new Cache(size);
        this.evaluationTime = Duration.ZERO;
        this.evaluation = new Evaluation();
    }

    public void put(Point point) {
        ChessEnum chess = toChess(currentRole);
        if (point.x() < 0 || point.x() >= size || point.y() < 0 || point.y() >= size) {
            throw new IllegalArgumentException("The position is out of the board.");
        }
        if (board[point.x()][point.y()] != ChessEnum.EMPTY) {
            throw new IllegalArgumentException("The position is already occupied.");
        }
        board[point.x()][point.y()] = chess;
        history.add(new OpHistory(point, chess));
        cache.togglePiece(point, chess);
        evaluation.move(point, chess);
        cache.togglePiece(point, chess);
        toggleRole();
    }

    public void toggleRole() {
        currentRole = currentRole == RoleEnum.HUMAN ? RoleEnum.AI : RoleEnum.HUMAN;
    }

    public boolean isGameOver() {
        Long hash = cache.getHash();
        Object obj = gameOverCache.get(hash);
        if (obj != null) {
            return true;
        }
        if (getWinner() != null) {
            gameOverCache.put(hash, true);
            return true;
        }
        // 没有赢家但是还有空位，说明游戏还在进行中
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == ChessEnum.EMPTY) {
                    gameOverCache.put(hash, false);
                    return false;
                }
            }
        }
        // 没有赢家并且没有空位，游戏结束
        gameOverCache.put(hash, true);
        return true;
    }

    public RoleEnum getWinner() {
        Long hash = cache.getHash();
        Object obj = winnerCache.get(hash);
        if (obj != null) {
            return (RoleEnum) obj;
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == ChessEnum.EMPTY) {
                    continue;
                }
                for (DirectionEnum direction : DirectionEnum.values()) {
                    {
                        int step = 0;
                        int nextX = i;
                        int nextY = j;
                        while (nextX >= 0 && nextX < size && nextY >= 0 && nextY < size &&
                                board[nextX][nextY] == board[i][j]) {
                            step++;
                            nextX = i + direction.getX() * step;
                            nextY = j + direction.getY() * step;
                        }
                        if (step >= 5) {
                            winnerCache.put(hash, board[i][j]);
                            return toRole(board[i][j]);
                        }
                    }
                }
            }
        }
        winnerCache.put(hash, ChessEnum.EMPTY);
        return null;
    }

    public void undo() {
        if (history.isEmpty()) {
            throw new IllegalArgumentException("No operation to undo.");
        }
        OpHistory lastOp = history.getLast();
        board[lastOp.getPoint().x()][lastOp.getPoint().y()] = ChessEnum.EMPTY;
        cache.togglePiece(lastOp.getPoint(), lastOp.getChess());
        evaluation.undo(lastOp.getPoint());
        toggleRole();
    }

    public void show(List<Point> points) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (points != null && points.contains(new Point(i, j))) {
                    result.append("?  ");
                    continue;
                }
                switch (board[i][j]) {
                    case BLACK -> result.append("O  ");
                    case WHITE -> result.append("X  ");
                    default -> result.append("-  ");
                }
            }
            result.append("\n");
        }
        System.out.println(result);
    }

    private RoleEnum toRole(ChessEnum chess) {
        assert chess == ChessEnum.BLACK || chess == ChessEnum.WHITE;
        return chess == ChessEnum.BLACK ? firstRole : secondRole();
    }

    private RoleEnum secondRole() {
        return firstRole == RoleEnum.HUMAN ? RoleEnum.AI : RoleEnum.HUMAN;
    }

    private ChessEnum toChess(RoleEnum role) {
        assert role == RoleEnum.HUMAN || role == RoleEnum.AI;
        return role == firstRole ? ChessEnum.BLACK : ChessEnum.WHITE;
    }
}
