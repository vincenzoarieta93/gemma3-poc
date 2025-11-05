package it.spindox.designsystem.utils

fun Float.avoidZero(): Float =
    if (this == 0f) Float.MIN_VALUE else this

fun Float.avoidNegative(): Float =
    if (this < 0f) Float.MIN_VALUE else this

fun Float.stayPositive(): Float =
    if (this <= 0f) Float.MIN_VALUE else this