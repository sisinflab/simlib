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
import weka.attributeSelection.PrincipalComponents;
import weka.attributeSelection.Ranker;

/**
 * This class use the featureSelection libary to implement Principal Components Analysis.
 */
public class PCA {

  public void execute(String dataset) {
    try {

       if (dataset.length() == 0)
        throw new IllegalArgumentException(); 
      // Load input dataset.
      DataSource source = new DataSource(dataset);
      Instances data = source.getDataSet();

      // Performs a principal components analysis.
      PrincipalComponents pcaEvaluator = new PrincipalComponents();

      // Sets the amount of variance to account for when retaining principal
      // components.
      pcaEvaluator.setVarianceCovered(1.0);
      // Sets maximum number of attributes to include in transformed attribute
      // names.
      pcaEvaluator.setMaximumAttributeNames(-1);

      // Scaled X such that the variance of each feature is 1.
      boolean scale = true;
      if (scale) {
        pcaEvaluator.setCenterData(true);
      } else {
        pcaEvaluator.setCenterData(false);
      }

      // Ranking the attributes.
      Ranker ranker = new Ranker();

      ranker.setNumToSelect(-1);
      
      AttributeSelection selector = new AttributeSelection();
      selector.setSearch(ranker);
      selector.setEvaluator(pcaEvaluator);
      selector.SelectAttributes(data);

      // Transform data into eigenvector basis.
      Instances transformedData = selector.reduceDimensionality(data);
      PrintStream o = new PrintStream(new File("data/"+"PCAResults"+".txt"));
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
