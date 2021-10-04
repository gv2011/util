package com.github.gv2011.util.beans.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@SupportedOptions("debug")
@SupportedAnnotationTypes("com.github.gv2011.util.beans.AbstractRoot")
@SupportedSourceVersion(javax.lang.model.SourceVersion.RELEASE_11)
public class BeansProcessor extends AbstractProcessor{

  private boolean done = false;

  @Override
  public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
    if(!done){
      done=true;
      try {
        final JavaFileObject builderFile = processingEnv.getFiler()
        .createSourceFile("com.github.gv2011.util.beans.examples.full.Test");
        try(Writer w = builderFile.openWriter()){
          w.write("package com.github.gv2011.util.beans.examples.full;\r\n"
                + "public class Test {}");
        }
      } catch (final IOException e) {
        e.printStackTrace();
      }
    }
    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Hallo");
    return true;
  }

}
