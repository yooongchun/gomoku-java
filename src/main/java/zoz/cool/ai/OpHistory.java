package zoz.cool.ai;

import lombok.Data;

@Data
public class OpHistory {
    private  Point point;
    private ChessEnum chess;

    public OpHistory(Point point, ChessEnum chess) {
        this.point = point;
        this.chess = chess;
    }
}
