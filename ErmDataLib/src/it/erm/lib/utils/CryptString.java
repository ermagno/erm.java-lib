package it.erm.lib.utils;

import java.security.spec.KeySpec;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;

public class CryptString {
    private static final String UNICODE_FORMAT = "UTF-8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private KeySpec ks;
    private SecretKeyFactory skf;
    private Cipher cipher;
    byte[] arrayBytes;
    private String myEncryptionKey;
    private String myEncryptionScheme;
    SecretKey key;
    
    private CryptString() throws Exception {
        myEncryptionKey = "ThisIsSpartaThisIsSparta";
        myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;
        arrayBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
        ks = new DESedeKeySpec(arrayBytes);
        skf = SecretKeyFactory.getInstance(myEncryptionScheme);
        cipher = Cipher.getInstance(myEncryptionScheme);
        key = skf.generateSecret(ks);
    }
    
    public static String encrypt(String unencryptedString) {
        try {
            CryptString cs = new CryptString();
            cs.cipher.init(Cipher.ENCRYPT_MODE, cs.key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cs.cipher.doFinal(plainText);
            String encryptedString = new String(Base64.encodeBytes(encryptedText));
            return encryptedString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    public static String decrypt(String encryptedString) {
        try {
            CryptString cs = new CryptString();
            cs.cipher.init(Cipher.DECRYPT_MODE, cs.key);
            byte[] encryptedText = Base64.decode(encryptedString);
            byte[] plainText = cs.cipher.doFinal(encryptedText);
            String decryptedText = new String(plainText);
            return decryptedText;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}