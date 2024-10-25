package com.narara.superboard.card.entity;

import com.narara.superboard.card.interfaces.dto.CardCreateRequestDto;
import com.narara.superboard.list.entity.List;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.util.Map;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @JoinColumn(name = "list_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private List list;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "start_at")
    private Long startAt; // Assuming this is a Unix timestamp or epoch time

    @Column(name = "end_at")
    private Long endAt; // Assuming this is a Unix timestamp or epoch time

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "cover", columnDefinition = "jsonb")
    private Map<String, Object> cover;

    @Column(name = "my_order", nullable = false, columnDefinition = "bigint default 0")
    private Long myOrder;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private Boolean isDeleted = false;

    @Column(name = "is_archived", nullable = false, columnDefinition = "boolean default false")
    private Boolean isArchived = false;

    public static Card createCard(CardCreateRequestDto cardCreateRequestDto, List list) {
        return Card.builder()
                .name(cardCreateRequestDto.cardName())
                .list(list)
                .myOrder(list.getLastCardOrder() + 1)
                .build();
    }
}

