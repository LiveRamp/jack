class InitialSchema < ActiveRecord::Migration
  def mysql?
    ActiveRecord::Base.configurations[Rails.env]['adapter'] == 'mysql2'
  end

  def psql?
    ActiveRecord::Base.configurations[Rails.env]['adapter'] == 'postgresql'
  end

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
    
    # bigint primary key!
    # try a belongs_to style association
    create_table :posts do |t|
      t.string :title
      t.date :posted_at_millis
      t.integer :user_id
    end
    if mysql?
      execute("ALTER TABLE posts CHANGE id id bigint DEFAULT NULL auto_increment")
    elsif psql?
      execute("ALTER TABLE posts ALTER COLUMN id TYPE bigint")
    end
    
    
    # renamed associations so we can test the craziness in the models
    create_table :comments do |t|
      t.text :content
      t.integer :commenter_id, :null => false
      t.integer :commented_on_id, :limit => 8, :null => false
      t.datetime :created_at, :default => '1970-01-01 00:00:00', :null => false
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
