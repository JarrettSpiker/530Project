# How To Use

Some things to note:
  - Not all output files are intermediate, so dont just delete them willy-nilly in the middle of the process
  - A lot (if not most) of these processes are quite memory intensive so you should:
    - expect large inputs to take a few minutes
    - GIVE JAVA more heap space. To give it, say, 6 gigs, run as `java -Xms6G -Xmx6G programName`. If you use eclipse (or any IDE) , those arguments go in the JVM arguments of the run configuration

# Generating Encodings

You can either generate codings based on English or on a generated language.

To use English:
  1. You need a directory full of txt files. The `books` directory in this repo should work
  2. Run the `ProvessFiles.java` program.
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
This is what the `FindStatisticalDistance.java` file is for. Havent gotten around to it yet

# Creating a sample
This will require probability files. Have not written code yet

# Encoding a file
I actually have code for this, but it is not compatible with what I have written so far for the project...so I have not pushed it yet.

