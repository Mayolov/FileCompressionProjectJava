
/*
 *  @authors Mayolo Valencia, Darren Wong, Meghana Kumar, Albert Wang
 * 
 * This program reads a file and adds each character to a huffman
 * tree. They are all placed as leafs in trees. They are then assigned 
 * a code to each with the most used character having the smallest binary code
 * usually of 00 or 01. The files are read from the command line, the first arguement
 * is the file we are reading from the second is the target file. Using a hash map the
 * bits are stored in the file aswell as the hashmap object.
 */
import java.io.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class MyCompress {

    public static void main(String[] args) throws IOException {
        // Checks if filename is passed
        if (args.length >= 2) {
            // Get the filename from the command line
            String filename = args[0];
            String filename2 = args[1];

            // Opens the file with a scanner
            Scanner scanner = new Scanner(new File(filename));

            // adds the text into a single string
            String text = "";
            while (scanner.hasNextLine()) {
                text += scanner.nextLine();
                text += "\n";
            }

            int[] counts = getCharacterFrequency(text); // Count frequency

            Tree tree = getHuffmanTree(counts); // Create a Huffman tree
            String[] codes = getCode(tree.root); // Get codes

            // creates hashmap
            HashMap<Character, String> map = new HashMap<>();

            for (int i = 0; i < codes.length; i++)
                if (counts[i] != 0) { // (char)i is not in text if counts[i] is 0
                    map.put((char) (i), codes[i]);
                }

            // Creates an ObjectOutputStream object
            try (ObjectOutputStream output = new ObjectOutputStream((new FileOutputStream(filename2)))) {
                output.writeObject(map);// adds the map object to the file
            }

            // Creates a PrintStream object
            try (PrintStream out = new PrintStream(new FileOutputStream(filename2, true))) {
                // adds a space between the object and the bits
                out.println(" ");

                // writes bits to the file with a delimiter
                for (int i = 0; i < text.length(); i++) {
                    out.print(map.get(text.charAt(i)) + " ");
                }
            }
        } else {

            // Console suggestion
            System.out.print("Use (Java FileName FileName.txt targetFile.txt) or "
                    + "(Java FileName.java FileName.txt targetFile.txt) in the command line");
        }
    }

    /**
     * Get Huffman codes for the characters
     * This method is called once after a Huffman tree is built
     */
    public static String[] getCode(Tree.Node root) {
        if (root == null)
            return null;
        String[] codes = new String[128];
        assignCode(root, codes);
        return codes;
    }

    /* Recursively get codes to the leaf node */
    private static void assignCode(Tree.Node root, String[] codes) {
        if (root.left != null) {
            root.left.code = root.code + "0";
            assignCode(root.left, codes);

            root.right.code = root.code + "1";
            assignCode(root.right, codes);
        } else {
            codes[(int) root.element] = root.code;
        }
    }

    /** Get a Huffman tree from the codes */
    public static Tree getHuffmanTree(int[] counts) {
        // Create a heap to hold trees
        Heap<Tree> heap = new Heap<>(); // Defined in Listing 24.10
        for (int i = 0; i < counts.length; i++) {
            if (counts[i] > 0)
                heap.add(new Tree(counts[i], (char) i)); // A leaf node tree
        }

        while (heap.getSize() > 1) {
            Tree t1 = heap.remove(); // Remove the smallest weight tree
            Tree t2 = heap.remove(); // Remove the next smallest weight
            heap.add(new Tree(t1, t2)); // Combine two trees
        }

        return heap.remove(); // The final tree
    }

    /** Get the frequency of the characters */
    public static int[] getCharacterFrequency(String text) {
        int[] counts = new int[128]; // 128 ASCII characters

        for (int i = 0; i < text.length(); i++)
            counts[(int) text.charAt(i)]++; // Count the character in text

        return counts;
    }

}

/** Define a Huffman coding tree */
class Tree implements Comparable<Tree> {
    Node root; // The root of the tree

    /** Create a tree with two subtrees */
    public Tree(Tree t1, Tree t2) {
        root = new Node();
        root.left = t1.root;
        root.right = t2.root;
        root.weight = t1.root.weight + t2.root.weight;
    }

    /** Create a tree containing a leaf node */
    public Tree(int weight, char element) {
        root = new Node(weight, element);
    }

    @Override /** Compare trees based on their weights */
    public int compareTo(Tree t) {
        if (root.weight < t.root.weight) // Purposely reverse the order
            return 1;
        else if (root.weight == t.root.weight)
            return 0;
        else
            return -1;
    }

    public class Node {
        char element; // Stores the character for a leaf node
        int weight; // weight of the subtree rooted at this node
        Node left; // Reference to the left subtree
        Node right; // Reference to the right subtree
        String code = ""; // The code of this node from the root

        /** Create an empty node */
        public Node() {
        }

        /** Create a node with the specified weight and character */
        public Node(int weight, char element) {
            this.weight = weight;
            this.element = element;
        }
    }
}

class Heap<E extends Comparable<E>> {
    private java.util.ArrayList<E> list = new java.util.ArrayList<E>();

    /** Create a default heap */
    public Heap() {
    }

    /** Create a heap from an array of objects */
    public Heap(E[] objects) {
        for (int i = 0; i < objects.length; i++)
            add(objects[i]);
    }

    /** Add a new object into the heap */
    public void add(E newObject) {
        list.add(newObject); // Append to the heap
        int currentIndex = list.size() - 1; // The index of the last node

        while (currentIndex > 0) {
            int parentIndex = (currentIndex - 1) / 2;
            // Swap if the current object is greater than its parent
            if (list.get(currentIndex).compareTo(
                    list.get(parentIndex)) > 0) {
                E temp = list.get(currentIndex);
                list.set(currentIndex, list.get(parentIndex));
                list.set(parentIndex, temp);
            } else
                break; // the tree is a heap now

            currentIndex = parentIndex;
        }
    }

    /** Remove the root from the heap */
    public E remove() {
        if (list.size() == 0)
            return null;

        E removedObject = list.get(0);
        list.set(0, list.get(list.size() - 1));
        list.remove(list.size() - 1);

        int currentIndex = 0;
        while (currentIndex < list.size()) {
            int leftChildIndex = 2 * currentIndex + 1;
            int rightChildIndex = 2 * currentIndex + 2;

            // Find the maximum between two children
            if (leftChildIndex >= list.size())
                break; // The tree is a heap
            int maxIndex = leftChildIndex;
            if (rightChildIndex < list.size()) {
                if (list.get(maxIndex).compareTo(
                        list.get(rightChildIndex)) < 0) {
                    maxIndex = rightChildIndex;
                }
            }

            // Swap if the current node is less than the maximum
            if (list.get(currentIndex).compareTo(
                    list.get(maxIndex)) < 0) {
                E temp = list.get(maxIndex);
                list.set(maxIndex, list.get(currentIndex));
                list.set(currentIndex, temp);
                currentIndex = maxIndex;
            } else
                break; // The tree is a heap
        }

        return removedObject;
    }

    /** Get the number of nodes in the tree */
    public int getSize() {
        return list.size();
    }
}
