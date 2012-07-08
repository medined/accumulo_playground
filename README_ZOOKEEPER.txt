
########
# Create the zookeeper user
########

$ su - root
$ addgroup zookeeper 
$ adduser --ingroup zookeeper zookeeper
$ su - zookeeper
$ ssh-keygen -t rsa -P ""
$ cat $HOME/.ssh/id_rsa.pub >> $HOME/.ssh/authorized_keys
$ chmod 600 ~/.ssh/authorized_keys
$ ssh localhost
$ exit

##########
# Update BASH Configuration (.bashrc)
##########

export DEFAULT_PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-i386
export ZOOKEEPER_HOME=/usr/local/zookeeper
export PATH=$JAVA_HOME/bin:$ZOOKEEPER_HOME/bin:$DEFAULT_PATH

##########
# Download and install zookeeper
##########

$ sudu su -
$ cd /usr/local
$ wget http://apache.petsads.us//zookeeper/zookeeper-3.4.3/zookeeper-3.4.3.tar.gz
$ tar xvfz zookeeper-3.4.3.tar.gz
$ sudo mv zookeeper-3.4.3 /usr/local
$ rm zookeeper-3.4.3.tar.gz
$ chown -R zookeeper:zookeeper zookeeper-3.4.3
$ ln -s zookeeper-3.4.3 zookeeper
$ exit
$ cp $ZOOKEEPER_HOME/conf/zoo_sample.cfg $ZOOKEEPER_HOME/conf/zoo.cfg

##########
# Edit $ZOOKEEPER_HOME/conf/zoo.cfg
#
#  Add "maxClientCnxns=100" to the end of the file.
#  Change dataDir to /var/local/zookeeper
##########

$ sudu su -
$ mkdir /var/local/zookeeper
$ chown  zookeeper:zookeeper /var/local/zookeeper
$ exit

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

