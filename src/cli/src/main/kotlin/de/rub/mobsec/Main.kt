package de.rub.mobsec

import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.main

fun main(args: Array<String>) = APC().context { exitProcess = { kotlin.system.exitProcess(it) } }.main(args)
