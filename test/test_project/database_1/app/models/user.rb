class User < ActiveRecord::Base
  has_many :posts
  has_many :comments, :foreign_key => :commenter_id
  # has_many :followers, :foreign_key => :followed_id
  # has_many :followees, :class_name => "Follower", :foreign_key => :follower_id
  has_one :image
end
