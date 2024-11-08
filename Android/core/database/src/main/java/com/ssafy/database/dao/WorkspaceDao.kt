package com.ssafy.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ssafy.database.dto.WorkspaceEntity
import com.ssafy.database.dto.with.WorkspaceInBoard
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceDao {

    // 로컬에서 오프라인으로 생성한 워크스페이스 하위 조회
    @Transaction
    @Query("""
        SELECT * 
        FROM workspace 
        WHERE isStatus = 'CREATE'
    """)
    suspend fun getLocalCreateWorkspaces(): List<WorkspaceInBoard>

    // 서버에 연산할 워크스페이스 조회
    @Query("""
        SELECT * 
        FROM workspace
        WHERE isStatus = 'UPDATE' OR isStatus = 'DELETE'
    """)
    suspend fun getLocalOperationWorkspaces(): List<WorkspaceEntity>

    // 워크스페이스 단일 조회
    @Query("SELECT * FROM workspace WHERE id = :workspaceId")
    fun getWorkspace(workspaceId: Long): WorkspaceEntity?

    // 워크스페이스 조회 + Drawable에서 볼 것
    @Query("SELECT * FROM workspace WHERE id = :workspaceId")
    fun getWorkspaceForDrawable(workspaceId: Long): Flow<WorkspaceEntity?>

    // Drawable에서 볼 것
    @Query("SELECT * FROM workspace WHERE isStatus != 'DELETE'")
    fun getAllWorkspaces(): Flow<List<WorkspaceEntity>>

    // 로컬에서 생성
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspace(workspace: WorkspaceEntity): Long

    // 서버 변경사항 동기화
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspaces(workspaces: List<WorkspaceEntity>): List<Long>

    // 서버에 존재하지 않는 로컬 데이터 삭제
    @Query("DELETE FROM workspace WHERE id NOT IN (:ids)")
    suspend fun deleteWorkspacesNotIn(ids: List<Long>)

    // 상태 업데이트
    @Update
    suspend fun updateWorkspace(workspace: WorkspaceEntity)

    // 로컬 삭제(isStatus: CREATE -> 즉시 삭제)
    @Delete
    suspend fun deleteLocalWorkspace(workspace: WorkspaceEntity)

    @Query("DELETE FROM workspace WHERE id = :id")
    suspend fun deleteByWorkspaceId(id: Long)
}