package org.apache.felix.framework;

import org.apache.felix.framework.util.FelixConstants;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * A simple class to manually test the felix framework under different vm versions.
 */
public class Java14Runner
{
    public static void main(String[] args) throws IOException, BundleException
    {
        Map params = new HashMap();
        File cacheDir = File.createTempFile("felix-cache", ".dir");
        cacheDir.delete();
        cacheDir.mkdirs();
        String cache = cacheDir.getPath();
        params.put(Constants.FRAMEWORK_SYSTEMPACKAGES,
            "org.osgi.framework; version=1.4.0,"
            + "org.osgi.service.packageadmin; version=1.2.0,"
            + "org.osgi.service.startlevel; version=1.1.0,"
            + "org.osgi.util.tracker; version=1.3.3,"
            + "org.osgi.service.url; version=1.0.0");
        params.put("felix.cache.profiledir", cache);
        params.put("felix.cache.dir", cache);
        params.put(Constants.FRAMEWORK_STORAGE, cache);

        String mf = "Bundle-SymbolicName: boot.test\n"
            + "Bundle-Version: 1.1.0\n"
            + "Bundle-ManifestVersion: 2";
        File bundle = BundleHelper.createBundle(mf);

        Framework f = new Felix(params);
        f.init();
        f.getBundleContext().installBundle(bundle.toURI().toString());
        f.start();
        f.stop();
    }


}
