package com.github.gv2011.http.imp.server;

import static com.github.gv2011.util.CollectionUtils.pair;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;

import java.net.URI;
import java.nio.file.Files;
import static java.nio.file.LinkOption.*;
import java.util.stream.Stream;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.UrlEncoding;
import com.github.gv2011.util.html.BlockBuilder;
import com.github.gv2011.util.html.HtmlBuilder;
import com.github.gv2011.util.html.HtmlDocument;
import com.github.gv2011.util.html.HtmlUtils;
import com.github.gv2011.util.icol.Path;

final class DirectoryFormatter {
  
  HtmlDocument format(final Path path, java.nio.file.Path dir){
    return callWithCloseable(()->Files.list(dir), s->{
      return format(
        path,
        (
          s
          .filter(f->Files.isRegularFile(f, NOFOLLOW_LINKS) || Files.isDirectory(f, NOFOLLOW_LINKS))
          .filter(f->!f.getFileName().toString().equals(FileHandler.AUTHORISED_USERS))
          .map(f->pair(Files.isDirectory(f, NOFOLLOW_LINKS), f.getFileName().toString()))
        )
      );
    });
  }

  HtmlDocument format(Path path, Stream<Pair<Boolean,String>> children){
    assert path.size()>=2;
    assert path.last().isEmpty() : "The last element of a directory path must be empty (trailing slash).";
    HtmlBuilder b = HtmlUtils.htmlFactory().newBuilder();
    b.setTitle(path.get(path.size()-2));
    if(path.size()>2){
      b.addText("\n");
      BlockBuilder parents = b.addBlock();
      for(int i=0; i<path.size()-2; i++){
        parents.addAnchor(path.get(i), URI.create(StringUtils.multiply("../",path.size()-2-i)));
        parents.addText("/");
      }
      parents.addText(path.get(path.size()-2));
      parents.close();
      b.addText("\n");
    }
    children.forEach(c->{
      final BlockBuilder block = b.addBlock();
      boolean isDir = c.getKey();
      String name = c.getValue();
      if(isDir){
        block.addText("d ");
        block.addAnchor(name, URI.create(UrlEncoding.encodePathElement(name)+"/"));
      }
      else{
        block.addText("f ");
        block.addAnchor(name, URI.create(UrlEncoding.encodePathElement(name)));
        block.addText(" ");
        block.addAnchor("as text", URI.create(UrlEncoding.encodePathElement(name)+"?txt=true"));
      }
      block.close();
      b.addText("\n");
    });
    return b.build();
  }

}
