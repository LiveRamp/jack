class CreateLockableModels < ActiveRecord::Migration
  def self.up
    create_table :lockable_models do |t|
      t.integer :lock_version, :null => false, :default => 0
      t.text :message

      t.timestamps
    end
  end

  def self.down
    drop_table :lockable_models
  end
end
