package com.github.gv2011.h2;

import static com.github.gv2011.util.ex.Exceptions.call;

import java.sql.DriverManager;

import org.h2.Driver;

import com.github.gv2011.util.jdbc.Database;
import com.github.gv2011.util.jdbc.DbProvider;
import com.github.gv2011.util.serviceloader.RecursiveServiceLoader;
import com.github.gv2011.util.tempfile.TempFileFactory;

public class H2DbProvider implements DbProvider{

  static {
    call(()->DriverManager.registerDriver(new Driver()));
  }

  private final TempFileFactory tempFileFactory;

  public H2DbProvider() {
    this(RecursiveServiceLoader.service(TempFileFactory.class));
  }

  public H2DbProvider(final TempFileFactory tempFileFactory) {
    this.tempFileFactory = tempFileFactory;
  }

  @Override
  public Database createDatabase() {
    return new H2Database(tempFileFactory.createTempDir());
  }

}
