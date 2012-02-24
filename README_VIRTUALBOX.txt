My goal was to get Accumulo running on a VirtualBox Ubuntu instance. I was 
successful using the following steps. If a line starts with $ then it is a 
command-line to execute. Note that you'll need to have sudo privilege. My 
username was 'ubuntu'. If you are using a different username, you'll 
need to change the process a little bit. I'll try to point out where.

https://issues.apache.org/jira/browse/ACCUMULO

##########
# Start a new VirtualBox instance using the Ubuntu 11.10 
# Desktop ISO with at least 4G RAM and at least 10G of 
# disk space.
##########

##########
# For verification, you can display the OS release.
##########
$ cat /etc/lsb-release
DISTRIB_ID=Ubuntu
DISTRIB_RELEASE=11.10
DISTRIB_CODENAME=oneiric
DISTRIB_DESCRIPTION="Ubuntu 11.10"

##########
# Download all of the packages you'll need. Hopefully,
# you have a fast download connection.
##########

$ sudo apt-get update
$ sudo apt-get upgrade
$ sudo apt-get install curl
$ sudo apt-get install git
$ sudo apt-get install maven2
$ sudo apt-get install openssh-server openssh-client
$ sudo apt-get install openjdk-7-jdk

##########
# Switch to the new Java. On my system, it was
# the third option (marked '2' naturally)
##########

$ sudo update-alternatives --config java

##########
# Set the JAVA_HOME variable. I took the
# time to update my .bashrc script.
##########

$ export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-i386

##########
# I stored the Accumulo source code into
# ~/workspace/accumulo. After compilation, you'll
# be working with a second Accumulo directory. By
# placing this 'original source' version into
# workspace it is nicely segregated.
##########

$ mkdir -p ~/workspace
$ cd ~/workspace
$ git clone https://github.com/apache/accumulo.git
$ cd accumulo

##########
# Now we can compile Accumulo which creates the
# accumulo-assemble-1.5.0-incubating-SNAPSHOT-dist.tar.gz
# file in the src/assemble/target directory.
#
# This step confused me because the Accumulo README
# mentions mvn assembly:single and I tried to use
# that Maven command. It is not needed, at least not
# in this situation.
##########

$ mvn package

##########
# Now we can download Cloudera's version of Hadoop. The
# first step is adding the repository. Note that oneiric 
# is not explicitly supported as of 2011-Dec-20. So I am
# using the 'maverick' repository.
##########

# Create a repository list file. Add the two indented lines
# to the new file.

$ sudo vi /etc/apt/sources.list.d/cloudera.list
  deb http://archive.cloudera.com/debian maverick-cdh3 contrib
  deb-src http://archive.cloudera.com/debian maverick-cdh3 contrib

# Add public key

$ curl -s http://archive.cloudera.com/debian/archive.key | sudo apt-key add -
$ sudo apt-get update

# Install all of the Hadoop components.

$ sudo apt-get install hadoop-0.20 
$ sudo apt-get install hadoop-0.20-namenode 
$ sudo apt-get install hadoop-0.20-datanode 
$ sudo apt-get install hadoop-0.20-secondarynamenode 
$ sudo apt-get install hadoop-0.20-jobtracker 
$ sudo apt-get install hadoop-0.20-tasktracker

# Install zookeeper. It will automatically
# start.

sudo apt-get install hadoop-zookeeper-server

##########
# As an aside, you can use Ubuntu's service
# command to control zookeeper like this:
# sudo service hadoop-zookeeper-server start
##########

##########
# Now we can configure Pseudo-Distributed hadoop
# These steps were borrowed from 
# http://hadoop.apache.org/common/docs/r0.20.2/quickstart.html
##########

# Set some environment variables. I added these to my 
# .bashrc file.

$ export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-i386
$ export HADOOP_HOME=/usr/lib/hadoop-0.20
$ export ZOOKEEPER_HOME=/usr/lib/zookeeper

$ cd $HADOOP_HOME/conf

# Create the hadoop temp directory. It should not 
# be in the /tmp directory because that directory
# disappears after each system restart. Something
# that is done a lot with virtual machines.
sudo mkdir /hadoop_tmp_dir
sudo chmod 777 hadoop_tmp_dir

# Replace the existing file with the indented lines.
$ sudo vi core-site.xml
    <?xml version="1.0"?>
    <?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
    <configuration>
      <property>
        <name>hadoop.tmp.dir</name>
        <value>/hadoop_tmp_dir</value>
      </property>
      <property>
        <name>fs.default.name</name>
        <value>hdfs://localhost:9000</value>
      </property>
    </configuration>

##########
# Notice that the dfs secondary http address is not
# the default in the XML below. I don't know what
# process was using the default, but I needed to
# change it to avoid the 'port already in use' message.
##########

# Replace the existing file with the indented lines.
$ sudo vi hdfs-site.xml
    <?xml version="1.0"?>
    <?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
    <configuration>
        <property>
          <name>dfs.secondary.http.address</name>
          <value>0.0.0.0:8002</value>
        </property>
        <property>
          <name>dfs.replication</name>
          <value>1</value>
        </property>
    </configuration>

# Replace the existing file with the indented lines.
$ sudo vi mapred-site.xml
    <?xml version="1.0"?>
    <?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
    <configuration>
      <property>
        <name>mapred.job.tracker</name>
        <value>localhost:9001</value>
      </property>
    </configuration>

# format the hadoop filesystem
$ hadoop namenode -format


##########
# Time to setup password-less ssh to localhost
##########
$ cd ~
$ ssh-keygen -t dsa -P '' -f ~/.ssh/id_dsa
$ cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys

# If you want to test that the ssh works, do this. Then exit.
$ ssh localhost

# Do some zookeeper configuration.
$ echo "maxClientCnxns=100" | sudo tee -a $ZOOKEEPER_HOME/conf/zoo.cfg

$ cd ~
$ export TAR_DIR=~/workspace/accumulo/src/assemble/target
$ tar xvzf $TAR_DIR/accumulo-1.5.0-incubating-SNAPSHOT-dist.tar.gz

# Add the following to your .bashrc file.
$ export ACCUMULO_HOME=~/accumulo-1.5.0-incubating-SNAPSHOT

$ cd $ACCUMULO_HOME/conf

###########
# I didn't see the metrics file mentioned in the README file but
# there was a complaint in a log file about its being missing.
###########

cp slaves.example slaves
cp masters.example masters
cp accumulo-env.sh.example accumulo-env.sh
cp accumulo-site.xml.example accumulo-site.xml
cp accumulo-metrics.xml.example accumulo-metrics.xml

# create the write-ahead directory.
cd ..
mkdir walogs

###########
# Configure for 4Gb RAM. I definitely recommend using more RAM 
# if you have it. Since I am using a VirtualBox instance, I don't
# have much memory to play with.
###########

# Change these two parameters to reduce memory usage.
$ vi conf/accumulo-site.xml
  tserver.memory.maps.max=256M
  tserver.cache.index.size=128M

# Change (or add) the trace.password entry if the root password is
# not the default of "secret"
  <property>
    <name>trace.password</name>
    <value>mypassword_for_root_user</value>
  </property>

# Reduce the JVM memory. I have no real idea what these should be but these
# settings work. I consider them a magic formula. :)
vi conf/accumulo-env.sh
  test -z "$ACCUMULO_TSERVER_OPTS" && export ACCUMULO_TSERVER_OPTS="${POLICY} -Xmx512m -Xms512m -Xss128k"
  test -z "$ACCUMULO_MASTER_OPTS"  && export ACCUMULO_MASTER_OPTS="${POLICY} -Xmx512m -Xms128m"
  test -z "$ACCUMULO_MONITOR_OPTS" && export ACCUMULO_MONITOR_OPTS="${POLICY} -Xmx256m -Xms128m" 
  test -z "$ACCUMULO_GC_OPTS"      && export ACCUMULO_GC_OPTS="-Xmx256m -Xms128m"
  test -z "$ACCUMULO_LOGGER_OPTS"  && export ACCUMULO_LOGGER_OPTS="-Xmx128m -Xms64m"
  test -z "$ACCUMULO_GENERAL_OPTS" && export ACCUMULO_GENERAL_OPTS="-XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=75"
  test -z "$ACCUMULO_OTHER_OPTS"   && export ACCUMULO_OTHER_OPTS="-Xmx256m -Xms128m"

#######
#######
#######
#######
# REPEAT FOR EACH RESTART
#
# Since we are working inside a virtual machine, I found that 
# some settings did not survive a shutdown or reboot. From this
# point on, repeat these command for each instance startup.
#######

# hadoop was installed as root. Therefore we need to 
# change the ownership so that your username can
# write. IF YOU ARE NOT USING 'ubuntu', CHANGE THE
# COMMAND ACCORDINGLY.

$ sudo chown -R ubuntu:ubuntu /usr/lib/hadoop-0.20
$ sudo chown -R ubuntu:ubuntu /var/run/hadoop-0.20
$ sudo chown -R ubuntu:ubuntu /var/log/hadoop-0.20

# Start hadoop. I remove the logs so that I can find errors
# faster when I iterate through configuration settings.

$ cd $HADOOP_HOME
$ rm -rf logs/*
$ bin/start-dfs.sh
$ bin/start-mapred.sh

# If desired, look at the hadoop jobs. Your output should look something
# like the intended lines.
$ jps
  4017 JobTracker
  4254 TaskTracker
  30279 Main
  9808 Jps
  3517 NameNode
  3737 DataNode

##########
# This is an optional step to prove that the NameNode is running.
# Use a web browser like Firefix if you can.
##########
$ wget http://localhost:50070/
$ cat index.html
$ rm index.html

##########
# This is an optional step to prove that the JobTracker is running.
# Use a web browser like Firefix if you can.
##########
$ wget http://localhost:50030/
$ cat index.html
$ rm index.html

##########
# This is an optional step to prove that a map-reduce job
# can be run. In other words, that hadoop is working.
##########

$ hadoop dfs -rmr input
$ hadoop fs -put $HADOOP_HOME/conf input
$ hadoop jar $HADOOP_HOME/hadoop-*-examples.jar grep input output 'dfs[a-z.]+'
$ hadoop fs -cat output/*

###########
# And now, the payoff. Let's get Accumulo to run.
###########

# Provide an instance (development) name and password (password) when asked.
$ cd $ACCUMULO_HOME
$ bin/accumulo init

# I remove the logs to make debugging easier.
rm -rf logs/*
bin/start-all.sh

##########
# This is an optional step to prove that Accumulo is running.
# Use a web browser like Firefox if you can.
##########
$ wget http://localhost:50095/
$ cat index.html
$ rm index.html

# Check the logs directory.
$ cd logs

# Look for content in .err or .out files. The file sizes should all be zero.
$ ls -l *.err *.out

# Look for error messages. Ignore messages about the missing libNativeMap file.
$ grep ERROR *

# Start the Accumulo shell. If this works, see the README file for an example 
# how to use the shell.
$ bin/accumulo shell -u root -p password

###########
# Do a little victory dance. You're now an Accumulo user!
###########

##########
# Building Accumulo Documentation
##########

$ sudo apt-get install texlive-latex-base
$ sudo apt-get install texlive-latex-extra
$ rm ./docs/accumulo_user_manual.pdf
$ mvn -Dmaven.test.skip=true prepare-package

cd docs/src/developer_manual
pdflatex developer_manual && pdflatex developer_manual && pdflatex developer_manual && pdflatex developer_manual

##########
# Reading Documentation
##########

http://incubator.apache.org/accumulo/user_manual_1.4-incubating

docs/src/developer_manual/developer_manual.pdf

$ ls -l docs/examples

##########
# Things to Try
##########

bin/accumulo org.apache.accumulo.server.util.ListInstances

WHY DOES THIS NPE?
bin/accumulo org.apache.accumulo.server.util.DumpTable batchtest1

