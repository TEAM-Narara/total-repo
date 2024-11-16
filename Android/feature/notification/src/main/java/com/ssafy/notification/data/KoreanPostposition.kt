package com.ssafy.notification.data

object KoreanPostposition {
    /**
     * 한글의 받침 유무를 판단하는 함수
     * @param word 검사할 단어
     * @return 마지막 글자가 받침을 가지고 있으면 true, 아니면 false
     */
    fun hasJongsung(word: String): Boolean {
        if (word.isEmpty()) return false

        val lastChar = word.last()
        if (!isKorean(lastChar)) return false

        // 한글 유니코드 분해
        // 가 = 0xAC00, 힣 = 0xD7A3
        // 초성 19개, 중성 21개, 종성 28개(없음 포함)
        val unicodeValue = lastChar.code - 0xAC00
        if (unicodeValue < 0) return false

        // 종성이 있는지 확인 (종성 index가 0이 아니면 받침이 있는 것)
        return unicodeValue % 28 != 0
    }

    /**
     * 문자가 한글인지 검사
     */
    private fun isKorean(char: Char): Boolean {
        return char in '가'..'힣'
    }

    /**
     * 은/는 조사 반환
     */
    fun eunNeun(word: String): String {
        return if (hasJongsung(word)) "은" else "는"
    }

    /**
     * 이/가 조사 반환
     */
    fun iGa(word: String): String {
        return if (hasJongsung(word)) "이" else "가"
    }

    /**
     * 을/를 조사 반환
     */
    fun eulReul(word: String): String {
        return if (hasJongsung(word)) "을" else "를"
    }

    /**
     * 과/와 조사 반환
     */
    fun gwaWa(word: String): String {
        return if (hasJongsung(word)) "과" else "와"
    }

    /**
     * 아/야 조사 반환
     */
    fun aYa(word: String): String {
        return if (hasJongsung(word)) "아" else "야"
    }

    /**
     * 이어/여 조사 반환
     */
    fun ieoYeo(word: String): String {
        return if (hasJongsung(word)) "이어" else "여"
    }

    /**
     * 으로/로 조사 반환
     */
    fun euroRo(word: String): String {
        return if (hasJongsung(word)) "으로" else "로"
    }

    /**
     * 단어에 조사를 붙여서 반환
     */
    fun addPostposition(word: String, type: PostpositionType): String {
        return "$word${getPostposition(word, type)}"
    }

    /**
     * 조사 유형에 따라 적절한 조사 반환
     */
    fun getPostposition(word: String, type: PostpositionType): String {
        return when (type) {
            PostpositionType.EUN_NEUN -> eunNeun(word)
            PostpositionType.I_GA -> iGa(word)
            PostpositionType.EUL_REUL -> eulReul(word)
            PostpositionType.GWA_WA -> gwaWa(word)
            PostpositionType.A_YA -> aYa(word)
            PostpositionType.IEO_YEO -> ieoYeo(word)
            PostpositionType.EURO_RO -> euroRo(word)
        }
    }
}

/**
 * 조사 유형을 정의하는 enum class
 */
enum class PostpositionType {
    EUN_NEUN,   // 은/는
    I_GA,       // 이/가
    EUL_REUL,   // 을/를
    GWA_WA,     // 과/와
    A_YA,       // 아/야
    IEO_YEO,    // 이어/여
    EURO_RO     // 으로/로
}

// String 확장 함수로 편리하게 사용할 수 있도록 구현
fun String.addPostposition(type: PostpositionType): String {
    return KoreanPostposition.addPostposition(this, type)
}

fun String.withEunNeun(): String = this.addPostposition(PostpositionType.EUN_NEUN)
fun String.withIGa(): String = this.addPostposition(PostpositionType.I_GA)
fun String.withEulReul(): String = this.addPostposition(PostpositionType.EUL_REUL)
fun String.withGwaWa(): String = this.addPostposition(PostpositionType.GWA_WA)
fun String.withAYa(): String = this.addPostposition(PostpositionType.A_YA)
fun String.withIeoYeo(): String = this.addPostposition(PostpositionType.IEO_YEO)
fun String.withEuroRo(): String = this.addPostposition(PostpositionType.EURO_RO)