package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ssafy.database.dto.BoardMember
import com.ssafy.database.dto.BoardMemberAlarm
import com.ssafy.database.dto.WorkspaceMember
import com.ssafy.database.dto.with.WorkspaceMemberWithMemberInfo

@Dao
interface WorkspaceMemberDao {

    // 서버에 연산할 워크스페이스 멤버 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM workspace_member
        WHERE isStatus != 'STAY'
    """)
    suspend fun getAllRemoteWorkspaceMember(): List<WorkspaceMember>

    // 워크스페이스 멤버 조회
    @Query("""
        SELECT *
        FROM workspace_member 
        WHERE workspaceId = :workspaceId
    """)
    suspend fun getWorkspaceMembers(workspaceId: Long): List<WorkspaceMemberWithMemberInfo>

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspaceMembers(workspaceMembers: List<WorkspaceMember>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM workspace_member WHERE id NOT IN (:ids)")
    suspend fun deleteWorkspaceMembersNotIn(ids: List<Long>)
}