package com.perkelle.dev.bot.utils

import com.google.common.reflect.ClassPath
import com.perkelle.dev.bot.command.ICommand
import java.lang.reflect.Modifier

fun getCommands(packageName: String) =
        ClassPath.from(Thread.currentThread().contextClassLoader)
                .getTopLevelClassesRecursive(packageName)
                .map { it.load() }
                .filter { it.interfaces.contains(ICommand::class.java) }
                .filter { it.hasEmptyConstructor() }
                .filter { !Modifier.isAbstract(it.modifiers) }
                .map { it.newInstance() as ICommand }

fun Class<*>.hasEmptyConstructor(): Boolean {
    return try {
        this.getDeclaredConstructor()
        true
    } catch(_: NoSuchMethodException) {
        false
    }
}