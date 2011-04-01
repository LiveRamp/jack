require File.expand_path(File.dirname(__FILE__) +"/spec_helper.rb")

describe FieldDefn do 
  it "should parse :not_null as expected" do
    f = FieldDefn.new("string", :my_string, 0, {":not_null" => "true"})
    f.nullable?.should == false

    f = FieldDefn.new("string", :my_string, 0, {":not_null" => "false"})
    f.nullable?.should == true

    f = FieldDefn.new("string", :my_string, 0, {})
    f.nullable?.should == true
  end
end