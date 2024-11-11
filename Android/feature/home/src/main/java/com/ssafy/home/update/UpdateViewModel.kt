package com.ssafy.home.update

import com.ssafy.member.GetMemberUseCase
import com.ssafy.member.UpdateMemberUseCase
import com.ssafy.model.member.MemberUpdateRequestDto
import com.ssafy.model.user.User
import com.ssafy.ui.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val getMemberUseCase: GetMemberUseCase,
    private val updateMemberUseCase: UpdateMemberUseCase,
) : BaseViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    fun getUser() = withIO {
        getMemberUseCase().safeCollect { user ->
            user?.let { _user.emit(it) }
        }
    }

    fun changeProfileImage(imagePath: String) = withIO {
        val user = _user.value ?: return@withIO
        val newUser = user.copy(profileImgUrl = imagePath)
        _user.emit(newUser)
    }

    fun change(nickname: String) = withIO {
        val user = _user.value ?: return@withIO
        val newUser = user.copy(nickname = nickname)
        _user.emit(newUser)

        val memberDto = MemberUpdateRequestDto(
            profileImgUrl = user.profileImgUrl ?: "",
            nickname = nickname
        )

        withSocketState { isConnected ->
            updateMemberUseCase(memberDto, isConnected).safeCollect()
        }
    }
}
