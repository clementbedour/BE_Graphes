package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class LabelStar extends Label {
    private double coutEstim;

    public LabelStar(Node sommet, boolean mar, double coutRea, Arc p, double coutEstim) {
        super(sommet, mar, coutRea, p);
        this.coutEstim = coutEstim;

    }


    public double getEstimCost() {
        return this.coutEstim;
    }

    public void setEstimCost(double a) {
        this.coutEstim = a;
    }


    @Override
    public double getTotalCost() {
        return this.getCost() + this.coutEstim; 
    }
}
