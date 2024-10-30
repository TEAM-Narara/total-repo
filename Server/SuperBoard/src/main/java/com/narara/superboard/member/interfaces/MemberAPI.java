package com.narara.superboard.member.interfaces;

import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.member.interfaces.dto.MemberCreateRequestDto;
import com.narara.superboard.member.interfaces.dto.MemberLoginRequestDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "멤버")
@RequestMapping("/api/v1/member")
public interface MemberAPI {

}
