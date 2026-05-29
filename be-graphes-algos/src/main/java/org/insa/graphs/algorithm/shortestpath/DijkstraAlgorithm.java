package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    private int[] carteFrequentation = null;
    private int seuilMax = -1;
    private boolean modeFiltrage = false;

    // 1er Constru (de base)
    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
        this.modeFiltrage = false;
    }

    // 2eme Constru pour la freq
    public DijkstraAlgorithm(ShortestPathData data, int[] frequentations, int seuilMax) {
        super(data);
        this.carteFrequentation = frequentations;
        this.seuilMax = seuilMax;
        this.modeFiltrage = true;
    }

    @Override
    protected ShortestPathSolution doRun() {
        final ShortestPathData data = getInputData();
        int taille = data.getGraph().size();
        Label[] tabLabel = new Label[taille];
        BinaryHeap<Label> tasLabel = new BinaryHeap<>();

        notifyOriginProcessed(data.getOrigin());
        for (Node noeudCourant : data.getGraph().getNodes()) {
            tabLabel[noeudCourant.getId()] = new Label(noeudCourant, false, Double.POSITIVE_INFINITY, null);
            if (noeudCourant == data.getOrigin()) {
                tabLabel[noeudCourant.getId()].setCost(0);
                tasLabel.insert(tabLabel[noeudCourant.getId()]);
            }
        }

        while (!tasLabel.isEmpty()) {
            Label min = tasLabel.deleteMin();
            if (min.getSommet() == data.getDestination()) {
                break; 
            }
            min.setMarque(true);      
            for (Arc arc : min.getSommet().getSuccessors()) {
                if (data.isAllowed(arc)) {
                    Node destination = arc.getDestination();

                    // Changement
                    if (modeFiltrage) {
                        if (destination != data.getDestination() && destination != data.getOrigin()) {
                            if (carteFrequentation[destination.getId()] > seuilMax) {
                                continue; 
                            }
                        }
                    }

                    Label recherche = tabLabel[destination.getId()];
                    if (!recherche.getMarque()) {
                        double nvCout = min.getCost() + data.getCost(arc);
                        
                        if (nvCout < recherche.getCost()) {
                            if (recherche.getCost() != Double.POSITIVE_INFINITY) {
                                tasLabel.remove(recherche);
                            } else {
                                notifyNodeReached(destination);
                            }
                            recherche.setCost(nvCout);
                            recherche.setPere(arc);
                            tasLabel.insert(recherche);
                        }
                    }
                }
            }
        }

        Label destinationLabel = tabLabel[data.getDestination().getId()];
        ShortestPathSolution solution = null;

        if (destinationLabel.getPere() == null && data.getOrigin() != data.getDestination()) {
            solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        } else {
            notifyDestinationReached(data.getDestination());
            java.util.ArrayList<Arc> arcs = new java.util.ArrayList<>();
            Arc arcActuel = destinationLabel.getPere();
            while (arcActuel != null) {
                arcs.add(arcActuel);
                Label labelPrecedent = tabLabel[arcActuel.getOrigin().getId()];
                arcActuel = labelPrecedent.getPere();
            }
            java.util.Collections.reverse(arcs);
            Path shortestPath = new Path(data.getGraph(), arcs);
            solution = new ShortestPathSolution(data, Status.OPTIMAL, shortestPath);
        }

        return solution;
    }
}