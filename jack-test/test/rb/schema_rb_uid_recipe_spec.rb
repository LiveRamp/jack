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
    expect(acs_inputs_fields.map(&:serial_version_uid_component)).to eq([
      "store_sizeinteger0:limit8",
      "created_atdatetime1",
      "updated_atdatetime2",
      "pathstring3:limit255",
    ])
  end

  it 'reproduces AcsInput.java attributes_serial_version_uid' do
    model = ModelDefn.new(20260609021546)
    model.fields = acs_inputs_fields
    expect(model.attributes_serial_version_uid).to eq(ACS_INPUTS_UID)
  end

  it 'digests components with MD5 + unpack(q) little-endian' do
    joined = "store_sizeinteger0:limit8created_atdatetime1updated_atdatetime2pathstring3:limit255"
    expect(Digest::MD5.digest(joined).unpack('q')[0]).to eq(ACS_INPUTS_UID)
  end
end
