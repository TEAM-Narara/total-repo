package com.narara.superboard.workspace.service;

import com.narara.superboard.board.interfaces.dto.BoardDetailResponseDto;
import com.narara.superboard.board.service.BoardService;
import com.narara.superboard.boardmember.interfaces.dto.MemberCollectionResponseDto;
import com.narara.superboard.common.application.kafka.KafkaConsumerService;
import com.narara.superboard.common.exception.NotFoundEntityException;
import com.narara.superboard.member.entity.Member;
import com.narara.superboard.member.exception.MemberNotFoundException;
import com.narara.superboard.member.infrastructure.MemberRepository;
import com.narara.superboard.workspace.exception.WorkspaceNameNotFoundException;
import com.narara.superboard.workspace.entity.WorkSpace;
import com.narara.superboard.workspace.infrastructure.WorkSpaceRepository;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceDetailResponseDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceCreateRequestDto;
import com.narara.superboard.workspace.interfaces.dto.WorkSpaceUpdateRequestDto;
import com.narara.superboard.workspace.service.mongo.WorkspaceOffsetService;
import com.narara.superboard.workspace.service.validator.WorkSpaceValidator;
import com.narara.superboard.workspacemember.entity.WorkSpaceMember;
import com.narara.superboard.workspacemember.infrastructure.WorkSpaceMemberRepository;
import com.narara.superboard.workspacemember.service.WorkSpaceMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class WorkSpaceServiceImpl implements WorkSpaceService {
    private final WorkSpaceValidator workSpaceValidator;
    private final MemberRepository memberRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final BoardService boardService;
    private final WorkSpaceMemberService workSpaceMemberService;
    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final WorkspaceOffsetService workspaceOffsetService;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaConsumerService kafkaConsumerService;
    private final KafkaAdmin kafkaAdmin;

    @Override
    @Transactional
    public WorkSpaceMember createWorkSpace(Long memberId, WorkSpaceCreateRequestDto workspaceCreateRequestDto) throws WorkspaceNameNotFoundException {
        workSpaceValidator.validateNameIsPresent(workspaceCreateRequestDto);

        Member member = memberRepository.findByIdAndIsDeletedFalse(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        WorkSpace workSpace = WorkSpace.createWorkSpace(workspaceCreateRequestDto);

        WorkSpace newWorkSpace = workSpaceRepository.save(workSpace);
        WorkSpaceMember workspaceMemberByAdmin = WorkSpaceMember.createWorkspaceMemberByAdmin(newWorkSpace, member); //offset++
        workSpaceMemberRepository.save(workspaceMemberByAdmin);

        // Kafka 토픽 생성 및 Consumer group Listener 설정

        String topicName = "workspace-" + newWorkSpace.getId();

        // Kafka: 워크스페이스용 토픽 생성
        // 토픽 이름 : workspace-1 ,파티션 수 :10개, 복제 개수 : 1개 (단일 브로커)
        // kafkaAdmin.createOrModifyTopics(new NewTopic(topicName, 10, (short) 1));

        try {
            kafkaAdmin.createOrModifyTopics(new NewTopic(topicName, 10, (short) 1));
            // 토픽 생성 확인 후 메시지 전송토픽 생성 확인 후 메시지 전송
            if (waitForTopicCreation(topicName)) {
                kafkaTemplate.send(topicName, "Workspace " + newWorkSpace.getId() + " created by member " + memberId);
            } else {
                System.out.println("토픽 생성에 실패했거나 시간 초과가 발생했습니다.");
            }
        } catch (Exception e) {
            System.err.println("Failed to send message to topic " + topicName + ": " + e.getMessage());
            // 필요한 경우 재시도 로직 추가
//            for (int retry = 1; retry <= 3; retry++) {
//                try {
//                    kafkaTemplate.send(topicName, "Workspace " + newWorkSpace.getId() + " created by member " + memberId);
//                    System.out.println("Message sent to topic on retry " + retry + ": " + topicName);
//                    break;
//                } catch (Exception retryException) {
//                    System.err.println("Retry " + retry + " failed for topic " + topicName + ": " + retryException.getMessage());
//                }
//            }
        }

        // 새로운 멤버를 Kafka Consumer Group에 등록
        kafkaConsumerService.registerMemberListener(newWorkSpace.getId(), memberId);

        return workspaceMemberByAdmin;
    }

    private boolean waitForTopicCreation(String topicName) {
        // Kafka Admin 설정을 가져옵니다.
        Map<String, Object> config = kafkaAdmin.getConfigurationProperties();
        try (AdminClient adminClient = AdminClient.create(config)) {
            int attempts = 0;
            while (attempts < 2) {
                attempts++;
                try {
                    // 주어진 토픽 이름으로 Kafka 토픽을 설명하는 요청을 보냅니다.
                    DescribeTopicsResult result = adminClient.describeTopics(Collections.singletonList(topicName));
                    // KafkaFuture 객체가 완료될 때까지 최대 2초 동안 기다리며, 이 시간이 초과되면 TimeoutException이 발생
                    Map<String, TopicDescription> descriptions = result.allTopicNames().get(2, TimeUnit.SECONDS);

                    // 토픽이 존재하면 준비가 완료된 것으로 간주하고 true를 반환합니다.
                    if (descriptions.containsKey(topicName)) {
                        System.out.println("토픽 " + topicName + "이(가) 준비되었습니다.");
                        return true;
                    }
                } catch (InterruptedException | ExecutionException | java.util.concurrent.TimeoutException e) {
                    System.out.println("토픽 생성 대기 중입니다. 시도 횟수: " + attempts);
                    Thread.sleep(1000);
                }
            }
        } catch (KafkaException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }




//    public void createTopicIfNotExists(String topicName) {
//        try {
//            Map<String, TopicDescription> topics = kafkaAdmin.describeTopics(new String[]{topicName});
//
//            // topicName이 Map에 포함되어 있는지 확인
//            if (!topics.containsKey(topicName)) {
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            // 예외 처리 추가 (필요 시 로깅 또는 오류 처리 로직 작성)
//        }
//    }


    @Override
    @Transactional
    public void deleteWorkSpace(Long workSpaceId) {
        WorkSpace workSpace = getWorkSpace(workSpaceId);
        workSpace.deleted(); //삭제 처리 offset++

//        workspaceOffsetService.saveDeleteWorkspaceDiff(workSpace);
    }

    @Override
    public WorkSpace getWorkSpace(Long workSpaceId) {
        return workSpaceRepository.findByIdAndIsDeletedFalse(workSpaceId)
                .orElseThrow(() -> new NotFoundEntityException(workSpaceId, "WorkSpace"));
    }

    @Override
    public WorkSpaceDetailResponseDto getWorkspaceDetail(Long workSpaceId) {
        WorkSpace workSpace = getWorkSpace(workSpaceId);

        List<BoardDetailResponseDto> boardCollectionResponseDto =
                boardService.getBoardCollectionResponseDto(workSpaceId);

        MemberCollectionResponseDto workspaceMemberCollectionResponseDto =
                workSpaceMemberService.getWorkspaceMemberCollectionResponseDto(workSpaceId);

        WorkSpaceDetailResponseDto workspaceDetailResponseDto = WorkSpaceDetailResponseDto.builder()
                .workSpaceId(workSpace.getId())
                .name(workSpace.getName())
                .boardList(boardCollectionResponseDto)
                .workspaceMemberList(workspaceMemberCollectionResponseDto)
                .build();

        workSpaceValidator.validateNameIsPresent(workspaceDetailResponseDto);

        return workspaceDetailResponseDto;
    }

    @Override
    public List<WorkSpace> getWorkspaceByMember(Long memberId) {
        List<WorkSpaceMember> allByMemberId = workSpaceMemberRepository.findAllByMemberId(memberId);
        List<WorkSpace> workspaceList = new ArrayList<>();
        for (WorkSpaceMember workSpaceMember : allByMemberId) {
            workspaceList.add(workSpaceMember.getWorkSpace());
        }

        return workspaceList;
    }

    @Transactional
    @Override
    public WorkSpace updateWorkSpace(Long workspaceId, String name) {
        workSpaceValidator.validateNameIsPresent(new WorkSpaceUpdateRequestDto(name));
        WorkSpace workSpace = getWorkSpace(workspaceId);
        workSpace.updateWorkSpace(name); //offset++

        workspaceOffsetService.saveEditWorkspaceDiff(workSpace);

        return workSpace;
    }
}
