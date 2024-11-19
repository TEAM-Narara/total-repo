package com.narara.superboard.common.exception.cardlabel;

public class MismatchedBoardException extends IllegalArgumentException {
    public MismatchedBoardException() {
        super("라벨과 카드가 서로 다른 보드에 속해 있습니다. 라벨을 확인해주세요.");
    }
}
