#!/usr/bin/ruby

today = `date -u +%Y%m%d_%H%m`.chomp!


bashrc_file = "/home/#{ENV['USER']}/.bashrc"
basename = 'accumulo-1.5.0-SNAPSHOT'
accumulo_home    = "/home/#{ENV['USER']}/#{basename}"
accumulo_link    = "/home/#{ENV['USER']}/accumulo"
accumulo_today   = "#{accumulo_home}/#{today}"
accumulo_install = "#{accumulo_today}/#{basename}"
accumulo_conf    = "#{accumulo_install}/conf"
accumulo_example = "#{accumulo_install}/conf/examples/1GB/standalone"
gzfile = "assemble/target/#{basename}-dist.tar.gz"

puts "Installation directory: #{accumulo_install}"
`mkdir -p #{accumulo_home}`
`tar xvzf #{gzfile} -C #{accumulo_today}`

# Define accumulo home directory in the bash startup profile.
export_already_defined = false
File.open(bashrc_file, "r") do |infile|
  while (line = infile.gets)
    export_already_defined = true if line =~ /export ACCUMULO_HOME/
  end
end
unless export_already_defined
 `echo export ACCUMULO_HOME=#{accumulo_home} >> #{bashrc_file}`
end

# Use the symbolic link to find previous configuration
# files. If they don't exist, then copy the examples.

puts "Copy example configuration files"
#`cp #{accumulo_example}/* conf

puts "Creating write-ahead log directory"
#`mkdir -p #{accumulo_install}/walogs`

puts "         Symbolic link: #{accumulo_link}"
`rm -f #{accumulo_link}`
`ln -s #{accumulo_install} #{accumulo_link}`

#`hadoop fs -rmr /accumulo`
#`#{accumulo_home}/bin/accumulo init`
