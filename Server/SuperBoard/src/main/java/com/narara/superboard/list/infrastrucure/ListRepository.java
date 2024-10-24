package com.narara.superboard.list.infrastrucure;

import com.narara.superboard.list.entity.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListRepository extends JpaRepository<List, Long> {
}
