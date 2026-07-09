# encoding: UTF-8
# Hand-written Rails 7.1 rendition of database_1/db/schema.rb.
#
# This is the SAME schema as schema.rb, but expressed the way a Rails 7.1
# schema dumper renders it against MySQL: the `Schema[7.1]` selector, an
# underscored version number, `t.bigint` for 8-byte integers, elided
# adapter-default limits, `precision: nil` on legacy datetimes, table-level
# charset/collation/comment kwargs, `t.index` inside the create_table block,
# and a scattering of modern-only column options (unsigned/collation/comment)
# that the 4.2 dump never carried.
#
# The parser normalization in schema_rb_parser.rb must reduce this to parse
# state that is bit-identical to schema.rb (see schema_rb_parser_rails71_spec).
# Do NOT "fix" divergences here to match — a divergence means a missing rule.

ActiveRecord::Schema[7.1].define(version: 2018_05_02_013029) do

  create_table "comments", charset: "utf8mb4", collation: "utf8mb4_general_ci", force: :cascade do |t|
    t.text     "content"
    t.integer  "commenter_id",                                                  null: false
    t.bigint   "commented_on_id",                                               null: false
    t.datetime "created_at",      precision: nil, default: "1970-01-01 00:00:00", null: false
  end

  create_table "images", charset: "utf8mb4", force: :cascade do |t|
    t.integer "user_id"
  end

  create_table "lockable_models", charset: "utf8mb4", force: :cascade do |t|
    t.integer  "lock_version",                default: 0, null: false
    t.text     "message"
    t.datetime "created_at", precision: nil
    t.datetime "updated_at", precision: nil
  end

  create_table "posts", charset: "utf8mb4", force: :cascade do |t|
    t.string   "title"
    t.date     "posted_at_millis"
    t.integer  "user_id"
    t.datetime "updated_at", precision: nil
  end

  create_table "profiles", charset: "utf8mb4", force: :cascade do |t|
    t.datetime "created_at", precision: nil, null: false
    t.datetime "updated_at", precision: nil, null: false
  end

  create_table "test_store", charset: "utf8mb4", force: :cascade do |t|
    t.integer  "entry_type"
    t.bigint   "entry_scope"
    t.string   "entry_key",   limit: 2048
    t.string   "entry_value", limit: 2048
    t.datetime "created_at", precision: nil
    t.datetime "updated_at", precision: nil
    t.index ["entry_scope", "entry_key", "entry_value"], name: "store_index_on_scope_key_value", length: { "entry_scope" => nil, "entry_key" => 20, "entry_value" => 60 }
  end

  create_table "users", charset: "utf8mb4", collation: "utf8mb4_general_ci", comment: "application users", force: :cascade do |t|
    t.string   "handle",                                      null: false, collation: "utf8mb4_bin", comment: "unique handle"
    t.bigint   "created_at_millis"
    t.integer  "num_posts",                                   null: false, unsigned: true
    t.date     "some_date"
    t.datetime "some_datetime", precision: nil
    t.text     "bio"
    t.binary   "some_binary"
    t.float    "some_float"
    t.decimal  "some_decimal",  precision: 20, scale: 10
    t.boolean  "some_boolean"
  end

end
