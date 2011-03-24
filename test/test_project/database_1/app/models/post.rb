class Post < ActiveRecord::Base
  belongs_to :user
  has_many :comments, :foreign_key => :commented_on_id
end
