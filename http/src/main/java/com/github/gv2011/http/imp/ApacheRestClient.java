package com.github.gv2011.http.imp;

import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.json.JsonUtils.jsonFactory;
import static org.slf4j.LoggerFactory.getLogger;

import java.net.URI;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpStatus;
import org.slf4j.Logger;

import com.github.gv2011.util.StreamUtils;
import static com.github.gv2011.util.http.HttpFactory.*;
import com.github.gv2011.util.http.RestClient;
import com.github.gv2011.util.icol.Opt;
import com.github.gv2011.util.json.JsonFactory;
import com.github.gv2011.util.json.JsonNode;

public final class ApacheRestClient implements RestClient{

  private static final Logger LOG = getLogger(ApacheRestClient.class);

  public static RestClient createInstance() {
    return new ApacheRestClient();
  }

  private ApacheRestClient(){}

  private final JsonFactory jsonFactory = jsonFactory();
  private final CloseableHttpClient hc = HttpClientBuilder.create().build();

  @Override
  public void close() {
    call(()->hc.close());
  }

  @Override
  public JsonNode read(final URI url){
    return request(new HttpGet(url));
  }

  @Override
  public JsonNode post(final URI url, final JsonNode body, final Opt<String> authToken) {
    final HttpPost request = new HttpPost(url);
    authToken.ifPresentDo(t->request.setHeader(AUTHORIZATION, format(BEARER_PATTERN, t)));
    request.setEntity(new JsonEntity(body));
    return request(request);
  }


  private JsonNode request(final ClassicHttpRequest request) {
    return call(()->hc.execute(
      request,
      response->{
        final int code = response.getCode();
        LOG.debug("{}: {}", code, response.getReasonPhrase());
        for(final Header h: response.getHeaders()) {
          LOG.trace("{}={}", h.getName(), h.getValue());
        }
        final String responseBody = StreamUtils.readText(()->response.getEntity().getContent());
        verifyEqual(code, HttpStatus.SC_OK, (e,a)->code+" "+response.getReasonPhrase()+":\n"+responseBody);
        try {
          return jsonFactory.deserialize(responseBody);
        }catch(final Exception e) {
          throw new RestClientException(format("Could not parse body as Json. Body: {}", responseBody), e);
        }
      }
    ));
  }

}
