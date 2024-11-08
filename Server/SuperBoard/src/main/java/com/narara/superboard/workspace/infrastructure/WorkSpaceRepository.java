package com.narara.superboard.workspace.infrastructure;

import com.narara.superboard.workspace.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkSpaceRepository extends JpaRepository<WorkSpace, Long> {
    Optional<WorkSpace> findByIdAndIsDeletedFalse(Long workSpaceId);
}
