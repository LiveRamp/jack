class AddJackStoreTable < ActiveRecord::Migration
  def change
    create_table :test_store do |t|
      t.integer :scope, :limit => 8
      t.string :key
      t.string :type
      t.string :value

      t.timestamps
    end

    add_index :test_store, [:scope, :key], :name => 'store_index_on_scope_key', :length => {:key => 100}
    add_index :test_store, [:scope, :value], :name => 'store_index_on_scope_value', :length => {:value => 100}
  end
end
