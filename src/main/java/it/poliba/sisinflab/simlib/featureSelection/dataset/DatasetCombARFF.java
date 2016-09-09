/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.poliba.sisinflab.simlib.featureSelection.dataset;

/**
 *
 * @author Corrado on 19/05/2016.
 */
import it.poliba.sisinflab.simlib.featureSelection.instance.InstanceARFF;
import it.poliba.sisinflab.simlib.datamodel.Graph;
import it.poliba.sisinflab.simlib.featureSelection.instance.InstanceCombARFF;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;


public class DatasetCombARFF extends DatasetARFF{
   
   
   public DatasetCombARFF(Graph graph)
   {
       super(graph);
   }
   
   @Override
   public void addNumericAttribute(String nameattribute)
   {  
     // - numeric
     atts.addElement(new Attribute(nameattribute));
   }
   
   @Override
   public void addNominalAttribute(String nameattribute,HashMap<String,Integer> listvalue)
   {  
    // - nominal
     attVals = new FastVector();
     listvalue.entrySet().stream().forEach((s) -> {
         attVals.addElement(s.getValue().toString());
       });
     attValsprop.add(attVals);
     atts.addElement(new Attribute(nameattribute, attVals));
   }
   
   @Override
   public void addStringAttribute(String nameattribute)
   { 
     // - string
     atts.addElement(new Attribute(nameattribute, (FastVector) null));
   }
    
   @Override
   public void addInstance(List<InstanceARFF> instancelist) throws Exception {
       
     //for(InstanceARFF in:instancelist)
     //{ 
        data.delete();
        
        instancelist.parallelStream().forEachOrdered(in->{
                InstanceCombARFF inoc = (InstanceCombARFF) in;
        //HashMap<String,String> propocc = inoc.getPropobject();
        double[]        vals;
        // 3. fill with data
        // first instance
        vals = new double[data.numAttributes()];
        LinkedList<Integer> listoccurence = new LinkedList<>();
        //listoccurence.addAll(propocc.values());
        listoccurence.addAll(inoc.getPropobject());
        for(int i=0;i<listoccurence.size();i++)
        {
             //nominal
            //vals[i] = listoccurence.get(i);
            vals[i]=attValsprop.get(i).indexOf(listoccurence.get(i).toString());
        }
        //vals[vals.length-1] = attVals.indexOf(inoc.getItem());
        vals[vals.length-1] = attValsprop.get(vals.length-1).indexOf(inoc.getItem());
        //add
        data.add(new Instance(1.0, vals));
        
        });
     //}
   }
   
   @Override
   public void addIntegerInstance(List<InstanceARFF> instancelist) throws Exception {
       
     //for(InstanceARFF in:instancelist)
     //{ 
        data.delete();
        
        instancelist.parallelStream().forEachOrdered(in->{
                InstanceCombARFF inoc = (InstanceCombARFF) in;
        //HashMap<String,String> propocc = inoc.getPropobject();
        double[]        vals;
        // 3. fill with data
        // first instance
        vals = new double[data.numAttributes()];
        LinkedList<Integer> listoccurence = new LinkedList<>();
        //listoccurence.addAll(propocc.values());
        listoccurence.addAll(inoc.getPropobject());
        for(int i=0;i<listoccurence.size();i++)
        {
             //nominal
            //vals[i] = listoccurence.get(i);
            vals[i]=listoccurence.get(i);
        }
        vals[vals.length-1] = attVals.indexOf(String.valueOf(inoc.getItem()));
        //vals[vals.length-1] = inoc.getItem();
        //add
        data.add(new Instance(1.0, vals));
        
        });
     //}
   }
   

    @Override
    public void addNominalAttribute(String nameattribute, Collection<Integer> listvalue) {
            // - nominal
     attVals = new FastVector();
     listvalue.stream().forEach((s) -> {
         attVals.addElement(String.valueOf(s));
       });
     attValsprop.add(attVals);
     atts.addElement(new Attribute(nameattribute, attVals));
    }
    
    @Override
    public void addIntegerAttribute(String nameattribute) {
    
        atts.addElement(new Attribute(nameattribute));
    }

 }
