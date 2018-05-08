package com.perkelle.dev.bot

class Constants {

    companion object {
        const val MESSAGE_DELETE_MILLIS = 60000L
        const val NO_PERMISSION = "You do not have permission to perform this command"
        val MENTION_REGEX = Regex("<@(\\d+)>")
        val BASS_BOOST = arrayOf(
                0.2f,
                0.15f,
                0.1f,
                0.05f,
                0.0f,
                -0.05f,
                -0.1f,
                -0.1f,
                -0.1f,
                -0.1f,
                -0.1f,
                -0.1f,
                -0.1f,
                -0.1f,
                -0.1f
        )
    }
}