# How To Use

Some things to note:
  - Not all output files are intermediate, so dont just delete them willy-nilly in the middle of the process
  - A lot (if not most) of these processes are quite memory intensive so you should:
    - expect large inputs to take a few minutes
    - GIVE JAVA more heap space. To give it, say, 6 gigs, run as `java -Xms6G -Xmx6G programName`. If you use eclipse (or any IDE) , those arguments go in the JVM arguments of the run configuration


# Getting Generated Files Output

You will want to run the `multipleGeneratedFiles.rb` script from the scripts directory (like, actually run the script in the directory, if you call it from somewhere else it might not work).

The call will look something like this 

`./multipleGeneratedFiles.rb -d ~/test1/ -e 5 -a 2 -A 5 -f 1 -F 3 -r 1 -R  10  -m 5000 -l 10000 -i 5000 -M 5000 -L 10000 -I 5000`

The arguments which you need to specify are as follows:

  - -d a working directory. This should be a directory which exists and doesnt contain anything important, as the script will delete everything currently in the directory.
   
  - -e the number of expreriments to run. These are probabalistic, so you will get different results for the same input, so it can be nice to run each experiment multiple times
  
  
  - -a and -A the lower and upper bounds (inclusive) for the alphabet size, respectively.
  
  - -f and -F the lower and upper bounds (inclusive) for the factor used when determining the probabilities of the alphabet (1-grams). The lower the value, the higher the entropy of the alphabet
  
  - -r and -R the lower and upper bounds (inclusive) for the n-gram ranges.
  
  - -m -l and -i these are the lower (m) and upper (l) bounds of the learning sample sizes. This is the size of the sample which is generated, and then used to generate the imperfect huffman codes. -i is the increment, the amount to add to the lower bound with each test case. So if you put -m 100 -l 300 -i 40, test cases will be run for sample sizes of 100, 140, 180, 220, 260, 300.
  
  - -M -L and -I these are the lower (M) and upper (L) bounds of the testing sample sizes. This is the size of thefile which will be encoded with the huffman codes. -I is the increment, the amount to add to the lower bound with each test case. So if you put -M 100 -L 300 -I 40, test cases will be run for testing sizes of 100, 140, 180, 220, 260, 300.
  
You are welcome to make the lower and upper bound the same value, if you dont want to test over different values for that input. A test case will be run for EACH POSSIBLE COMBINATION of inputs in the ranges specified, and this gets big reaaaallly quick if you make all of the ranges large.

v

# Getting Natural Language Output

To start, most of the same rules apply here as in the Generate Files output, just with a different script and slightly different arguments

To run this, though, you need to start with 2 things:
  1. A directory with several txt files (the books directory from this project works)
  2. A different txt file youd like to encode. If you want, you can just pull one of the files out of the books dir, but dont include that file in #1 then.
  
So the script for this is: `multipleNaturalFiles.rb` and the call looks like this:

`./multipleNaturalFiles.rb -d /Users/jspiker/test2 -a 2 -A 10 -b ../books -n 1 -n 10 -e 2 -t /Users/jspiker/kingarthur.txt`


  - -d a working directory. This should be a directory which exists and doesnt contain anything important, as the script will delete everything currently in the directory.
   
  - -e the number of expreriments to run. These are probabalistic, so you will get different results for the same input, so it can be nice to run each experiment multiple times
  
  
  - -a and -A the lower and upper bounds (inclusive) for the alphabet size, respectively.
  
  - -n and -N the lower and upper bounds (inclusive) for the n-gram ranges.
  
  - -b is the directory with the text files which will be used to generate huffman encodings(the #1 dir described above)
  
  - -t the txt file you want to encode. This will be used for all test cases (#2 described above) 

The output will be written to an output file inside of the working directory specified. It is ALMOST a csv, it just needs column headers. It is easy enough to just import it into excel or an excel equivalent
  

# Generating Encodings

You can either generate codings based on English or on a generated language.

To use English:
  1. You need a directory full of txt files. The `books` directory in this repo should work
  2. Run the `ProcessFiles.java` program.
      - Point it at the directory containing the txt files.
      - You will need to provide an output directory as well. We can call this `outputDirA`
      - IF YOU WANT to restrict the characters used in "English", this is the file you need to change (just have it ignore ASCII values you dont care about)
  3. Run the `GenerateProbabilities.java` program
      - The input directory is `outputDirA`
      - The n-gram-size can be an integer over 0. The larger this number, the longer this whole process will take
      - You will need to provide an output file name. We will call this `probs.txt`
        - It might be worth pointing out that the probabilities in this file wont be sorted. That's easy enough to do, but has a significant overhead for no real gain
  4. Run the `GenerateCoding.java` program
      - The input is the `probs.txt` file
      - You will need to provide an output file. This is the file which will contain the encodings

To use a generated language:
  1. Run the `CreateDistribution.java` program
      - I will describe the function this uses to generate the distribution later in this file, once it is finalized
      - You will need to provide the output directory name. We will call this `generatedProbsDir`
        - There will be several files generated in this directory
      - Enter an alphabet size (ie the nubmer of 1-grams). Increasing this makes the probram take longer very rapidly
      - Enter an n-gram range. You will calculate n-gram probabilities from [1, range]
      - Enter a "factor".
        - Again, I'll go into this when I describe the probability function (when that is finalized), but suffice it to say that this just changes the probabilities of the 1-grams in a meaningful way
      - One file will be generated with the probabilities of all the possible n-grams in the output directory
  2. See step 4+ for generating codes in English, where all of the generated files in the `generatedProbsDir` are a differenct `probs.txt`

# Finding statistical difference
This is what the `FindStatisticalDistance.java` file is for. Pass it the output of 2 probability files as generated by either `GenerateProbabilities.java` or `CreateDistribution.java`.

This uses Jensen-Shannon divergence. See https://en.wikipedia.org/wiki/Jensen%E2%80%93Shannon_divergence

# Creating a sample

This works with both generated and with English, all it requires is the probability files.

To use a generated language:
  1. Run the `GenerateFile.java` program
  2. Enter the name of the probabilities directory
       - This will be the output directory of the `CreateDistribution.java` program
  3. Enter the name of the output file. This is the file which the sample will be written to
  4. Enter the number of characters you want to be in the output file (ie, the size of the sample)
  5. Enter the maximum n-gram size
       - You must have probabilities for ngrams of every size UP TO AND INCLUDING this number

To use a generated language:
  1. You will need to generate probability files for n of size 1 through your desired maximum n-gram size (inclusive). This will mean running the `GenerateProbabilites.java` program n times.
       - These files must be names `probs1.txt`, `probs2.txt`, etc., up to `probsN.txt`, and must exist in a single directory, which we will call `inputDir`
  2. Run the `GenerateFile.java` program
  3. Enter the full path of `inputDir`
  4. Enter the name of the output file. This is the file which the sample will be written to
  5. Enter the number of characters you want to be in the output file (ie, the size of the sample)
  6. Enter the maximum n-gram size
       - You must have probability files for for n of every size UP TO AND INCLUDING this number

# Encoding a file
  1. Run the `FindCodeLength.java` program
  2. Enter the name of the file to encode.
       - Only the first line of this file will be used. This will be true if the file was generated using the `ProcessFiles.java` program, or the `GenerateFile.java` program
  3. Enter the name of the encodings file. This is the file created by `GenerateCoding.java` program
  4. The coding length of the file will be printed to std out. This is a worst case number.
       - In order to account for codes not found in the encodings file, we count the number of codes which are not found in the encodings, and determine how long the encodings of that many 0-probability n-grams would be if they had been considered when generating the encoding (I know that doesnt make much sense, I pretty much need to draw a picture, or give a lot more detail, to show what I mean, but feel free to ask).
       - In the case where the length of the sample is not divisible by n, we find what the worst possible encoding of the trailing characters are. This is the maximum of the lengths of all codes which map to an n-gram which the trailing characters prefix (again, feel free to ask if that doesnt make sense...though I dont really think that it matters).

