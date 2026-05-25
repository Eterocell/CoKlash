package com.github.kr328.clash.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Main

@Serializable
data object Profiles

@Serializable
data class Properties(val uuid: String)

@Serializable
data class Files(val uuid: String)

@Serializable
data object Logs

@Serializable
data class Logcat(val fileName: String? = null)

@Serializable
data object Settings

@Serializable
data object AppSettings

@Serializable
data object NetworkSettings

@Serializable
data object AccessControl

@Serializable
data object OverrideSettings

@Serializable
data object MetaFeatureSettings

@Serializable
data object Proxy

@Serializable
data object Providers

@Serializable
data object NewProfile

@Serializable
data object Help
