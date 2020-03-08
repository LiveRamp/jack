# frozen_string_literal: true

# Jack is designed for a version of Rails 3, which was end-of-lifed
# back in June of 2016. When Jack reads from the ActiveRecord
# schema, it does not account for various changes in the schema
# format that have been introduced subsequently:
#   1. add_index(table, columns, opts={}) was replaced by
#      t.index(columns, opts={}) within a create_table block
#   2. t.integer(column, limit: 8) was replaced by t.bigint(column, opts={})
#
# Because there aren't very many schema changes and they are relatively
# simple, it seemed to Jacob that the laziest way to offer compatibility
# between Jack and newer versions of Rails is to programmatically
# translate Rails 4+ schemas into Rails 3 schemas and then run Jack
# as usual. He at least tried to update Jack to Rails 5, but it soon
# became clear that doing so would be more of a struggle than the
# aforementioned solution, and time happens to be of the essence right now.
# Future maintainers of this project should not hesitate to update Jack
# properly as time allows.
class Rails3To5Translator
  def initialize(schema_rb_path)
    @schema_rb_path = schema_rb_path
  end

  def translate
    new_contents = ''
    index_calls_by_table_name = hash_with_empty_arrays_as_new_values
    this_table_name = nil
    nesting_degree = 0

    File.readlines(@schema_rb_path).each do |line|
      # comments and empty lines
      next if line.strip.start_with?('#') || line.strip == ''

      if line.strip.start_with?('create_table')
        # remember which table we're operating on
        match = /create_table \"(.*?)\"/.match(line)
        this_table_name = match[1]
        nesting_degree += 1

      elsif line.end_with?("do\n") || /do \|.*\|/.match(line)
        # remember how far nested we are in terms of do/end blocks
        nesting_degree += 1

      elsif line.strip.start_with?('end')
        # check if we have reached the final "end" in the file
        # and, if so, replace it with the add_index calls we need
        nesting_degree -= 1

        if nesting_degree.zero?
          index_calls_by_table_name.each do |table_name, index_calls|
            index_calls.each do |index_call|
              new_contents += "  add_index [\"#{table_name}\"], #{index_call}\n"
            end
          end
        end

      elsif /\.bigint/.match(line)
        # case 1 described in the top level comment
        line.sub!('.bigint', '.integer')
        line.sub!("\n", ", limit: 8\n")

      elsif /\.index/.match(line)
        # case 2 described in the top level comment
        method_parameters = /\.index (.*?)\n/.match(line)[1]
        index_calls_by_table_name[this_table_name].push(method_parameters)
        next
      end

      new_contents += line
    end

    new_contents
  end

  private

  def hash_with_empty_arrays_as_new_values
    Hash.new do |hash, unrecognized_key|
      hash[unrecognized_key] = []
    end
  end
end
