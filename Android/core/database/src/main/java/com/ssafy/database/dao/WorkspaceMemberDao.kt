package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ssafy.database.dto.WorkspaceMember
import com.ssafy.database.dto.with.WorkspaceMemberWithMemberInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceMemberDao {

    // 서버에 연산할 워크스페이스 멤버 조회
    @Query("""SELECT * 
        FROM workspace_member
        WHERE isStatus != 'STAY'
    """)
    suspend fun getAllRemoteWorkspaceMember(): List<WorkspaceMember>

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

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspaceMembers(workspaceMembers: List<WorkspaceMember>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM workspace_member WHERE id NOT IN (:ids)")
    suspend fun deleteWorkspaceMembersNotIn(ids: List<Long>)
}