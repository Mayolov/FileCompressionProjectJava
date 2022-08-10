/*
 * @authors Mayolo Valencia, Darren Wong, Meghana Kumar, Albert Wang
 *  
 * This program decodes the file. By reading the file and then pulling
 * the object from it. It is given the tools that it needs to decode it.
 * The map object is retrieved from the file then the keys and values are reversed
 * to suit the need of reading the reading the key and getting the ascii value of the
 * binary code. The binary is then placed into a list and split by spaces after then placed
 * into an array list. When placed in a for loop that carries on for the length of the Arraylist
 * if the map contains the key at the index i of the arrayList it adds it to a string and then 
 * printed into the file.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Map.Entry;

public class MyDecompress {
    public static void main(String[] args) throws ClassNotFoundException,
            IOException {

        // reads from the command line
        if (args.length >= 2) {
            // Get the filename from the command line
            String filename = args[0];
            String filename2 = args[1];

            try {
                // reading the objects and binary from the file
                FileInputStream readFile = new FileInputStream(filename);
                ObjectInputStream mapObjStream = new ObjectInputStream(readFile);
                Scanner scanner = new Scanner(new File(filename));

                // read object from file
                HashMap<Character, String> fileObject = (HashMap<Character, String>) mapObjStream.readObject();

                // Creates new
                HashMap<String, Character> binaryMap = reverse(fileObject);

                // adds all the text into the string
                String text = "";
                while (scanner.hasNextLine()) {
                    text += scanner.nextLine();
                }

                // create a list with the file split on spaces
                String str[] = text.split(" ");
                List<String> list = new ArrayList<String>();

                // Adds in the array into the array list
                list = Arrays.asList(str);

                String decompressedWords = "";
                // gets the value for the key
                for (int i = 0; i < list.size(); i++) {
                    if (binaryMap.containsKey(list.get(i))) {
                        decompressedWords += binaryMap.get(list.get(i));
                    }
                }

                // writes the decompressed words to the file
                try (PrintStream out = new PrintStream(new FileOutputStream(filename2))) {
                    out.print(decompressedWords);
                }

                // close reader
                mapObjStream.close();

            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.print("Use (Java FileName FileName.txt targetFile.txt) or "
                    + "(Java FileName.java FileName.txt targetFile.txt) in the command line");
        }
    }

    // reverses the hashmap for our use
    public static <K, V> HashMap<V, K> reverse(HashMap<K, V> map) {
        HashMap<V, K> rev = new HashMap<V, K>();
        for (Entry<K, V> entry : map.entrySet())
            rev.put(entry.getValue(), entry.getKey());
        return rev;
    }
}
