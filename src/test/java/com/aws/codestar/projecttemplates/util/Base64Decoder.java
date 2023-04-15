package com.aws.codestar.projecttemplates.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
public class Base64Decoder {
    public static void main(String[] args) {
        String inputFile = "/tmp/b64.txt"; // path to input file
        String outputFile = "/tmp/b64-plain.txt"; // path to output file

        try {
            // Read base64-encoded text from input file
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String base64Text = reader.readLine();
            reader.close();

            // Decode base64-encoded text to plain text
            byte[] decodedBytes = Base64.getDecoder().decode(base64Text);
            String plainText = new String(decodedBytes, StandardCharsets.UTF_8);

            // Write plain text to output file
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(plainText);
            writer.close();

            System.out.println("Decoded text has been written to " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
