package com.github.gv2011.http.imp.server;

import static com.github.gv2011.util.icol.ICollections.listOf;
import static com.github.gv2011.util.icol.ICollections.pathBuilder;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

import java.nio.file.Files;

import com.github.gv2011.http.imp.HttpFactoryImp;
import com.github.gv2011.util.FileUtils;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.DataTypes;
import com.github.gv2011.util.http.HostName;
import com.github.gv2011.util.http.Request;
import com.github.gv2011.util.http.RequestHandler;
import com.github.gv2011.util.http.Response;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.icol.Path;

public final class FileHandler implements RequestHandler{
  
  private final java.nio.file.Path dir;
  private final HttpFactoryImp http;
  private final DirectoryFormatter directoryFormatter = new DirectoryFormatter();
  private boolean useIndexFiles;
  
  public FileHandler(HttpFactoryImp http, java.nio.file.Path dir, boolean useIndexFiles) {
    this.http = http;
    this.dir = dir;
    this.useIndexFiles = useIndexFiles;
  }

  @Override
  public Response handle(Request request) {
    final HostName host = request.host().orElse(HostName.LOCALHOST);
    final Path path = pathBuilder()
      .add(host.toString())
      .addAll(request.path())
      .build()
    ;
    return 
      FileUtils.resolveSafely(dir, path)
      .map(p->{
        final Response resp;
        if(Files.isDirectory(p, NOFOLLOW_LINKS)) {
          if(useIndexFiles){
            resp = 
              tryGetIndexFile(p)
              .map(i->http.createResponse(ByteUtils.readTyped(i)))
              .orElseGet(()->createDirectoryResponse(path, p))
            ;
          }
          else resp = createDirectoryResponse(path, p);
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

  private Opt<java.nio.file.Path> tryGetIndexFile(java.nio.file.Path dir) {
    final java.nio.file.Path indexFile = dir.resolve("index.html");
    return Files.isRegularFile(indexFile, NOFOLLOW_LINKS) ? Opt.of(indexFile) : Opt.empty();
  }

  private Response createDirectoryResponse(final Path path, java.nio.file.Path p) {
    return http.createResponse(directoryFormatter.format(path, p).asEntity());
  }

  @Override
  public boolean accepts(Request request) {
    return true;
  }

}
