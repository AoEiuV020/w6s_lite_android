package com.foreveross.atwork.infrastructure.utils.encryption

import java.math.BigInteger
import java.security.*
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher


/**
 *  create by reyzhang22 at 2019-10-16
 */
const val RSA = "RSA"
class RsaUtil

    /**
     * 随机生成RSA密钥对(默认密钥长度为1024)
     *
     * @return
     */
    fun generateRSAKeyPair(): KeyPair? {
        return generateRSAKeyPair(1024)
    }

    /**
     * 随机生成RSA密钥对
     *
     * @param keyLength
     * 密钥长度，范围：512～2048<br></br>
     * 一般1024
     * @return
     */
    fun generateRSAKeyPair(keyLength: Int): KeyPair? {
        try {
            val kpg = KeyPairGenerator.getInstance(RSA)
            kpg.initialize(keyLength)
            return kpg.genKeyPair()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 用公钥加密 <br></br>
     * 每次加密的字节数，不能超过密钥的长度值减去11
     *
     * @param data
     * 需加密数据的byte数据
     * @param pubKey
     * 公钥
     * @return 加密后的byte型数据
     */
    fun encryptData(data: ByteArray, publicKey: PublicKey): ByteArray? {
        try {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            // 编码前设定编码方式及密钥
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            // 传入编码数据并返回编码结果
            return cipher.doFinal(data)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 用私钥解密
     *
     * @param encryptedData
     * 经过encryptedData()加密返回的byte数据
     * @param privateKey
     * 私钥
     * @return
     */
    fun decryptData(encryptedData: ByteArray, privateKey: PrivateKey): ByteArray? {
        try {
            val cipher = Cipher.getInstance(RSA)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            return cipher.doFinal(encryptedData)
        } catch (e: Exception) {
            return null
        }

    }

    /**
     * 通过公钥byte[](publicKey.getEncoded())将公钥还原，适用于RSA算法
     *
     * @param keyBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPublicKey(keyBytes: ByteArray): PublicKey {
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(RSA)
        return keyFactory.generatePublic(keySpec)
    }

    /**
     * 通过私钥byte[]将公钥还原，适用于RSA算法
     *
     * @param keyBytes
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPrivateKey(keyBytes: ByteArray): PrivateKey {
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(RSA)
        return keyFactory.generatePrivate(keySpec)
    }

    /**
     * 使用N、e值还原公钥
     *
     * @param modulus
     * @param publicExponent
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPublicKey(modulus: String, publicExponent: String): PublicKey {
        val bigIntModulus = BigInteger(modulus)
        val bigIntPrivateExponent = BigInteger(publicExponent)
        val keySpec = RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent)
        val keyFactory = KeyFactory.getInstance(RSA)
        return keyFactory.generatePublic(keySpec)
    }

    /**
     * 使用N、d值还原私钥
     *
     * @param modulus
     * @param privateExponent
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun getPrivateKey(modulus: String, privateExponent: String): PrivateKey {
        val bigIntModulus = BigInteger(modulus)
        val bigIntPrivateExponent = BigInteger(privateExponent)
        val keySpec = RSAPublicKeySpec(bigIntModulus, bigIntPrivateExponent)
        val keyFactory = KeyFactory.getInstance(RSA)
        return keyFactory.generatePrivate(keySpec)
    }


