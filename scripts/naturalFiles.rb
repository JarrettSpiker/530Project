#!/usr/bin/ruby
require 'optparse'
require 'open3'

# <ngram size>;<alphabet size>;<skipped spaces?>;<entopy of the ngrams>;<number of characters used to generate probabilities>;<number of characters in the encoded file>

workingDir = ""

numCharacters = 26
skipSpaces = false
ngramSize = 1

booksDir = ""
testFile = ""

outputFileName = ""

skipCompiling = false
classpath = ""

# ./naturalFiles.rb -d /Users/jspiker/test2 -a 5 -b ../books -n 4 -t ../books/lesMis.txt -o /Users/jspiker/naturalFilesOutput.txt --skipSpaces

OptionParser.new do |options|
  options.on("-d n", "--dir=n", String, "Working dir") do |n|
    workingDir = n
  end

  options.on("-a n", "--alphabetSize=n", "Alphabet Size") do |n|
    numCharacters = n.to_i
  end

  options.on("-b n", "--books=n", String, "booksDir") do |n|
    booksDir = n
  end

  options.on("-n n", "--ngrams=n", "ngramSize") do |n|
    ngramSize = n
  end

  options.on("-t n", "--testFile=n", String, "testFile") do |n|
    testFile = n
  end

  options.on("-o n", "--outputFile=n", String, "Output File") do |n|
    outputFileName = n
  end

  options.on("-s", "--skipSpaces",  "Skip Spaces") do
    skipSpaces = true
  end

  options.on("-z", "--skipCompiling",  "Skip Compiling") do
    skipCompiling = true
  end

  options.on("-c n", "--classpath=n", String,  "classpath") do |n|
    classpath = n
  end



end.parse!

if(classpath == "")
  classpath = "#{workingDir}/obj/"
end

if(outputFileName == "unknown")
  outputFileName = workingDir + "/output.txt"
end

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

#remove everything in the working dir
puts("Cleaning the working dir")
`rm -rf #{workingDir}/*`


if(!skipCompiling)
  `mkdir #{classpath}`
  #compile all of the src files into an obj folder
  puts("Compiling...")


  Dir.glob('../src/*.java') do |java_file|
    `javac #{java_file} -d #{classpath}`
  end
end

#copy and process the test file

`mkdir #{workingDir}/sample`
`cp #{testFile} #{workingDir}/sample/`

Dir.glob("#{workingDir}/sample/*.txt") do |sample_file|
  `mv #{sample_file} #{workingDir}/sample/sample.txt`
end

puts("Running: java -cp #{classpath} ProcessFiles")
Open3.popen2("java -Xms6G -Xmx6G -cp #{classpath} ProcessFiles") do |stdin, stdout, wait_thr|
  stdin.puts("#{workingDir}/sample/")
  puts("#{workingDir}/sample/")
  stdin.puts(numCharacters)
  puts(numCharacters)
  if(skipSpaces)
    stdin.puts("y")
    puts("y")
  else
    stdin.puts("n")
    puts("n")
  end
  stdin.close
  while line = stdout.gets
    puts line
  end
  stdout.close
end

sampleSize = File.read("#{workingDir}/sample/sample.proc.txt").length
learningSize = 0

outputFile = File.open(outputFileName ,"a+")


`mkdir #{workingDir}/preBooks`
`mkdir #{workingDir}/books`
`cp #{booksDir}/*.txt #{workingDir}/preBooks/`

#Process the books directory
puts("Running: java -cp #{classpath} ProcessFiles")
Open3.popen2("java -Xms6G -Xmx6G -cp #{classpath} ProcessFiles") do |stdin, stdout, wait_thr|
  stdin.puts("#{workingDir}/preBooks")
  puts("#{workingDir}/preBooks")
  stdin.puts(numCharacters)
  puts(numCharacters)
  if(skipSpaces)
    stdin.puts("y")
    puts("y")
  else
    stdin.puts("n")
    puts("n")
  end
  stdin.close
  while line = stdout.gets
    puts line
  end
  stdout.close
end


counter = 0
Dir.glob("#{workingDir}/preBooks/*.proc.txt") do |book|
    `cp #{book} #{workingDir}/books/`

    learningSize = learningSize + File.read("#{book}").length


    puts("Running: java -cp #{classpath} GenerateProbabilities")
    Open3.popen2("java -Xms6G -Xmx6G -cp #{classpath} GenerateProbabilities") do |stdin, stdout, wait_thr|
      stdin.puts("#{workingDir}/books")
      puts("#{workingDir}/books")
      stdin.puts(ngramSize)
      puts(ngramSize)
      stdin.puts("#{workingDir}/probs#{counter}.txt")
      puts("#{workingDir}/probs#{counter}.txt")
      stdin.close
      while line = stdout.gets
        puts line
      end
      stdout.close
    end

    entropy = 0

    #Find the entrpy of the ngrams
    ngramEntropy = ""
    puts("Running: java -cp #{classpath} FindEntropy")
    Open3.popen2("java -Xms6G -Xmx6G -cp #{classpath} FindEntropy") do |stdin, stdout, wait_thr|
      stdin.puts("#{workingDir}/probs#{counter}.txt")
      puts("#{workingDir}/probs#{counter}.txt")
      stdin.close
      lastLine = ""
      while line = stdout.gets
        puts line
        lastLine = line
      end
      stdout.close
      entropy = lastLine.strip
    end

    puts("Running: java -cp #{classpath} GenerateCoding")
    Open3.popen2("java -Xms6G -Xmx6G -cp #{classpath} GenerateCoding") do |stdin, stdout, wait_thr|
      stdin.puts("#{workingDir}/probs#{counter}.txt")
      puts("#{workingDir}/probs#{counter}.txt")
      stdin.puts("#{workingDir}/coding#{counter}.txt")
      puts("#{workingDir}/coding#{counter}.txt")
      stdin.close
      while line = stdout.gets
        puts line
      end
      stdout.close
    end

    puts("Running: java -cp #{classpath} FindCodeLength")
    Open3.popen2("java -Xms6G -Xmx6G -cp #{classpath} FindCodeLength") do |stdin, stdout, wait_thr|
      stdin.puts("#{workingDir}/sample/sample.proc.txt")
      puts("#{workingDir}/sample/sample.proc.txt")
      stdin.puts("#{workingDir}/coding#{counter}.txt")
      puts("#{workingDir}/coding#{counter}.txt")
      stdin.close
      lastLine = ""
      while line = stdout.gets
        puts line
        lastLine = line
      end
      lastLine = lastLine.strip
      stdout.close
      skipped = "n"
      if(skipSpaces)
        skipped = "y"
      end
      outputFile.puts(ngramSize.to_s + ";" + numCharacters.to_s + ";" + skipped + ";" + entropy.to_s + ";" + learningSize.to_s + ";" + sampleSize.to_s + ";" + lastLine )
    end

    counter = counter + 1
end
