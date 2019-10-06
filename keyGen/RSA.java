import java.util.*;
import java.io.*;
import java.security.Key;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.spec.SecretKeySpec;

import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.Scanner;
import java.security.spec.RSAPrivateKeySpec;

import java.math.BigInteger;
import javax.crypto.SecretKey;

import javax.crypto.Cipher;

public class RSA {
  public static void main(String[] args) throws Exception {
    String[] list = {"X","Y"};
    for(String item:list){
        SecureRandom random = new SecureRandom();
    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    generator.initialize(1024, random);  //1024: key size in bits
    KeyPair pair = generator.generateKeyPair();
    Key pubKey = pair.getPublic();
    Key privKey = pair.getPrivate();
    
    KeyFactory factory = KeyFactory.getInstance("RSA");
    RSAPublicKeySpec pubKSpec = factory.getKeySpec(pubKey, 
        RSAPublicKeySpec.class);
    RSAPrivateKeySpec privKSpec = factory.getKeySpec(privKey, 
        RSAPrivateKeySpec.class);

    //save the parameters of the keys to the files
    saveToFile("../sender/"+item+"Public.key", pubKSpec.getModulus(), 
        pubKSpec.getPublicExponent());
    saveToFile("../reciever/"+item+"Public.key", pubKSpec.getModulus(), 
        pubKSpec.getPublicExponent());
    saveToFile(""+item+"Public.key", pubKSpec.getModulus(), 
        pubKSpec.getPublicExponent());
    saveToFile("../sender/"+item+"Private.key", privKSpec.getModulus(), 
        privKSpec.getPrivateExponent());
    saveToFile("../reciever/"+item+"Private.key", privKSpec.getModulus(), 
        privKSpec.getPrivateExponent());
    saveToFile(""+item+"Private.key", privKSpec.getModulus(), 
        privKSpec.getPrivateExponent());
    }
    Scanner scannyboi = new Scanner(System.in);
    SecretKeySpec key  = new SecretKeySpec(scannyboi.nextLine().getBytes("UTF-8"),"AES");
    // SecretKey twobaby = SecretKey.getInstance("AES").key;
    String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
    // System.out.println(encodedKey);
    saveToFile("../symmetric.key", encodedKey);
    readSymmetricKey("../symmetric.key");
  }
  public static String readSymmetricKey(String keyFileName) throws IOException {
    InputStream inputStream = new FileInputStream(keyFileName);
    ObjectInputStream oin =
        new ObjectInputStream(new BufferedInputStream(inputStream));

    try {
       String key = oin.readObject().toString();

      System.out.println("Read from " + keyFileName + ": modulus = " + key.toString() );

    //   RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
    //   KeyFactory factory = KeyFactory.getInstance("RSA");
    //   PublicKey key = factory.generatePublic(keySpec);

      return key;
    } catch (Exception e) {
      throw new RuntimeException("Spurious serialisation error", e);
    } finally {
      oin.close();
    }
  }
  public static void saveToFile(String fileName, String key) throws IOException{
    

    ObjectOutputStream oout = new ObjectOutputStream(
      new BufferedOutputStream(new FileOutputStream(fileName)));

    try {
      oout.writeObject(key);
    //   oout.writeObject(exp);
    } catch (Exception e) {
      throw new IOException("Unexpected error", e);
    } finally {
      oout.close();
    }
  }
  public static void saveToFile(String fileName,
        BigInteger mod, BigInteger exp) throws IOException {

    System.out.println("Write to " + fileName + ": modulus = " + 
        mod.toString() + ", exponent = " + exp.toString() + "\n");

    ObjectOutputStream oout = new ObjectOutputStream(
      new BufferedOutputStream(new FileOutputStream(fileName)));

    try {
      oout.writeObject(mod);
      oout.writeObject(exp);
    } catch (Exception e) {
      throw new IOException("Unexpected error", e);
    } finally {
      oout.close();
    }
  }
//   public static PublicKey readPubKeyFromFile(String keyFileName) 
//       throws IOException {

//     InputStream in = 
//         RSAConfidentiality.class.getResourceAsStream(keyFileName);
//     ObjectInputStream oin =
//         new ObjectInputStream(new BufferedInputStream(in));

//     try {
//       BigInteger m = (BigInteger) oin.readObject();
//       BigInteger e = (BigInteger) oin.readObject();

//       System.out.println("Read from " + keyFileName + ": modulus = " + 
//           m.toString() + ", exponent = " + e.toString() + "\n");

//       RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
//       KeyFactory factory = KeyFactory.getInstance("RSA");
//       PublicKey key = factory.generatePublic(keySpec);

//       return key;
//     } catch (Exception e) {
//       throw new RuntimeException("Spurious serialisation error", e);
//     } finally {
//       oin.close();
//     }
//   }
//   public static PrivateKey readPrivKeyFromFile(String keyFileName) 
//       throws IOException {

//     InputStream in = 
//         RSAConfidentiality.class.getResourceAsStream(keyFileName);
//     ObjectInputStream oin =
//         new ObjectInputStream(new BufferedInputStream(in));

//     try {
//       BigInteger m = (BigInteger) oin.readObject();
//       BigInteger e = (BigInteger) oin.readObject();

//       System.out.println("Read from " + keyFileName + ": modulus = " + 
//           m.toString() + ", exponent = " + e.toString() + "\n");

//       RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
//       KeyFactory factory = KeyFactory.getInstance("RSA");
//       PrivateKey key = factory.generatePrivate(keySpec);

//       return key;
//     } catch (Exception e) {
//       throw new RuntimeException("Spurious serialisation error", e);
//     } finally {
//       oin.close();
//     }
//   }
}