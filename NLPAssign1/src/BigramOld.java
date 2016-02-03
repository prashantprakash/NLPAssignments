import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class BigramOld 
{
    Map<String,Integer> Vocabulary;
    Map<String,ColumnData> BigramCountTable;
    int bigramCount;
    Map<Integer,Integer> bigramCountMap;
    
    public BigramOld()
    {
        bigramCount=0;
        Vocabulary=new HashMap<String,Integer>();
        BigramCountTable=new HashMap<String,ColumnData>();
        bigramCountMap = new HashMap<>();
    }
 
    /**
     * Function Read the corpus and create vocabulary of words with its occurence
     * @param path 
     */
    public void ExtractData(String path)
    {
        try
        {
            FileReader input=new FileReader(path);
            BufferedReader br=new BufferedReader(input);
            String line=br.readLine();
            while(line!=null)
            {
                StringTokenizer token=new StringTokenizer(line," ");
                while(token.hasMoreTokens())
                {
                   String word=token.nextToken().trim().toLowerCase();
                   if(word.length()==1 && !word.equals("."))
                   {
                       word=word.replaceAll("[^a-zA-Z0-9]+","");
                   }
                   if(word.length()==0)
                       continue;
                   if(Vocabulary.containsKey(word))
                   {
                       int value=Vocabulary.get(word);
                       int newValue=value+1;
                       Vocabulary.put(word, newValue);
                   }
                   else
                   {
                      Vocabulary.put(word,1);
                   }
                } 
                line=br.readLine();
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        System.out.println("");
    }
    
    /**
     * Function to create Bigram Count Table
     * @param path 
     */
    public void calculateBigramCounts(String path)
    {
        String previousWord="";
        for(String word:Vocabulary.keySet())
        {
          BigramCountTable.put(word, new ColumnData(Vocabulary.keySet()));
        }
        
        //read corpus
        try
        {
            FileReader input=new FileReader(path);
            BufferedReader br=new BufferedReader(input);
            String line=br.readLine();
            while(line!=null)
            {
                StringTokenizer token=new StringTokenizer(line," ");
                while(token.hasMoreTokens())
                {
                   String word=token.nextToken().trim().toLowerCase();
                   if(word.length()==1 && !word.equals("."))
                   {
                       word=word.replaceAll("[^a-zA-Z0-9]+","");
                   }
                   if(word.length()==0)
                       continue;
                  
                   if(!previousWord.equals(""))
                   {
                       double value=BigramCountTable.get(word).columnValues.get(previousWord)+1;
                       BigramCountTable.get(word).columnValues.put(previousWord, value);
                       bigramCount++;
                   }
                   previousWord=word;
                } 
                line=br.readLine();
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        } 
        buildBigramCountMap();
    }
    /**
     * Function to create Bigram count Map,Where Key is bigram and Value is occurence
     */
    public void buildBigramCountMap()
    {
        for(String key : BigramCountTable.keySet())
        {
            for(String prev : BigramCountTable.get(key).columnValues.keySet())
            {
                double dVal = BigramCountTable.get(key).columnValues.get(prev);
                int val = (int) dVal;
                if(bigramCountMap.get(val)==null){
                    bigramCountMap.put(val, 1);
                }
                else
                {
                    int oldVal = bigramCountMap.get(val);
                    bigramCountMap.put(val, oldVal+1);
                }
            }
        }
    }
    
     /**
     * Function to count total number of words in vocabulary
     * @return 
     */
    public int VocabularyWordCount()
    {
        int vCount=0;
        for(String s:Vocabulary.keySet())
        {
            vCount+=Vocabulary.get(s);
        }
        return vCount;
    }
    
     /**
     * Function to calculate Bigram probability.If the word does not exist returns 0
     * @param word
     * @param prevWord
     * @return 
     */
    public double getBigramProbability(String word,String prevWord)
    {
        double v=0.0;
        if(BigramCountTable.containsKey(word))
        {
            if(BigramCountTable.get(word).columnValues.containsKey(prevWord))
                v=BigramCountTable.get(word).columnValues.get(prevWord);
        }
        if(v>0)
        {
            double prob=(double)v/Vocabulary.get(prevWord);
            return prob;
        }
        return 0.0;
    }
    
    /**
     * Function to calculate Bigram Smoothing Probability(One Laplace Smoothing)
     * @param word
     * @param prevWord
     * @return 
     */
    public double getBigramSmoothingProbability(String word,String prevWord)
    {
        double v=0.0;
        double prob=0.0;
        if(BigramCountTable.containsKey(word))
        {
            if(BigramCountTable.get(word).columnValues.containsKey(prevWord))
                v=BigramCountTable.get(word).columnValues.get(prevWord);
        }
        else
        {
            v=1.0; //smoothing
        }
        if(Vocabulary.get(prevWord)!=null)
        {
             prob=(double)(v+1)/(Vocabulary.get(prevWord)+Vocabulary.size());
        }
        else
        {
           prob=(double)(v+1)/(1+Vocabulary.size()); 
        }
        
        return prob;
    }
    
     /** 
     * FUnction to calculate Good Turing Smoothing Probability
     * @param word
     * @param prevWord
     * @return 
     */
    public double getBigramGoodTuringProbability(String word,String prevWord)
    {
        double cD=0.0;
        if(BigramCountTable.containsKey(word))
        {
            if(BigramCountTable.get(word).columnValues.containsKey(prevWord))
                cD=BigramCountTable.get(word).columnValues.get(prevWord);
        }
        int c = (int) cD;
        if(bigramCountMap.get(c+1)!=null)
        {
            double cStar=(double)(c+1)*bigramCountMap.get(c+1)/bigramCountMap.get(c);
            return (double)cStar/bigramCount;
        }
        return 0.0;
    }
    
    /**
     * Function to print a table given array of words in a Sentence
     * @param input 
     */
    public void printTable(Map<String,ColumnData> input) {
        System.out.format("%-30s", "");
        List<String> keys = new LinkedList<>();
        keys.addAll(input.keySet());
        for(String key : input.keySet())
        {
            System.out.format("%-30s", key);
        }
        System.out.println("");
        for(String key : input.keySet()){
            System.out.format("%-30s", key);
            for(int i=0;i<input.keySet().size(); i++){
                String w = keys.get(i);
                String val = Double.toString(input.get(w).columnValues.get(key));
                System.out.format("%-30s", val);
            }
            System.out.println("");
        }
        
    }
    
     /**
     * Function to Construct Bigram Count Table given sentence
     * @param sentence 
     */
    public void constructBigramCountTable(String sentence)
    {
        Map<String,ColumnData> sentenceBigramProbTable=new HashMap<>();
        String [] segments=sentence.toLowerCase().split(" ");
        Set<String> minimizedSet = new HashSet<String>(Arrays.asList(segments));
        for(int i=0;i<segments.length;i++)
        {
            ColumnData d=new ColumnData(minimizedSet);
            for(int j=0;j<segments.length;j++)
            {
                if(BigramCountTable.containsKey(segments[i]))
                {
                    if(BigramCountTable.get(segments[i]).columnValues.containsKey(segments[j]))
                    {
                        d.columnValues.put(segments[j],BigramCountTable.get(segments[i]).columnValues.get(segments[j]));
                    }
                    else
                    {
                     d.columnValues.put(segments[j],0.0);   
                    }
                }
            }
            sentenceBigramProbTable.put(segments[i].trim().toLowerCase(),d);
        }
        printTable(sentenceBigramProbTable);
        
    }
    
    /**
     * Function to Construct Bigram Probabilities Table given sentence
     * @param sentence 
     */
    public void constructBigramProbabilitiesTable(String sentence)
    {
        Map<String,ColumnData> sentenceBigramCountTable=new HashMap<>();
        String [] segments=sentence.toLowerCase().split(" ");
        Set<String> minimizedSet = new HashSet<String>(Arrays.asList(segments));
        for(int i=0;i<segments.length;i++)
        {
            ColumnData d=new ColumnData(minimizedSet);
            for(int j=0;j<segments.length;j++)
            {
                d.columnValues.put(segments[j],getBigramProbability(segments[i],segments[j]));
            }
            sentenceBigramCountTable.put(segments[i].trim().toLowerCase(),d);
        }
        printTable(sentenceBigramCountTable);
    }
    
    /**
     * Function to Construct Bigram Probabilites Table with one laplace Smoothing ,given sentence as input
     * @param sentence 
     */
    public void constructBigramSmoothingProbabilitiesTable(String sentence)
    {
        Map<String,ColumnData> sentenceBigramCountTable=new HashMap<>();
        String [] segments=sentence.toLowerCase().split(" ");
        Set<String> minimizedSet = new HashSet<String>(Arrays.asList(segments));
        for(int i=0;i<segments.length;i++)
        {
            ColumnData d=new ColumnData(minimizedSet);
            for(int j=0;j<segments.length;j++)
            {
                d.columnValues.put(segments[j],getBigramSmoothingProbability(segments[i],segments[j]));
            }
            sentenceBigramCountTable.put(segments[i].trim().toLowerCase(),d);
        }
        printTable(sentenceBigramCountTable);
    }
    
     /**
     * Function to Construct Bigram Probabilites Table with Good Turing ,given sentence as input
     * @param sentence 
     */
    public void constructBigramGoodTuringProbabilitiesTable(String sentence)
    {
        Map<String,ColumnData> sentenceBigramCountTable=new HashMap<>();
        String [] segments=sentence.toLowerCase().split(" ");
        Set<String> minimizedSet = new HashSet<String>(Arrays.asList(segments));
        for(int i=0;i<segments.length;i++)
        {
            ColumnData d=new ColumnData(minimizedSet);
            for(int j=0;j<segments.length;j++)
            {
                d.columnValues.put(segments[j],getBigramGoodTuringProbability(segments[i],segments[j]));
            }
            sentenceBigramCountTable.put(segments[i].trim().toLowerCase(),d);
        }
        printTable(sentenceBigramCountTable);
    }
    
     /**
     * Function to calculate Total Probability given sentence
     * @param sentence
     * @return 
     */
    public double calculateTotalProbability(String sentence) {
        double totalProb = 1;
        String [] segments=sentence.toLowerCase().split(" ");
        for(int i=0;i<segments.length; i++) {
            if(i==0) {
                double p = getBigramProbability(segments[i],".");
                if(p>0)
                    totalProb = totalProb * p;
            }
            else
            {
                double p = getBigramProbability(segments[i],segments[i-1]);
                if(p>0)
                    totalProb = totalProb * p;
           }
        }
        return totalProb;
    }
    
      /**
     * Function to calculate Total Probability with one laplace smoothing given sentence
     * @param sentence
     * @return 
     */
    public double calculateTotalSmoothingProbability(String sentence) {
        double totalProb = 1;
        String [] segments=sentence.toLowerCase().split(" ");
        for(int i=0;i<segments.length; i++) {
            if(i==0) {
                double p = getBigramSmoothingProbability(segments[i],".");
                if(p>0)
                    totalProb = totalProb * p;
            }
            else
            {
                double p = getBigramSmoothingProbability(segments[i],segments[i-1]);
                if(p>0)
                    totalProb = totalProb * p;
            }
        }
        return totalProb;
    }
    
     /**
     * Function to calculate Total Probability with Go0d Turing Smoothing given sentence
     * @param sentence
     * @return 
     */
    public double calculateGoodTuringProbability(String sentence) {
        double totalProb = 1;
        String [] segments=sentence.toLowerCase().split(" ");
        for(int i=0;i<segments.length; i++) {
            if(i==0) {
                double p = getBigramGoodTuringProbability(segments[i],".");
                if(p>0)
                    totalProb = totalProb * p;
           }
            else
            {
                double p = getBigramGoodTuringProbability(segments[i],segments[i-1]);
                if(p>0)
                    totalProb = totalProb * p;
            }
            
        }
        return totalProb;
    }
    
    public static void main(String args[])
    {
        //String corpusPath="/Users/bhumikasaivamani/corpus.txt";
        //String inputFilePath="/Users/bhumikasaivamani/input.txt";
        
        String corpusPath=args[0];
        String inputFilePath=args[1];
        
        BigramOld b=new BigramOld();
        b.ExtractData(corpusPath);
        b.calculateBigramCounts(corpusPath);
        
        int count=0;
        double maxprobWithoutSmoothing=0.0;
        double maxprobWithSmoothing=0.0;
        double maxprobGoodTuring=0.0;
        
        String outSentenceWithoutSmoothing=null;
        String outSentenceWithSmoothing=null;
        String outSentenceGoodTurning=null;
        
        try
        {
            FileReader reader=new FileReader(inputFilePath);
            BufferedReader br=new BufferedReader(reader);
            String sentence=br.readLine();
            while(sentence!=null)
            {
                count++;
                System.out.println("\n\tSENTENCE "+count);
                System.out.println("********************");
                System.out.println("Bigram Count Table");
                System.out.println("********************");
                b.constructBigramCountTable(sentence);
                
                System.out.println("**************************************");
                System.out.println("Probabilities Table(Without Smoothing)");
                System.out.println("**************************************");
                b.constructBigramProbabilitiesTable(sentence);
                
                System.out.println("\n**************************************");
                System.out.println("Probabilities Table(With Smoothing)");
                System.out.println("**************************************");
                b.constructBigramSmoothingProbabilitiesTable(sentence);
                
                System.out.println("\n**************************************");
                System.out.println("Probabilities Table(Good Turing)");
                System.out.println("**************************************");
                System.out.println("****Bigram Good Turing Probabilities Table - Sentence 1****");
                b.constructBigramGoodTuringProbabilitiesTable(sentence);
                
                double probWithoutSmoothing=b.calculateTotalProbability(sentence);
                double probWithSmoothing=b.calculateTotalSmoothingProbability(sentence);
                double probGoodTuring=b.calculateGoodTuringProbability(sentence);
                System.out.println("\n\n\t**********************************************");
                System.out.println("\tProbability of Sentence (Without Smoothing): "+probWithoutSmoothing);
                System.out.println("\tProbability of Sentence (With Smoothing): "+probWithSmoothing);
                System.out.println("\tProbability of Sentence (Good Turing): "+probGoodTuring);
                System.out.println("\t**********************************************");
                
                if(probWithoutSmoothing>maxprobWithoutSmoothing)
                {
                    maxprobWithoutSmoothing=probWithoutSmoothing;
                    outSentenceWithoutSmoothing=sentence;
                }
                if(probWithSmoothing>maxprobWithSmoothing)
                {
                    maxprobWithSmoothing=probWithSmoothing;
                    outSentenceWithSmoothing=sentence;
                }
                if(probGoodTuring>maxprobGoodTuring)
                {
                    maxprobGoodTuring=probGoodTuring;
                    outSentenceGoodTurning=sentence;
                }
                sentence=br.readLine();
                
                System.out.println("\n\n");
            }
            System.out.println("\n\n***********************");
            System.out.println("Maximum Probabilities");
            System.out.println("***********************");
            System.out.println("Without Smoothing: "+maxprobWithoutSmoothing);
            System.out.println("With Smoothing: "+maxprobWithSmoothing);
            System.out.println("Good Turing : "+maxprobGoodTuring);
            
            System.out.println("\n********************");
            System.out.println("Probable Sentences");
            System.out.println("********************");
            System.out.println("Without Smoothing: "+outSentenceWithoutSmoothing);
            System.out.println("With Smoothing: "+outSentenceWithSmoothing);
            System.out.println("Good Turing : "+outSentenceGoodTurning);
        }
        catch(Exception e)
        {}
        
    }
    
}
 

