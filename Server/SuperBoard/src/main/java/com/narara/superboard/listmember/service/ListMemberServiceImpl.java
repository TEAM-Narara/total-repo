package com.narara.superboard.listmember.service;

import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.list.entity.List;
import com.narara.superboard.list.infrastructure.ListRepository;
import com.narara.superboard.listmember.entity.ListMember;
import com.narara.superboard.listmember.infrastructure.ListMemberRepository;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListMemberServiceImpl implements ListMemberService {

    private final ListRepository listRepository;
    private final MemberRepository memberRepository;
    private final ListMemberRepository listMemberRepository;

    @Override
    public void setListMemberIsAlert(Member member, Long listId) {
        List list = validateListExists(listId);

        listMemberRepository.findByListIdAndMember(listId, member)
                .ifPresentOrElse(
                        this::toggleAlertAndSave,
                        () -> addNewListMember(member, list)
                );
    }

    @Override
    public Boolean getListMemberIsAlert(Member member, Long listId) {
        ListMember listMember = listMemberRepository.findByListIdAndMember(listId, member)
                .orElse(null);

        List list = listRepository.findById(listId)
                .orElseThrow(() -> new NotFoundEntityException(listId, "list"));

        //없으면 새로 추가
        if (listMember == null) {
            addNewListMember(member, list);
        }

        return listMember.isAlert();
    }

    // 리스트 존재 확인 및 조회
    private List validateListExists(Long listId) {
        return listRepository.findById(listId)
                .orElseThrow(() -> new NotFoundEntityException(listId, "리스트"));
    }

    // 멤버 존재 확인 및 조회
    private Member validateMemberExists(Long memberId) {
        return memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new NotFoundEntityException(memberId, "멤버"));
    }

    //  ListMember 객체의 isAlert 상태를 반대로 변경하고 저장
    private void toggleAlertAndSave(ListMember listMember) {
        listMember.changeIsAlert();
        listMemberRepository.save(listMember);
    }

    // 새로운 ListMember 추가 및 저장
    private void addNewListMember(Member member, List list) {
        ListMember newListMember = ListMember.builder()
                .member(member)
                .list(list)
                .isAlert(true)
                .build();
        listMemberRepository.save(newListMember);
    }
    
}
