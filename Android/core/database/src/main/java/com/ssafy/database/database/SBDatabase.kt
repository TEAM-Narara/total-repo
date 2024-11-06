package com.ssafy.database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ssafy.database.dao.AlertDao
import com.ssafy.database.dao.AttachmentDao
import com.ssafy.database.dao.BoardDao
import com.ssafy.database.dao.BoardMemberDao
import com.ssafy.database.dao.CardDao
import com.ssafy.database.dao.CardLabelDao
import com.ssafy.database.dao.CardMemberDao
import com.ssafy.database.dao.LabelDao
import com.ssafy.database.dao.ListDao
import com.ssafy.database.dao.ListMemberDao
import com.ssafy.database.dao.MemberBackgroundDao
import com.ssafy.database.dao.MemberDao
import com.ssafy.database.dao.ReplyDao
import com.ssafy.database.dao.WorkspaceDao
import com.ssafy.database.dao.WorkspaceMemberDao
import com.ssafy.database.dto.AlertEntity
import com.ssafy.database.dto.AttachmentEntity
import com.ssafy.database.dto.BoardEntity
import com.ssafy.database.dto.BoardMemberEntity
import com.ssafy.database.dto.BoardMemberAlarmEntity
import com.ssafy.database.dto.CardEntity
import com.ssafy.database.dto.CardLabelEntity
import com.ssafy.database.dto.CardMemberEntity
import com.ssafy.database.dto.CardMemberAlarmEntity
import com.ssafy.database.dto.LabelEntity
import com.ssafy.database.dto.ListEntity
import com.ssafy.database.dto.MemberEntity
import com.ssafy.database.dto.MemberBackgroundEntity
import com.ssafy.database.dto.ReplyEntity
import com.ssafy.database.dto.ListMemberEntity
import com.ssafy.database.dto.ListMemberAlarmEntity
import com.ssafy.database.dto.WorkspaceEntity
import com.ssafy.database.dto.WorkspaceMemberEntity

@Database(entities = [
    WorkspaceEntity::class, WorkspaceMemberEntity::class,
    MemberEntity::class, MemberBackgroundEntity::class,
    ListEntity::class, ListMemberEntity::class, ListMemberAlarmEntity::class,
    BoardEntity::class, BoardMemberEntity::class, BoardMemberAlarmEntity::class, LabelEntity::class,
    CardEntity::class, CardLabelEntity::class, CardMemberEntity::class, CardMemberAlarmEntity::class, AttachmentEntity::class,
    ReplyEntity::class, AlertEntity::class], version = 1)
abstract class SBDatabase : RoomDatabase() {
    abstract fun AlertDao(): AlertDao
    abstract fun AttachmentDao(): AttachmentDao
    abstract fun BoardDao(): BoardDao
    abstract fun BoardMemberDao(): BoardMemberDao
    abstract fun CardDao(): CardDao
    abstract fun CardLabelDao(): CardLabelDao
    abstract fun CardMemberDao(): CardMemberDao
    abstract fun LabelDao(): LabelDao
    abstract fun ListDao(): ListDao
    abstract fun ListMemberDao(): ListMemberDao
    abstract fun MemberBackgroundDao(): MemberBackgroundDao
    abstract fun MemberDao(): MemberDao
    abstract fun ReplyDao(): ReplyDao
    abstract fun WorkspaceDao(): WorkspaceDao
    abstract fun WorkspaceMemberDao(): WorkspaceMemberDao
}