require File.expand_path(File.dirname(__FILE__) + "/spec_helper.rb")

describe SchemaRbParser do
  before(:context) do
    schema_file = File.expand_path('../../test_project/database_1/db/schema.rb', __FILE__)
    @models, @version = SchemaRbParser.parse(schema_file, ['profiles'])
    puts @models.inspect
  end

  describe 'parser output' do
    it 'includes an array of models' do
      expect(@models).to be_a Array
      expect(@models.size).to eq 6
      expect(@models.map(&:class).uniq.first).to eq ModelDefn
    end

    it 'includes a migration version' do
      expect(@version).to be_a String
      version_number = @version.to_i
      expect(version_number).to be_a Integer
      expect(version_number).to be > 0
    end

    it 'does not include ignored tables' do
      expect(@models.select{|m| m.table_name == 'profiles'}).to be_empty
    end
  end

  describe 'user model' do
    before(:context) do
      @user = @models.select{|m| m.table_name == 'users' }.first
    end

    it 'creates a model' do
      expect(@user).to be_present
    end

    it 'has correct model name' do
      expect(@user.model_name).to eq 'User'
    end

    it 'has correct migration number' do
      expect(@user.migration_number).to eq @version
    end
  end

  describe 'fields' do
    before(:context) do
      @user_fields = @models.select{|m| m.table_name == 'users' }.first.fields
      @comment_fields = @models.select{|m| m.table_name == 'comments'}.first.fields
      @lockable_fields = @models.select{|m| m.table_name == 'lockable_models'}.first.fields
    end

    it 'contains all fields' do
      expect(@user_fields.map(&:class).uniq.first).to eq FieldDefn
      expect(@user_fields.size).to eq 10
    end

    it 'can parse string column' do
      handle = @user_fields.select{|f| f.name == 'handle'}.first
      expect(handle.data_type).to eq :string
      expect(handle.args[:limit]).to eq 255
      expect(handle.args[:null]).to be_falsey
      expect(handle).not_to be_nullable
      expect(handle.default_value).to be_nil
    end

    it 'can parse text column' do
      bio = @user_fields.select{|f| f.name == 'bio'}.first
      expect(bio.data_type).to eq :text
      expect(bio.args[:limit]).to eq 65535
      expect(bio).to be_nullable
      expect(bio.default_value).to be_nil
    end

    it 'can parse integer column' do
      num_posts = @user_fields.select{|f| f.name == 'num_posts'}.first
      expect(num_posts.data_type).to eq :integer
      expect(num_posts.args[:limit]).to eq 4
      expect(num_posts.args[:null]).to be_falsey
      expect(num_posts).not_to be_nullable
      expect(num_posts.default_value).to be_nil

      created_at_millis = @user_fields.select{|f| f.name == 'created_at_millis'}.first
      expect(created_at_millis.data_type).to eq :integer
      expect(created_at_millis.args[:limit]).to eq 8
      expect(created_at_millis).to be_nullable
      expect(created_at_millis.default_value).to be_nil
    end

    it 'can parse date column' do
      some_date = @user_fields.select{|f| f.name == 'some_date'}.first
      expect(some_date.data_type).to eq :date
      expect(some_date).to be_nullable
      expect(some_date.default_value).to be_nil
    end

    it 'can parse datetime column' do
      some_datetime = @user_fields.select{|f| f.name == 'some_datetime'}.first
      expect(some_datetime.data_type).to eq :datetime
      expect(some_datetime).to be_nullable
      expect(some_datetime.default_value).to be_nil
    end

    it 'can parse binary column' do
      some_binary = @user_fields.select{|f| f.name == 'some_binary'}.first
      expect(some_binary.data_type).to eq :binary
      expect(some_binary.args[:limit]).to eq 65535
      expect(some_binary).to be_nullable
      expect(some_binary.default_value).to be_nil
    end

    it 'can parse float column' do
      some_float = @user_fields.select{|f| f.name == 'some_float'}.first
      expect(some_float.data_type).to eq :float
      expect(some_float.args[:limit]).to eq 24
      expect(some_float).to be_nullable
      expect(some_float.default_value).to be_nil
    end

    it 'can parse decimal column' do
      some_decimal = @user_fields.select{|f| f.name == 'some_decimal'}.first
      expect(some_decimal.data_type).to eq :decimal
      expect(some_decimal.args[:precision]).to eq 20
      expect(some_decimal.args[:scale]).to eq 10
      expect(some_decimal).to be_nullable
      expect(some_decimal.default_value).to be_nil
    end

    it 'can parse boolean column' do
      some_boolean = @user_fields.select{|f| f.name == 'some_boolean'}.first
      expect(some_boolean.data_type).to eq :boolean
      expect(some_boolean).to be_nullable
      expect(some_boolean.default_value).to be_nil
    end

    it 'can parse default value' do
      created_at = @comment_fields.select{|f| f.name == 'created_at'}.first
      expect(created_at.args[:default]).to eq '"1970-01-01 00:00:00"'

      lock_version = @lockable_fields.select{|f| f.name == 'lock_version'}.first
      expect(lock_version.default_value).to eq 0
    end
  end
end
