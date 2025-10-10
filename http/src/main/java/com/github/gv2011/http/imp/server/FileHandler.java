package com.github.gv2011.http.imp.server;

import static com.github.gv2011.util.StringUtils.tryRemovePrefix;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.icol.ICollections.listOf;
import static com.github.gv2011.util.icol.ICollections.pathBuilder;
import static com.github.gv2011.util.icol.ICollections.toISet;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static org.slf4j.LoggerFactory.getLogger;

import java.nio.file.Files;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.github.gv2011.http.imp.HttpFactoryImp;
import com.github.gv2011.util.FileUtils;
import com.github.gv2011.util.UrlEncoding;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.DataType;
import com.github.gv2011.util.bytes.DataTypeProvider;
import com.github.gv2011.util.bytes.DataTypes;
import com.github.gv2011.util.bytes.FileExtension;
import com.github.gv2011.util.bytes.TypedBytes;
import com.github.gv2011.util.http.Request;
import com.github.gv2011.util.http.RequestHandler;
import com.github.gv2011.util.http.Response;
import com.github.gv2011.util.icol.ISet;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.icol.Path;
import com.github.gv2011.util.sec.CertificateChain;
import com.github.gv2011.util.sec.Domain;
import com.github.gv2011.util.sec.SecUtils;

public final class FileHandler implements RequestHandler{

  static final String AUTHORISED_USERS = ".authorised-users";

  private static final FileExtension ASC = FileExtension.parse("asc");

  private static final String PUB_RSA = ".pub.rsa";

  private static final String INACTIVE = "inactive-";

  private static final Logger LOG = getLogger(FileHandler.class);

  private final HttpFactoryImp http;
  private final DataTypeProvider dataTypeProvider;
  private final java.nio.file.Path dir;
  private final DirectoryFormatter directoryFormatter = new DirectoryFormatter();
  private final boolean useIndexFiles;

  public FileHandler(final HttpFactoryImp http, final DataTypeProvider dataTypeProvider, final java.nio.file.Path dir, final boolean useIndexFiles) {
    this.http = http;
    this.dataTypeProvider = dataTypeProvider;
    this.dir = dir;
    this.useIndexFiles = useIndexFiles;
  }

  @Override
  public Response handle(final Request request) {
    final Domain host = request.host();
    final Path path = pathBuilder()
      .add(host.toString())
      .addAll(request.path())
      .build()
    ;

    final boolean authorised;
    if(useAuthorisation(host)){
      final Opt<X509Certificate> cert = request.peerCertificateChain().map(CertificateChain::leafCertificate);
      final Opt<RSAPublicKey> userKey = cert
        .map(X509Certificate::getPublicKey)
        .tryCast(RSAPublicKey.class)
      ;
      authorised = userKey.map(authorisedUsers(host)::contains).orElse(false);
      if(!authorised){
        userKey.ifPresentDo(c->{
          final String ppal = cert.get().getSubjectX500Principal().getName();
          ByteUtils.newBytes(c.getEncoded())
          .write(getAuthDir(host).resolve(
            INACTIVE+UrlEncoding.encodePathElement(tryRemovePrefix(ppal, "CN=").orElse(ppal))+PUB_RSA
          ));
        });
      }
    }
    else authorised = true;
    tryRemovePrefix(PUB_RSA, AUTHORISED_USERS); //TODO

    return
      (authorised ? resolve(dir, path) : Opt.<java.nio.file.Path>empty())
      .map(filePath->{
        final Response resp;
        if(Files.isDirectory(filePath, NOFOLLOW_LINKS)) {
          if(useIndexFiles){
            resp =
              tryGetIndexFile(filePath)
              .map(i->http.createResponse(ByteUtils.readTyped(i)))
              .orElseGet(()->createDirectoryResponse(host, path, filePath))
            ;
          }
          else resp = createDirectoryResponse(host, path, filePath);
        }
        else {
          if(request.parameters().tryGet("txt").equals(Opt.of(listOf("true")))){
            resp = http.createResponse(ByteUtils.read(filePath).typed(DataTypes.TEXT_PLAIN_UTF_8));
          }
          else resp = http.createResponse(readFile(filePath));
        }
        return resp;
      })
      .orElseGet(()->http.createResponse())
    ;
  }

  private TypedBytes readFile(final java.nio.file.Path filePath) {
    final FileExtension extension = FileUtils.getExtension(filePath);
    DataType dataType =
      localDataTypeOverride(extension)
      .orElseGet(()->dataTypeProvider.dataTypeForExtension(extension))
    ;
    if(dataType.charset().isEmpty() && dataType.primaryType().equals(DataTypes.TEXT)){
      dataType = dataType.withCharset(UTF_8);
    }
    return ByteUtils.read(filePath).typed(dataType);
  }

  private Opt<DataType> localDataTypeOverride(final FileExtension extension) {
    return extension.equals(ASC) ? Opt.of(DataTypes.TEXT_PLAIN_UTF_8) : Opt.empty();
  }

  private Opt<java.nio.file.Path> resolve(final java.nio.file.Path baseDirectory, final Path path) {
    return
      path.stream().anyMatch(pe->pe.startsWith("."))
      ? Opt.empty()
      : FileUtils.resolveSafely(baseDirectory, path);
  }

  private ISet<RSAPublicKey> authorisedUsers(final Domain host) {
    return readUsers(getAuthDir(host));
  }

  private boolean useAuthorisation(final Domain host) {
    return Files.exists(getAuthDir(host));
  }

  private java.nio.file.Path getAuthDir(final Domain host) {
    return dir.resolve(host.toString()).resolve(AUTHORISED_USERS);
  }

  private Opt<java.nio.file.Path> tryGetIndexFile(final java.nio.file.Path dir) {
    final java.nio.file.Path indexFile = dir.resolve("index.html");
    return Files.isRegularFile(indexFile, NOFOLLOW_LINKS) ? Opt.of(indexFile) : Opt.empty();
  }

  private Response createDirectoryResponse(final Domain host, final Path path, final java.nio.file.Path p) {
    if(listDirectories(host, path)) return http.createResponse(directoryFormatter.format(path, p).asEntity());
    else return http.createResponse();
  }

  private boolean listDirectories(final Domain host, final Path path) {
    return useAuthorisation(host);
  }

  @Override
  public boolean accepts(final Request request) {
    return true;
  }

  private static ISet<RSAPublicKey> readUsers(final java.nio.file.Path authDir) {
    return callWithCloseable(
      ()->Files.list(authDir),
      (final Stream<java.nio.file.Path> s)->{
        return s
          .filter(f->f.getFileName().toString().endsWith(PUB_RSA))
          .flatMap(f->{
            try {
              final RSAPublicKey key = SecUtils.parseRsaPublicKey(ByteUtils.read(f));
              return f.getFileName().toString().startsWith(INACTIVE)
                ? Stream.empty()
                : Stream.of(key)
              ;
            } catch (final Exception e) {
              LOG.error(format("Could not read key from file {}.", f), e);
              return Stream.empty();
            }
          })
          .collect(toISet())
        ;
      }
    );
  }

  @Override
  public String toString() {
    return "FH-"+dir;
  }


}
