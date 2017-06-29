class AddJackStoreTable < ActiveRecord::Migration
  def change
    create_table :test_store do |t|
      t.integer :scope, :limit => 8
      t.string :key
      t.string :type
      t.string :value

      t.timestamps
    end

    add_index :test_store, [:scope, :key, :value], :name => 'store_index_on_scope_key_value', :length => {:key => 20, :value => 60}
  end
end
