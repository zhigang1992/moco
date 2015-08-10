package com.github.dreamhead.moco.internal;

import com.github.dreamhead.moco.AbstractMocoHttpTest;
import com.github.dreamhead.moco.HttpServer;
import com.github.dreamhead.moco.HttpsCertificate;
import com.github.dreamhead.moco.Runnable;
import org.apache.http.client.HttpResponseException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.github.dreamhead.moco.HttpsCertificate.certificate;
import static com.github.dreamhead.moco.Moco.*;
import static com.github.dreamhead.moco.Runner.running;
import static com.github.dreamhead.moco.helper.RemoteTestUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ActualHttpServerTest extends AbstractMocoHttpTest {
    private HttpServer httpServer;
    private HttpServer anotherServer;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        httpServer = httpServer(12306, context("/foo"));
        httpServer.response("foo");
        anotherServer = httpServer(12306, context("/bar"));
    }

    @Test
    public void shouldPass() throws Exception {
        HttpServer server = httpServer(12306, context("/"));
        server.get(match(uri("/repos/.*")))
                .response(proxy(from("/repos").to("https://api.github.com/repos"), playback("playback.response")));
        running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get("http://localhost:12306/repos/dreamhead/moco/contributors?a[]=1&a[]=2");
            }
        });
    }

    @Test
    public void should_merge_http_server_with_any_handler_one_side() throws Exception {
        HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeHttpServer((ActualHttpServer) httpServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo/anything")), is("foo"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_exception_for_merging_http_server_with_any_handler_one_side() throws Exception {
        HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeHttpServer((ActualHttpServer) httpServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(remoteUrl("/bar/anything"));
            }
        });
    }

    @Test
    public void should_merge_http_server_with_any_handler_other_side() throws Exception {
        HttpServer mergedServer = ((ActualHttpServer) httpServer).mergeHttpServer((ActualHttpServer) anotherServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteUrl("/foo/anything")), is("foo"));
            }
        });
    }

    @Test(expected = HttpResponseException.class)
    public void should_throw_for_merging_http_server_with_any_handler_other_side() throws Exception {
        HttpServer mergedServer = ((ActualHttpServer) httpServer).mergeHttpServer((ActualHttpServer) anotherServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                helper.get(remoteUrl("/bar/anything"));
            }
        });
    }

    @Test
    public void should_config_handler_correctly_while_merging() throws Exception {
        httpServer = httpServer(12306, fileRoot("src/test/resources"));
        httpServer.response(file("foo.response"));
        HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeHttpServer((ActualHttpServer) httpServer);

        running(mergedServer, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(root()), is("foo.response"));
            }
        });
    }

    @Test
    public void should_config_handler_correctly_other_side_while_merging() throws Exception {
        httpServer = httpServer(12306, fileRoot("src/test/resources"));
        httpServer.response(file("foo.response"));
        HttpServer mergedServer = ((ActualHttpServer) httpServer).mergeHttpServer((ActualHttpServer) anotherServer);

        running(mergedServer, new Runnable() {
            @Override
            public void run() throws IOException {
                assertThat(helper.get(root()), is("foo.response"));
            }
        });
    }

    private final HttpsCertificate DEFAULT_CERTIFICATE = certificate(pathResource("cert.jks"), "mocohttps", "mocohttps");

    @Test
    public void should_merge_https_server() throws Exception {
        anotherServer = httpsServer(12306, DEFAULT_CERTIFICATE, context("/bar"));
        HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeHttpServer((ActualHttpServer) httpServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteHttpsUrl("/foo/anything")), is("foo"));
            }
        });
    }

    @Test
    public void should_merge_https_server_into_http_server() throws Exception {
        httpServer = httpsServer(12306, DEFAULT_CERTIFICATE, context("/foo"));
        httpServer.response("foo");
        HttpServer mergedServer = ((ActualHttpServer) anotherServer).mergeHttpServer((ActualHttpServer) httpServer);
        running(mergedServer, new Runnable() {
            @Override
            public void run() throws Exception {
                assertThat(helper.get(remoteHttpsUrl("/foo/anything")), is("foo"));
            }
        });
    }
}
