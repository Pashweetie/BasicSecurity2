import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.Scanner;
import java.security.Key;
import java.security.KeyFactory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.DigestInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.math.BigInteger;
import javax.crypto.SecretKey;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import java.security.Key;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.io.*;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.Scanner;
import java.security.spec.RSAPrivateKeySpec;
import java.math.BigInteger;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Cipher;

public class Sender {
  private static int BUFFER_SIZE = 5 * 1024;
  private static String IV = "AAAAAAAAAAAAAAAA";
  public static void main(String[] args){
    try {
      sendMessage();
    } catch (Exception e) {
      System.out.println(e);
    }
  }
  public static void sendMessage() throws Exception {
    Scanner in = new Scanner(System.in);
    System.out.println("Input the name of the massage file: ");
    String key = "";
    PublicKey Ky;
    try {
      key = readSymmetricKey("../symmetric.key");
      Ky = readPubKeyFromFile("YPublic.key");
    } catch (IOException e) {
      throw e;
    }
    String file = in.nextLine();
    try{
      concate(file, key);
    } catch (IOException e) {
      System.out.println(e);
      in.close();
      throw new Exception("borked");
    }
    try {
      genHash();
      writeAES(key, file);
      writeRSA(key, Ky);
    } catch (Exception e) {
      System.out.println(e);
      in.close();
      throw e;
    }
    in.close();
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

  private static void concate(String f, String key) throws IOException {
    BufferedReader rfile = new BufferedReader(new FileReader(f));
    BufferedWriter wfile = new BufferedWriter(new FileWriter("message.kmk"));
    try {
      String message = "";
      String line;
      while((line = rfile.readLine()) != null) {
        message += line.toString();
      }
      String kmk = key + message + key;
      wfile.write(kmk);
    } catch (Exception e) {
      throw new IOException("Unexpected error", e);
    } finally {
      rfile.close();
      wfile.close();
    }
  }

  private static void genHash() throws Exception {
    BufferedInputStream file = new BufferedInputStream(new FileInputStream("message.kmk"));
    BufferedWriter wfile = new BufferedWriter(new FileWriter("message.khmac"));
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    DigestInputStream in = new DigestInputStream(file, md);
    int i;
    byte[] buffer = new byte[BUFFER_SIZE];
    do {
      i = in.read(buffer, 0, BUFFER_SIZE);
    } while (i == BUFFER_SIZE);
    md = in.getMessageDigest();
    in.close();

    byte[] hash = md.digest();
    System.out.println("hash value:");
    for (int k=0, j=0; k<hash.length; k++, j++) {
      System.out.format("%2X ", new Byte(hash[k]));
      if (j >= 15) {
        System.out.println("");
        j=-1;
      }
    }
    System.out.println("");
    String xhash = "";
    for (i=0; i < hash.length; i++) {
      int x = (int)hash[i];
      String y = Integer.toHexString(x);
      if(y.length() == 1) {
        y = "0"+y;
      }
      y = (y.substring(y.length()-2, y.length()));
      xhash += y;
    }
    wfile.write(xhash);
    file.close();
    wfile.close();
  }

  public static PublicKey readPubKeyFromFile(String keyFileName) 
      throws IOException {

    InputStream in = 
        Sender.class.getResourceAsStream(keyFileName);
    ObjectInputStream oin =
        new ObjectInputStream(new BufferedInputStream(in));

    try {
      BigInteger m = (BigInteger) oin.readObject();
      BigInteger e = (BigInteger) oin.readObject();

      System.out.println("Read from " + keyFileName + ": modulus = " + 
          m.toString() + ", exponent = " + e.toString() + "\n");

      RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
      KeyFactory factory = KeyFactory.getInstance("RSA");
      PublicKey key = factory.generatePublic(keySpec);

      return key;
    } catch (Exception e) {
      throw new RuntimeException("Spurious serialisation error", e);
    } finally {
      oin.close();
    }
  }

  private static SecretKeySpec keyxy;
  private static byte[] keybyte;
  public static void setKey(String myKey)
  {
    MessageDigest sha = null;
    try {
        keybyte = myKey.getBytes("UTF-8");
        sha = MessageDigest.getInstance("SHA-1");
        keybyte = sha.digest(keybyte);
        keybyte = Arrays.copyOf(keybyte, 16);
        keyxy = new SecretKeySpec(keybyte, "AES");
    }
    catch (Exception e) {
        e.printStackTrace();
    }
  }

  private static void writeAES(String kxy, String f) throws Exception {
    BufferedReader rfile = new BufferedReader(new FileReader(f));
    ObjectOutputStream wfile = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("message.aescipher")));
    String message = "";
    try {
      String line;
      while((line = rfile.readLine()) != null) {
        message += line.toString();
      }
    } catch (Exception e) {
      wfile.close();
      throw e;
    } finally {
      rfile.close();
    }
    
    try {
      setKey(kxy);
      Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, keyxy);
      wfile.writeObject(cipher.doFinal(message.getBytes("UTF-8")));
    } catch (Exception e) {
      System.out.println("Error while encrypting: " + e.toString());
    } finally {
      wfile.close();
    }
  }

  private static void writeRSA(String kxy, PublicKey ky) throws Exception{
    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    ObjectOutputStream wfile = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("xky.rsacipher")));
    cipher.init(Cipher.ENCRYPT_MODE, ky, new SecureRandom());
    byte[] cipherText = cipher.doFinal(stringtobyte(kxy));
    try {
      wfile.writeObject(cipherText);
    } catch (Exception e) {
      throw e;
    } finally {
      wfile.close();
    }
  }

  public static byte[] stringtobyte(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                             + Character.digit(s.charAt(i+1), 16));
    }
    return data;
  }

}