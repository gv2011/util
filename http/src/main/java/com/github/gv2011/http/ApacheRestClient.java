package com.github.gv2011.http;

/*-
 * #%L
 * The MIT License (MIT)
 * %%
 * Copyright (C) 2017 Vinz (https://github.com/gv2011)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
import static com.github.gv2011.util.Verify.verify;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.json.JsonUtils.jsonFactory;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.net.URI;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
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
    call(hc::close);
  }

  @Override
  public JsonNode read(final URI url){
    try(CloseableHttpResponse response = hc.execute(new HttpGet(url))){
      final StatusLine statusLine = response.getStatusLine();
      verify(statusLine, l->l.getStatusCode()==HttpStatus.SC_OK);
      LOG.info(statusLine.toString());
      for(final Header h: response.getAllHeaders()) {
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
