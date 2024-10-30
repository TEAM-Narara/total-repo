package com.narara.superboard.alert;

import com.narara.superboard.common.constant.enums.Status;
import com.narara.superboard.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alert")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 아래는 혹시 몰라서 나둔 컬럼들!
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body")
    private String body;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

}