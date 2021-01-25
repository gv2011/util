package com.github.gv2011.http.imp;

import static com.github.gv2011.util.icol.ICollections.toIMap;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.github.gv2011.util.FileUtils;
import com.github.gv2011.util.StringUtils;
import com.github.gv2011.util.icol.IMap;
import com.github.gv2011.util.icol.Path;
import com.github.gv2011.util.sec.Domain;

public final class ConfigurableHttpsDomainPredicate extends SimpleHttpsDomainPredicate{
  
  private static final String WILDCARD = "*.";

  private static final Duration CHECK_INTERVAL = Duration.ofMinutes(5);
  
  private final Supplier<String> file;
  
  private final Map<Domain,Boolean> knownDomains = new ConcurrentHashMap<>();
  
  private volatile Instant nextFileCheck = Instant.now().plus(CHECK_INTERVAL);
  
  
  public ConfigurableHttpsDomainPredicate(){
    this(Paths.get("domains.txt"));
  }
  
  public ConfigurableHttpsDomainPredicate(java.nio.file.Path domainList){
    this(()->Files.exists(domainList) ? FileUtils.readText(domainList) : "");
  }
  
  public ConfigurableHttpsDomainPredicate(Supplier<String> fileReader){
    this.file = fileReader;
  }
  
  @Override
  public final boolean isHttpsDomain(Domain d){
    return super.isHttpsDomain(d)
      ? (
        isConfigured(d)
      )
      : false
    ;
  }

  private boolean isConfigured(Domain domain) {
    if(Instant.now().isAfter(nextFileCheck)){
      nextFileCheck = nextFileCheck.plus(CHECK_INTERVAL);
      knownDomains.clear();
    }
    return knownDomains.computeIfAbsent(domain, d->{
      final IMap<Domain, Boolean> domains = readDomains();
      boolean result;
      if(domains.entrySet().stream() //exact match 
        .filter(e->!e.getValue().booleanValue())
        .anyMatch(e->e.getKey().equals(d))
      ) result = true;
      else{ //wildcard match 
        result = domains.entrySet().stream()
          .filter(e->e.getValue().booleanValue())
          .anyMatch(e->{
            final Path wildCard = e.getKey().asPath();
            final Path dmn = d.asPath();
            return dmn.size()>wildCard.size() && dmn.endsWith(wildCard);
          })
        ;
      }
      return result;
    });
  }

  private final IMap<Domain,Boolean> readDomains() {
    return 
      StringUtils.split(file.get(), '\n').stream()
      .map(String::trim)
      .filter(l->!l.isEmpty())
      .collect(toIMap(
        l->Domain.parse(StringUtils.tryRemovePrefix(l, WILDCARD).orElse(l)),
        l->l.startsWith(WILDCARD)
      ))
    ;
  }

}
