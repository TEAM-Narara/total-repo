package com.narara.superboard.board.entity;

import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.board.interfaces.dto.BoardCreateRequestDto;
import com.narara.superboard.board.interfaces.dto.BoardUpdateRequestDto;
import com.narara.superboard.boardmember.entity.BoardMember;
import com.narara.superboard.common.document.Identifiable;
import com.narara.superboard.common.entity.BaseTimeEntity;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.workspace.entity.WorkSpace;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

import static com.narara.superboard.common.constant.MoveConst.DEFAULT_TOP_ORDER;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "board")
public class Board extends BaseTimeEntity implements Identifiable {
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

    @Setter
    @Column(name = "last_list_order", nullable = false, columnDefinition = "bigint default 4000000000000000000")
    @Builder.Default
    private Long lastListOrder = DEFAULT_TOP_ORDER; // 보드 내 마지막 리스트 순서

    @Column(name = "is_archived", nullable = false, columnDefinition = "boolean default false")
    private Boolean isArchived;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private Boolean isDeleted = false;

    // TODO: 사용하는지 체크
    @Column(name = "list_order_version", nullable = false, columnDefinition = "bigint default 0")
    private Long listOrderVersion;  // 버전

    @JoinColumn(name = "workspace_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private WorkSpace workSpace;  // 워크스페이스 키

    @OneToMany(mappedBy = "board")
    @Builder.Default
    private java.util.List<List> listCollection = new ArrayList<>();  // 보드 키

    @OneToMany(mappedBy = "board")
    @Builder.Default
    private java.util.List<BoardMember> boardMemberList = new ArrayList<>();

    @Builder.Default
    @Column(name = "\"offset\"")
    private Long offset = 0L;

    public static Board createBoard(BoardCreateRequestDto boardCreateRequestDto, WorkSpace workSpace) {
        Map<String, Object> coverMap;

        if (boardCreateRequestDto.cover() == null) {
            coverMap = Map.of(
                    "type", "NONE",
                    "value", "NONE"
            );
        } else {
            coverMap = Map.of(
                    "type", boardCreateRequestDto.cover().type(),
                    "value", boardCreateRequestDto.cover().value()
            );
        }

        return Board.builder()
                .cover(coverMap)
                .name(boardCreateRequestDto.name())
                .visibility(Visibility.fromString(boardCreateRequestDto.visibility()))
                .workSpace(workSpace)
                .lastListOrder(DEFAULT_TOP_ORDER)
                .isArchived(boardCreateRequestDto.isClosed())
                .listOrderVersion(0L)
                .build();
    }

    public Board updateBoardByAdmin(BoardUpdateRequestDto boardUpdateRequestDto) {
        if (boardUpdateRequestDto.cover() != null) {
            this.cover = new HashMap<>();
            this.cover.put("type", boardUpdateRequestDto.cover().type());
            this.cover.put("value", boardUpdateRequestDto.cover().value());
        }
        if (!(boardUpdateRequestDto.name() == null || boardUpdateRequestDto.name().isEmpty() || boardUpdateRequestDto.name().isBlank())) {
            this.name = boardUpdateRequestDto.name();
        }
        if (!(boardUpdateRequestDto.visibility() == null || boardUpdateRequestDto.visibility().isEmpty() || boardUpdateRequestDto.visibility().isBlank())) {
            this.visibility = Visibility.valueOf(boardUpdateRequestDto.visibility());
        }
        return this;
    }

    public Board updateBoardByMember(BoardUpdateRequestDto boardUpdateRequestDto) {
        if (boardUpdateRequestDto.cover() != null) {
            this.cover = new HashMap<>();
            this.cover.put("type", boardUpdateRequestDto.cover().type());
            this.cover.put("value", boardUpdateRequestDto.cover().value());
        }

        if (!(boardUpdateRequestDto.name() == null || boardUpdateRequestDto.name().isEmpty() || boardUpdateRequestDto.name().isBlank())) {
            this.name = boardUpdateRequestDto.name();
        }
        return this;
    }

    public void changeArchiveStatus() {
        this.isArchived = !isArchived;
    }

    public void increaseVersion() {
        this.listOrderVersion += 1;
    }

    public Board deleted() {
        this.isDeleted = true;
        return this;
    }

    public Board(Long id, String name, Map<String, Object> cover) {
        this.id = id;
        this.name = name;
        this.cover = cover;
    }

    public Board(Long id, String name) {
        this.id = id;
        this.name = name;
    }


}
