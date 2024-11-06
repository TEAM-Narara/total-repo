package com.narara.superboard.listmember.service;

import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.listmember.entity.ListMember;
import com.narara.superboard.listmember.infrastructure.ListMemberRepository;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ListMemberServiceImplTest {

    @Mock
    private ListRepository listRepository;

    @Mock
    private ListMemberRepository listMemberRepository;

    @Mock
    private MemberRepository memberRepository;


    @InjectMocks
    private ListMemberServiceImpl listMemberService;
    private Member member;
    private List list;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Long listId = 1L;
        Long memberId = 1L;

        // Member 및 list 객체 초기화
        member = new Member(memberId, "username", "password");
        list = List.builder()
                .id(listId)
                .board(null)
                .build();
    }

    @Test
    @DisplayName("리스트와 멤버가 존재할 때 watch 상태를 반대로 변경")
    void testSetListMemberWatch_listExistsAndMemberExists() {
        Long listId = list.getId();
        Long memberId = member.getId();
        ListMember existingListMember = new ListMember(member, list, true);

        // 리스트와 멤버 유효성 확인
        when(listRepository.findById(listId)).thenReturn(Optional.of(list));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        when(listRepository.existsById(listId)).thenReturn(true);
        when(listMemberRepository.findByListIdAndMemberId(listId, memberId))
                .thenReturn(Optional.of(existingListMember));

        listMemberService.setListMemberIsAlert(memberId, listId);

        assertFalse(existingListMember.isAlert());
        verify(listRepository, times(1)).findById(listId);
        verify(listMemberRepository, times(1)).findByListIdAndMemberId(listId, memberId);
        verify(listMemberRepository, times(1)).save(existingListMember);
    }

    @Test
    @DisplayName("리스트가 존재하고 멤버가 없을 때 watch 상태를 true로 설정")
    void testSetListMemberWatch_ListExistsAndMemberDoesNotExist() {
        Long listId = list.getId();
        Long memberId = member.getId();

        // 리스트가 존재한다고 설정
        when(listRepository.existsById(listId)).thenReturn(true);
        when(listRepository.findById(listId)).thenReturn(Optional.of(list));

        // 멤버가 존재한다고 설정
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // listMember가 존재하지 않는 경우
        when(listMemberRepository.findByListIdAndMemberId(listId, memberId)).thenReturn(Optional.empty());

        // 메서드 호출
        listMemberService.setListMemberIsAlert(memberId, listId);

        // 메서드 호출 횟수 검증
        verify(listRepository, times(1)).findById(listId);
        verify(memberRepository, times(1)).findById(memberId);
        verify(listMemberRepository, times(1)).findByListIdAndMemberId(listId, memberId);
        verify(listMemberRepository, times(1)).save(any(ListMember.class));
    }
    @Test
    @DisplayName("리스트가 존재하지 않을 때 예외 발생")
    void testSetlistMemberWatch_listDoesNotExist() {
        Long listId = list.getId();
        Long memberId = member.getId();

        when(listRepository.existsById(listId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> listMemberService.setListMemberIsAlert(memberId, listId));
        verify(listRepository, times(1)).findById(listId);
        verify(listMemberRepository, never()).findByListIdAndMemberId(anyLong(), anyLong());
        verify(listMemberRepository, never()).save(any(ListMember.class));
    }
}