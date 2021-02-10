package com.github.gv2011.util.email.imp;

import static com.github.gv2011.util.icol.ICollections.emptyList;
import static com.github.gv2011.util.icol.ICollections.listOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.Instant;

import org.junit.Test;

import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.email.Email;
import com.github.gv2011.util.email.EmailAddress;
import com.github.gv2011.util.email.ParsedEmail;
import com.github.gv2011.util.icol.Opt;

public class DefaultMailProviderTest {

  @Test
  public void testPlain() {
    final DefaultMailProvider prov = new DefaultMailProvider();
    Email raw = prov.asEmail(ByteUtils.read(getClass().getResource("mail1.eml")));
    final ParsedEmail mail = prov.parse(raw);
    assertThat(mail.from(), is(listOf(EmailAddress.parse("mail@example.eu"))));
    assertThat(mail.replyTo(), is(mail.from()));
    assertThat(mail.to(), is(listOf(EmailAddress.parse("test@example.com"))));
    assertThat(mail.cc(), is(emptyList()));
    assertThat(mail.bcc(), is(emptyList()));
    assertThat(mail.subject(), is("subject8"));
    assertThat(mail.sentDate(), is(Opt.of(Instant.parse("2021-02-09T22:48:52Z"))));
    assertThat(mail.receivedDate(), is(Opt.empty()));
    assertThat(mail.plainText(), is("Contänt8\r\n"));
  }

  @Test
  public void testWithAttachment() {
    final DefaultMailProvider prov = new DefaultMailProvider();
    Email raw = prov.asEmail(ByteUtils.read(getClass().getResource("with-attachment.eml")));
    final ParsedEmail mail = prov.parse(raw);
    assertThat(mail.from(), is(listOf(EmailAddress.parse("mail@example.eu"))));
    assertThat(mail.replyTo(), is(mail.from()));
    assertThat(mail.to(), is(listOf(EmailAddress.parse("test@example.com"))));
    assertThat(mail.cc(), is(emptyList()));
    assertThat(mail.bcc(), is(emptyList()));
    assertThat(mail.subject(), is("Bätröff 9"));
    assertThat(mail.sentDate(), is(Opt.of(Instant.parse("2021-02-10T12:26:48Z"))));
    assertThat(mail.receivedDate(), is(Opt.empty()));
    assertThat(
      mail.plainText(), 
      is(
        "Inhalt, der länger ist als das, was in eine Zeile passt und deswegen von \r\n"
        + "Thunderbird umgebrochen wurde.\r\n\r\n.\r\n\r\n\r\n")
    );
  }

  @Test
  public void testMultipartAlternative() {
    final DefaultMailProvider prov = new DefaultMailProvider();
    Email raw = prov.asEmail(ByteUtils.read(getClass().getResource("multipart-alternative.eml")));
    final ParsedEmail mail = prov.parse(raw);
    assertThat(mail.from(), is(listOf(EmailAddress.parse("mail@example.eu"))));
    assertThat(mail.replyTo(), is(mail.from()));
    assertThat(mail.to(), is(listOf(EmailAddress.parse("test@example.com"))));
    assertThat(mail.cc(), is(emptyList()));
    assertThat(mail.bcc(), is(emptyList()));
    assertThat(mail.subject(), is("Bätröff 9"));
    assertThat(mail.sentDate(), is(Opt.of(Instant.parse("2021-02-10T12:26:48Z"))));
    assertThat(mail.receivedDate(), is(Opt.empty()));
    assertThat(
      mail.plainText(), 
      is("Dies ist der *erste* Absatz.\r\n\r\nDies ist der /zweite/, automatisch umgebrochene umgebrochene \r\n"
          + "umgebrochene umgebrochene umgebrochene umgebrochene umgebrochene Absatz.\r\n\r\n"
      )
    );
  }

  @Test
  public void testMultipartAlternativeAttachment() {
    final DefaultMailProvider prov = new DefaultMailProvider();
    Email raw = prov.asEmail(ByteUtils.read(getClass().getResource("multipart-alternative-attachment.eml")));
    final ParsedEmail mail = prov.parse(raw);
    assertThat(mail.from(), is(listOf(EmailAddress.parse("mail@example.eu"))));
    assertThat(mail.replyTo(), is(mail.from()));
    assertThat(mail.to(), is(listOf(EmailAddress.parse("test@example.com"))));
    assertThat(mail.cc(), is(emptyList()));
    assertThat(mail.bcc(), is(emptyList()));
    assertThat(mail.subject(), is("Bätröff 9"));
    assertThat(mail.sentDate(), is(Opt.of(Instant.parse("2021-02-10T12:26:48Z"))));
    assertThat(mail.receivedDate(), is(Opt.empty()));
    assertThat(
      mail.plainText(), 
      is("Dies ist eine *HTML*-Nachricht.\r\n\r\n")
    );
  }

}
