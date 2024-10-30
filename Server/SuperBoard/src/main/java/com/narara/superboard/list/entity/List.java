package com.narara.superboard.list.entity;

import com.narara.superboard.board.entity.Board;
import com.narara.superboard.card.entity.Card;
import com.narara.superboard.list.interfaces.dto.ListCreateRequestDto;
import com.narara.superboard.list.interfaces.dto.ListUpdateRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "list")
public class List {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본키

    @JoinColumn(name = "board_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;  // 보드 키

    @OneToMany(mappedBy = "list")
    private java.util.List<Card> cardCollection;

    @Column(name = "name", nullable = false)
    private String name;  // 이름

    @Column(name = "my_order", nullable = false)
    private Long myOrder;  // 보드 내 순서

    @Column(name = "last_card_order", nullable = false, columnDefinition = "bigint default 0")
    private Long lastCardOrder;  // 리스트 내 마지막 카드 순서

    @Column(name = "is_archived", nullable = false, columnDefinition = "boolean default false")
    private Boolean isArchived;  // 아카이브 여부 (기본값: false)

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private Boolean isDeleted;  // 삭제 여부 (기본값: false)

    @Column(name = "card_order_version", nullable = false, columnDefinition = "bigint default 0")
    private Long cardOrderVersion;  // 버전

    public static List createList(ListCreateRequestDto listCreateRequestDto, Board board) {
        return List.builder()
                .name(listCreateRequestDto.listName())
                .board(board)
                .myOrder(board.getLastListOrder() +1)
                .lastCardOrder(0L)
                .isArchived(false)
                .isDeleted(false)
                .cardOrderVersion(0L)
                .build();
    }

    public void updateList(ListUpdateRequestDto listUpdateRequestDto) {
        this.name = listUpdateRequestDto.listName();
    }

    public void increaseCardOrderVersion() {
        this.cardOrderVersion += 1;
    }

    public void changeListIsArchived() {
        this.isArchived = !isArchived;
    }
}
