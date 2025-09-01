package io.github.aleksandar_stefanovic.composematerialdatatable

public actual fun String.format(int: Int): String = int.toString()

public actual fun String.format(float: Float): String = float.toString()

public actual fun String.format(double: Double): String = double.toString()
