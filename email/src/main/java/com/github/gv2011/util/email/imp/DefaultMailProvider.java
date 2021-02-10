package com.github.gv2011.util.email.imp;

import static com.github.gv2011.util.CollectionUtils.recursiveStream;
import static com.github.gv2011.util.Equal.equal;
import static com.github.gv2011.util.StringUtils.toLowerCase;
import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.callWithCloseable;
import static com.github.gv2011.util.ex.Exceptions.notYetImplemented;
import static com.github.gv2011.util.icol.ICollections.emptyList;
import static com.github.gv2011.util.icol.ICollections.toIList;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.joining;

import java.util.Arrays;
import java.util.Date;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.BeanUtils;
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
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;

public final class DefaultMailProvider implements MailProvider{

  private static final String RFC822 = "rfc822";
  
  
  @Override
  public AutoCloseableNt createMailListener(Consumer<Email> mailReceiver, MailAccount mailAccount) {
    return new MailListener(this, mailReceiver, mailAccount);
  }

  @Override
  public ParsedEmail parse(Email opaque) {
    return callWithCloseable(opaque.content()::openStream, s->{
      MimeMessage mime = new MimeMessage(null, s);
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

  private String getPlainText(MimeMessage mime) {
    return recursiveStream((Part)mime, m->getChildren(m))
      .filter(p->DataType.parse(call(p::getContentType)).baseType().equals(DataTypes.TEXT_PLAIN))
      .map(p->(String)call(p::getContent))
      .collect(joining("\n ----\n"))
    ;
  }

  private Stream<BodyPart> getChildren(Part m) {
    if(DataType.parse(call(m::getContentType)).baseType().primaryType().equals(DataTypes.MULTIPART)){
      final Multipart multipart = (Multipart) call(m::getContent);
      return IntStream.range(0, call(multipart::getCount))
        .mapToObj(i->call(()->multipart.getBodyPart(i)))
        .filter(not(this::isAttachment))
      ;
    }
    else return Stream.empty();
  }
  
  private boolean isAttachment(BodyPart bodyPart){
    return toLowerCase(call(()->Opt.ofNullable(bodyPart.getDisposition()).orElse(""))).equals("attachment");
  }

  private IList<EmailAddress> convert(@Nullable Address[] aa) {
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
  public ParsedEmail sendEmail(ParsedEmail email, MailAccount mailAccount) {
    // TODO Auto-generated method stub
    return notYetImplemented();
  }

}
