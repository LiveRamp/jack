require File.expand_path(File.dirname(__FILE__) + "/spec_helper.rb")
require 'tempfile'

# NOT run by the Maven build/CI — run manually from jack-test:
#   bundle exec rspec test/rb
#
# Per-rule equivalence checks: for each normalization rule, a Rails 7.1 dump
# form and its Rails 4.2 canonical form must produce identical
# serial_version_uid_component values (and therefore identical UIDs) for every
# column.  These are small standalone schemas parsed in isolation.
describe 'SchemaRbParser normalization variants' do
  # Parse a schema.rb body (a full ActiveRecord::Schema.define block) from a
  # temp file and return the single table's [name, component] pairs.
  def components_for(body)
    Tempfile.create(['variant', '.rb']) do |f|
      f.write(body)
      f.flush
      models, _version = SchemaRbParser.parse(f.path, [])
      models.first.fields.map { |fd| [fd.name, fd.serial_version_uid_component] }
    end
  end

  # Assert two schema bodies yield identical per-column UID components.
  def expect_equivalent(legacy, modern)
    expect(components_for(modern)).to eq(components_for(legacy))
  end

  it 'text size: matches explicit limit:, plain text refills 65535' do
    legacy = <<~RUBY
      ActiveRecord::Schema.define(version: 1) do
        create_table "t", force: :cascade do |t|
          t.text "tiny_t",  limit: 255
          t.text "med_t",   limit: 16777215
          t.text "long_t",  limit: 4294967295
          t.text "plain_t"
        end
      end
    RUBY
    modern = <<~RUBY
      ActiveRecord::Schema[7.1].define(version: 1) do
        create_table "t", charset: "utf8mb4", force: :cascade do |t|
          t.text "tiny_t",  size: :tiny
          t.text "med_t",   size: :medium
          t.text "long_t",  size: :long
          t.text "plain_t"
        end
      end
    RUBY
    expect_equivalent(legacy, modern)
  end

  it 'binary/blob size: matches explicit limit:, plain binary refills 65535' do
    legacy = <<~RUBY
      ActiveRecord::Schema.define(version: 1) do
        create_table "t", force: :cascade do |t|
          t.binary "med_b",   limit: 16777215
          t.binary "long_b",  limit: 4294967295
          t.binary "plain_b"
        end
      end
    RUBY
    modern = <<~RUBY
      ActiveRecord::Schema[7.1].define(version: 1) do
        create_table "t", force: :cascade do |t|
          t.binary "med_b",   size: :medium
          t.binary "long_b",  size: :long
          t.binary "plain_b"
        end
      end
    RUBY
    expect_equivalent(legacy, modern)
  end

  it 'bigint matches integer limit: 8' do
    legacy = <<~RUBY
      ActiveRecord::Schema.define(version: 1) do
        create_table "t", force: :cascade do |t|
          t.integer "big_a", limit: 8
          t.integer "big_b", limit: 8, null: false
        end
      end
    RUBY
    modern = <<~RUBY
      ActiveRecord::Schema[7.1].define(version: 1) do
        create_table "t", force: :cascade do |t|
          t.bigint "big_a"
          t.bigint "big_b", null: false
        end
      end
    RUBY
    expect_equivalent(legacy, modern)
  end

  it 'elided integer/string/float limits refill to adapter defaults' do
    legacy = <<~RUBY
      ActiveRecord::Schema.define(version: 1) do
        create_table "t", force: :cascade do |t|
          t.integer "i", limit: 4
          t.string  "s", limit: 255
          t.float   "f", limit: 24
        end
      end
    RUBY
    modern = <<~RUBY
      ActiveRecord::Schema[7.1].define(version: 1) do
        create_table "t", force: :cascade do |t|
          t.integer "i"
          t.string  "s"
          t.float   "f"
        end
      end
    RUBY
    expect_equivalent(legacy, modern)
  end

  it 'decimal/float string default matches numeric default' do
    legacy = <<~RUBY
      ActiveRecord::Schema.define(version: 1) do
        create_table "t", force: :cascade do |t|
          t.decimal "d", precision: 10, scale: 2, default: 0.0
          t.float   "f", limit: 24,               default: 1.5
        end
      end
    RUBY
    modern = <<~RUBY
      ActiveRecord::Schema[7.1].define(version: 1) do
        create_table "t", force: :cascade do |t|
          t.decimal "d", precision: 10, scale: 2, default: "0.0"
          t.float   "f",                          default: "1.5"
        end
      end
    RUBY
    expect_equivalent(legacy, modern)
  end

  it 'dropped modern column options leave UID components unchanged' do
    legacy = <<~RUBY
      ActiveRecord::Schema.define(version: 1) do
        create_table "t", force: :cascade do |t|
          t.integer "n", limit: 4,   null: false
          t.string  "s", limit: 255
          t.string  "c", limit: 255
        end
      end
    RUBY
    modern = <<~RUBY
      ActiveRecord::Schema[7.1].define(version: 1) do
        create_table "t", charset: "utf8mb4", collation: "utf8mb4_general_ci", comment: "tbl", force: :cascade do |t|
          t.integer "n", null: false, unsigned: true
          t.string  "s", collation: "utf8mb4_bin"
          t.string  "c", comment: "a comment"
        end
      end
    RUBY
    expect_equivalent(legacy, modern)
  end

  it 't.timestamp normalizes to datetime (MySQL TIMESTAMP dumped as datetime by 4.2)' do
    legacy = <<~RUBY
      ActiveRecord::Schema.define(version: 1) do
        create_table "t", force: :cascade do |t|
          t.datetime "a"
          t.datetime "b", null: false
        end
      end
    RUBY
    modern = <<~RUBY
      ActiveRecord::Schema[7.1].define(version: 1) do
        create_table "t", charset: "utf8", force: :cascade do |t|
          t.timestamp "a", precision: nil
          t.timestamp "b", precision: nil, null: false
        end
      end
    RUBY
    expect_equivalent(legacy, modern)
  end

  it 'expression (lambda) defaults are dropped to match the 4.2 dump' do
    # Rails >= 5 captures CURRENT_TIMESTAMP-style defaults as a lambda; the 4.2
    # dumper never captured them, so the committed Java carries no such default.
    legacy = <<~RUBY
      ActiveRecord::Schema.define(version: 1) do
        create_table "t", force: :cascade do |t|
          t.datetime "created_at", null: false
          t.datetime "updated_at", null: false
        end
      end
    RUBY
    modern = <<~RUBY
      ActiveRecord::Schema[7.1].define(version: 1) do
        create_table "t", charset: "utf8", force: :cascade do |t|
          t.datetime  "created_at", precision: nil, default: -> { "CURRENT_TIMESTAMP" }, null: false
          t.timestamp "updated_at", precision: nil, default: -> { "CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" }, null: false
        end
      end
    RUBY
    expect_equivalent(legacy, modern)
  end

  it 'modern table-level options (primary_key/options/charset) do not crash' do
    # Composite-PK partitioned table: `id: false` + explicit "id" column in 4.2,
    # `primary_key: [...]` + options string in 7.1. The "id" column is forbidden
    # either way, so both reduce to the same non-id columns.
    legacy = <<~RUBY
      ActiveRecord::Schema.define(version: 1) do
        create_table "t", id: false, force: :cascade do |t|
          t.integer "id",  limit: 8, null: false
          t.integer "fk",  limit: 8, null: false
        end
      end
    RUBY
    modern = <<~RUBY
      ActiveRecord::Schema[7.1].define(version: 1) do
        create_table "t", primary_key: ["id", "fk"], charset: "latin1", options: "ENGINE=InnoDB", force: :cascade do |t|
          t.bigint "id", null: false, auto_increment: true
          t.bigint "fk", null: false
        end
      end
    RUBY
    expect_equivalent(legacy, modern)
  end

  it 'check constraints (dumped by MySQL >= 8.0) are ignored' do
    legacy = <<~RUBY
      ActiveRecord::Schema.define(version: 1) do
        create_table "t", force: :cascade do |t|
          t.string "kind", limit: 255
        end
      end
    RUBY
    modern = <<~RUBY
      ActiveRecord::Schema[7.1].define(version: 1) do
        create_table "t", charset: "utf8mb3", force: :cascade do |t|
          t.string "kind"
          t.check_constraint "\`kind\` in (_utf8mb3'a',_utf8mb3'b')", name: "kind_values"
        end
      end
    RUBY
    expect_equivalent(legacy, modern)
  end

  it 'an unknown size: value raises loudly' do
    body = <<~RUBY
      ActiveRecord::Schema[7.1].define(version: 1) do
        create_table "t", force: :cascade do |t|
          t.text "x", size: :gigantic
        end
      end
    RUBY
    expect { components_for(body) }.to raise_error(/unknown size/)
  end
end
