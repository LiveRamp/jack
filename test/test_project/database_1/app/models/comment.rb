class Comment < ActiveRecord::Base
  # string-style foreign key specification
  belongs_to :user, :foreign_key => "commenter_id"
  belongs_to :post, :foreing_key => "commented_on_id"
end
