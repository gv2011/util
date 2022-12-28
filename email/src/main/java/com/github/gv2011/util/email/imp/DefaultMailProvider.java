package com.github.gv2011.util.email.imp;

import static com.github.gv2011.util.CollectionUtils.recursiveStream;
import static com.github.gv2011.util.Equal.equal;
import static com.github.gv2011.util.StringUtils.toLowerCase;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.format;
import static com.github.gv2011.util.ex.Exceptions.notYetImplemented;
import static com.github.gv2011.util.icol.ICollections.emptyList;
import static com.github.gv2011.util.icol.ICollections.toIList;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.BeanUtils;
import com.github.gv2011.util.StreamUtils;
import com.github.gv2011.util.ann.Nullable;
import com.github.gv2011.util.bytes.DataType;
import com.github.gv2011.util.bytes.DataTypes;
import com.github.gv2011.util.email.Email;
import com.github.gv2011.util.email.EmailAddress;
import com.github.gv2011.util.email.MailAccount;
import com.github.gv2011.util.email.MailProvider;
import com.github.gv2011.util.email.ParsedEmail;
import com.github.gv2011.util.icol.IList;
import com.github.gv2011.util.icol.Opt;

import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

public final class DefaultMailProvider implements MailProvider{

  private static final String RFC822 = "rfc822";


  @Override
  public AutoCloseableNt createMailListener(final Consumer<Email> mailReceiver, final MailAccount mailAccount) {
    return new MailListener(this, mailReceiver, mailAccount);
  }

  @Override
  public ParsedEmail parse(final Email opaque) {
    return callWithCloseable(opaque.content()::openStream, s->{
      final MimeMessage mime = new MimeMessage(Session.getDefaultInstance(new Properties()), s);
      return BeanUtils.beanBuilder(ParsedEmail.class)
        .set(ParsedEmail::from).to(convert(mime.getFrom()))
        .set(ParsedEmail::replyTo).to(convert(mime.getReplyTo()))
        .set(ParsedEmail::to).to(convert(mime.getRecipients(RecipientType.TO)))
        .set(ParsedEmail::cc).to(convert(mime.getRecipients(RecipientType.CC)))
        .set(ParsedEmail::bcc).to(convert(mime.getRecipients(RecipientType.BCC)))
        .set(ParsedEmail::subject).to(mime.getSubject())
        .set(ParsedEmail::sentDate).to(Opt.ofNullable(mime.getSentDate()).map(Date::toInstant))
        .set(ParsedEmail::receivedDate).to(Opt.ofNullable(mime.getReceivedDate()).map(Date::toInstant))
        .set(ParsedEmail::plainText).to(getPlainText(mime))
        .build()
      ;
    });
  }

  private String getPlainText(final MimeMessage mime) {
    return recursiveStream((Part)mime, m->getChildren(m))
      .filter(p->DataType.parse(call(p::getContentType)).baseType().equals(DataTypes.TEXT_PLAIN))
      .flatMap(p->{
        final DataType dataType = DataType.parse(call(p::getContentType));
        if(!dataType.baseType().equals(DataTypes.TEXT_PLAIN)) {
          return Stream.empty();
        }
        else {
          final Object content = call(p::getContent);
          if(content instanceof String) return Stream.of((String) content);
          else if(content instanceof InputStream) {
            return Stream.of(new String(
              StreamUtils.readAndClose(()->(InputStream)content),
              dataType.charset().orElse(StandardCharsets.UTF_8)
            ));
          }
          else {
            throw new UnsupportedOperationException(format("Unexpected content {}.", content.getClass()));
          }
        }
      })
      .collect(joining("\n ----\n"))
    ;
  }

  private Stream<BodyPart> getChildren(final Part m) {
    if(DataType.parse(call(m::getContentType)).baseType().primaryType().equals(DataTypes.MULTIPART)){
      final Object content = call(m::getContent);
      final Multipart multipart;
      if(content instanceof Multipart) {
        multipart = (Multipart) content;
      }
      else if(content instanceof InputStream) {
        multipart = call(()->new MimeMultipart(new ByteArrayDataSource(
          StreamUtils.readAndClose(()->(InputStream)content),
          m.getContentType()
        )));
      }
      else {
        throw new UnsupportedOperationException(format("Unexpected content {}.", content.getClass()));
      }
      return IntStream.range(0, call(multipart::getCount))
        .mapToObj(i->call(()->multipart.getBodyPart(i)))
        .filter(not(this::isAttachment))
      ;
    }
    else return Stream.empty();
  }

  private boolean isAttachment(final BodyPart bodyPart){
    return toLowerCase(call(()->Opt.ofNullable(bodyPart.getDisposition()).orElse(""))).equals("attachment");
  }

  private IList<EmailAddress> convert(@Nullable final Address[] aa) {
    return aa==null
      ? emptyList()
      : (Arrays.stream(aa)
        .filter(a->equal(a.getType(), RFC822))
        .filter(a->a instanceof InternetAddress)
        .map(a->
          EmailAddress.parse(((InternetAddress)a).getAddress())
        )
        .collect(toIList())
      )
    ;
  }

  @Override
  public ParsedEmail sendEmail(final ParsedEmail email, final MailAccount mailAccount) {
    // TODO Auto-generated method stub
    return notYetImplemented();
  }

}
