package com.narara.superboard.memberbackground.service;

import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.exception.MemberNotFoundException;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.memberbackground.entity.MemberBackground;
import com.narara.superboard.memberbackground.infrastructure.MemberBackgroundRepository;
import com.narara.superboard.memberbackground.service.validator.MemberBackgroundValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class MemberBackgroundServiceImpl implements MemberBackgroundService {

    private final MemberBackgroundRepository memberBackgroundRepository;
    private final MemberBackgroundValidator memberBackgroundValidator;
    private final MemberRepository memberRepository;

    @Override
    public MemberBackground addMemberBackground(Long memberId, String imgUrl) {
        memberBackgroundValidator.validateImgUrl(imgUrl);
        Member member = findMemberByIdOrThrow(memberId);

        MemberBackground memberBackground = MemberBackground.builder()
                .member(member).imgUrl(imgUrl).build();

        return memberBackgroundRepository.save(memberBackground);
    }

    @Override
    public List<MemberBackground> getAllMemberBackground(Member member) {

        return memberBackgroundRepository.findAllByMember(member);
    }

    @Override
    public void deleteMemberBackground(Member member, Long backgroundId) {
        MemberBackground background = memberBackgroundRepository.findByIdAndMember(backgroundId, member)
                .orElseThrow(() -> new NotFoundEntityException(backgroundId, "멤버 배경"));
        memberBackgroundRepository.delete(background);
    }

    private Member findMemberByIdOrThrow(Long memberId) {
        return memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }
}
