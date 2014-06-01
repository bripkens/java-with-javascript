package de.bripkens.nashorn;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

public class AsyncHttpClientTest {

  @Test
  public void shouldSendRequest() throws Exception {
    AsyncHttpClient client = null;
    try {
      client = new AsyncHttpClient();
      client.prepareGet("http://movie-database.herokuapp.com/movies")//
      .addHeader("Accept", "application/json")//
      .execute(new AsyncCompletionHandler<Response>() {

        @Override
        public Response onCompleted(Response response) throws Exception {
          System.out.println(response.getStatusCode());
          System.out.println(response.getResponseBody("UTF-8"));
          return response;
        }

        @Override
        public void onThrowable(Throwable t) {
          System.out.println(t);
          super.onThrowable(t);
        }

      }).get();
    } finally {
      IOUtils.closeQuietly(client);
    }
  }

}
