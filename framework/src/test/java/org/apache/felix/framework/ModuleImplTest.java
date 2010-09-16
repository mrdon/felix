package org.apache.felix.framework;

import junit.framework.TestCase;
import org.apache.felix.framework.resolver.Content;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import java.net.URLStreamHandler;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class ModuleImplTest extends TestCase
{
    private Bundle bundle;
    private Logger logger;
    private Map configMap;
    private ModuleImpl module;
    private Felix.FelixResolver felixResolver;
    private Map headerMap;
    private Content content;
    private URLStreamHandler streamHandler;

    public void testGetResourcesWithLeakingFromFrameworkClassLoader() throws Exception
    {
        when(content.getEntryAsBytes("somepkg/Foo.class")).thenReturn(BundleHelper.compileClass(getClass().getClassLoader(), "somepkg.Foo", "package somepkg; public class Foo{}"));
        Class cls = module.getClassByDelegation("somepkg.Foo");

        Enumeration e = cls.getClassLoader().getResources("somepkg/test-resource.txt");
        assertNotNull(e);
        assertTrue(e.hasMoreElements());
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        bundle = mock(BundleImpl.class);
        logger = mock(Logger.class);
        configMap = new HashMap();
        configMap.put(Constants.FRAMEWORK_BUNDLE_PARENT, Constants.FRAMEWORK_BUNDLE_PARENT_FRAMEWORK);
        felixResolver = mock(Felix.FelixResolver.class);
        headerMap = new HashMap();
        content = mock(Content.class);
        streamHandler = mock(URLStreamHandler.class);
        module = new ModuleImpl(logger, configMap, felixResolver,  bundle, "foo", headerMap, content, streamHandler, new String[] {"java.lang"}, new boolean[] {false});
    }
}
