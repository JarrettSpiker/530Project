#!/usr/bin/ruby
require 'optparse'
require 'open3'

# <ngram size>;<alphabet size>;<skipped spaces?>;<entopy of the ngrams>;<number of characters used to generate probabilities>;<number of characters in the encoded file>

workingDir = ""


numCharactersMin = 26
numCharactersMax = 26

ngramSizeMin = 1
ngramSizeMax = 1

skipSpaces = false

booksDir = ""
testFile = ""

expirimentTimes = 1

# ./multipleNaturalFiles.rb -d /Users/jspiker/test2 -a 2 -A 10 -b ../books -n 1 -n 10 -e 2 -t /Users/jspiker/kingarthur.txt

OptionParser.new do |options|
  options.on("-d n", "--dir=n", String, "Working dir") do |n|
    workingDir = n
  end

  options.on("-a n", "--alphabetSizeMin=n", "Alphabet Size Min") do |n|
    numCharactersMin = n.to_i
  end

  options.on("-A n", "--alphabetSizeMax=n", "Alphabet Size Max") do |n|
    numCharactersMax = n.to_i
  end

  options.on("-b n", "--books=n", String, "booksDir") do |n|
    booksDir = n
  end

  options.on("-n n", "--ngramsMin=n", "ngramSize Min") do |n|
    ngramSizeMin = n.to_i
  end

  options.on("-N n", "--ngramsMax=n", "ngramSize Max") do |n|
    ngramSizeMax = n.to_i
  end

  options.on("-t n", "--testFile=n", String, "testFile") do |n|
    testFile = n
  end
  options.on("-s", "--dontToggleSkipSpaces",  "Dont Toggle Skip Spaces") do
    skipSpaces = true
  end

  options.on("-e n", "--expirimentTimes=n", "expirimentTimes") do |n|
    expirimentTimes = n.to_i
  end

end.parse!

outputFileName = "#{workingDir}/outputFile.txt"

if(booksDir == "")
  puts("A books dir is required")
  exit
end

if(testFile == "")
  puts("A test file to encode is required")
  exit
end

if(workingDir == "")
  puts("A working dir is required")
  exit
end

classpath = "#{workingDir}/obj"

#remove everything in the working dir
puts("Cleaning the working dir")
`rm -rf #{workingDir}/*`

`mkdir #{workingDir}/workingDir`
`mkdir #{classpath}`
#compile all of the src files into an obj folder
puts("Compiling...")


Dir.glob('../src/*.java') do |java_file|
  `javac #{java_file} -d #{classpath}`
end

for ngram in ngramSizeMin..ngramSizeMax do
  for numChars in numCharactersMin..numCharactersMax do
    limit = 1
    if skipSpaces
      limit = 0
    end
    for spaces in 0..limit do
      for e in 1..expirimentTimes do
        #run the expiriment and copy the results file to the output dir
        `rm -rf #{workingDir}/workingDir/*`
        puts()
        puts()
        puts()
        puts()
        puts()

        spacesStr = ""
        if(spaces == 1)
          spacesStr = "-s"
        end
        puts("Running: ./naturalFiles.rb -d #{workingDir}/workingDir/ -a #{numChars} -b #{booksDir} -n #{ngram} -o #{outputFileName} #{spacesStr} -t #{testFile} --skipCompiling -c #{workingDir}/obj/")
        puts("_____________________________________________________________")
        Open3.popen2("./naturalFiles.rb -d #{workingDir}/workingDir/ -a #{numChars} -b #{booksDir} -n #{ngram} -o #{outputFileName} #{spacesStr} -t #{testFile} --skipCompiling -c #{workingDir}/obj/") do |stdin, stdout, wait_thr|
          stdin.close
          while line = stdout.gets
            puts line
          end
          stdout.close
        end

      end
    end
  end
end
