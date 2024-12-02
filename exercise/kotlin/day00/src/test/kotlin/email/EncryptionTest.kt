package email

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import java.nio.file.Paths
import java.security.MessageDigest
import java.util.*
import kotlin.io.path.readText

class EncryptionTest : StringSpec({
    val encryption = Encryption(
        Configuration(
            key = convertKey("Advent Of Craft"),
            iv = convertIv("2024")
        )
    )

    "should encrypt a string" {
        encryption
            .encrypt("Unlock Your Potential with the Advent Of Craft Calendar!")
            .shouldBe("L7wht/YddOoTvYvrc+wFcZhtXNvZ2cHFxq9ND27h1Ovv/aWLxN8lWv1xMsguM/R4Yodk3rn9cppI+YarggtPjA==")
    }

    //It is a Property-Based test that checks the below property
    //I'm pretty sure we will talk about this concept during our Journey 🎅
    "for all x (x: valid string) -> decrypt(encrypt(x)) == x" {
        checkAll(Arb.string(1..100)) { plainText ->
            encryption.decrypt(
                encryption.encrypt(plainText)
            ) shouldBe plainText
        }
    }

    "should decrypt a string" {
        encryption
            .decrypt(loadFile("EncryptedEmail.txt"))
            .shouldBe("Dear consultant,\n" +
                    "\n" +
                    "We are facing an unprecedented challenge in Christmas Town.\n" +
                    "\n" +
                    "The systems that keep our magical operations running smoothly are outdated, fragile, and in dire need of modernization. \n" +
                    "We urgently require your expertise to ensure Christmas happens this year.\n" +
                    "Our town is located within a mountain circlet at the North Pole, surrounded by high peaks and protected by an advanced communication and shield system to hide it from the outside world.\n" +
                    "\n" +
                    "You have been selected for your exceptional skills and dedication. \n" +
                    "Please report to the North Pole immediately. \n" +
                    "\n" +
                    "Enclosed are your travel details and a non-disclosure agreement that you must sign upon arrival.\n" +
                    "Our dwarf friends from the security will receive and escort you in as soon as you check security.\n" +
                    "In the following days, you will receive bracelets to be able to pass through the magic shield.\n" +
                    "\n" +
                    "Time is of the essence.\n" +
                    "You must arrive before the beginning of December to be able to acclimate yourself with all the systems.\n" +
                    "\n" +
                    "We are counting on you to help save Christmas.\n" +
                    "\n" +
                    "Sincerely,\n" +
                    "\n" +
                    "Santa Claus \uD83C\uDF85")
    }
})

private fun convertKey(key: String): String {
    val sha256 = MessageDigest.getInstance("SHA-256")
    val keyBytes = sha256.digest(key.toByteArray(Charsets.UTF_8))
    return Base64.getEncoder().encodeToString(keyBytes)
}

private fun convertIv(iv: String): String {
    val md5 = MessageDigest.getInstance("MD5")
    val ivBytes = md5.digest(iv.toByteArray(Charsets.UTF_8))
    return Base64.getEncoder().encodeToString(ivBytes)
}

private fun loadFile(fileName: String): String =
    Paths.get(requireNotNull(EncryptionTest::class.java.classLoader.getResource(fileName)) {
        "File not found: $fileName"
    }.toURI()).readText(Charsets.UTF_8)