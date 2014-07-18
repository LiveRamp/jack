package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.AbstractModelQuery;

import com.rapleaf.jack.test_project.database_1.models.Comment;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;


public class CommentQuery extends AbstractModelQuery<Comment> {

  public CommentQuery (ICommentPersistence caller) {
    super(caller);
  }

  public CommentQuery content(String value) {
    fieldsMap.put(Comment._Fields.content, value);
    return this;
  }

  public CommentQuery commenter_id(int value) {
    fieldsMap.put(Comment._Fields.commenter_id, value);
    return this;
  }

  public CommentQuery commented_on_id(long value) {
    fieldsMap.put(Comment._Fields.commented_on_id, value);
    return this;
  }

  public CommentQuery created_at(long value) {
    fieldsMap.put(Comment._Fields.created_at, value);
    return this;
  }
}
