package com.socyno.base.bscservice;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.socyno.base.bscmixutil.Base64Util;

import lombok.NonNull;

public abstract class AbstractAesEncrypt {
    
    public static String generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecretKey secretKey = keyGen.generateKey();
        return Base64Util.encode(secretKey.getEncoded());
    }
    
    protected abstract byte[] getKey() throws Exception;
    
    public byte[] decrypt(@NonNull byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey secretKey = new SecretKeySpec(getKey(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }
    
    public byte[] decrypt(@NonNull String data) throws Exception {
    	return decrypt(data.getBytes());
    }
    
    public byte[] decryptFromBase64(@NonNull String data) throws Exception {
    	return decrypt(Base64Util.decode(data));
    }
    
    public byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKey secretKey = new SecretKeySpec(getKey(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }
    
    public String encryptAsBase64(byte[] data) throws Exception {
        return Base64Util.encode(encrypt(data));
    }
    
    public String encryptAsBase64(@NonNull String data) throws Exception {
        return encryptAsBase64(data.getBytes());
    }
}
