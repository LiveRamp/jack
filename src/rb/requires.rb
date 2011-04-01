require 'rubygems'
require 'erb'
require "fileutils"
require "rails"


require File.expand_path(File.dirname(__FILE__) + "/association_defn")
require File.expand_path(File.dirname(__FILE__) + "/field_defn")
require File.expand_path(File.dirname(__FILE__) + "/model_defn")
require File.expand_path(File.dirname(__FILE__) + "/database_defn")
require File.expand_path(File.dirname(__FILE__) + "/project_defn")
require File.expand_path(File.dirname(__FILE__) + "/schema_rb_parser")
require File.expand_path(File.dirname(__FILE__) + "/models_dir_processor")
require File.expand_path(File.dirname(__FILE__) + "/template_processor")