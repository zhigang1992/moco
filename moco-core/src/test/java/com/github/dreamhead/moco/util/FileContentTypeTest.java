package com.github.dreamhead.moco.util;

import com.google.common.base.Optional;
import com.google.common.net.MediaType;
import org.junit.Test;

import java.nio.charset.Charset;

import static com.google.common.base.Optional.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FileContentTypeTest {
	@Test
	public void should_get_type_from_filename() {
		FileContentType contentType = new FileContentType("logo.png", Optional.<Charset>absent());
		assertThat(contentType.getContentType(), is(MediaType.PNG));
	}

	@Test
	public void should_get_default_type_from_unknown_name() {
		FileContentType contentType = new FileContentType("UNKNOWN_FILE", Optional.<Charset>absent());
		assertThat(contentType.getContentType(), is(MediaType.PLAIN_TEXT_UTF_8));
	}

	@Test
	public void should_have_charset_for_file() {
		Charset gbk = Charset.forName("gbk");
		FileContentType contentType = new FileContentType("result.response", of(gbk));
		assertThat(contentType.getContentType(), is(MediaType.create("text", "plain").withCharset(gbk)));
	}

	@Test
	public void should_have_charset_for_css_file() {
		FileContentType contentType = new FileContentType("result.css", Optional.<Charset>absent());
		assertThat(contentType.getContentType(), is(MediaType.create("text", "css")));
	}
}
