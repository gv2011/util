# Logback System Properties

## Specifying the location of the default configuration file as a system property
(from https://logback.qos.ch/manual/configuration.html)

"You may specify the location of the default configuration file with a system property named "**logback.configurationFile**". The value of this property can be a URL, a resource on the class path or a path to a file external to the application.

java -Dlogback.configurationFile=/path/to/config.xml chapters.configuration.MyApp1

Note that the file extension must be ".xml" or ".groovy". Other extensions are ignored. Explicitly registering a status listener may help debugging issues locating the configuration file.

Given that "logback.configurationFile" is a Java system property, it may be set within your application as well. However, the system property must be set before any logger instance is created."

## Enable Debug Mode

**logback.debug=true**
