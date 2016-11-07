# Load the rails application
require File.expand_path('../application', __FILE__)

# Initialize the rails application
TestProject::Application.initialize!

# This can be removed once this test project is upgraded to rails 4
require File.expand_path('../../lib/patches/57key.rb', __FILE__)
