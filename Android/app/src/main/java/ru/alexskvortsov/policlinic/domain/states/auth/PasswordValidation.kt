package ru.alexskvortsov.policlinic.domain.states.auth

import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

class PasswordValidation @Inject constructor() {

    fun fullValidationPassword(password: String): Boolean {
        val buildPattern = buildValidator(forceLetterMeet = true, forceNumberMeet = true, minLength = 6)
        val matcher = initRegex(buildPattern, password)
        return matcher.matches()
    }

    fun validationNumberMeet(password: String): Boolean {
        val buildPattern = buildValidator(forceLetterMeet = false, forceNumberMeet = true, minLength = 6)
        val matcher = initRegex(buildPattern, password)
        return matcher.matches()
    }

    fun validationLetterMeet(password: String): Boolean {
        val buildPattern = buildValidator(forceLetterMeet = true, forceNumberMeet = false, minLength = 6)
        val matcher = initRegex(buildPattern, password)
        return matcher.matches()
    }

    fun validationMinSizePassword(password: String): Boolean {
        val buildPattern = buildValidator(forceLetterMeet = false, forceNumberMeet = false, minLength = 6)
        val matcher = initRegex(buildPattern, password)
        return matcher.matches()
    }

    private fun initRegex(buildPattern: String, password: String): Matcher {
        val pattern = Pattern.compile(buildPattern)
        return pattern.matcher(password)
    }

    private fun buildValidator(forceLetterMeet: Boolean,
                               forceNumberMeet: Boolean,
                               minLength: Int): String {
        val patternBuilder = StringBuilder("(")
        if (forceLetterMeet) {
            patternBuilder.append("(?=.*[a-zA-Z])")
        }
        if (forceNumberMeet) {
            patternBuilder.append("^(?=.*[0-9])")
        }
        patternBuilder.append("(?!.*[^a-zA-Z0-9])(?=\\S+$).{$minLength,}$)")

        return patternBuilder.toString()
    }
}