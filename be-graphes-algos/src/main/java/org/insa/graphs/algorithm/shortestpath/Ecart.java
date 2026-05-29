package org.insa.graphs.algorithm.shortestpath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.insa.graphs.algorithm.AbstractInputData;
import org.insa.graphs.algorithm.AbstractSolution;
import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;

public class Ecart extends ShortestPathAlgorithm {

    public Ecart(ShortestPathData data) {
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun() {
        final ShortestPathData data = getInputData();
        
        double tolerance = 1.2;
        
        List<Node> tabNodes = data.getGraph().getNodes();
        int taille = tabNodes.size();
        Frequentation[] tabfreq = new Frequentation[taille];
        
        // Init du tab
        for(int i = 0; i < taille; i++){
            Node node = tabNodes.get(i);
            tabfreq[i] = new Frequentation(node);
        }
        
        // Tri du tab dans l'odre croissant de fréquentation
        Arrays.sort(tabfreq, Comparator.comparingInt(Frequentation::getFrequentation));
        

        List<ArcInspector> arcInspectors = ArcInspectorFactory.getAllFilters();
        ShortestPathData sPData = new ShortestPathData(data.getGraph(), data.getOrigin(), data.getDestination(), arcInspectors.get(0));
        DijkstraAlgorithm graphDij = new DijkstraAlgorithm(sPData);
        ShortestPathSolution dijSol = graphDij.run();
        
        // Si aucun chemin de base n'existe, on s'arrête là
        if (!dijSol.isFeasible()) {
            return new ShortestPathSolution(data, AbstractSolution.Status.INFEASIBLE);
        }
        
        Path pathDij = dijSol.getPath();
        
        
        double coutOptimal;
        if (data.getMode() == AbstractInputData.Mode.TIME) {
            coutOptimal = pathDij.getMinimumTravelTime();
        } else {
            coutOptimal = pathDij.getLength();
        }
        double coutMaxAutorise = coutOptimal * tolerance;

       
        ShortestPathSolution bestSolution = dijSol; // Par défaut, la solution est le chemin normal
        int debut = 0;
        int fin = taille - 1;

        while (debut <= fin) {
            int milieu = (debut + fin) / 2;
            List<Node> sommetsAutorises = new ArrayList<>();
            for(int indice = 0; indice <= milieu; indice++){
                sommetsAutorises.add(tabfreq[indice].getNode());
            }
            if (!sommetsAutorises.contains(data.getOrigin())) sommetsAutorises.add(data.getOrigin());
            if (!sommetsAutorises.contains(data.getDestination())) sommetsAutorises.add(data.getDestination());
            DijkstraAlgorithm nvGraphDij = new DijkstraAlgorithm(sPData, sommetsAutorises);
            ShortestPathSolution nvDijSol = nvGraphDij.run();
            if (nvDijSol.isFeasible()) {
                double nvCout;
                if (data.getMode() == AbstractInputData.Mode.TIME) {
                    nvCout = nvDijSol.getPath().getMinimumTravelTime();
                } else {
                    nvCout = nvDijSol.getPath().getLength();
                }
                if (nvCout <= coutMaxAutorise) {
                    bestSolution = nvDijSol;
                    fin = milieu - 1;
                } else {
                    debut = milieu + 1;
                }
            } else {      
                debut = milieu + 1;
            }
        }
        
        return bestSolution;    
    }
}