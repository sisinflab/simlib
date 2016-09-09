/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.poliba.sisinflab.simlib.featureSelection.dataset;

/**
 *
 * @author Corrado on 17/05/2016.
 */
import it.poliba.sisinflab.simlib.featureSelection.instance.InstanceARFF;
import it.poliba.sisinflab.simlib.datamodel.Graph;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import weka.core.FastVector;
import weka.core.Instances;


public abstract class DatasetARFF {

    protected Graph graph;
    protected Instances data;
    protected FastVector atts = new FastVector();
    protected FastVector  attVals = new FastVector();
    protected ArrayList<FastVector>  attValsprop = new ArrayList<>();


    public DatasetARFF(Graph graph)
    {
        this.graph= graph;
    }


    public DatasetARFF() {

    }

    public abstract void addNumericAttribute(String nameattribute);
    public abstract void addIntegerAttribute(String nameattribute);
    public abstract void addNominalAttribute(String nameattribute,Collection<Integer> listvalue);
    public abstract void addNominalAttribute(String nameattribute,HashMap<String,Integer> listvalue);
    public abstract void addStringAttribute(String nameattribute);
    public abstract void addInstance(List<InstanceARFF> instancelist) throws Exception;
    public abstract void addIntegerInstance(List<InstanceARFF> instancelist) throws Exception;

    public void create(String datasetName)throws FileNotFoundException
    {
        //create Instances object
        data = new Instances(datasetName, atts, 0);
        //PrintStream o = new PrintStream(new File("data/"+datasetName+".arff"));
        //System.setOut(o);
        //System.out.println(data);
    }

    public void save(String datasetName) throws FileNotFoundException
    {
        //output data
        // Creating a File object that represents the disk file.
        PrintStream o = new PrintStream(new File(datasetName+".arff"));
        o.println(data);

    }


    public void appendInstances(String datasetName)  throws FileNotFoundException, IOException
    {
        PrintStream o = new PrintStream(new File(datasetName+".arff"));
        //PrintStream o = new PrintStream(new File(datasetName+".arff"));
        o.println(data);
    }
}
