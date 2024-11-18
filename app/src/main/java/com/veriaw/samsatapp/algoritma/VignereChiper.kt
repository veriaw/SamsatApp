package com.veriaw.kriptografiapp.algoritma

class VignereChiper {

    fun generateRandomKey(length: Int): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }

    fun encryption(text: String, key: String): String {
        var encryptedText = StringBuilder()
        var keyIndex = 0

        // Perulangan untuk setiap huruf yang akan di enkripsi
        for (i in text.indices) {
            val c = text[i]

            // Jika karakter berupa alphabet maka akan di lakukan enkripsi
            if (c.isLetter()) {
                // Sesuaikan huruf besar dan kecil
                val offset = if (c.isUpperCase()) 'A' else 'a'

                // Geser huruf berdasarkan kunci
                val encryptedChar = ((c - offset + (key[keyIndex].toUpperCase() - 'A')) % 26 + offset.toInt()).toChar()
                encryptedText.append(encryptedChar)

                // Melanjutkan untuk index kunci yang lain
                keyIndex = (keyIndex + 1) % key.length
            } else {
                // Jika karakter bukan alphabet, tambahkan tanpa perubahan
                encryptedText.append(c)
            }
        }
        return encryptedText.toString()
    }

    fun decryption(encryptedText: String, key: String): String {
        val decryptedText = StringBuilder()
        var keyIndex = 0

        // Perulangan untuk setiap huruf yang akan di Deskripsi
        for (i in encryptedText.indices) {
            val c = encryptedText[i]

            // Jika karakter berupa alphabet maka akan di lakukan dekripsi
            if (c.isLetter()) {
                // Sesuaikan huruf besar dan kecil
                val offset = if (c.isUpperCase()) 'A' else 'a'

                // Geser huruf berdasarkan kunci
                val decryptedChar = ((c - offset - (key[keyIndex].toUpperCase() - 'A') + 26) % 26 + offset.toInt()).toChar()
                decryptedText.append(decryptedChar)

                // Lanjutkan ke karakter berikutnya di kunci
                keyIndex = (keyIndex + 1) % key.length
            } else {
                // Tambahkan karakter selain alfabet tanpa perubahan
                decryptedText.append(c)
            }
        }

        return decryptedText.toString()
    }

}