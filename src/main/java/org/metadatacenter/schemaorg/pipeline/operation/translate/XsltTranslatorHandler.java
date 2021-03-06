package org.metadatacenter.schemaorg.pipeline.operation.translate;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.metadatacenter.schemaorg.pipeline.mapping.model.FunctionNode;
import org.metadatacenter.schemaorg.pipeline.mapping.model.MapNode;
import org.metadatacenter.schemaorg.pipeline.mapping.model.ObjectNode;
import org.metadatacenter.schemaorg.pipeline.mapping.model.PathNode;
import com.google.common.collect.Maps;

public class XsltTranslatorHandler extends TranslatorHandler {

  @Override
  public void translate(ObjectNode objectNode, OutputStream out) {
    final XsltLayout xsltLayout = new XsltLayout();
    init(objectNode, xsltLayout);
    try (PrintWriter printer = new PrintWriter(out)) {
      printer.println(xsltLayout.toString());
    }
  }

  private void init(ObjectNode objectNode, final XsltLayout xsltLayout) {
    translateObjectNode("instance", objectNode, xsltLayout);
    visit(objectNode, xsltLayout);
  }

  private void visit(MapNode mapNode, XsltLayout xsltLayout) {
    for (Iterator<String> iter = mapNode.attributeNames(); iter.hasNext();) {
      String attrName = iter.next();
      MapNode node = mapNode.get(attrName);
      if (node.isObjectNode()) {
        translateObjectNode(attrName, (ObjectNode) node, xsltLayout);
        visit(node, xsltLayout);
      } else if (node.isPathNode()) {
        translatePathNode(attrName, (PathNode) node, xsltLayout);
      } else if (node.isFunctionNode()) {
        translateFunctionNode(attrName, (FunctionNode) node, xsltLayout);
      }
      else if (node.isArrayNode()) {
        for (Iterator<MapNode> arrIter = node.elements(); arrIter.hasNext();) {
          MapNode item = arrIter.next();
          if (item.isObjectNode()) {
            translateObjectNode(attrName, (ObjectNode) item, xsltLayout);
            visit(item, xsltLayout);
          } else if (item.isPathNode()) {
            translatePathNode(attrName, (PathNode) item, xsltLayout);
          } else if (item.isFunctionNode()) {
            translateFunctionNode(attrName, (FunctionNode) item, xsltLayout);
          }
        }
      }
    }
  }

  private void translatePathNode(String attrName, PathNode pathNode, XsltLayout xsltLayout) {
    String relativePath = pathNode.getRelativePath();
    if (!pointsToCurrentLocation(relativePath)) {
      xsltLayout.addPathTemplate(fixAttributeName(attrName), pathNode.getAbsolutePath());
    }
  }

  private void translateFunctionNode(String attrName, FunctionNode functionNode, XsltLayout xsltLayout) {
    String functionName = functionNode.getName();
    List<String> arguments = functionNode.getArguments();
    xsltLayout.addFunctionTemplate(fixAttributeName(attrName), functionName, arguments);
  }

  private static boolean pointsToCurrentLocation(String path) {
    return "/.".equals(path);
  }

  private void translateObjectNode(String attrName, ObjectNode objectNode, XsltLayout xsltLayout) {
    String objectPath = objectNode.getAbsolutePath();
    Map<String, String> objectMap = toMapOfString(objectNode.getObjectMap());
    xsltLayout.addObjectTemplate(fixAttributeName(attrName), objectPath, objectMap);
  }

  private static Map<String, String> toMapOfString(Map<String, MapNode> objectMap) {
    Map<String, String> mapOfString = Maps.newLinkedHashMap();
    for (String attrName : objectMap.keySet()) {
      MapNode mapNode = objectMap.get(attrName);
      mapOfString.put(fixAttributeName(attrName), mapNode.getValue());
    }
    return mapOfString;
  }

  private static String fixAttributeName(String name) {
    return name.replaceFirst("@", "_");
  }
}
