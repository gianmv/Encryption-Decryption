package encryptdecrypt;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    static String PARAM_MARKER = "-[A-Za-z]+";
    public static void main(String[] args) {
        String mode = "enc";
        String alg = "shift";
        int key = 0;
        String data = "";
        String inputFile = "";
        String outputFile = "";

        for (int i = 0; i < args.length - 1; i++) {
            if ("-mode".equals(args[i])) {
                String temp = args[i + 1];
                if (!temp.matches(PARAM_MARKER)) {
                    mode = args[++i];
                }
            }

            if ("-key".equals(args[i])) {
                String temp = args[i + 1];
                if (!temp.matches(PARAM_MARKER)) {
                    temp = args[++i];
                    key = Integer.valueOf(temp);
                }
            }

            if ("-data".equals(args[i])) {
                String temp = args[i + 1];
                if (!temp.matches(PARAM_MARKER)) {
                    data = args[++i];
                }
            }

            if ("-in".equals(args[i])) {
                String temp = args[i + 1];
                if (!temp.matches(PARAM_MARKER)) {
                    inputFile = args[++i];
                }
            }

            if ("-out".equals(args[i])) {
                String temp = args[i + 1];
                if (!temp.matches(PARAM_MARKER)) {
                    outputFile = args[++i];
                }
            }

            if ("-alg".equals(args[i])) {
                String temp = args[i + 1];
                if (!temp.matches(PARAM_MARKER)) {
                    alg = args[++i];
                }
            }
        }
        
        Encryption enigma = Encryption.getInstance(alg,key);

        if (data.isEmpty()) {
            data = readFile(inputFile);
        }

        switch (mode) {
            case "enc":
                if (outputFile.isEmpty()) {
                    System.out.println(enigma.encrypt(data));
                } else {
                    saveFile(enigma.encrypt(data), outputFile);
                }
                break;
            case "dec":
                if (outputFile.isEmpty()) {
                    System.out.println(enigma.decrypt(data));
                } else {
                    saveFile(enigma.decrypt(data), outputFile);
                }
                break;
        }
    }

    private static String readFile(String inputFile) {
        File file = new File(inputFile);
        String ans = "";
        try (Scanner reader = new Scanner(file)){
            while (reader.hasNext()) {
                ans += reader.nextLine() + "\n";
            }
        } catch (FileNotFoundException e) {
            System.err.printf("The file %s doesn't exist\n",inputFile);
        }
        return ans;
    }

    private static void saveFile(String encrypt, String outputFile) {
        File output = new File(outputFile);
        try (PrintWriter out = new PrintWriter(output)){
            out.print(encrypt);
        } catch (FileNotFoundException e) {
            System.err.printf("The file %s doesn't exist\n",output);
        }
    }
}

abstract class Encryption {
    int key;
    Encryption(int key){
        this.key = key;
    }
    abstract public String encrypt(String phrase);
    abstract public String decrypt(String encryptedPhrase);

    public static Encryption getInstance(String type,int key) {
        switch (type) {
            case "shift":
                return new ShiftEncryption(key);
            case "unicode":
                return new UnicodeEncryption(key);
            default:
                return null;
        }
    }
}

class ShiftEncryption extends Encryption {
    HashMap<Character, Character> encipher = new HashMap<>();
    HashMap<Character, Character> decipher = new HashMap<>();

    ShiftEncryption(int key) {
        super(key);
        int alphabetLength = 'z' - 'a' + 1;

        for (int i = 'a'; i <= 'z'; i++) {
            int value = i + key > 'z' ? i + key - alphabetLength : i + key;
            encipher.put((char) i,(char) value);
            decipher.put((char) value, (char) i);
        }
    }

    @Override
    public String encrypt(String phrase) {
        StringBuilder encryptedPhrase = new StringBuilder();
        for (char x:
                phrase.toCharArray()) {
            if (encipher.containsKey(x)) {
                encryptedPhrase.append(encipher.get(x));
            } else {
                encryptedPhrase.append(x);
            }
        }

        return encryptedPhrase.toString();
    }

    @Override
    public String decrypt(String encryptedPhrase) {
        StringBuilder decrypted = new StringBuilder();
        for (char x:
                encryptedPhrase.toCharArray()) {
            if (encipher.containsKey(x)) {
                decrypted.append(decipher.get(x));
            } else {
                decrypted.append(x);
            }
        }

        return decrypted.toString();
    }
}

class UnicodeEncryption extends Encryption {

    UnicodeEncryption(int key) {
        super(key);
    }

    @Override
    public String encrypt(String phrase) {
        StringBuilder ans = new StringBuilder();
        for (char letter:
                phrase.toCharArray()) {
            ans.append((char) (letter + key));
        }

        return ans.toString();
    }

    @Override
    public String decrypt(String encryptedPhrase) {
        StringBuilder ans = new StringBuilder();
        for (char letter:
                encryptedPhrase.toCharArray()) {
            ans.append((char) (letter - key));
        }

        return ans.toString();
    }
}


