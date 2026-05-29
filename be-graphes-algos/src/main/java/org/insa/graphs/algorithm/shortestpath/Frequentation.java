package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Node;

public class Frequentation{
    private Node sommetCourant;
    private int frequentation;

    public Frequentation(Node sommet) {
        sommetCourant = sommet;
        frequentation = sommet.getNumberOfSuccessors();

    }

    public Node getNode() {
        return this.sommetCourant;
    }

    public int getFrequentation(){
        return this.frequentation;
    }

}
