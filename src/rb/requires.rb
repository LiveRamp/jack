require 'rubygems'
require 'erb'
require "fileutils"

# Set up gems listed in the Gemfile.
gemfile = ARGV[0]
begin
  ENV['BUNDLE_GEMFILE'] = gemfile
  require 'bundler'
  Bundler.setup
rescue Bundler::GemNotFound => e
  STDERR.puts e.message
  STDERR.puts "Try running `bundle install`."
  exit!
end if File.exist?(gemfile)


require 'rails/all'



require File.expand_path(File.dirname(__FILE__) + "/association_defn")
require File.expand_path(File.dirname(__FILE__) + "/field_defn")
require File.expand_path(File.dirname(__FILE__) + "/model_defn")
require File.expand_path(File.dirname(__FILE__) + "/database_defn")
require File.expand_path(File.dirname(__FILE__) + "/project_defn")
require File.expand_path(File.dirname(__FILE__) + "/schema_rb_parser")
require File.expand_path(File.dirname(__FILE__) + "/models_dir_processor")
require File.expand_path(File.dirname(__FILE__) + "/template_processor")