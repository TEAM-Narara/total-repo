package com.narara.superboard.worksapce.interfaces;

import com.narara.superboard.worksapce.interfaces.dto.WorkspaceRequestCreateDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "워크스페이스")
@RequestMapping("/api/v1/workspaces")
public interface WorkSpaceAPI {

    @PostMapping()
    void createWorkSpace(WorkspaceRequestCreateDto workspaceRequestCreateDto);

}
