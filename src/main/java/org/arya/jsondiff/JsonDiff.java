package org.arya.jsondiff;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/** */
public class JsonDiff {
  public JSONObject diff(JSONObject source, JSONObject target, DiffOptions diffOptions) {
    JSONObject outputObject = new JSONObject(source.length());
    Iterator<String> sourceKeysItr = source.keys();
    while (sourceKeysItr.hasNext()) {
      String sourceKey = sourceKeysItr.next();
      // check key in target
      if (!target.has(sourceKey)) {
        // its a delete
        outputObject.put(sourceKey, createDiffNode(Operation.DELETE, source.get(sourceKey), null));
        continue;
      }
      // key exists in both source and target
      Object sourceObj = source.get(sourceKey);
      Object targetObj = target.get(sourceKey);
      // if types are
      if (!typesAreSame(sourceObj, targetObj)) {
        // since types are not same, lets mark them as update
        outputObject.put(sourceKey, createDiffNode(Operation.UPDATE, sourceObj, targetObj));
        continue;
      }
      // types are same, check if they are primitive
      if (arePrimitive(sourceObj)) {
        doPrimitiveComparision(sourceKey, sourceObj, targetObj, outputObject, diffOptions);
        continue;
      }
      // they are not primitive, means they could be array or another JSONObject
      if (sourceObj instanceof JSONArray) {
        //
      } else if (sourceObj instanceof JSONObject) {
        // call recursively
        outputObject.put(
            sourceKey, diff((JSONObject) sourceObj, (JSONObject) targetObj, diffOptions));
      }
    }
    // find the diff and they are all inserts
    Set<String> targetKeys = new HashSet<>(target.keySet());
    targetKeys.removeAll(source.keySet());
    for (String targetKey : targetKeys) {
      outputObject.put(targetKey, createDiffNode(Operation.INSERT, null, target.get(targetKey)));
    }
    return outputObject;
  }

  private void doPrimitiveComparision(
      String key,
      Object sourceObj,
      Object targetObj,
      JSONObject outputObject,
      DiffOptions diffOptions) {
    int compareResult = 0;
    if (sourceObj instanceof String) {
      if (diffOptions.isCaseSensitiveMatch()) {
        compareResult = StringUtils.compare(sourceObj.toString(), targetObj.toString());
      } else {
        compareResult = StringUtils.compareIgnoreCase(sourceObj.toString(), targetObj.toString());
      }
    } else {
      compareResult = Objects.equals(sourceObj, targetObj) ? 0 : -1;
    }
    if(compareResult != 0){
      outputObject.put(key, createDiffNode(Operation.UPDATE, sourceObj, targetObj));
      return;
    }
    if (!diffOptions.isChangesOnly()) {
      outputObject.put(key, sourceObj);
    }
  }

  private boolean arePrimitive(Object sourceObj) {
    return ClassUtils.isPrimitiveOrWrapper(sourceObj.getClass()) || sourceObj instanceof String;
  }

  private boolean typesAreSame(Object sourceObj, Object targetObj) {
    if (sourceObj == null) return false;
    if (targetObj == null) return false;
    return Objects.equals(sourceObj.getClass(), targetObj.getClass());
  }

  private Object createDiffNode(Operation operation, Object source, Object target) {
    JSONObject diffObject = new JSONObject();
    diffObject.put("op", operation.toString().toLowerCase());
    diffObject.put("source", source == null ? JSONObject.NULL : source);
    diffObject.put("target", target == null ? JSONObject.NULL : target);
    return new JSONObject().put("diff", diffObject);
  }

  private enum Operation {
    INSERT,
    UPDATE,
    DELETE;
  }
}
