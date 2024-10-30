package com.narara.superboard.workspace.interfaces.dto.websocket;

import com.narara.superboard.common.constant.enums.Authority;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.websocket.interfaces.dto.websocket.BoardAddMemberDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class WorkSpaceAddMemberDto {
    private Long workspaceId;
    private Long workspaceOffset;
    private Authority authority;
    private Member member;
    private List<BoardAddMemberDto> boardAddMemberDtoList;
}
