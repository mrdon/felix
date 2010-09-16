package org.apache.felix.framework;

import org.codehaus.janino.ByteArrayClassLoader;
import org.codehaus.janino.CompileException;
import org.codehaus.janino.Parser;
import org.codehaus.janino.Scanner;
import org.codehaus.janino.SimpleCompiler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Helper class for bundle related testing
 */
public class BundleHelper
{
    /**
     * Creates a bundle for a given manifest
     */
    public static File createBundle(String manifest) throws IOException
    {
        File f = File.createTempFile("felix-bundle", ".jar");
        f.deleteOnExit();

        Manifest mf = new Manifest(new ByteArrayInputStream(manifest.getBytes("utf-8")));
        mf.getMainAttributes().putValue("Manifest-Version", "1.0");
        JarOutputStream os = new JarOutputStream(new FileOutputStream(f), mf);
        os.close();

        return f;
    }

    /**
     * Compiles a class and returns the bytecode
     */
    public static byte[] compileClass(ClassLoader parent, String className, String content) throws Exception
    {
        SimpleCompiler compiler = new SimpleCompiler();
        compiler.setParentClassLoader(parent);
        try
        {
            compiler.cook(new StringReader(content));
        }
        catch (Parser.ParseException ex)
        {
            throw new IllegalArgumentException("Unable to compile " + className, ex);
        }
        catch (CompileException ex)
        {
            throw new IllegalArgumentException("Unable to compile " + className, ex);
        }
        return getClassFile(compiler.getClassLoader(), className);
    }

    private static byte[] getClassFile(ClassLoader classLoader, String className) throws NoSuchFieldException, IllegalAccessException
    {
        // Silly hack because I'm too lazy to do it the "proper" janino way
        ByteArrayClassLoader cl = (ByteArrayClassLoader) classLoader;
        Field field = cl.getClass().getDeclaredField("classes");
        field.setAccessible(true);
        Map classes = (Map) field.get(cl);

        return (byte[]) classes.get(className);
    }
}
