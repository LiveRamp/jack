# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended to check this file into your version control system.

ActiveRecord::Schema.define(:version => 20151226013740) do

  create_table "comments", :force => true do |t|
    t.text     "content"
    t.integer  "commenter_id",                                                    :null => false
    t.integer  "commented_on_id", :limit => 8,                                    :null => false
    t.datetime "created_at",                   :default => '1970-01-01 00:00:00', :null => false
  end

  create_table "images", :force => true do |t|
    t.integer "user_id"
  end

  create_table "lockable_models", :force => true do |t|
    t.integer  "lock_version", :default => 0, :null => false
    t.text     "message"
    t.datetime "created_at",                  :null => false
    t.datetime "updated_at",                  :null => false
  end

  create_table "posts", :force => true do |t|
    t.string   "title"
    t.date     "posted_at_millis"
    t.integer  "user_id"
    t.datetime "updated_at"
  end

  create_table "users", :force => true do |t|
    t.string   "handle",                                                         :null => false
    t.integer  "created_at_millis", :limit => 8
    t.integer  "num_posts",                                                      :null => false
    t.date     "some_date"
    t.datetime "some_datetime"
    t.text     "bio"
    t.binary   "some_binary"
    t.float    "some_float"
    t.decimal  "some_decimal",                   :precision => 20, :scale => 10
    t.boolean  "some_boolean"
  end

end
