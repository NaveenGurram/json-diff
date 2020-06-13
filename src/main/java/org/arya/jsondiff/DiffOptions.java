package org.arya.jsondiff;

import lombok.Builder;
import lombok.Data;

/**
 *
 */
@Builder
@Data
public class DiffOptions {
  private boolean changesOnly;
  private boolean caseSensitiveMatch;
}
