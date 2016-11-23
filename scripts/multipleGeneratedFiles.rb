#!/usr/bin/ruby
require 'optparse'
require 'open3'

directory = "/Users/jspiker/temp/"

maxFactor = 1
maxNgramRange = 1
maxAlphabetSize = 5

learningSampleMin = 0
learningSampleMax = 0
learningSampleIncrement = -1

testingSampleMin = 0
testingSampleMax = 0
testingSampleIncrement = -1

expirimentTimes = 1

OptionParser.new do |options|
  options.on("-d n", "--dir=n", String, "Working dir") do |n|
    directory = n
  end

  options.on("-a n", "--alphabetSize=n", "Alphabet Size") do |n|
    maxAlphabetSize = n.to_i
  end

  options.on("-f f", "--factor=f", "Factor") do |f|
    maxFactor = f.to_i
  end

  options.on("-r n", "--range=n", "N-gram range") do |n|
    maxNgramRange = n.to_i
  end

  options.on("-e n", "--eTimes=n", "Expirimental Runs") do |n|
    expirimentTimes = n.to_i
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
end.parse!

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

`rm -rf #{directory}/*`


`mkdir #{directory}/obj/`
#compile all of the src files into an obj folder
puts("Compiling...")


Dir.glob('../src/*.java') do |java_file|
  `javac #{java_file} -d #{directory}/obj/`
end

`mkdir #{directory}/workingDir/`


for factor in 1..maxFactor do

  for alphabetSize in 2..maxAlphabetSize do

    for ngramRange in 1..maxNgramRange do

      for e in 1..expirimentTimes do
        #run the expiriment and copy the results file to the output dir
        `rm -rf #{directory}/workingDir/*`
        puts()
        puts()
        puts()
        puts()
        puts()
        puts("Running: ./generatedFiles.rb -d #{directory}/workingDir/ -a #{alphabetSize} -f #{factor} -r #{ngramRange}  -m #{learningSampleMin} -l #{learningSampleMax} -i #{learningSampleIncrement} -M #{testingSampleMin} -L #{testingSampleMax} -I #{testingSampleIncrement} --skipCompiling -c #{directory}/obj/")
        puts("_____________________________________________________________")
        Open3.popen2("./generatedFiles.rb -d #{directory}/workingDir/ -a #{alphabetSize} -f #{factor} -r #{ngramRange}  -m #{learningSampleMin} -l #{learningSampleMax} -i #{learningSampleIncrement} -M #{testingSampleMin} -L #{testingSampleMax} -I #{testingSampleIncrement} -o #{directory}/workingDir/output.txt --skipCompiling -c #{directory}/obj/") do |stdin, stdout, wait_thr|
          stdin.close
          while line = stdout.gets
            puts line
          end
          stdout.close

        end

        `mv #{directory}/workingDir/output.txt #{directory}/workingDir/e#{e}f#{factor}n#{ngramRange}a#{alphabetSize}.txt`
        `cp #{directory}/workingDir/e#{e}f#{factor}n#{ngramRange}a#{alphabetSize}.txt #{directory}`

      end
    end

  end
end
