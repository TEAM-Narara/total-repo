package com.ssafy.model.search

enum class DueDate {
    NO_DUE_DATE {
        override fun toString() = "날짜 제한 없음"
    },
    OVERDUE {
        override fun toString() = "기한 만료"
    },
    DUE_IN_THE_NEXT_DAY {
        override fun toString() = "내일 내"
    },
    DUE_IN_THE_NEXT_WEEK {
        override fun toString() = "일주일 내"
    },
    DUE_IN_THE_NEXT_MONTH {
        override fun toString() = "한 달 내"
    };
}
