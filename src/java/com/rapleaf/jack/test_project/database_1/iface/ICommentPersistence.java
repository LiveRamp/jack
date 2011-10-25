
/**
 * Autogenerated by Jack
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 */
/* generated from migration version 20110324000133 */
package com.rapleaf.jack.test_project.database_1.iface;

import com.rapleaf.jack.test_project.database_1.models.Comment;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.rapleaf.jack.IModelPersistence;

public interface ICommentPersistence extends IModelPersistence<Comment> {
  public Comment create(final String content, final Integer commenter_id, final Long commented_on_id, final long created_at) throws IOException;
  public Comment create(final long created_at) throws IOException;
  public Set<Comment> findByContent(String value)  throws IOException;
  public Set<Comment> findByCommenterId(Integer value)  throws IOException;
  public Set<Comment> findByCommentedOnId(Long value)  throws IOException;
  public Set<Comment> findByCreatedAt(long value)  throws IOException;
}
