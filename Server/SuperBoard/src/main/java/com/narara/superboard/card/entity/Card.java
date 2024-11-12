package com.narara.superboard.card.entity;

import com.narara.superboard.attachment.entity.Attachment;
import com.narara.superboard.card.interfaces.dto.CardCreateRequestDto;
import com.narara.superboard.card.interfaces.dto.CardUpdateRequestDto;
import com.narara.superboard.cardlabel.entity.CardLabel;
import com.narara.superboard.cardmember.entity.CardMember;
import com.narara.superboard.common.document.Identifiable;
import com.narara.superboard.common.entity.BaseTimeEntity;
import com.narara.superboard.common.interfaces.dto.CoverDto;
import com.narara.superboard.list.entity.List;
import jakarta.persistence.*;
import java.util.HashMap;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.util.Map;

import static com.narara.superboard.common.constant.MoveConst.LARGE_INCREMENT;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card")
public class Card extends BaseTimeEntity implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Setter(value = AccessLevel.PRIVATE)
    @JoinColumn(name = "list_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
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
    @Setter
    private Map<String, Object> cover;

    @Setter
    @Column(name = "my_order", nullable = false, columnDefinition = "bigint default 0")
    private Long myOrder;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private Boolean isDeleted;

    @Column(name = "is_archived", nullable = false, columnDefinition = "boolean default false")
    private Boolean isArchived;

    @OneToMany(mappedBy = "card")
    private java.util.List<CardLabel> cardLabelList;

    @OneToMany(mappedBy = "card")
    private java.util.List<Attachment> attachmentList;

    @OneToMany(mappedBy = "card")
    private java.util.List<CardMember> cardMemberList;

    public static Card createCard(CardCreateRequestDto cardCreateRequestDto, List list) {
        long cardListOrder = list.getLastCardOrder() + LARGE_INCREMENT;
        list.setLastCardOrder(cardListOrder);

        return Card.builder()
                .name(cardCreateRequestDto.cardName())
                .list(list)
                .cover(new HashMap<>(){{
                    put("type", "NONE");
                    put("value", "NONE");
                }}) //default cover 지정
                .myOrder(cardListOrder)
                .isDeleted(false)
                .isArchived(false)
                .build();
    }

    public Card updateCard(CardUpdateRequestDto requestDto) {
        updateName(requestDto.name());
        updateDescription(requestDto.description());
        updateCover(requestDto.cover());
        updateStartAt(requestDto.startAt());
        updateEndAt(requestDto.endAt());

        return this;
    }

    private void updateCover(CoverDto updateCover) {
        //null로 보내면 변경하지 않음
        if (updateCover == null) {
            return;
        }

        this.cover = new HashMap<>(){{
            put("type", updateCover.type());
            put("value", updateCover.value());
        }};
    }

    private void updateEndAt(Long updateEndAt) {
        //null로 보내면 변경하지 않음
        if (updateEndAt == null) {
            return;
        }

        //-1로 오면 null로 변경
        if (updateEndAt.equals(-1L)) {
            this.endAt = null;
            return;
        }

        this.endAt = updateEndAt;
    }

    private void updateStartAt(Long updateStartAt) {
        //null로 보내면 변경하지 않음
        if (updateStartAt == null) {
            return;
        }

        //-1로 오면 null로 변경
        if (updateStartAt.equals(-1L)) {
            this.endAt = null;
            return;
        }

        this.startAt = updateStartAt;
    }

    private void updateDescription(String updateDescription) {
        //description 이 null 이면 수정하지 말아주세요
        if (updateDescription == null) {
            return;
        }

        //description 이 blank 이면 없애버리기
        if (updateDescription.isEmpty() || updateDescription.isBlank()) {
            this.description = null;
            return;
        }
        
        this.description = updateDescription;
    }

    private void updateName(String updateName) {
        //null로 보내거나 빈 값이면 수정 x
        if (updateName == null || updateName.isBlank() || updateName.isEmpty()) {
            return;
        }

        this.name = updateName;
    }

    public void delete() {
        this.isDeleted = true;
    }

    public void changeArchiveStatus() {
        this.isArchived = !isArchived;
    }

    public void moveToListWithOrder(List targetList, long newOrder) {
        this.setMyOrder(newOrder);
        this.setList(targetList);
    }
}

