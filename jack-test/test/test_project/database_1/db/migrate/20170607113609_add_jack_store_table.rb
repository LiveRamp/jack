class AddJackStoreTable < ActiveRecord::Migration
  def change
    create_table :test_store do |t|
      t.integer :entry_type,  :limit => 4
      t.integer :entry_scope, :limit => 8
      t.string  :entry_key,   :limit => 2048
      t.string  :entry_value, :limit => 2048

      t.timestamps
    end

    add_index :test_store, [:entry_scope, :entry_key, :entry_value], :name => 'store_index_on_scope_key_value', :length => {:entry_key => 20, :entry_value => 60}
  end
end
