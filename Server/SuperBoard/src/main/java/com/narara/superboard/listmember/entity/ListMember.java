package com.narara.superboard.listmember.entity;

import com.narara.superboard.list.entity.List;
import com.narara.superboard.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "list_member")
public class ListMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 기본 키

    @JoinColumn(name = "member", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;  // 멤버 ID

    @JoinColumn(name = "list", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @ManyToOne(fetch = FetchType.LAZY)
    private List list;  // 리스트 ID

    @Column(name = "is_alert", nullable = false, columnDefinition = "boolean default false")
    @Setter
    private boolean isAlert;

    public ListMember(Member member, List list, boolean isAlert) {
        this.member = member;
        this.list = list;
        this.isAlert = isAlert;
    }

    public void changeIsAlert() {
        this.isAlert = !isAlert;
    }

}
