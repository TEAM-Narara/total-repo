package com.narara.superboard.workspace.infrastructure;

import com.narara.superboard.workspace.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkSpaceRepository extends JpaRepository<WorkSpace, Long> {
}
