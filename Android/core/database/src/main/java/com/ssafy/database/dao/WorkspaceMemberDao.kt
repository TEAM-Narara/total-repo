package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ssafy.database.dto.WorkspaceMember
import com.ssafy.database.dto.with.WorkspaceMemberWithMemberInfo

@Dao
interface WorkspaceMemberDao {
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