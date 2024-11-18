package com.veriaw.kriptografiapp.algoritma

import android.graphics.Bitmap
import android.graphics.Color

class SteganografiAlgorithm {
    fun hideMessageInImage(image: Bitmap, message: String): Bitmap {
        val mutableImage = image.copy(Bitmap.Config.ARGB_8888, true)
        val messageBits = message.toByteArray(Charsets.UTF_8).flatMap { byte ->
            (7 downTo 0).map { (byte.toInt() shr it) and 1 }
        } + List(8) { 0 } // Penanda akhir pesan (byte 0)

        var bitIndex = 0
        for (y in 0 until mutableImage.height) {
            for (x in 0 until mutableImage.width) {
                if (bitIndex >= messageBits.size) return mutableImage

                val pixel = mutableImage.getPixel(x, y)
                val r = Color.red(pixel) and 0xFE or messageBits[bitIndex++]
                val g = Color.green(pixel) and 0xFE or messageBits[bitIndex++]
                val b = Color.blue(pixel) and 0xFE or messageBits[bitIndex++]

                mutableImage.setPixel(x, y, Color.rgb(r, g, b))
                if (bitIndex >= messageBits.size) break
            }
        }
        return mutableImage
    }

    fun extractMessageFromImage(image: Bitmap): String {
        val messageBits = mutableListOf<Int>()

        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                val pixel = image.getPixel(x, y)
                messageBits.add(Color.red(pixel) and 1)
                messageBits.add(Color.green(pixel) and 1)
                messageBits.add(Color.blue(pixel) and 1)
            }
        }

        val bytes = messageBits.chunked(8)
            .map { it.joinToString("").toInt(2).toByte() }
            .takeWhile { it != 0.toByte() }
            .toByteArray()

        return String(bytes, Charsets.UTF_8)
    }
}