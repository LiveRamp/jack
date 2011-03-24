class Follower < ActiveRecord::Base
  belongs_to :follower, :foreign_key => "follower_id"
  belongs_to :followed, :class_name => "Follower", :foreign_key => "followed_id"
end
