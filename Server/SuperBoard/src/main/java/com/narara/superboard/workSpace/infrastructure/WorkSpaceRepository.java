package com.narara.superboard.workSpace.infrastructure;

import com.narara.superboard.workSpace.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkSpaceRepository extends JpaRepository<WorkSpace, Long> {

}
