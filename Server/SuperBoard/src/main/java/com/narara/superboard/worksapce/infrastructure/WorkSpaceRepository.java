package com.narara.superboard.worksapce.infrastructure;

import com.narara.superboard.worksapce.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkSpaceRepository extends JpaRepository<WorkSpace, Long> {

}
