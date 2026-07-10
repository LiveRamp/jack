require File.expand_path(File.dirname(__FILE__) + "/spec_helper.rb")

# Run by the Maven build (jack-test pom, run-parser-specs execution) and
# directly from jack-test with:  bundle exec rspec test/rb
#
# Proves that a Rails 7.1-dialect schema dump normalizes to parse state that is
# bit-identical to the equivalent Rails 4.2 dump — same version, same per-model
# serialVersionUID inputs, same field args — and therefore produces byte-
# identical generated Java.  The two fixtures (schema.rb and schema_rails71.rb)
# describe the same database in the two dialects.
describe SchemaRbParser do
  def full_state(models)
    models.sort_by(&:table_name).map do |m|
      [
        m.table_name,
        m.migration_number,
        m.attributes_serial_version_uid,
        m.fields.map do |f|
          [f.name, f.data_type, f.ordinal, f.args, f.default_value, f.serial_version_uid_component]
        end
      ]
    end
  end

  before(:context) do
    base    = File.expand_path('../../test_project/database_1/db', __FILE__)
    ignored = ['profiles']
    @models_42, @version_42 = SchemaRbParser.parse("#{base}/schema.rb", ignored)
    @models_71, @version_71 = SchemaRbParser.parse("#{base}/schema_rails71.rb", ignored)
  end

  it 'parses the same version from both dialects' do
    expect(@version_71).to eq @version_42
    expect(@version_42).to eq '20180502013029'
  end

  it 'produces the same set of models' do
    expect(@models_71.map(&:table_name).sort).to eq @models_42.map(&:table_name).sort
    expect(@models_42).not_to be_empty
  end

  it 'produces bit-identical per-model parse state' do
    expect(full_state(@models_71)).to eq full_state(@models_42)
  end

  it 'produces identical attributes_serial_version_uid per model' do
    uids_42 = @models_42.sort_by(&:table_name).map { |m| [m.table_name, m.attributes_serial_version_uid] }
    uids_71 = @models_71.sort_by(&:table_name).map { |m| [m.table_name, m.attributes_serial_version_uid] }
    expect(uids_71).to eq uids_42
  end
end
