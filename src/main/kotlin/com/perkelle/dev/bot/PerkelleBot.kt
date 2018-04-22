package com.perkelle.dev.bot

import com.perkelle.dev.bot.command.ICommand
import com.perkelle.dev.bot.listeners.CommandListener
import com.perkelle.dev.bot.listeners.ReactListener
import com.perkelle.dev.bot.utils.getCommands
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.bot.sharding.ShardManager
import net.dv8tion.jda.core.entities.Game

fun main(args: Array<String>) {
    PerkelleBot().run()
}

fun getBot() = PerkelleBot.instance
fun getConfig() = getBot().config

class PerkelleBot: Runnable {

    companion object {
        lateinit var instance: PerkelleBot
    }

    init {
        instance = this
    }

    lateinit var config: BotConfig
    lateinit var shardManager: ShardManager

    override fun run() {
        println("Loading configs...")
        try {
            config = BotConfig()
            config.load()
        } catch(ex: Exception) {
            println("FATAL: The bot config was not loaded. Does it exist?")
            ex.printStackTrace()
            System.exit(-1)
        }

        println("Starting bot...")
        try {
            val builder = DefaultShardManagerBuilder()
                    .setToken(config.getToken())
                    .setAudioEnabled(true)
                    .setAutoReconnect(true)
                    .setGame(Game.of(Game.GameType.DEFAULT, "p!help"))
                    .setShardsTotal(config.getTotalShards())
                    .setShards(config.getLowestShard(), config.getLowestShard() + config.getTotalShards() - 1)

            builder.addEventListeners(CommandListener(), ReactListener())

            shardManager = builder.build()

            getCommands("com.perkelle.dev.bot.command.impl").forEach(ICommand::register)
        } catch(ex: Exception) {
            println("FATAL: The bot did not load correctly. Is the token correct?")
            ex.printStackTrace()
            System.exit(-1)
        }

        println("Starting audio module...")
        try {
            val playerManager = DefaultAudioPlayerManager()

            //Register sources
            setOf<AudioSourceManager>(
                    lazy {
                        val source = YoutubeAudioSourceManager(true)
                        source.setPlaylistPageCount(6 )
                        source
                    }.value,
                    SoundCloudAudioSourceManager(),
                    BandcampAudioSourceManager(),
                    VimeoAudioSourceManager(),
                    TwitchStreamAudioSourceManager(),
                    BeamAudioSourceManager(),
                    HttpAudioSourceManager()
            ).forEach(playerManager::registerSourceManager)

            AudioSourceManagers.registerLocalSource(playerManager)
            AudioSourceManagers.registerRemoteSources(playerManager)
        } catch(ex: Exception) {
            println("FATAL: The audio module could not be loaded. Are the natives installed?")
            ex.printStackTrace()
            System.exit(-1)
        }

        println("Bot is online!")
    }
}