package org.arya.jsondiff;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

/** unit test class for {@link JsonDiff} */
public class JsonDiffTest {
  private JsonDiff jsonDiff = new JsonDiff();
  private DiffOptions diffOptions = DiffOptions.builder().build();

  @Test
  public void testChangesOnly() {
    JSONObject sourceObject =readJson("changesOnly/source.json");
    JSONObject targetObject =readJson("changesOnly/target.json");
    JSONObject expected =readJson("changesOnly/expected.json");
    diffOptions.setChangesOnly(true);
    JSONObject actual = jsonDiff.diff(sourceObject, targetObject, diffOptions);
    JSONAssert.assertEquals(expected.getJSONObject("changesOnly"), actual,true);
  }

  @Test
  public void testChangesOnlyCaseInsensitive() {
    JSONObject sourceObject =readJson("changesOnly/source.json");
    JSONObject targetObject =readJson("changesOnly/target.json");
    JSONObject expected =readJson("changesOnly/expected.json");
    diffOptions.setChangesOnly(true);
    diffOptions.setCaseSensitiveMatch(true);
    JSONObject actual = jsonDiff.diff(sourceObject, targetObject, diffOptions);
    JSONAssert.assertEquals(expected.getJSONObject("caseSensitiveMatch"), actual,true);
  }

  @Test
  public void testFullContext() {
    JSONObject sourceObject =readJson("changesOnly/source.json");
    JSONObject targetObject =readJson("changesOnly/target.json");
    JSONObject expected =readJson("changesOnly/expected.json");
    JSONObject actual = jsonDiff.diff(sourceObject, targetObject, diffOptions);
    System.out.println(actual.toString());
    JSONAssert.assertEquals(expected.getJSONObject("fullContext"), actual,true);
  }

  private JSONObject readJson(String path) {
    return new JSONObject(
        new JSONTokener(Thread.currentThread().getContextClassLoader().getResourceAsStream(path)));
  }
}
