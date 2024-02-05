module com.github.gv2011.util.email.imp{
  requires transitive com.github.gv2011.util;
  requires org.eclipse.angus.mail.imap;
  requires jakarta.mail;

  exports com.github.gv2011.util.email.imp to com.github.gv2011.util;

  provides com.github.gv2011.util.email.MailProvider with com.github.gv2011.util.email.imp.DefaultMailProvider;
}