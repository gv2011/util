package com.github.gv2011.util;

import static com.github.gv2011.util.StreamUtils.readBytes;
import static com.github.gv2011.util.Verify.notNull;
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.staticClass;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.ex.ThrowingSupplier;

public class JarUtils {

  private static final String M_PREFIX = "META-INF/maven/";
  private static final String M_SUFFIX = "/pom.properties";

  private JarUtils(){staticClass();}

  public static final MvnJarId getMavenId(final ThrowingSupplier<InputStream> streamSource){
    return callWithCloseable(()->streamSource.get(), s->{
      final JarInputStream jis = new JarInputStream(s);
      @Nullable MvnJarId result = null;
      @Nullable JarEntry e = jis.getNextJarEntry();
      while(e!=null){
        final String n = e.getName();
        if(n.startsWith(M_PREFIX)&&n.endsWith(M_SUFFIX)){
          verify(result==null);
          final long size = e.getSize();
          verify(size>0 && size<100000);
          final byte[] file = readBytes(jis,(int)size);
          final Properties props = new Properties();
          props.load(new ByteArrayInputStream(file));
          result = new MvnJarId(props);
        }
        e = jis.getNextJarEntry();
      }
      return notNull(result);
    });
  }

  public static final Iterator<JarEntry> asIterator(final JarInputStream jarInputStream){
    return new Iterator<JarEntry>(){
      private @Nullable JarEntry next = call(()->jarInputStream.getNextJarEntry());
      @Override
      public boolean hasNext() {return next!=null;}
      @Override
      public JarEntry next() {
        final @Nullable JarEntry result = next;
        if(result==null) throw new NoSuchElementException();
        next =  call(()->jarInputStream.getNextJarEntry());
        return result;
      }
    };
  }

  public static MvnJarId mvnJarId(final String groupId, final String artifactId, final String version){
    return new MvnJarId(groupId, artifactId, version);
  }

  public static final class MvnJarId{
    private final String groupId;
    private final String artifactId;
    private final String version;
    private MvnJarId(final Properties props) {
      this(
        notNull(props.getProperty("groupId")),
        notNull(props.getProperty("artifactId")),
        notNull(props.getProperty("version"))
      );
    }
    private MvnJarId(final String groupId, final String artifactId, final String version) {
      this.groupId = check(groupId);
      this.artifactId = check(artifactId);
      this.version = check(version);
    }
    private String check(final String v) {
      verify(!v.isEmpty());
      verify(v.trim().equals(v));
      verifyEqual(v.indexOf(':'),-1);
      return v;
    }
    public String groupId()   {return groupId;}
    public String artifactId(){return artifactId;}
    public String classifier(){return "jar";}
    public String version()   {return version;}
    @Override
    public int hashCode() {return toString().hashCode();}
    @Override
    public boolean equals(final Object obj) {
      if(this==obj) return true;
      else if(!(obj instanceof MvnJarId)) return false;
      else return toString().equals(obj.toString());
    }
    @Override
    public String toString() {
      return groupId+":"+artifactId+":jar:"+version;
    }
  }
}
