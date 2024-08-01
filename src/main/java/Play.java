import zoz.cool.ai.ChessEnum;
import zoz.cool.ai.Point;
import zoz.cool.ai.RoleEnum;

import java.util.Random;
import java.util.Scanner;

public class Play {
    private final int size;
    private final Board board;

    public Play(int size, boolean aiFirst) {
        this.size = size;
        this.board = new Board(size, aiFirst ? RoleEnum.AI : RoleEnum.HUMAN);
    }

    public void play() {
        System.out.println("Game started!");
        while (true) {
            if (board.isGameOver()) {
                System.out.println("Game is over!");
                break;
            }
            RoleEnum winner = board.getWinner();
            if (winner != null) {
                System.out.println("Winner is: " + winner);
                break;
            }
            Point point;
            if (board.getCurrentRole() == RoleEnum.AI) {
                // AI player
                point = getRandomMove(board.getBoard());
            } else {
                // Human player
                point = getUserInput(size);
            }
            board.put(point);
            board.show(null);
        }
    }

    private Point getUserInput(int size) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please input your move: ");
        String xy = scanner.nextLine();
        String[] split = xy.split(",| +");
        if (split.length != 2) {
            System.out.println("Invalid input, please input again.");
            return getUserInput(size);
        }
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        if (x < 0 || x >= size || y < 0 || y >= size) {
            System.out.println("Invalid input, please input again.");
            return getUserInput(size);
        }
        return new Point(x, y);
    }

    private Point getRandomMove(ChessEnum[][] board) {
        Random random = new Random();
        int x = random.nextInt(board.length);
        int y = random.nextInt(board.length);
        while (board[x][y] != ChessEnum.EMPTY) {
            x = random.nextInt(board.length);
            y = random.nextInt(board.length);
        }
        return new Point(x, y);
    }
}