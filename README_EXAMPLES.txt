
##########
# Running Accumulo Examples
##########

export EXAMPLE_JAR=lib/examples-simple-1.5.0-incubating-SNAPSHOT.jar
export EXAMPLE_PACKAGE=org.apache.accumulo.examples.simple
cd $ACCUMULO_HOME

export AINSTANCE=development
export AZOOKEEPERS=localhost
export AUSER=root
export APASSWORD=password
export AOPTIONS="$AINSTANCE $AZOOKEEPERS $AUSER $APASSWORD"

# ---------------------------
# Examples from README.batch 
# ---------------------------
# start the command-line shell.
bin/accumulo shell -u root -p password
> setauths -u root -s exampleVis
> createtable batchtest1
> exit

export TABLE=batchtest1
export START=0
export NUM=10000
export VALUE_SIZE=50
export MAX_MEMORY=20000000
export MAX_LATENCY=500
export NUM_THREADS=20
export COLUMN_VISIBILITY=exampleVis
bin/accumulo $EXAMPLE_PACKAGE.client.SequentialBatchWriter $AOPTIONS $TABLE $START $NUM $VALUE_SIZE $MAX_MEMORY $MAX_LATENCY $NUM_THREADS $COLUMN_VISIBILITY

export NUM=1000
export MIN=0
export MAX=10000
export EXPECTED_VALUE_SIZE=50
bin/accumulo $EXAMPLE_PACKAGE.client.RandomBatchScanner $AOPTIONS $TABLE $NUM $MIN $MAX $EXPECTED_VALUE_SIZE $NUM_THREADS $COLUMN_VISIBILITY

# ----------------------------
# Examples from README.bloom
# ----------------------------

### create table without bloom filter.

bin/accumulo shell -u $AUSER -p $APASSWORD
> setauths -u root -s exampleVis
> createtable bloom_test1
bloom_test1> config -t bloom_test1 -s table.compaction.major.ratio=7
bloom_test1> exit

export TABLE=bloom_test1
export NUM=1000000
export MIN=0
export MAX=1000000000
export VALUE_SIZE=50
export MAX_MEMORY=2000000
export MAX_LATENCY=60000
export NUM_THREADS=20
export COLUMN_VISIBILITY=exampleVis
# create a million records
bin/accumulo $EXAMPLE_PACKAGE.client.RandomBatchWriter -s 7 $AOPTIONS $TABLE $NUM $MIN $MAX $VALUE_SIZE $MAX_MEMORY $MAX_LATENCY $NUM_THREADS $COLUMN_VISIBILITY

bin/accumulo shell -u $AUSER -p $APASSWORD -e 'flush -t bloom_test1 -w'

bin/accumulo $EXAMPLE_PACKAGE.client.RandomBatchWriter -s 8 $AOPTIONS $TABLE $NUM $MIN $MAX $VALUE_SIZE $MAX_MEMORY $MAX_LATENCY $NUM_THREADS $COLUMN_VISIBILITY

bin/accumulo shell -u $AUSER -p $APASSWORD -e 'flush -t bloom_test1 -w'

bin/accumulo $EXAMPLE_PACKAGE.client.RandomBatchWriter -s 9 $AOPTIONS $TABLE $NUM $MIN $MAX $VALUE_SIZE $MAX_MEMORY $MAX_LATENCY $NUM_THREADS $COLUMN_VISIBILITY

### create table with bloom filter.

bin/accumulo shell -u $AUSER -p $APASSWORD
> createtable bloom_test2
bloom_test2> config -t bloom_test2 -s table.compaction.major.ratio=7
bloom_test2> config -t bloom_test2 -s table.bloom.enabled=true
bloom_test2> exit

export TABLE=bloom_test2

bin/accumulo $EXAMPLE_PACKAGE.client.RandomBatchWriter -s 7 $AOPTIONS $TABLE $NUM $MIN $MAX $VALUE_SIZE $MAX_MEMORY $MAX_LATENCY $NUM_THREADS $COLUMN_VISIBILITY

bin/accumulo shell -u $AUSER -p $APASSWORD -e 'flush -t bloom_test2 -w'

bin/accumulo $EXAMPLE_PACKAGE.client.RandomBatchWriter -s 8 $AOPTIONS $TABLE $NUM $MIN $MAX $VALUE_SIZE $MAX_MEMORY $MAX_LATENCY $NUM_THREADS $COLUMN_VISIBILITY

bin/accumulo shell -u $AUSER -p $APASSWORD -e 'flush -t bloom_test2 -w'

bin/accumulo $EXAMPLE_PACKAGE.client.RandomBatchWriter -s 9 $AOPTIONS $TABLE $NUM $MIN $MAX $VALUE_SIZE $MAX_MEMORY $MAX_LATENCY $NUM_THREADS $COLUMN_VISIBILITY

bin/accumulo shell -u $AUSER -p $APASSWORD -e 'flush -t bloom_test2 -w'

### read table without bloom filter.

export TABLE=bloom_test1
export NUM=500

# same seed, records are found.
bin/accumulo $EXAMPLE_PACKAGE.client.RandomBatchScanner -s 7 $AOPTIONS $TABLE $NUM $MIN $MAX $EXPECTED_VALUE_SIZE $NUM_THREADS $COLUMN_VISIBILITY
# different seed, no results
bin/accumulo $EXAMPLE_PACKAGE.client.RandomBatchScanner -s 8 $AOPTIONS $TABLE $NUM $MIN $MAX $EXPECTED_VALUE_SIZE $NUM_THREADS $COLUMN_VISIBILITY

### read table with bloom filter.

export TABLE=bloom_test2

bin/accumulo $EXAMPLE_PACKAGE.client.RandomBatchScanner -s 7 $AOPTIONS $TABLE $NUM $MIN $MAX $EXPECTED_VALUE_SIZE $NUM_THREADS $COLUMN_VISIBILITY

### verify the map tables

# display the table ids.
bin/accumulo shell -u $AUSER -p $APASSWORD -e 'tables -l'
# display the hdfs files associated with the table id.
hadoop fs -lsr /accumulo/tables/3
# use PrintInfo to show the fies has a bloom filter.

bin/accumulo org.apache.accumulo.core.file.rfile.PrintInfo /accumulo/tables/4/default_tablet/F000000e.rf

# ----------------------------
# Examples from README.bulkIngest
# ----------------------------

export TABLE=test_bulk
export FIRST_SPLIT=row_00000333
export SECOND_SPLIT=row_00000666
bin/accumulo $EXAMPLE_PACKAGE.mapreduce.bulk.SetupTable $AOPTIONS $TABLE $FIRST_SPLIT $SECOND_SPLIT

export START=0
export END=1000
export BULK_FILE=bulk/test_1.txt
bin/accumulo $EXAMPLE_PACKAGE.mapreduce.bulk.GenerateTestData $START $END $BULK_FILE

#
# see the file that was just created
#
hadoop fs -cat $BULK_FILE

export INPUT=bulk
export OUTPUT=tmp/bulkWork
bin/tool.sh lib/accumulo-examples-*[^c].jar $EXAMPLE_PACKAGE.mapreduce.bulk.BulkIngestExample $AOPTIONS $TABLE $INPUT $OUTPUT

bin/accumulo $EXAMPLE_PACKAGE.mapreduce.bulk.VerifyIngest $AOPTIONS $TABLE $START $END

# -------------------------------
# Examples from README.combiner
# -------------------------------

bin/accumulo shell -u $AUSER -p $APASSWORD
>createtable runners
# enter 'stat' and '10' when asked
runners> setiter -t runners -p 10 -scan -minc -majc -n decStats -class org.apache.accumulo.examples.combiner.StatsCombiner
runners> setiter -t runners -p 11 -scan -minc -majc -n hexStats -class org.apache.accumulo.examples.combiner.StatsCombiner
runners> insert 123456 name first Joe
runners> insert 123456 stat marathon 240
runners> scan
runners> insert 123456 stat marathon 230
runners> insert 123456 stat marathon 220
#
# The next scan will show the min, max, sum, and count for the 123456:stat:marathon row.
#
runners> scan
runners> insert 123456 hstat virtualMarathon 6a
runners> insert 123456 hstat virtualMarathon 6b
#
# The next scan will show the min, max, sum, and count (in hexadecimal) for the 123456:hstat:marathon row.
#
runners> scan
runners> exit


# -------------------------------
# Examples from README.constraints
# -------------------------------

bin/accumulo shell -u $AUSER -p $APASSWORD
> createtable testConstraints
testConstraints> config -t testConstraints -s table.constraint.1=org.apache.accumulo.examples.constraints.NumericValueConstraint
testConstraints> config -t testConstraints -s table.constraint.2=org.apache.accumulo.examples.constraints.AlphaNumKeyConstraint
testConstraints> insert r1 cf1 cq1 1111
testConstraints> insert r1 cf1 cq1 ABC
      Constraint Failures:
          ConstraintViolationSummary(...NumericValueConstraint, ..., violationDescription:Value is not numeric...)
testConstraints> insert r1! cf1 cq1 ABC 
      Constraint Failures:
          ConstraintViolationSummary(...NumericValueConstraint, ..., violationDescription:Value is not numeric...)
          ConstraintViolationSummary(...AlphaNumKeyConstraint, ..., violationDescription:Row was not alpha numeric...)
testConstraints> scan
    r1 cf1:cq1 []    1111
testConstraints> exit


# -------------------------------
# Examples from README.dirlist
# -------------------------------

export DIR_TABLE=dirTable
export INDEX_TABLE=indexTable
export DATA_TABLE=dataTable
export AUTHORIZATION=exampleVis
export COLUMN_VISIBILITY=exampleVis
export DATA_CHUNK_SIZE=100000
export DIR_TO_INDEX=/home/$USER/workspace

# index the directory on local disk
bin/accumulo $EXAMPLE_PACKAGE.dirlist.Ingest $AOPTIONS $DIR_TABLE $INDEX_TABLE $DATA_TABLE $COLUMN_VISIBILITY $DATA_CHUNK_SIZE $DIR_TO_INDEX

export DIR_TO_VIEW=/home/$USER/workspace/accumulo/conf
bin/accumulo $EXAMPLE_PACKAGE.dirlist.Viewer $AOPTIONS $DIR_TABLE $DATA_TABLE $AUTHORIZATION $DIR_TO_VIEW

# display information about a directory.
export DIR_TO_VIEW=/home/$USER/workspace/accumulo/conf
bin/accumulo org.apache.accumulo.examples.dirlist.QueryUtil $AOPTIONS $DIR_TABLE $COLUMN_VISIBILITY $DIR_TO_VIEW

# find files
export FILE_TO_FIND=masters.example
bin/accumulo $EXAMPLE_PACKAGE.dirlist.QueryUtil $AOPTIONS $INDEX_TABLE $COLUMN_VISIBILITY $FILE_TO_FIND -search

export TRAILING_WILDCARD="masters*"
bin/accumulo $EXAMPLE_PACKAGE.dirlist.QueryUtil $AOPTIONS $INDEX_TABLE $COLUMN_VISIBILITY $TRAILING_WILDCARD -search

export LEADING_WILDCARD="*.jar"
bin/accumulo $EXAMPLE_PACKAGE.dirlist.QueryUtil $AOPTIONS $INDEX_TABLE $COLUMN_VISIBILITY $LEADING_WILDCARD -search

export WILDCARD="commons*.jar"
bin/accumulo $EXAMPLE_PACKAGE.dirlist.QueryUtil $AOPTIONS $INDEX_TABLE $COLUMN_VISIBILITY $WILDCARD -search

# count files
export AUTHORIZATION=exampleVis
export COLUMN_VISIBILITY=exampleVis
bin/accumulo $EXAMPLE_PACKAGE.dirlist.FileCount $AOPTIONS $DIR_TABLE $AUTHORIZATION $COLUMN_VISIBILITY

# -------------------------------
# Examples from README.filedata
# -------------------------------

How is FileDataIngest used?

 * FileDataIngest - Takes a list of files and archives them into Accumulo keyed on the SHA1 hashes of the files.

# -------------------------------
# Examples from README.filter
# -------------------------------
bin/accumulo shell -u $AUSER -p $APASSWORD
> createtable filtertest
filtertest> setiter -t filtertest -scan -p 10 -n myfilter -filter

WAITING FOR JIRA TICKET RESOLUTION.

# -------------------------------
# Examples from README.helloworld
# -------------------------------

bin/accumulo shell -u $AUSER -p $APASSWORD
> createtable hellotable
hellotable> exit
export TABLE=hellotable
bin/accumulo $EXAMPLE_PACKAGE.helloworld.InsertWithBatchWriter $AINSTANCE $AZOOKEEPERS $TABLE $AUSER $APASSWORD
# insert via map-reduce
bin/accumulo $EXAMPLE_PACKAGE.helloworld.InsertWithOutputFormat $AINSTANCE $AZOOKEEPERS $TABLE $AUSER $APASSWORD
# display the records using the shell
bin/accumulo shell -u $AUSER -p $APASSWORD
> table hellotable
> scan
> exit
# display the records
bin/accumulo $EXAMPLE_PACKAGE.helloworld.ReadData $AINSTANCE $AZOOKEEPERS $TABLE $AUSER $APASSWORD


# -------------------------------
# Examples from README.mapred
# -------------------------------

hadoop fs -copyFromLocal $ACCUMULO_HOME/README wc/Accumulo.README
hadoop fs -ls wc
bin/accumulo shell -u $AUSER -p $APASSWORD
> createtable wordCount -a count=org.apache.accumulo.core.iterators.aggregation.StringSummation
> exit
export INPUT=wc
export OUTPUT=wordCount
bin/tool.sh lib/accumulo-examples-*[^c].jar $EXAMPLE_PACKAGE.mapreduce.WordCount $AINSTANCE $AZOOKEEPERS $INPUT $OUTPUT -u $AUSER -p $APASSWORD

# read the count from the accumulo table.
bin/accumulo shell -u $AUSER -p $APASSWORD
> table wordCount
wordCount> scan -b the
wordCount> exit


# -------------------------------
# Examples from README.shard
# -------------------------------

# create accumulo tables
bin/accumulo shell -u $AUSER -p $APASSWORD
> createtable shard
shard> createtable doc2term
doc2term> exit

# index some files
cd $ACCUMULO_HOME
export SHARD_TABLE=shard
export NUM_PARTITIONS=30
find src -name "*.java" | xargs bin/accumulo $EXAMPLE_PACKAGE.shard.Index $AINSTANCE $AZOOKEEPERS $SHARD_TABLE $AUSER $APASSWORD $NUM_PARTITIONS

export TERMS_TO_FIND="foo bar"
bin/accumulo $EXAMPLE_PACKAGE.shard.Query $AINSTANCE $AZOOKEEPERS $SHARD_TABLE $AUSER $APASSWORD $TERMS_TO_FIND

# populate doc2term
export DOC2TERM_TABLE=doc2term
bin/accumulo $EXAMPLE_PACKAGE.shard.Reverse $AINSTANCE $AZOOKEEPERS $SHARD_TABLE $DOC2TERM_TABLE $AUSER $APASSWORD

export NUM_TERMS=5
export ITERATION_COUNT=5
bin/accumulo org.apache.accumulo.examples.shard.ContinuousQuery $AINSTANCE $AZOOKEEPERS $SHARD_TABLE $DOC2TERM_TABLE $AUSER $APASSWORD $NUM_TERMS $ITERATION_COUNT

#####################################################################################
#####################################################################################
#####################################################################################


# ---------------------------------------------
# Other programs in client package
# ---------------------------------------------

bin/accumulo $EXAMPLE_PACKAGE.client.Flush $A_OPTIONS $TABLE

# To see all options.
bin/accumulo $EXAMPLE_PACKAGE.client.ReadWriteExample

bin/accumulo $EXAMPLE_PACKAGE.client.ReadWriteExample -i $AINSTANCE -z $AZOOKEEPERS -u $AUSER -p $APASSWORD -t $TABLE -s $COLUMN_VISIBILITY --read

bin/accumulo $EXAMPLE_PACKAGE.client.RowOperations $AOPTIONS

./src/main/java/org/apache/accumulo/examples/constraints/MaxMutationSize.java
./src/main/java/org/apache/accumulo/examples/isolation/InterferenceTest.java

