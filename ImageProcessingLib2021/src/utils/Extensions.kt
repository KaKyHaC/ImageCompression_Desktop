package utils

fun Boolean?.isTrue() = this == true

fun <T> T?.notNull() = this != null

fun <T> T?.isNull() = this == null