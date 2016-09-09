/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.poliba.sisinflab.simlib.featureSelection.methods;

/**
 *
 * @author Corrado
 */
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.ChiSquaredAttributeEval;
import weka.attributeSelection.Ranker;

/**
 * This class use the featureSelection libary to implement Chi-Squared Test.
 */
public class CHI {

  public void execute(String dataset) {
    try {

       if (dataset.length() == 0)
        throw new IllegalArgumentException(); 
      // Load input dataset.
      DataSource source = new DataSource(dataset);
      System.out.println("Reading instances...");
      Instances data = source.getDataSet();
     
      // Performs a principal components analysis.
      ChiSquaredAttributeEval chiEvaluator = new ChiSquaredAttributeEval();

      // Ranking the attributes.
      Ranker ranker = new Ranker();
      // Specify the number of attributes to select from the ranked list.
      /*ranker.setThreshold(-1.7976931348623157E308);
      ranker.setNumToSelect(-1);
      ranker.setGenerateRanking(true);*/
      ranker.setNumToSelect(-1);
      
      AttributeSelection selector = new AttributeSelection();
      System.out.println("Selecting attributes...");
      selector.setSearch(ranker);
      selector.setEvaluator(chiEvaluator);
      selector.SelectAttributes(data);

      PrintStream o = new PrintStream(new File("data/"+"CHIResults"+".txt"));
      System.setOut(o);
      System.out.println(Arrays.toString(selector.rankedAttributes()));
      System.out.println(Arrays.toString(selector.selectedAttributes()));
      //System.out.println(selector.CVResultsString());
      System.out.println(selector.toResultsString());
      
      System.out.println();

    } catch (IllegalArgumentException e) {
      System.err.println("Error");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
