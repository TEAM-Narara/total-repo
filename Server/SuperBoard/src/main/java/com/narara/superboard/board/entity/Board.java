package com.narara.superboard.board.entity;

import com.narara.superboard.board.enums.Visibility;
import com.narara.superboard.common.constant.enums.CoverType;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.workspace.entity.WorkSpace;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Entity
@Getter
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

    // JPA가 관리하지 않도록 transient로 선언
    @Transient
    private Cover cachedCover;

    // 엔티티가 처음 생성될 때, Cover 객체를 생성해 둡니다.
    @PostLoad
    @PostPersist
    private void initializeCover() {
        if (cover == null) {
            this.cachedCover = null;
        } else {
            this.cachedCover = new Cover(cover);
        }
    }

    // 이후 get 메소드에서 이 캐시된 Cover 객체를 재사용합니다.
    public Cover getCoverInfo() {
        if (cachedCover == null) {
            if (cover == null) {
                throw new NotFoundException("Board", "background");
            }
            this.cachedCover = new Cover(cover);
        }
        return cachedCover;
    }

    public CoverType getCoverType() {
        //        return "mysql -> postgresql ";
        return getCoverInfo().getType();
    }

    public String getCoverTypeValue() {
        //        return "mysql -> postgresql ";
        return getCoverInfo().getTypeValue();
    }

    public String getCoverValue() {
//        return "mysql -> postgresql ";
        return getCoverInfo().getValue();
    }

    public void increaseVersion() {
        this.version += 1;
    }
}
