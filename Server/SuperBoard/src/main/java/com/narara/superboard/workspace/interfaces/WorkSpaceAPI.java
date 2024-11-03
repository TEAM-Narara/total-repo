package com.narara.superboard.workspace.interfaces;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "워크스페이스")
@RequestMapping("/api/v1/workspaces")
public interface WorkSpaceAPI {

//    @PostMapping()
//    ResponseEntity createWorkSpace(Member member, WorkSpaceCreateRequestDto workspaceCreateRequestDto);

}
