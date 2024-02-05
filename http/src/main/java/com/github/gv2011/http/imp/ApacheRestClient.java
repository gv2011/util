package com.github.gv2011.http.imp;

import static com.github.gv2011.util.Verify.verifyEqual;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.json.JsonUtils.jsonFactory;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.net.URI;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpStatus;
import org.slf4j.Logger;

import com.github.gv2011.util.StreamUtils;
import com.github.gv2011.util.http.RestClient;
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
    try(@SuppressWarnings("deprecation") //TODO
      final CloseableHttpResponse response = hc.execute(new HttpGet(url))
    ){
      final int code = response.getCode();
      verifyEqual(code, HttpStatus.SC_OK);
      LOG.info("{}: {}", code, response.getReasonPhrase());
      for(final Header h: response.getHeaders()) {
        LOG.debug("{}={}", h.getName(), h.getValue());
      }
      final String body = StreamUtils.readText(()->response.getEntity().getContent());
      try {
        return jsonFactory.deserialize(body);
      }catch(final Exception e) {
        throw new RestClientException(format("Could not parse body as Json. Body: {}", body), e);
      }
    } catch (final IOException e1) {
      throw new RestClientException(e1);
    }
  }

}
