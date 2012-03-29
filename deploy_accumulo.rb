#!/usr/bin/ruby

bashrc_file = "/home/#{ENV['USER']}/.bashrc"
basename = 'accumulo-1.5.0-SNAPSHOT'
accumulo_home = "/home/#{ENV['USER']}/#{basename}"
accumulo_conf = "#{accumulo_home}/conf"
accumulo_example = "#{accumulo_home}/conf/examples/1GB/standalone"
gzfile = "assemble/target/#{basename}-dist.tar.gz"

puts "Removing Accumulo Install Directory"
`rm -rf #{accumulo_home}`

puts "Uncompressing Accumulo from #{gzfile}"
`tar xvzf #{gzfile} -C /home/#{ENV['USER']}`

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

puts "Copy example configuration files"
`cp #{accumulo_example}/accumulo-env.sh #{accumulo_conf}/accumulo-env.sh`
`cp #{accumulo_example}/accumulo-metrics.xml #{accumulo_conf}/accumulo-metrics.xml`
`cp #{accumulo_example}/accumulo-site.xml #{accumulo_conf}/accumulo-site.xml`
`cp #{accumulo_example}/gc #{accumulo_conf}/gc`
`cp #{accumulo_example}/masters #{accumulo_conf}/masters`
`cp #{accumulo_example}/monitor #{accumulo_conf}/monitor`
`cp #{accumulo_example}/slaves #{accumulo_conf}/slaves`
`cp #{accumulo_example}/tracers #{accumulo_conf}/tracers`

puts "Creating write-ahead log directory"
`mkdir -p #{accumulo_home}/walogs`

#`hadoop fs -rmr /accumulo`
#`#{accumulo_home}/bin/accumulo init`
