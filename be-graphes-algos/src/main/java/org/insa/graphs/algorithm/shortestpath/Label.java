package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class Label implements Comparable<Label> {
    private Node sommetCourant;
    private boolean marque;
    private double coutRealise;
    private Arc pere;

    public Label(Node sommet, boolean mar, double coutRea, Arc p) {
        sommetCourant = sommet;
        marque = mar;
        coutRealise = coutRea;
        pere = p;

    }

    public Node getSommet() {
        return this.sommetCourant;
    }

    public boolean getMarque() {
        return this.marque;
    }

    public double getcoutRealise() {
        return this.coutRealise;
    }

    public Arc getPere() {
        return this.pere;
    }

    public double getCost() {
        return getcoutRealise();
    }

    public void setCost(double a) {
        this.coutRealise = a;
    }

    public void setMarque(boolean a) {
        this.marque = a;
    }

    public void setPere(Arc a) {
        this.pere = a;
    }

        
    public double getTotalCost() {
        return this.getCost();
    }


    @Override
    public int compareTo(Label a) {
        return Double.compare(this.getCost(), a.getCost());
    }

}
