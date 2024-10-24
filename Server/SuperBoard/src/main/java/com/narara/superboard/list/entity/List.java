package com.narara.superboard.list.entity;

import com.narara.superboard.board.entity.Board;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "list")
public class List {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본키

    @JoinColumn(name = "board_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;  // 보드 키

    @Column(name = "name", nullable = false)
    private String name;  // 이름

    @Column(name = "order", nullable = false, columnDefinition = "bigint default 0")
    private Long order;  // 보드 내 순서

    @Column(name = "last_card_order", nullable = false, columnDefinition = "bigint default 0")
    private Long lastCardOrder;  // 리스트 내 마지막 카드 순서

    @Column(name = "is_archived", nullable = false, columnDefinition = "boolean default false")
    private Boolean isArchived = false;  // 아카이브 여부 (기본값: false)

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private Boolean isDeleted = false;  // 삭제 여부 (기본값: false)

    @Column(name = "version", nullable = false, columnDefinition = "bigint default 0")
    private Long version;  // 버전

}
