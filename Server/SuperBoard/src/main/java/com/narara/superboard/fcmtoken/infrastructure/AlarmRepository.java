package com.narara.superboard.fcmtoken.infrastructure;

import com.narara.superboard.fcmtoken.entity.Alarm;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AlarmRepository extends MongoRepository<Alarm, String> {
    // toMemberId로 조회하고 createdAt으로 정렬
    List<Alarm> findByToMemberId(String toMemberId, Sort sort);

    // 페이징 처리가 필요한 경우를 위한 메서드
    Page<Alarm> findByToMemberId(String toMemberId, Pageable pageable);
}
