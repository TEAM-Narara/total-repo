package com.narara.superboard.board.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Map;

@Entity
@Getter
@Table(name = "board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본키

    @Column(name = "workspace_id", nullable = false)
    private Long workspaceId;  // 워크스페이스 키

    @Column(name = "name", nullable = false, length = 255)
    private String name;  // 이름

    @Column(name = "background", columnDefinition = "jsonb")
    private Map<String, Object> background;  // 커버 (JSONB)

    @Column(name = "last_list_order", nullable = false)
    private Long lastListOrder;  // 보드 내 마지막 리스트 순서

    @Column(name = "visibility", nullable = false, length = 50)
    private String visibility;  // 가시성 (WORKSPACE, PRIVATE)

    @Column(name = "version", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long version;  // 버전

    public String getBackGroundType() {
        return background.get("type").toString();
    }
    public String getBackGroundValue() {
        return background.get("value").toString();
    }
}
