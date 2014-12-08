package com.twosigma.beaker.scala.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xeustechnologies.jcl.ProxyClassLoader;

import com.twosigma.beaker.jvm.classloader.DynamicClassLoader;

public class ScalaDynamicClassLoader extends DynamicClassLoader {
  protected final ScalaLoaderProxy glp = new ScalaLoaderProxy();
  protected final List<String> paths = new ArrayList<String>();
  
  public ScalaDynamicClassLoader(String dir) {
    super(dir);
    glp.setOrder(70);
    parent.addLoader(glp);
    parent.getLocalLoader().setEnabled(false);
  }

  public void add(Object s) {
    parent.add(s);
    if(s instanceof String) {
      String ss = (String)s;
      File fs = new File(ss);
      if(fs.exists() && fs.isDirectory()) {
        paths.add(ss);
      }
      if (ss.endsWith(".jar")) {
    	parent.getLocalLoader().setEnabled(true);
    	System.out.println("enabling local loader");
      }
    }
    else if(s instanceof URL && ((URL)s).toString().startsWith("file:")) {
      String ss = ((URL)s).toString().substring(5);
      File fs = new File(ss);
      if(fs.exists() && fs.isDirectory()) {
        paths.add(ss);
      }        
      if (((URL)s).toString().endsWith(".jar")) {
    	parent.getLocalLoader().setEnabled(true);
    	System.out.println("enabling local loader");
      }
    }
  }

  @SuppressWarnings("rawtypes")
  public void addAll(List sources) {
    parent.addAll(sources);
    for(Object o : sources) {
      if(o instanceof String) {
        String ss = (String)o;
        File fs = new File(ss);
        if(fs.exists() && fs.isDirectory()) {
          paths.add(ss);
        }        
        if (ss.endsWith(".jar")) {
        	parent.getLocalLoader().setEnabled(true);
        	System.out.println("enabling local loader");;
        }
      }
      else if(o instanceof URL && ((URL)o).toString().startsWith("file:")) {
        String ss = ((URL)o).toString().substring(5);
        File fs = new File(ss);
        if(fs.exists() && fs.isDirectory()) {
          paths.add(ss);
        }        
        if (((URL)o).toString().endsWith(".jar")) {
        	parent.getLocalLoader().setEnabled(true);
        	System.out.println("enabling local loader");
        }
      }
    }
  }

  class ScalaLoaderProxy extends ProxyClassLoader {

    public ScalaLoaderProxy() {
        order = 10;
        enabled = true;
    }
    @Override
    public Class<?> loadClass(String className, boolean resolveIt) {
      Class<?> result = null;
      
      result = classes.get( className );
      if (result != null) {
          return result;
      }

      for(String p : paths) {     
        String tpath = p + File.separator + className.replace(".", File.separator) + ".scala";

        File f = new File(tpath);
        if(f.exists()) {
          try {
            setEnabled(false);
            System.out.println("ERROR: NOT IMPLEMENTED: Should load scala file "+tpath);
            setEnabled(true);
            return null;
          } catch(Exception e) { e.printStackTrace(); }
          setEnabled(true);          
        }
      }
      return null;
    }
    
    @Override
    public InputStream loadResource(String name) {
      for(String p : paths) {
        String tpath = p + File.separator + name.replace(".", File.separator) + ".scala";

        File f = new File(tpath);
        if(f.exists()) {
          try {
            return new FileInputStream(f);
          } catch(Exception e) { }
        }
      }
      return null;
    }

    @Override
    public URL findResource(String name) {
        return null;
    }
  }
  
}
