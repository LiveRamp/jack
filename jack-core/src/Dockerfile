# Use an official Ruby runtime as a parent image
FROM ruby:2.3.0

# Set the working directory to /app
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

RUN bundle install

# The consumer is expected to provide two additional arguments:
#   1. the path to the Jack config file
#   2. the output directory
#
# example usage: docker run -w /app -v $(pwd):/app/temp --rm jack temp/jack_config.yml temp/jack
ENTRYPOINT ["ruby", "./rb/jack.rb"]
