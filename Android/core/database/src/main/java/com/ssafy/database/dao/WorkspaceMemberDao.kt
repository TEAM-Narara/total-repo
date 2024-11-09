package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ssafy.database.dto.BoardMemberEntity
import com.ssafy.database.dto.MemberEntity
import com.ssafy.database.dto.WorkspaceEntity
import com.ssafy.database.dto.WorkspaceMemberEntity
import com.ssafy.database.dto.with.WorkspaceMemberWithMemberInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceMemberDao {

    // 서버에 연산할 워크스페이스 멤버 조회
    @Query("""SELECT * 
        FROM workspace_member
        WHERE isStatus != 'STAY'
    """)
    suspend fun getLocalOperationWorkspaceMember(): List<WorkspaceMemberEntity>

    // 보드 멤버 단일 id 조회
    @Query("SELECT * FROM workspace_member WHERE id == :id")
    fun getWorkspaceMember(id: Long): WorkspaceMemberEntity?

    // 워크스페이스 단일 조회
    @Query("SELECT * FROM workspace_member WHERE workspaceId = :workspaceId AND memberId = :memberId")
    fun getWorkspaceMember(workspaceId: Long, memberId: Long): WorkspaceMemberEntity?

    @Query("SELECT * FROM workspace_member WHERE workspaceId = :workspaceId AND memberId = :memberId")
    fun getWorkspaceMemberFlow(workspaceId: Long, memberId: Long): Flow<WorkspaceMemberEntity?>

    // 워크스페이스 멤버 조회
    @Transaction
    @Query("""
        SELECT 
            workspace_member.id AS workspace_member_id,
            workspace_member.memberId AS workspace_member_memberId,
            workspace_member.workspaceId AS workspace_member_workspaceId,
            workspace_member.authority AS workspace_member_authority,
            workspace_member.isStatus AS workspace_member_isStatus,
            member.id AS member_id,
            member.email AS member_email,
            member.nickname AS member_nickname,
            member.profileImageUrl AS member_profileImageUrl
        FROM workspace_member 
        INNER JOIN member ON member.id = workspace_member.memberId
        WHERE workspace_member.workspaceId = :workspaceId AND workspace_member.isStatus != 'DELETE'
    """)
    fun getWorkspaceMembers(workspaceId: Long): Flow<List<WorkspaceMemberWithMemberInfo>>

    @Transaction
    @Query("""
        SELECT workspace.* 
        FROM workspace 
        INNER JOIN workspace_member ON workspace.id = workspace_member.workspaceId 
        WHERE workspace_member.memberId = :memberId
    """)
    fun getWorkspacesByMember(memberId: Long): Flow<List<WorkspaceEntity>>

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspaceMembers(workspaceMembers: List<WorkspaceMemberEntity>): List<Long>

    // 단일 추가
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspaceMember(workspaceMember: WorkspaceMemberEntity): Long

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM workspace_member WHERE id NOT IN (:ids)")
    suspend fun deleteWorkspaceMembersNotIn(ids: List<Long>)

    // 상태 업데이트
    @Update
    suspend fun updateWorkspaceMember(workspaceMember: WorkspaceMemberEntity)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Query("""
        DELETE FROM workspace_member 
        WHERE memberId = :memberId AND workspaceId = :workspaceId;
    """)
    suspend fun deleteLocalWorkspaceMember(memberId: Long, workspaceId: Long)

    @Query("SELECT * FROM workspace_member WHERE workspaceId = :workspaceId and memberId = :memberId")
    fun getWorkspaceMemberByWorkspaceIdAndMemberId(workspaceId: Long, memberId: Long): WorkspaceMemberEntity?

    @Query("DELETE FROM workspace_member WHERE workspaceId = :workspaceId and memberId = :memberId")
    suspend fun deleteByWorkspaceId(workspaceId: Long, memberId: Long)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Delete
    suspend fun deleteWorkspaceMember(workspaceMember: WorkspaceMemberEntity)
}