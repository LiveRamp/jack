class Follower < ActiveRecord::Base
  belongs_to :follower, :foreign_key => "follower_id"
  belongs_to :followee, :class_name => "Followers", :foreign_key => "followee_id"
end
