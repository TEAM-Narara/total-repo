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
import com.ssafy.database.dao.MemberBackgroundDao
import com.ssafy.database.dao.MemberDao
import com.ssafy.database.dao.ReplyDao
import com.ssafy.database.dao.WorkspaceDao
import com.ssafy.database.dao.WorkspaceMemberDao
import com.ssafy.database.dto.Alert
import com.ssafy.database.dto.Attachment
import com.ssafy.database.dto.Board
import com.ssafy.database.dto.BoardMember
import com.ssafy.database.dto.Card
import com.ssafy.database.dto.CardLabel
import com.ssafy.database.dto.CardMember
import com.ssafy.database.dto.Label
import com.ssafy.database.dto.List
import com.ssafy.database.dto.Member
import com.ssafy.database.dto.MemberBackground
import com.ssafy.database.dto.Reply
import com.ssafy.database.dto.Workspace
import com.ssafy.database.dto.WorkspaceMember

@Database(entities = [Alert::class, Attachment::class, Board::class, BoardMember::class, Card::class,
    CardLabel::class, CardMember::class, Label::class, List::class, Member::class,
    MemberBackground::class, Reply::class, Workspace::class, WorkspaceMember::class], version = 1)
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
    abstract fun MemberBackgroundDao(): MemberBackgroundDao
    abstract fun MemberDao(): MemberDao
    abstract fun ReplyDao(): ReplyDao
    abstract fun WorkspaceDao(): WorkspaceDao
    abstract fun WorkspaceMemberDao(): WorkspaceMemberDao
}