package com.github.gv2011.util.email.imp;

import static com.github.gv2011.util.ex.Exceptions.call;
import static com.github.gv2011.util.ex.Exceptions.format;
import static org.slf4j.LoggerFactory.getLogger;

import java.time.Duration;
import java.util.Properties;
import java.util.function.Consumer;

import org.slf4j.Logger;

import com.github.gv2011.util.AutoCloseableNt;
import com.github.gv2011.util.bytes.ByteUtils;
import com.github.gv2011.util.bytes.Bytes;
import com.github.gv2011.util.bytes.BytesBuilder;
import com.github.gv2011.util.email.Email;
import com.github.gv2011.util.email.MailAccount;
import com.github.gv2011.util.email.MailProvider;
import com.sun.mail.imap.IMAPFolder;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;

final class MailListener implements AutoCloseableNt{
  
  private static final Logger LOG = getLogger(MailListener.class);

  private final MailProvider mailProvider;
  private final Consumer<Email> mailReceiver;
  private final MailAccount mailAccount;
  private final Store store;
  private final Thread thread;
  private final Object lock = new Object();
  private boolean closed;


  MailListener(MailProvider mailProvider, Consumer<Email> mailReceiver, MailAccount mailAccount) {
    this.mailProvider = mailProvider;
    this.mailReceiver = mailReceiver;
    this.mailAccount = mailAccount;
    final Properties props = new Properties();
    props.setProperty("mail.store.protocol", "imaps");
    final Session session = Session.getDefaultInstance(props, null);
    store = call(()->session.getStore("imaps"));
    thread = new Thread(this::listen, MailListener.class.getSimpleName());
    thread.start();
  }

  
  @Override
  public void close() {
    synchronized(lock){
      closed = true;
    }
    call(store::close);
    if(!Thread.currentThread().equals(thread))call(()->thread.join());
  }
  
  private void listen() {
    try{
      call(()->store.connect(mailAccount.host().toAscii(), mailAccount.user(), mailAccount.password()));
      while(!closed()){
        try(final IMAPFolder inbox = (IMAPFolder) store.getFolder("INBOX")){
          inbox.open(Folder.READ_ONLY);
          int count = inbox.getMessageCount();
          int oldCount = count;
          System.out.println(format("{} messages.", count));
          while(count==oldCount){
            inbox.idle(true);
            oldCount = count;
            count = inbox.getMessageCount();
            if(count<oldCount) LOG.warn("Number of messages shrunk from {} to {}.", oldCount, count);
            System.out.println(format("{} messages.", count));
            for(int n=oldCount+1; n<=count; n++){
              final Message msg = inbox.getMessage(n);
              LOG.info("Message {} received. Subject: {}.", n, msg.getSubject());
              mailReceiver.accept(convert(msg));
            }
            oldCount = count;
          }
        } catch (Exception e) {
          if(!(closed() && e instanceof IllegalStateException)){
            LOG.error("Exception while listening for emails.", e);
            if(!closed())call(()->Thread.sleep(Duration.ofSeconds(10).toMillis()));
          }
        }
      }
    }
    finally{
      if(!closed()) {
        LOG.error("Premature end of listener thread.");
        close();
      }
      else LOG.info("Listener terminated.");
    }
  }
  
  private Email convert(Message msg){
    final Bytes raw;
    try(final BytesBuilder bb = ByteUtils.newBytesBuilder()){
      call(()->msg.writeTo(bb));
      raw = bb.build();
    }
    return mailProvider.asEmail(raw);
  }

  @Override
  public boolean closed() {
    synchronized(lock){
      return closed;
    }
  }

}
