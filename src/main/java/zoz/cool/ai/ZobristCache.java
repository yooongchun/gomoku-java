package zoz.cool.ai;

import lombok.Getter;

@Getter
public class ZobristCache {
    private final Integer size;
    private Long hash;
    private final Long[][][] table; // [x][y][chess]

    public ZobristCache(Integer size) {
        this.size = size;
        this.hash = 0L;
        this.table = new Long[size][size][3];
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                table[x][y][ChessEnum.BLACK.ordinal()] = (long) (Math.random() * Long.MAX_VALUE);
                table[x][y][ChessEnum.WHITE.ordinal()] = (long) (Math.random() * Long.MAX_VALUE);
            }
        }
    }

    public void togglePiece(Point point, ChessEnum chess) {
        hash ^= table[point.x()][point.y()][chess.ordinal()];
    }
}
