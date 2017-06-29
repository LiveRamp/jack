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
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20170607113609) do

  create_table "comments", force: :cascade do |t|
    t.text     "content",         limit: 65535
    t.integer  "commenter_id",    limit: 4,                                     null: false
    t.integer  "commented_on_id", limit: 8,                                     null: false
    t.datetime "created_at",                    default: '1970-01-01 00:00:00', null: false
  end

  create_table "images", force: :cascade do |t|
    t.integer "user_id", limit: 4
  end

  create_table "lockable_models", force: :cascade do |t|
    t.integer  "lock_version", limit: 4,     default: 0, null: false
    t.text     "message",      limit: 65535
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "posts", force: :cascade do |t|
    t.string   "title",            limit: 255
    t.date     "posted_at_millis"
    t.integer  "user_id",          limit: 4
    t.datetime "updated_at"
  end

  create_table "test_store", force: :cascade do |t|
    t.integer  "scope",      limit: 8
    t.string   "key",        limit: 255
    t.string   "type",       limit: 255
    t.string   "value",      limit: 255
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "test_store", ["scope", "key", "value"], name: "store_index_on_scope_key_value", length: {"scope"=>nil, "key"=>20, "value"=>60}, using: :btree

  create_table "users", force: :cascade do |t|
    t.string   "handle",            limit: 255,                             null: false
    t.integer  "created_at_millis", limit: 8
    t.integer  "num_posts",         limit: 4,                               null: false
    t.date     "some_date"
    t.datetime "some_datetime"
    t.text     "bio",               limit: 65535
    t.binary   "some_binary",       limit: 65535
    t.float    "some_float",        limit: 24
    t.decimal  "some_decimal",                    precision: 20, scale: 10
    t.boolean  "some_boolean"
  end

end
