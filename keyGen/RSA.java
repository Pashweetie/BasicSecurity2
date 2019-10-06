import java.io.*;

import java.security.Key;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.RSAPrivateKeySpec;

import java.math.BigInteger;

import javax.crypto.Cipher;

public class RSAConfidentiality {
  public static void main(String[] args) throws Exception {

    byte[] input = "012340123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF".getBytes();
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

    //Generate a pair of keys
    SecureRandom random = new SecureRandom();
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(1024, random);  //1024: key size in bits
    KeyPair pair = generator.generateKeyPair();
    Key pubKey = pair.getPublic();
    Key privKey = pair.getPrivate();

  }
}