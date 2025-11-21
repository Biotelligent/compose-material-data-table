package io.github.aleksandar_stefanovic.composematerialdatatable

public actual fun String.format(int: Int): String = String.format(this, int)

public actual fun String.format(float: Float): String = String.format(this, float)

public actual fun String.format(double: Double): String = String.format(this, double)
