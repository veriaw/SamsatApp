package com.veriaw.kriptografiapp.algoritma

import kotlin.random.Random

class RsaAlgorithm {
    var publicKey: Int = 0
    var privateKey: Int = 0
    var n: Int = 0

    fun isPrime(number: Int): Boolean {
        if (number <= 1) return false
        for (i in 2..Math.sqrt(number.toDouble()).toInt()) {
            if (number % i == 0) return false
        }
        return true
    }

    fun getRandomPrime(min: Int, max: Int, exclude: List<Int>): Int {
        val primes = mutableListOf<Int>()

        // Cari semua bilangan prima dalam rentang yang diberikan
        for (num in min..max) {
            if (isPrime(num)) {
                primes.add(num)
            }
        }

        // Pilih bilangan prima secara acak dari daftar
        return if (primes.isNotEmpty()) {
            primes[Random.nextInt(primes.size)] // Pilih secara acak
        } else {
            throw Exception("No primes found in this range.")
        }
    }

    fun gcd(a: Int, b: Int): Int {
        return if (b == 0) a else gcd(b, a % b)
    }

    fun setKeys(){
        val usedPrimes = mutableListOf<Int>()
        val prime1 = getRandomPrime(10,100, usedPrimes)
        usedPrimes.add(prime1)
        val prime2 = getRandomPrime(10,100, usedPrimes)
        n = prime1 * prime2
        val fi = (prime1 - 1) * (prime2 - 1)

        var e = 2
        while (gcd(e, fi) != 1) {
            e++
        }
        publicKey = e

        var d = 2
        while ((d * e) % fi != 1) {
            d++
        }
        privateKey = d

        // Anda bisa mencetak hasilnya atau mengembalikan nilai
        println("Public Key: $publicKey, Private Key: $privateKey, n: $n")
    }

    fun encrypt(message: Double): Long {
        var e = publicKey
        var encryptedText: Long = 1
        while (e-- > 0) {
            encryptedText *= message.toLong()
            encryptedText %= n.toLong()
        }
        return encryptedText
    }

    fun decrypt(encryptedText: Int): Long {
        var d = privateKey
        var decrypted: Long = 1
        while (d-- > 0) {
            decrypted *= encryptedText.toLong()
            decrypted %= n.toLong()
        }
        return decrypted
    }

    fun encoder(message: String): String {
        val encryptedMessage = StringBuilder()
        for (letter in message) {
            val encryptedChar = encrypt(letter.toDouble()).toInt()
            encryptedMessage.append(encryptedChar.toChar())  // Convert the encrypted number back to a char
        }
        return encryptedMessage.toString()
    }

    // Decoder now decodes the encrypted message back to the original string
    fun decoder(encoded: String): String {
        val decodedMessage = StringBuilder()
        for (char in encoded) {
            val decryptedChar = decrypt(char.toInt()).toChar()  // Convert the char back to its decrypted value
            decodedMessage.append(decryptedChar)
        }
        return decodedMessage.toString()
    }

}