class AddJackStoreTable < ActiveRecord::Migration
  def change
    create_table :test_store do |t|
      t.string :scope
      t.string :key
      t.string :type
      t.string :value

      t.timestamps
    end

    add_index :test_store, [:scope, :key], :name => 'store_index_on_scope_key'
    add_index :test_store, [:scope, :value], :name => 'store_index_on_scope_value'
  end
end
