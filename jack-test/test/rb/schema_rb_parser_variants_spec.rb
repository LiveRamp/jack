require File.expand_path(File.dirname(__FILE__) + "/spec_helper.rb")
require 'tempfile'

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

  it 'C5: text size: matches explicit limit:, plain text refills 65535' do
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

  it 'C5: binary/blob size: matches explicit limit:, plain binary refills 65535' do
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

  it 'C1: bigint matches integer limit: 8' do
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

  it 'C2/C3/C6: elided integer/string/float limits refill to adapter defaults' do
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

  it 'C7: decimal/float string default matches numeric default' do
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

  it 'C9: dropped modern column options leave UID components unchanged' do
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

  it 'C5: an unknown size: value raises loudly' do
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
