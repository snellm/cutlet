package org.snellm.cutlet;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertTrue;

public class TestUtil {
    private TestUtil() {}

    /**
     * Open a stream for a file relative to the class
     */
    public static InputStream openStreamResource(Class<?> clazz, String filename) {
        InputStream ret = clazz.getResourceAsStream(filename);
        if (ret == null) {
            throw new RuntimeException("File [" + filename + "] not found for class [" + clazz.getCanonicalName() + "]");
        }
        return ret;
    }

    /**
     * Read a file relative to the class
     */
    public static String readFileResource(Class<?> clazz, String filename) {
        try {
            return IOUtils.toString(openStreamResource(clazz, filename));
        } catch (IOException e) {
            throw new RuntimeException("IO error reading file [" + filename + "]", e);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error reading file [" + filename + "]", e);
        }
    }

    /**
     * Asserts with str contains substr, and fails with useful message if it does not
     */
    public static void assertContains(String str, String substr) {
        assertTrue(str + " does not contain " + substr, str.contains(substr));
    }
}
