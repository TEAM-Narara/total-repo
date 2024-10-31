package com.narara.superboard.board.entity;

import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.board.interfaces.dto.BoardCreateRequestDto;
import com.narara.superboard.board.interfaces.dto.BoardUpdateByMemberRequestDto;
import com.narara.superboard.board.interfaces.dto.BoardUpdateRequestDto;
import com.narara.superboard.common.entity.BaseTimeEntity;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.workspace.entity.WorkSpace;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "board")
public class Board extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본키

    @Column(name = "name", nullable = false)
    private String name;  // 이름

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "cover", columnDefinition = "jsonb")
    private Map<String, Object> cover;  // 배경 (JSON)

    @Column(name = "visibility", nullable = false, length = 50)
    private Visibility visibility;  // 가시성 (WORKSPACE, PRIVATE)

    @Column(name = "last_list_order", nullable = false, columnDefinition = "bigint default 0")
    private Long lastListOrder;  // 보드 내 마지막 리스트 순서

    @Column(name = "is_archived", nullable = false, columnDefinition = "boolean default false")
    private Boolean isArchived;  // 버전

    @Column(name = "list_order_version", nullable = false, columnDefinition = "bigint default 0")
    private Long listOrderVersion;  // 버전

    @JoinColumn(name = "workspace_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private WorkSpace workSpace;  // 워크스페이스 키

    @OneToMany(mappedBy = "board")
    private java.util.List<List> listCollection;  // 보드 키

    private Long offset;

    public static Board createBoard(BoardCreateRequestDto boardCreateRequestDto, WorkSpace workSpace) {
        return Board.builder()
                .cover(boardCreateRequestDto.background())
                .name(boardCreateRequestDto.name())
                .visibility(Visibility.fromString(boardCreateRequestDto.visibility()))
                .workSpace(workSpace)
                .lastListOrder(0L)
                .isArchived(false)
                .listOrderVersion(0L)
                .build();
    }

    public Board updateBoardByAdmin(BoardUpdateRequestDto boardUpdateRequestDto) {
        this.cover = boardUpdateRequestDto.background();
        this.name = boardUpdateRequestDto.name();
        this.visibility = Visibility.valueOf(boardUpdateRequestDto.visibility());
        return this;
    }
    public Board updateBoardByMember(BoardUpdateByMemberRequestDto boardUpdateByMemberRequestDto) {
        this.cover = boardUpdateByMemberRequestDto.background();
        this.name = boardUpdateByMemberRequestDto.name();
        return this;

    }

    public void changeArchiveStatus() {
        this.isArchived = !isArchived;
    }

    public void increaseVersion() {
        this.listOrderVersion += 1;
    }

    public Board(Long id, String name, Map<String, Object> cover) {
        this.id = id;
        this.name = name;
        this.cover = cover;
    }

    public Board(Long id, String name){
        this.id = id;
        this.name = name;
    }


}
