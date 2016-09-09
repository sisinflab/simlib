package it.poliba.sisinflab.simlib.featureSelection.instance;


/**
 * Defines the concept of a generic instance
 *
 * @author Corrado Magarelli on 19/05/2016.
 */
public abstract class InstanceARFF {

    
    protected Integer item;
    //protected String propertie;

    public InstanceARFF(){}

    public InstanceARFF(Integer item){
    //, String propertie){
        this.item = item;
        //this.propertie = propertie;
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }
}
