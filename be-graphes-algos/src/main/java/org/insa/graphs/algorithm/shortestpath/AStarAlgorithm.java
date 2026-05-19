package org.insa.graphs.algorithm.shortestpath;
import java.util.List;

import org.insa.graphs.algorithm.AbstractInputData;
import org.insa.graphs.algorithm.AbstractSolution.Status;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) {
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun() {
        ShortestPathSolution solution = null;
        // retrieve data from the input problem (getInputData() is inherited from the
        // parent class ShortestPathAlgorithm)
        final ShortestPathData data = getInputData();
        // variable that will contain the solution of the shortest path problem

        // TODO: implement the A* algorithm
        int taille = data.getGraph().size();
        LabelStar[] tabLabel = new LabelStar[taille]; // On enlève le -1 !
        BinaryHeap<Label> tasLabel = new BinaryHeap<>();
        List<Node> nodes = data.getGraph().getNodes();
        notifyOriginProcessed(data.getOrigin());
        // On récupère la vitesse max du graphe en m/s pour le calcul en temps
double vitesseMax = 130.0 / 3.6; // Vitesse max par défaut (sur autoroute)
if (data.getGraph().getGraphInformation().hasMaximumSpeed()) {
    vitesseMax = data.getGraph().getGraphInformation().getMaximumSpeed() / 3.6;
}

for (int i = 0; i < taille; i++) {
    Node noeudCourant = nodes.get(i);
    double distance = noeudCourant.getPoint().distanceTo(data.getDestination().getPoint());
    double coutEstim = distance;
    if (data.getMode() == AbstractInputData.Mode.TIME) {
        coutEstim = distance / vitesseMax;
    }
    tabLabel[i] = new LabelStar(noeudCourant, false, Double.POSITIVE_INFINITY, null, coutEstim);
    if (noeudCourant == data.getOrigin()) {
        tabLabel[i].setCost(0);
        tasLabel.insert(tabLabel[i]);
    }
}

while (!tasLabel.isEmpty()) {
    Label min = tasLabel.deleteMin();
    if (min.getSommet() == data.getDestination()) {
        break;
    }
    min.setMarque(true);
    notifyNodeMarked(min.getSommet());
    for (Arc arc : min.getSommet().getSuccessors()) {
        if (data.isAllowed(arc)) {
            LabelStar recherche = tabLabel[arc.getDestination().getId()]; 
            if (recherche.getMarque()) {
                continue;
            }
            double nvCout = min.getCost() + data.getCost(arc);
            // Dans AStarAlgorithm.java
            if (nvCout < recherche.getCost()) {
                // On utilise la même valeur d'initialisation !
                if (recherche.getCost() != Double.POSITIVE_INFINITY) { 
                    tasLabel.remove(recherche);
                } else {
                    notifyNodeReached(arc.getDestination());
                }
                recherche.setCost(nvCout);
                recherche.setPere(arc);
                tasLabel.insert(recherche);
            }
        }
    }
}
        Label destinationLabel = tabLabel[data.getDestination().getId()];
        if (destinationLabel.getPere() == null
                && data.getOrigin() != data.getDestination()) {
            solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        }
        else {
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