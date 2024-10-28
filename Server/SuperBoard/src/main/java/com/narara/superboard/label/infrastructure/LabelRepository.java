package com.narara.superboard.label.infrastructure;

import com.narara.superboard.label.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {
}
