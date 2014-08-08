package com.rapleaf.jack.test_project.database_1.query;

import com.rapleaf.jack.SimpleQueryBuilder;

import com.rapleaf.jack.test_project.database_1.models.Comment;
import com.rapleaf.jack.test_project.database_1.iface.ICommentPersistence;


public class CommentQueryBuilder extends SimpleQueryBuilder<Comment> {

  public CommentQueryBuilder (ICommentPersistence caller) {
    super(caller);
  }

  public CommentQueryBuilder content(String value) {
    fieldsMap.put(Comment._Fields.content, value);
    return this;
  }

  public CommentQueryBuilder commenterId(int value) {
    fieldsMap.put(Comment._Fields.commenter_id, value);
    return this;
  }

  public CommentQueryBuilder commentedOnId(long value) {
    fieldsMap.put(Comment._Fields.commented_on_id, value);
    return this;
  }

  public CommentQueryBuilder createdAt(long value) {
    fieldsMap.put(Comment._Fields.created_at, value);
    return this;
  }
}
