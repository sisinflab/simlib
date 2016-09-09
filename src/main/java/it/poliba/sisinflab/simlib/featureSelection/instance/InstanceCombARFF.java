package it.poliba.sisinflab.simlib.featureSelection.instance;

import java.util.List;


/**
 * Defines a single value of occurrence for the instance
 *
 * @author Corrado Magarelli
 * 
 */
public class InstanceCombARFF extends InstanceARFF {

  //private HashMap<String,String> propobject;
    //provare a inserire List per risparmio memoria!
 private List<Integer> propobject;
  

    public InstanceCombARFF(Integer item, List<Integer> propobject) {
        super(item);
        //LinkedList<String> a = new LinkedList<>();

        //a.addAll(value);

        this.propobject=propobject;
    }

    public List<Integer> getPropobject() {
        return propobject;
    }

    public void setPropobject(List<Integer> propobject) {
        this.propobject = propobject;
    }

}
