package com.github.gv2011.http.imp;

import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;

import java.net.URI;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.apache.hc.core5.net.URIBuilder;

import com.github.gv2011.util.Pair;
import com.github.gv2011.util.UrlBuilder;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.Opt;

public final class UrlBuilderFactory implements UrlBuilder.Factory{

  @Override
  public UrlBuilder newUrlBuilder() {
    return new UrlBuilderImp(Opt.empty());
  }

  @Override
  public UrlBuilder newUrlBuilder(final URI baseUrl) {
    return new UrlBuilderImp(Opt.of(baseUrl));
  }

  private static class UrlBuilderImp implements UrlBuilder{

    private final URIBuilder b;

    private UrlBuilderImp(final Opt<URI> baseUrl) {
      b = baseUrl.map(u->new URIBuilder(u)).orElseGet(URIBuilder::new);
    }

    @Override
    public UrlBuilder setQuery(final IMap<String, String> query) {
      return setQuery(query.entrySet().stream());
    }

    @Override
    public UrlBuilder setQuery(final IList<Pair<String, String>> query) {
      verifyEqual((int)query.stream().map(Pair::getKey).distinct().count(), query.size());
      return setQuery(query.stream());
    }

    private UrlBuilder setQuery(final Stream<? extends Entry<String, String>> query) {
      b.clearParameters();
      query.forEach(e->b.addParameter(e.getKey(), e.getValue()));
      return this;
    }

    @Override
    public URI build() {
      return call(b::build);
    }
  }

}
