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
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;

import java.util.ArrayList;
import java.util.List;

/**
 * This class use the featureSelection library to implement Information Gain
 */
public class IG {

  public List<String> execute(String workingDirectory, String dataset) {
    try {

       if (dataset.length() == 0)
        throw new IllegalArgumentException(); 
      // Load input dataset.
      DataSource source = new DataSource(workingDirectory + dataset);
      System.out.println("Reading instances...");
      Instances data = source.getDataSet();

      // Performs information gain.
      InfoGainAttributeEval igEvaluator = new InfoGainAttributeEval();


      // Ranking the attributes.
      Ranker ranker = new Ranker();
      // Specify the number of attributes to select from the ranked list.
      ranker.setThreshold(-1.7976931348623157E308);
      //select all attributes
      ranker.setNumToSelect(-1);
      ranker.setGenerateRanking(true);
      
      AttributeSelection selector = new AttributeSelection();
      System.out.println("Selecting attributes...");
      selector.setSearch(ranker);
      selector.setEvaluator(igEvaluator);
      selector.SelectAttributes(data);

//      PrintStream o = new PrintStream(new File(workingDirectory + "IGResults.txt"));
//      System.setOut(o);
//      System.out.println(Arrays.toString(selector.rankedAttributes()));
//      System.out.println(Arrays.toString(selector.selectedAttributes()));
//      //System.out.println(selector.CVResultsString());
//      System.out.println(selector.toResultsString());
        double[][] attrs = selector.rankedAttributes();
        List<String> attributes = new ArrayList<>();
        for (double[] a: attrs) {
            attributes.add(String.valueOf(new Double(a[0]).intValue() + 1));
        }

      return attributes;

    } catch (IllegalArgumentException e) {
        e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ArrayList<>();
  }
}
