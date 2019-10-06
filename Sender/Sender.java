import java.security.MessageDigest;
import java.util.Scanner;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeyGenerator;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.DigestInputStream;

class Sender {
  private static int BUFFER_SIZE = 32 * 1024;
  public static void sendMessage() throws Exception {
    Scanner in = new Scanner(System.in);
    System.out.println("Input the name of the massage file: ");
    // get symmetric.key
    String file = in.nextLine();
    concat(file, key);
    hash = genHash(in);
  }

  private static void concate(String file, String key) {
    ObjectInputStream file = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
    BufferedOutputStream wfile = new BufferedOutputStream(new FileOutputStream("message.kmk"));
    int i;
    byte[] buffer = new byte[BUFFER_SIZE];
    do {
      i = file.read(buffer, 0, BUFFER_SIZE);
    }
  }

  private static String genHash(String file) {
    BufferedInputStream file = new BufferedInputStream(new FileInputStream("message.kmk"));
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

    System.out.println("digit digest (hash value):");
    for (int k=0, j=0; k<hash.length; k++, j++) {
      System.out.format("%2X ", new Byte(hash[k]));
      if (j >= 15) {
        System.out.println("");
        j=-1;
      }
    }
    System.out.println("");

    return new String(hash);
  }
}