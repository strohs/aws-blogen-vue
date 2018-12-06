package com.cliff.aws.lambda.utils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Utilities for reading from and writing to Java Streams
 * <p>
 * Author: Cliff
 */
public class StreamUtils {

    /**
     * convert an input stream into a string
     *
     * @param stream
     * @param charsetName - the character encoding to use when reading the stream
     * @return
     * @throws IOException
     */
    public static String inputStreamToString (InputStream stream, String charsetName) throws IOException {
        StringBuilder textBuilder = new StringBuilder();
        try ( Reader reader = new BufferedReader(new InputStreamReader
                (stream, Charset.forName(charsetName))) ) {
            int c = 0;
            while ( (c = reader.read()) != -1 ) {
                textBuilder.append(( char ) c);
            }
        } catch ( IOException e ) {
            throw new IOException("error reading input stream");
        }
        return textBuilder.toString();
    }

    /**
     * writes a string to an output stream using the specified charsetName
     *
     * @param s           - the string to write
     * @param stream      - the output stream to write to
     * @param charsetName - name of a charset to use to write the
     */
    public static void stringToOutputStream (String s, OutputStream stream, String charsetName) throws IOException {
        try ( OutputStreamWriter streamWriter = new OutputStreamWriter(stream, charsetName) ) {
            streamWriter.write(s);
            streamWriter.flush();
        }
    }
}
