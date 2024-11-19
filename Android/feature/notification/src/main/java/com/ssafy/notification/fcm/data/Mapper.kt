package com.ssafy.notification.fcm.data

import com.ssafy.model.fcm.FcmDestinationResponse
import com.ssafy.model.fcm.FcmMessageResponse
import com.ssafy.notification.fcm.FcmDestination

fun FcmMessageResponse.toFcmMessage(): FcmMessage {
    return when (goTo) {
        FcmDestinationResponse.HOME -> HomeMessage(
            title = title,
            time = time,
            type = type,
            goTo = goTo.toFcmDestination(),
            manOfActionId = manOfActionId,
            manOfActionUrl = manOfActionUrl
        )

        FcmDestinationResponse.WORKSPACE -> WorkspaceMessage(
            title = title,
            time = time,
            type = type,
            goTo = goTo.toFcmDestination(),
            manOfActionId = manOfActionId,
            manOfActionUrl = manOfActionUrl,
            workspaceId = workspaceId
        )

        FcmDestinationResponse.BOARD -> BoardMessage(
            title = title,
            time = time,
            type = type,
            goTo = goTo.toFcmDestination(),
            manOfActionId = manOfActionId,
            manOfActionUrl = manOfActionUrl,
            workspaceId = workspaceId,
            boardId = boardId,
        )

        FcmDestinationResponse.CARD -> CardMessage(
            title = title,
            time = time,
            type = type,
            goTo = goTo.toFcmDestination(),
            manOfActionId = manOfActionId,
            manOfActionUrl = manOfActionUrl,
            workspaceId = workspaceId,
            boardId = boardId,
            listId = listId,
            cardId = cardId,
        )
    }
}

private fun FcmDestinationResponse.toFcmDestination(): FcmDestination {
    return when (this) {
        FcmDestinationResponse.HOME -> FcmDestination.HOME
        FcmDestinationResponse.WORKSPACE -> FcmDestination.WORKSPACE
        FcmDestinationResponse.BOARD -> FcmDestination.BOARD
        FcmDestinationResponse.CARD -> FcmDestination.CARD
    }
}
