
##########
# Update BASH Configuration
##########

$ cd ~
$ echo "export ZOOKEEPER_HOME=/usr/lib/zookeeper-3.4.3" >> ~/.bashrc
$ source .bashrc

##########
# Download and install zookeeper
##########

$ wget http://apache.petsads.us//zookeeper/zookeeper-3.4.3/zookeeper-3.4.3.tar.gz
$ tar xvfz zookeeper-3.4.3.tar.gz
$ sudo mv zookeeper-3.4.3 /usr/lib
$ rm zookeeper-3.4.3.tar.gz
$ cp $ZOOKEEPER_HOME/conf/zoo_sample.cfg $ZOOKEEPER_HOME/conf/zoo.cfg

##########
# Edit $ZOOKEEPER_HOME/conf/zoo.cfg
#
#  Add "maxClientCnxns=100" to the end of the file.
#  Change dataDir to /var/local/zookeeper
##########

$ mkdir /var/local/zookeeper


$ $ZOOKEEPER_HOME/bin/zkServer.sh start

##########
# Test
##########

$ $ZOOKEEPER_HOME/bin/zkCli.sh -server 127.0.0.1:2181
] help
] create /zk_test my_data
] ls /
] get /zk_test
] delete /zk_test
] quit

