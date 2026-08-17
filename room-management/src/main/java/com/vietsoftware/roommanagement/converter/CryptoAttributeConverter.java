//package com.vietsoftware.roommanagement.converter;
//
//import jakarta.persistence.AttributeConverter;
//import jakarta.persistence.Converter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.util.StringUtils;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//
///**
// * JPA AttributeConverter implementing deterministic AES-256 column encryption.
// *
// * <p>Encrypts sensitive entity attribute strings (e.g. email) before persisting to the database,
// * and decrypts them automatically upon retrieval by Hibernate.</p>
// */
//@Slf4j
//@Converter
//public class CryptoAttributeConverter implements AttributeConverter<String, String> {
////
////    private static final String ALGORITHM = "AES";
////    // 256-bit secret key (32 bytes) for deterministic AES encryption
////    private static final byte[] AES_KEY = "RoomMgmtSecretKeyForCrypto2026!".getBytes(StandardCharsets.UTF_8);
////
////    private final SecretKeySpec keySpec = new SecretKeySpec(AES_KEY, ALGORITHM);
////
////    /**
////     * Encrypts the entity attribute value before saving to database.
////     *
////     * @param attribute plain text attribute value
////     * @return Base64-encoded encrypted cipher text, or {@code null} if attribute is null
////     */
////    @Override
////    public String convertToDatabaseColumn(String attribute) {
////        if (!StringUtils.hasText(attribute)) {
////            return attribute;
////        }
////        try {
////            Cipher cipher = Cipher.getInstance(ALGORITHM);
////            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
////            byte[] encryptedBytes = cipher.doFinal(attribute.getBytes(StandardCharsets.UTF_8));
////            return Base64.getEncoder().encodeToString(encryptedBytes);
////        } catch (Exception e) {
////            log.error("Error encrypting attribute value: {}", e.getMessage(), e);
////            throw new IllegalStateException("Failed to encrypt attribute", e);
////        }
////    }
////
////    /**
////     * Decrypts the database column value when loading entity from database.
////     *
////     * @param dbData Base64-encoded encrypted column data from database
////     * @return plain text decrypted string, or {@code null} if dbData is null
////     */
////    @Override
////    public String convertToEntityAttribute(String dbData) {
////        if (!StringUtils.hasText(dbData)) {
////            return dbData;
////        }
////        try {
////            Cipher cipher = Cipher.getInstance(ALGORITHM);
////            cipher.init(Cipher.DECRYPT_MODE, keySpec);
////            byte[] decodedBytes = Base64.getDecoder().decode(dbData);
////            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
////            return new String(decryptedBytes, StandardCharsets.UTF_8);
////        } catch (Exception e) {
////            log.error("Error decrypting database column value: {}", e.getMessage(), e);
////            throw new IllegalStateException("Failed to decrypt database column", e);
////        }
////    }
//}
