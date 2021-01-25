package com.github.gv2011.http.imp.server;

import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.listOf;
import static com.github.gv2011.util.icol.ICollections.pathBuilder;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static org.slf4j.LoggerFactory.getLogger;

import java.nio.file.Files;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.github.gv2011.http.imp.HttpFactoryImp;
import com.github.gv2011.util.FileUtils;
import static com.github.gv2011.util.StringUtils.*;
import com.github.gv2011.util.UrlEncoding;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.DataTypes;
import com.github.gv2011.util.http.Request;
import com.github.gv2011.util.http.RequestHandler;
import com.github.gv2011.util.http.Response;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.icol.Path;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.SecUtils;

public final class FileHandler implements RequestHandler{
  
  static final String AUTHORISED_USERS = ".authorised-users";

  private static final String PUB_RSA = ".pub.rsa";

  private static final String INACTIVE = "inactive-";

  private static final Logger LOG = getLogger(FileHandler.class);
  
  private final java.nio.file.Path dir;
  private final HttpFactoryImp http;
  private final DirectoryFormatter directoryFormatter = new DirectoryFormatter();
  private final boolean useIndexFiles;

  
  public FileHandler(HttpFactoryImp http, java.nio.file.Path dir, boolean useIndexFiles) {
    this.http = http;
    this.dir = dir;
    this.useIndexFiles = useIndexFiles;
  }
  
  @Override
  public Response handle(Request request) {
    final Domain host = request.host();
    final Path path = pathBuilder()
      .add(host.toString())
      .addAll(request.path())
      .build()
    ;
    
    final boolean authorised;
    if(useAuthorisation(host)){
      final Opt<X509Certificate> cert = request.peerCertificateChain().tryGetFirst();
      final Opt<RSAPublicKey> userKey = cert
        .map(X509Certificate::getPublicKey)
        .tryCast(RSAPublicKey.class)
      ;
      authorised = userKey.map(authorisedUsers(host)::contains).orElse(false);
      if(!authorised){
        userKey.ifPresent(c->{
          final String ppal = cert.get().getSubjectX500Principal().getName();
          ByteUtils.newBytes(c.getEncoded())
          .write(getAuthDir(host).resolve(
            INACTIVE+UrlEncoding.encodePathElement(tryRemovePrefix(ppal, "CN=").orElse(ppal))+PUB_RSA
          ));
        });
      }
    }
    else authorised = true;
    tryRemovePrefix(PUB_RSA, AUTHORISED_USERS);
    
    return
      (authorised ? resolve(dir, path) : Opt.<java.nio.file.Path>empty())
      .map(p->{
        final Response resp;
        if(Files.isDirectory(p, NOFOLLOW_LINKS)) {
          if(useIndexFiles){
            resp = 
              tryGetIndexFile(p)
              .map(i->http.createResponse(ByteUtils.readTyped(i)))
              .orElseGet(()->createDirectoryResponse(host, path, p))
            ;
          }
          else resp = createDirectoryResponse(host, path, p);
        }
        else {
          if(request.parameters().tryGet("txt").equals(Opt.of(listOf("true")))){
            resp = http.createResponse(ByteUtils.read(p).typed(DataTypes.TEXT_PLAIN_UTF_8));
          }
          else resp = http.createResponse(ByteUtils.readTyped(p));
        }
        return resp;
      })
      .orElseGet(()->http.createResponse())
    ;
  }

  private Opt<java.nio.file.Path> resolve(java.nio.file.Path dir, Path path) {
    return path.containsElement(AUTHORISED_USERS) ? Opt.empty() : FileUtils.resolveSafely(dir, path);
  }

  private ISet<RSAPublicKey> authorisedUsers(Domain host) {
    return readUsers(getAuthDir(host));
  }

  private boolean useAuthorisation(Domain host) {
    return Files.exists(getAuthDir(host));
  }

  private java.nio.file.Path getAuthDir(Domain host) {
    return dir.resolve(host.toString()).resolve(AUTHORISED_USERS);
  }

  private Opt<java.nio.file.Path> tryGetIndexFile(java.nio.file.Path dir) {
    final java.nio.file.Path indexFile = dir.resolve("index.html");
    return Files.isRegularFile(indexFile, NOFOLLOW_LINKS) ? Opt.of(indexFile) : Opt.empty();
  }

  private Response createDirectoryResponse(Domain host, final Path path, java.nio.file.Path p) {
    if(listDirectories(host, path)) return http.createResponse(directoryFormatter.format(path, p).asEntity());
    else return http.createResponse();
  }

  private boolean listDirectories(Domain host, Path path) {
    return useAuthorisation(host);
  }

  @Override
  public boolean accepts(Request request) {
    return true;
  }
  
  private static ISet<RSAPublicKey> readUsers(java.nio.file.Path authDir) {
    return callWithCloseable(
      ()->Files.list(authDir), 
      (Stream<java.nio.file.Path> s)->{
        return s
          .filter(f->f.getFileName().toString().endsWith(PUB_RSA))
          .flatMap(f->{
            try {
              final RSAPublicKey key = SecUtils.parseRsaPublicKey(ByteUtils.read(f));
              return f.getFileName().toString().startsWith(INACTIVE)
                ? Stream.empty()
                : Stream.of(key)
              ;
            } catch (Exception e) {
              LOG.error(format("Could not read key from file {}.", f), e);
              return Stream.empty();
            }
          })
          .collect(toISet())
        ;
      }
    );
  }


}
