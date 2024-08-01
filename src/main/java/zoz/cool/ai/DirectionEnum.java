package zoz.cool.ai;

import lombok.Getter;

@Getter
public enum DirectionEnum {
    HORIZONTAL(0, 1), VERTICAL(1, 0), DIAGONAL(1, 1), REVERSE_DIAGONAL(1, -1);

    private final Integer x;
    private final Integer y;

    DirectionEnum(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
