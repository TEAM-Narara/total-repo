package com.narara.superboard.board.entity;

import com.narara.superboard.workspace.entity.WorkSpace;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본키

    @JoinColumn(name = "workspace_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private WorkSpace workSpace;  // 워크스페이스 키

    @Column(name = "name", nullable = false, length = 255)
    private String name;  // 이름

    // postgresql 에서 사용 가능.
//    @Type(JsonBinaryType.class)  // Hibernate Types 라이브러리를 통해 jsonb 타입으로 매핑
//    @Type(type = "jsonb")  // 정의한 jsonb 타입을 사용
//    @Column(name = "background", columnDefinition = "jsonb")
//    private Map<String, Object> background;  // 커버 (JSON)

    @Column(name = "last_list_order", nullable = false)
    private Long lastListOrder;  // 보드 내 마지막 리스트 순서

    @Column(name = "visibility", nullable = false, length = 50)
    private String visibility;  // 가시성 (WORKSPACE, PRIVATE)

    @Version
    @Column(name = "version", nullable = false)
    private Long version;  // 버전

    public String getBackGroundType() {
        return "postgresql";
//        return background != null ? background.get("type").toString() : null;
    }

    public String getBackGroundValue() {
        return "postgresql";
//        return background != null ? background.get("value").toString() : null;
    }
}
