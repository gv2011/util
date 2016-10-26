package com.github.gv2011.util.xml;

import javax.xml.stream.events.XMLEvent;

public enum XmlEventType{
  NULL,
  START_ELEMENT,
  END_ELEMENT,
  PROCESSING_INSTRUCTION,
  CHARACTERS,
  COMMENT,
  SPACE,
  START_DOCUMENT,
  END_DOCUMENT,
  ENTITY_REFERENCE,
  ATTRIBUTE,
  DTD,
  CDATA,
  NAMESPACE,
  NOTATION_DECLARATION,
  ENTITY_DECLARATION;
  
  public static final XmlEventType eventType(XMLEvent event) {
    return XmlEventType.values()[event.getEventType()];
  }


}
