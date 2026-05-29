package org.insa.graphs.algorithm.shortestpath;

import java.util.List;

import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    int taille;
    List<Node> nodes;


    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
        this.taille = data.getGraph().size();
        this.nodes = data.getGraph().getNodes();
    }

    public DijkstraAlgorithm(ShortestPathData data,List<Node> List) {
        super(data);
        this.taille = List.size();
        this.nodes = List;
    }

    @Override
    protected ShortestPathSolution doRun() {
        // retrieve data from the input problem (getInputData() is inherited from the
        // parent class ShortestPathAlgorithm)
        final ShortestPathData data = getInputData();
        // variable that will contain the solution of the shortest path problem
        ShortestPathSolution solution = null;

        // TODO: implement the Dijkstra algorithm
        Label[] tabLabel = new Label[taille]; // On enlève le -1 !
        BinaryHeap<Label> tasLabel = new BinaryHeap<>();

        notifyOriginProcessed(data.getOrigin());
        for (int i = 0; i < taille; i++) {
            Node noeudCourant = nodes.get(i);
            tabLabel[i] = new Label(noeudCourant, false, Integer.MAX_VALUE, null);
            if (noeudCourant == data.getOrigin()) {
                tabLabel[i].setCost(0);
                tasLabel.insert(tabLabel[i]);
            }
        }
        Label min;
        min = tasLabel.findMin();
        while (!tasLabel.isEmpty() && min.getSommet() != data.getDestination()) {
            min = tasLabel.deleteMin();
            min.setMarque(true);
            //System.out.println("Cout du label Marqué :" + min.getCost() +"\n");
            for (Arc arc : min.getSommet().getSuccessors()) {
                if (data.isAllowed(arc)) {
                    notifyNodeReached(arc.getDestination());
                    Label recherche = tabLabel[arc.getDestination().getId()];
                    double nvCout = min.getCost() + data.getCost(arc);
                    if (nvCout < recherche.getCost()) {
                        if (recherche.getCost() != Integer.MAX_VALUE) {
                            tasLabel.remove(recherche);
                        }
                        recherche.setCost(nvCout);
                        recherche.setPere(arc);
                        tasLabel.insert(recherche);
                    }
                }
            }
        }

        Label destinationLabel = tabLabel[data.getDestination().getId()];

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

        // when the algorithm terminates, return the solution that has been found
        return solution;

    }

}
