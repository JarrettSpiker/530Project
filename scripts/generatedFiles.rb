#!/usr/bin/ruby
require 'optparse'
require 'open3'

directory = "/Users/jspiker/temp/"

factor = 1
ngramRange = 1
alphabetSize = 5

learningSampleMin = 0
learningSampleMax = 0
learningSampleIncrement = -1

testingSampleMin = 0
testingSampleMax = 0
testingSampleIncrement = -1

outputFileName = "unknown"

skipCompiling = false
classpath = "unknown"

OptionParser.new do |options|
  options.on("-d n", "--dir=n", String, "Working dir") do |n|
    directory = n
  end

  options.on("-a n", "--alphabetSize=n", "Alphabet Size") do |n|
    alphabetSize = n.to_i
  end

  options.on("-f f", "--factor=f", "Factor") do |f|
    factor = f.to_i
  end

  options.on("-r n", "--range=n", "N-gram range") do |n|
    ngramRange = n.to_i
  end

  options.on("-m n", "--learningSampleMin=n", "Leaning Sample Minimum") do |n|
    learningSampleMin = n.to_i
  end


  options.on("-l n", "--learningSampleMax=n", "Learning Sample Maximum") do |n|
    learningSampleMax = n.to_i
  end

  options.on("-i n", "--learningIncrement=n", "Learning Sample Increment") do |n|
    learningSampleIncrement = n.to_i
  end

  options.on("-M n", "--testSampleMin=n", "Testing Sample Minimum") do |n|
    testingSampleMin = n.to_i
  end

  options.on("-L n", "--testSampleMax=n", "Testing Sample Maximum") do |n|
    testingSampleMax = n.to_i
  end

  options.on("-I n", "--testIncrement=n", "Testing Sample Increment") do |n|
    testingSampleIncrement = n.to_i
  end

  options.on("-o n", "--outputFile=n", String, "Output File") do |n|
    outputFileName = n
  end

  options.on("-z", "--skipCompiling",  "Skip Compiling") do
    skipCompiling = true
  end

  options.on("-c n", "--classpath=n", String,  "classpath") do |n|
    classpath = n
  end
end.parse!

if(classpath == "unknown")
  classpath = "#{directory}/obj/"
end

if(learningSampleMin == -1)
  learningSampleMin = learningSampleMax
  if(learningSampleIncrement == -1)
    learningSampleIncrement = 1
  end
end

if(learningSampleIncrement == -1)
  learningSampleIncrement = learningSampleMax - learningSampleMin
end

if(testingSampleMin == -1)
  testingSampleMin = learningSampleMax
  if(testingSampleIncrement == -1)
    testingSampleIncrement = 1
  end
end

if(testingSampleIncrement == -1)
  testingSampleIncrement = testingSampleMax - testingSampleMin
end

if(outputFileName == "unknown")
  outputFileName = directory + "/output.txt"
end

#remove everything in the working dir
puts("Cleaning the working dir")
`rm -rf #{directory}/*`


if(!skipCompiling)
  `mkdir #{classpath}`
  #compile all of the src files into an obj folder
  puts("Compiling...")


  Dir.glob('../src/*.java') do |java_file|
    `javac #{java_file} -d #{classpath}`
  end
end

outputFile = File.open(outputFileName ,"a+")
#outputFile.puts("Data for generated language")
#outputFile.puts("Language stats:")
#outputFile.puts("Factor : "  +factor.to_s)
#outputFile.puts("N-gram range : " + ngramRange.to_s)
#outputFile.puts("Alphabet Size: " + alphabetSize.to_s)
#outputFile.puts("-------------------------")
#outputFile.puts()

#create the distribution
`mkdir #{directory}/probs/`

puts("Running: java -cp #{classpath} CreateDistribution")
Open3.popen2("java -Xms6G -Xmx6G -cp #{classpath} CreateDistribution") do |stdin, stdout, wait_thr|
  stdin.puts("#{directory}/probs/")
  puts("#{directory}/probs/")
  stdin.puts(alphabetSize)
  puts(alphabetSize)
  stdin.puts(ngramRange)
  puts(ngramRange)
  stdin.puts(factor)
  puts(factor)
  stdin.close
  while line = stdout.gets
    puts line
  end
  stdout.close

end

#Find the entrpy of the alphabet
alphabetEntropy = ""
puts("Running: java -cp #{classpath} FindEntropy")
Open3.popen2("java -Xms6G -Xmx6G -cp #{classpath} FindEntropy") do |stdin, stdout, wait_thr|
  stdin.puts("#{directory}/probs/probs1.txt")
  puts("#{directory}/probs/probs1.txt")
  stdin.close
  lastLine = ""
  while line = stdout.gets
    puts line
    lastLine = line
  end
  stdout.close
  alphabetEntropy = lastLine.strip

end

#Find the entrpy of the ngrams
ngramEntropy = ""
puts("Running: java -cp #{classpath} FindEntropy")
Open3.popen2("java -Xms6G -Xmx6G -cp #{classpath} FindEntropy") do |stdin, stdout, wait_thr|
  stdin.puts("#{directory}/probs/probs#{ngramRange}.txt")
  puts("#{directory}/probs/probs#{ngramRange}.txt")
  stdin.close
  lastLine = ""
  while line = stdout.gets
    puts line
    lastLine = line
  end
  stdout.close
  ngramEntropy = lastLine.strip

end

#create the ideal coding for the upper ngram range
puts("Running: java -cp #{classpath} GenerateCoding")
Open3.popen2("java -Xms6G -Xmx6G -cp #{classpath} GenerateCoding") do |stdin, stdout, wait_thr|
  stdin.puts("#{directory}/probs/probs#{ngramRange}.txt")
  puts("#{directory}/probs/probs#{ngramRange}.txt")
  stdin.puts("#{directory}/idealCoding.txt")
  puts("#{directory}/idealCoding.txt")
  stdin.close
  while line = stdout.gets
    puts line
  end
  stdout.close
end


#create learning sample files
`mkdir #{directory}/learningSampleFiles/`
incrementCounter = learningSampleMin
while(incrementCounter <= learningSampleMax ) do
  #create a sample file
  puts("Running: java -cp #{classpath} GenerateFile")

  `mkdir #{directory}/learningSampleFiles/#{incrementCounter}/`

  Open3.popen2("java -Xms6G -Xmx6G -cp #{classpath} GenerateFile") do |stdin, stdout, wait_thr|
    stdin.puts("#{directory}/probs/")
    puts("#{directory}/probs/")
    stdin.puts("#{directory}/learningSampleFiles/#{incrementCounter}/sample.proc.txt")
    puts("#{directory}/learningSampleFiles/#{incrementCounter}/sample.proc.txt")
    stdin.puts(incrementCounter)
    puts(incrementCounter)
    stdin.puts(ngramRange)
    puts(ngramRange)
    stdin.close
    while line = stdout.gets
      puts line
    end
    stdout.close
  end


  incrementCounter = incrementCounter + learningSampleIncrement
end

#create testing sample files
`mkdir #{directory}/testingSampleFiles/`
incrementCounter = testingSampleMin
while(incrementCounter <= testingSampleMax ) do
  #create a sample file
  puts("Running: java -cp #{classpath} GenerateFile")

  `mkdir #{directory}/testingSampleFiles/#{incrementCounter}/`

  Open3.popen2("java -Xms6G -Xmx6G -cp #{classpath} GenerateFile") do |stdin, stdout, wait_thr|
    stdin.puts("#{directory}/probs/")
    puts("#{directory}/probs/")
    stdin.puts("#{directory}/testingSampleFiles/#{incrementCounter}/sample.proc.txt")
    puts("#{directory}/testingSampleFiles/#{incrementCounter}/sample.proc.txt")
    stdin.puts(incrementCounter)
    puts(incrementCounter)
    stdin.puts(ngramRange)
    puts(ngramRange)
    stdin.close
    while line = stdout.gets
      puts line
    end
    stdout.close
  end


  incrementCounter = incrementCounter + testingSampleIncrement
end

#array to track statistical distances
statDist = Array.new

#generate probabilities/codings for the learning files
incrementCounter = learningSampleMin
while(incrementCounter <= learningSampleMax ) do
  #generate probabilities
  puts("Running: java -cp #{classpath} GenerateProbabilities")

  Open3.popen2("java -Xms6G -Xmx6G -cp #{classpath} GenerateProbabilities") do |stdin, stdout, wait_thr|
    stdin.puts("#{directory}/learningSampleFiles/#{incrementCounter}/")
    puts("#{directory}/learningSampleFiles/#{incrementCounter}/")
    stdin.puts(ngramRange)
    puts(ngramRange)
    stdin.puts("#{directory}/learningSampleFiles/#{incrementCounter}/probs.txt")
    puts("#{directory}/learningSampleFiles/#{incrementCounter}/probs.txt")
    stdin.close
    while line = stdout.gets
      puts line
    end
    stdout.close
  end

  #Find the statistical distance for the generated probabilities file
  puts("Running: java -cp #{classpath} FindStatisticalDistance")
  Open3.popen2("java -Xms6G -Xmx6G -cp #{classpath} FindStatisticalDistance") do |stdin, stdout, wait_thr|
    stdin.puts("#{directory}/learningSampleFiles/#{incrementCounter}/probs.txt")
    puts("#{directory}/learningSampleFiles/#{incrementCounter}/probs.txt")
    stdin.puts("#{directory}/probs/probs#{ngramRange}.txt")
    puts("#{directory}/probs/probs#{ngramRange}.txt")
    stdin.close
    lastLine = ""
    while line = stdout.gets
      puts line
      lastLine = line
    end
    lastLine = lastLine.strip
    stdout.close
    #track the statistical distance
    statDist << lastLine

  end

  #generate codings
  puts("Running: java -cp #{classpath} GenerateCoding")

  Open3.popen2("java -Xms6G -Xmx6G -cp #{classpath} GenerateCoding") do |stdin, stdout, wait_thr|
    stdin.puts("#{directory}/learningSampleFiles/#{incrementCounter}/probs.txt")
    puts("#{directory}/learningSampleFiles/#{incrementCounter}/probs.txt")
    stdin.puts("#{directory}/learningSampleFiles/#{incrementCounter}/codings.txt")
    puts("#{directory}/learningSampleFiles/#{incrementCounter}/codings.txt")
    stdin.close
    while line = stdout.gets
      puts line
    end
    stdout.close
  end

  incrementCounter = incrementCounter + learningSampleIncrement
end


#determine the ideal coding length for each testing sample
counter = 0
incrementCounter = testingSampleMin
while(incrementCounter <= testingSampleMax ) do

  puts("Running: java -cp #{classpath} FindCodeLength")

  Open3.popen2("java -Xms6G -Xmx6G -cp #{classpath} FindCodeLength") do |stdin, stdout, wait_thr|
    stdin.puts("#{directory}/testingSampleFiles/#{incrementCounter}/sample.proc.txt")
    puts("#{directory}/testingSampleFiles/#{incrementCounter}/sample.proc.txt")
    stdin.puts("#{directory}/idealCoding.txt")
    puts("#{directory}/idealCoding.txt")
    stdin.close
    lastLine = ""
    while line = stdout.gets
      puts line
      lastLine = line
    end
    lastLine = lastLine.strip
    stdout.close
    outputFile.puts(factor.to_s + ";" + ngramRange.to_s + ";" + alphabetSize.to_s + ";" + alphabetEntropy + ";" + ngramEntropy + ";" + incrementCounter.to_s + ";ideal;"+ statDist[counter] +";"+ lastLine )
  end
  counter = counter +1
  incrementCounter = incrementCounter + testingSampleIncrement
end


#determine the derived coding length for each learning/testing pair.
testingSampleCounter = testingSampleMin
while(testingSampleCounter <= testingSampleMax) do

  learningSampleCounter = learningSampleMin
  counter = 0
  while(learningSampleCounter <= learningSampleMax) do

    puts("For " + testingSampleCounter.to_s + " and " + learningSampleCounter.to_s)

    puts("Running: java -cp #{classpath} FindCodeLength")

    Open3.popen2("java -Xms6G -Xmx6G -cp #{classpath} FindCodeLength") do |stdin, stdout, wait_thr|
      stdin.puts("#{directory}/testingSampleFiles/#{testingSampleCounter}/sample.proc.txt")
      puts("#{directory}/testingSampleFiles/#{testingSampleCounter}/sample.proc.txt")
      stdin.puts("#{directory}/learningSampleFiles/#{learningSampleCounter}/codings.txt")
      puts("#{directory}/learningSampleFiles/#{learningSampleCounter}/codings.txt")
      stdin.puts(incrementCounter)
      puts(incrementCounter)
      stdin.puts(ngramRange)
      puts(ngramRange)
      stdin.close
      lastLine = ""
      while line = stdout.gets
        puts line
        lastLine =line
      end
      lastLine = lastLine.strip
      stdout.close
      outputFile.puts(factor.to_s + ";" + ngramRange.to_s + ";" + alphabetSize.to_s + ";" + alphabetEntropy + ";" + ngramEntropy + ";"+ testingSampleCounter.to_s + ";" +  learningSampleCounter.to_s + ";" + statDist[counter] + ";"+ lastLine)
    end

    counter = counter +1
    learningSampleCounter = learningSampleCounter + learningSampleIncrement
  end

  testingSampleCounter = testingSampleCounter + testingSampleIncrement
end
