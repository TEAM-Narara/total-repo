package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ssafy.database.dto.Workspace
import com.ssafy.database.dto.with.WorkspaceDetail
import com.ssafy.database.dto.with.WorkspaceMemberWithMemberInfo

@Dao
interface WorkspaceDao {
    // 워크스페이스 상세 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM workspace 
        WHERE id == :workspaceId
    """)
    suspend fun getWorkspaceDetail(workspaceId: Long): WorkspaceDetail

    // Drawable에서 볼 것
    @Query("""
        SELECT * 
        FROM workspace 
        WHERE isStatus != 'DELETE'
    """)
    suspend fun getAllWorkspaces(): List<Workspace>

    // 로컬에서 생성
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspace(workspace: Workspace): Long

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspaces(workspaces: List<Workspace>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM workspace WHERE id NOT IN (:ids)")
    suspend fun deleteWorkspacesNotIn(ids: List<Long>)

    // 원격 삭제 (isStatus: 'STAY' -> isStatus: 'DELETE')
    @Update
    suspend fun updateWorkspace(workspace: Workspace)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Delete
    suspend fun deleteWorkspace(workspace: Workspace)
}