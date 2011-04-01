class InitialSchema < ActiveRecord::Migration
  def self.up
    # get a table that has an example of every type
    create_table :users do |t|
      t.string :handle, :null => false
      t.integer :created_at_millis, :limit => 8
      t.integer :num_posts, :null => false
      t.date :some_date
      t.datetime :some_datetime
      t.text :bio
      # t.varbinary :some_varbinary
      t.binary :some_binary
      t.float :some_float
      t.boolean :some_boolean
      # t.bytes :some_bytes
    end

    create_table :images do |t|
      t.integer :user_id
    end

    # try a belongs_to style association
    create_table :posts do |t|
      t.string :title
      t.date :posted_at_millis
      t.integer :user_id
    end
    
    # bigint primary key!
    # also, renamed associations so we can test the craziness in the models
    # create_table :comments, :id => :bigint do |t|
    create_table :comments do |t|
      t.text :content
      t.integer :commenter_id
      t.integer :commented_on_id
    end

    # # no primary key!!!
    # create_table :followers, :id => false do |t|
    #   t.integer :follower_id
    #   t.integer :followed_id
    # end
  end

  def self.down
  end
end
