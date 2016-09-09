package it.poliba.sisinflab.simlib.datamodel.abstat;

import it.poliba.sisinflab.simlib.datamodel.Arrow;

/**
 * Created by giorgio on 12/05/16.
 */
public class AbstatArrow extends Arrow{

    private int frequency = 0;

    protected AbstatArrow(AbstatNode property, String direction, int frequency) {
        super(property, direction);
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
