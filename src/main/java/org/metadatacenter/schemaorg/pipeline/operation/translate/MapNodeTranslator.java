package org.metadatacenter.schemaorg.pipeline.operation.translate;

import org.metadatacenter.schemaorg.pipeline.mapping.MappingLanguages;
import org.metadatacenter.schemaorg.pipeline.mapping.caml.databind.AttributeMapper;
import org.metadatacenter.schemaorg.pipeline.mapping.model.ObjectNode;
import org.metadatacenter.schemaorg.pipeline.mapping.rml.databind.RmlMapper;

public class MapNodeTranslator {

  public static String translate(TranslatorHandler handler, String mappingString) {
    return translate(handler, mappingString, MappingLanguages.CAML);
  }

  public static String translate(TranslatorHandler handler, String mappingString, String language) {
    ObjectNode objectNode = null;
    if (MappingLanguages.CAML.equals(language)) {
      final AttributeMapper mapper = new AttributeMapper();
      objectNode = mapper.readText(mappingString);
    } else if (MappingLanguages.RML.equals(language)) {
      final RmlMapper mapper = new RmlMapper();
      objectNode = mapper.readText(mappingString);
    } else {
      throw new IllegalArgumentException("The language selection '" + language + "' is not supported yet");
    }
    return translate(handler, objectNode);
  }

  public static String translate(TranslatorHandler handler, final ObjectNode objectNode) {
    return handler.translate(objectNode);
  }
}
