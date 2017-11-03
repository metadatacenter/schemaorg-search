package org.metadatacenter.schemaorg.pipeline.alma.databind;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import org.junit.Test;
import org.metadatacenter.schemaorg.pipeline.alma.databind.AttributeMapper;
import org.metadatacenter.schemaorg.pipeline.alma.databind.node.MapNode;

public class AttributeMapperTest {

  @Test
  public void shouldParsePathMap() {
    final String text = "a: /path";
    AttributeMapper mapper = new AttributeMapper();
    MapNode mapNode = mapper.readText(text);
    // Assertions
    assertThat(mapNode.get("a").isPathNode(), equalTo(true));
    assertThat(mapNode.get("a").isConstantNode(), equalTo(false));
    assertThat(mapNode.get("a").isObjectNode(), equalTo(false));
  }

  @Test
  public void shouldParseConstantMap() {
    final String text = "a: constant";
    AttributeMapper mapper = new AttributeMapper();
    MapNode mapNode = mapper.readText(text);
    // Assertions
    assertThat(mapNode.get("a").isPathNode(), equalTo(false));
    assertThat(mapNode.get("a").isConstantNode(), equalTo(true));
    assertThat(mapNode.get("a").isObjectNode(), equalTo(false));
  }

  @Test
  public void shouldParseObjectMap() {
    final String text = 
          "a: /path\n"
        + "  b: /path\n"
        + "  c: constant";
    AttributeMapper mapper = new AttributeMapper();
    MapNode mapNode = mapper.readText(text);
    // Assertions
    assertThat(mapNode.get("a").isPathNode(), equalTo(false));
    assertThat(mapNode.get("a").isConstantNode(), equalTo(false));
    assertThat(mapNode.get("a").isObjectNode(), equalTo(true));
  }

  @Test
  public void shouldParseMappingText() {
    final String text =
          "a1: /path1\n"
        + "a2: constant1\n"
        + "a3: /path2\n"
        + "  a4: /path3\n"
        + "  a5: constant2";
    AttributeMapper mapper = new AttributeMapper();
    MapNode mapNode = mapper.readText(text);
    // Assertions
    assertThat(mapNode.get("a1").asText(), equalTo("/path1"));
    assertThat(mapNode.get("a2").asText(), equalTo("constant1"));
    assertThat(mapNode.get("a3").asText(), equalTo("/path2"));
    assertThat(mapNode.get("a3").get("a4").asText(), equalTo("/path3"));
    assertThat(mapNode.get("a3").get("a5").asText(), equalTo("constant2"));
  }
}
