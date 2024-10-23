package com.narara.superboard.board.entity;

import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.common.constant.enums.CoverType;
import com.narara.superboard.common.exception.NotFoundException;
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
public class Board {
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

    @Column(name = "version", nullable = false, columnDefinition = "bigint default 0")
    private Long version;  // 버전

    @JoinColumn(name = "workspace_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private WorkSpace workSpace;  // 워크스페이스 키

    public void increaseVersion() {
        this.version += 1;
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
