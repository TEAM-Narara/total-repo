package com.narara.superboard.attachment.infrastructure;

import com.narara.superboard.attachment.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}
