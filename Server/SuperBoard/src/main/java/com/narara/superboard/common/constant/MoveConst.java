package com.narara.superboard.common.constant;

public class MoveConst {
    public static final long DEFAULT_TOP_ORDER = 3_000_000_000_000_000_000L; // 초기 순서 값
    public static final double MOVE_TOP_ORDER_RATIO = 2.0 / 3.0;
    public static final double MOVE_BOTTOM_ORDER_RATIO = 1.0 / 3.0;
    public static final long LARGE_INCREMENT = 100_000_000_000_000_000L;

    public static final long MAX_INSERTION_DISTANCE_FOR_FIXED_GAP = 10_000_000_000_000_000L;
    public static final long HALF_DIVIDER = 2;

    public static final long REORDER_GAP = 10_000_000_000_000_000L;           // 각 카드 사이 간
}
