require File.expand_path(File.dirname(__FILE__) + "/spec_helper.rb")

# Run by the Maven build (jack-test pom, run-parser-specs execution) and
# directly from jack-test with:  bundle exec rspec test/rb
#
# Pins the serialVersionUID recipe against future refactors of FieldDefn /
# ModelDefn.  The acs_inputs model's committed UID is 3515946867191399219
# (AcsInput.java); it is reproduced here from first principles.  If this test
# breaks, the wire-compat stamp of every generated Java model has moved.
describe 'serialVersionUID recipe' do
  ACS_INPUTS_UID = 3515946867191399219

  # Failure here almost never means this spec is stale — see the README section
  # "What the UID recipe spec pins" before touching the expected values.
  def uid_break_message(expected, actual)
    <<~MSG
      expected #{expected.inspect}
           got #{actual.inspect}

      The serialVersionUID derivation has changed. That re-stamps EVERY generated
      model — including models whose schema did not change — and breaks any consumer
      that holds serialized models across a deploy boundary (Spark/Hadoop shuffles,
      caches: they will throw InvalidClassException reading pre-deploy bytes).

      See README.md, "What the UID recipe spec pins". The fix is almost certainly to
      revert the change to the derivation (FieldDefn/ModelDefn), NOT to update this
      spec's expected values.
    MSG
  end

  # acs_inputs, in declaration order (ordinal), as it appears in schema.rb:
  #   t.integer  "store_size", limit: 8
  #   t.datetime "created_at"
  #   t.datetime "updated_at"
  #   t.string   "path",       limit: 255
  def acs_inputs_fields
    [
      FieldDefn.new("store_size", :integer,  0, { limit: 8 }),
      FieldDefn.new("created_at", :datetime, 1, {}),
      FieldDefn.new("updated_at", :datetime, 2, {}),
      FieldDefn.new("path",       :string,   3, { limit: 255 }),
    ]
  end

  it 'reproduces the documented per-field components' do
    expected = [
      "store_sizeinteger0:limit8",
      "created_atdatetime1",
      "updated_atdatetime2",
      "pathstring3:limit255",
    ]
    actual = acs_inputs_fields.map(&:serial_version_uid_component)
    expect(actual).to eq(expected), uid_break_message(expected, actual)
  end

  it 'reproduces AcsInput.java attributes_serial_version_uid' do
    model = ModelDefn.new(20260609021546)
    model.fields = acs_inputs_fields
    actual = model.attributes_serial_version_uid
    expect(actual).to eq(ACS_INPUTS_UID), uid_break_message(ACS_INPUTS_UID, actual)
  end

  it 'digests components with MD5 + unpack(q) little-endian' do
    joined = "store_sizeinteger0:limit8created_atdatetime1updated_atdatetime2pathstring3:limit255"
    actual = Digest::MD5.digest(joined).unpack('q')[0]
    expect(actual).to eq(ACS_INPUTS_UID), uid_break_message(ACS_INPUTS_UID, actual)
  end
end
