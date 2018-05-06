package com.perkelle.dev.bot.music

import com.perkelle.dev.bot.getBot
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack

typealias LoadedTracks = Pair<List<AudioTrack>, Boolean>

object TrackLoader {

    fun load(query: String, amount: Int = 5): LoadedTracks {
        val tracks = mutableListOf<AudioTrack>()
        var isYoutubePlaylist = false

        getBot().playerManager.loadItem(query, object: AudioLoadResultHandler {
            override fun loadFailed(exception: FriendlyException) {}

            override fun trackLoaded(track: AudioTrack) {
                tracks.add(track)
            }

            override fun noMatches() {}

            override fun playlistLoaded(playlist: AudioPlaylist) {
                if(playlist.isSearchResult) {
                    for((index, track) in playlist.tracks.withIndex()) {
                        if (index < amount) tracks.add(track)
                        else break
                    }
                }
                else {
                    isYoutubePlaylist = true
                    tracks.addAll(playlist.tracks)
                }
            }
        }).get()

        return tracks to isYoutubePlaylist
    }
}