package com.wooyassss.fileyassss.common.util

fun IllegalArgsEx(msg: String? = ""): Nothing {
    throw IllegalArgumentException(msg)
}