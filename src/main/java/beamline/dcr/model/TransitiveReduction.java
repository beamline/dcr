package beamline.dcr.model;

import org.apache.commons.lang3.tuple.Triple;

import java.util.*;

public class TransitiveReduction {

    private List<String> activityList;
    private BitSet[] originalMatrix;
    private Set<Triple<String, String, DcrModel.RELATION>> allPattersWithPattern;
    private DcrModel.RELATION reducedPattern;
    public TransitiveReduction() {
    }

    private void setUp(UnionRelationSet unionRelationSet, DcrModel.RELATION pattern){
        this.reducedPattern = pattern;
        this.allPattersWithPattern = unionRelationSet.getDcrRelationWithPattern(pattern);
        //Consider ordering Set in alphabetic order
        //Get set of unique activies
        Set<String> activitySet = new LinkedHashSet<String>();
        for (Triple<String, String, DcrModel.RELATION> patternRelation : allPattersWithPattern) {
            activitySet.add(patternRelation.getLeft());
            activitySet.add(patternRelation.getMiddle());
        }
        this.activityList = new ArrayList<>(activitySet);
        //initialize originalMatrix
        this.originalMatrix = new BitSet[activityList.size()];
        for(int i = 0; i < originalMatrix.length; i++) {
            this.originalMatrix[i] = new BitSet(activityList.size());
        }

    }

    private void transformToPathMatrix(final BitSet[] matrix) {
        // compute path matrix
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                if (i == j) {
                    continue;
                }
                if (matrix[j].get(i)) {
                    for (int k = 0; k < matrix.length; k++) {
                        if (!matrix[j].get(k)) {
                            matrix[j].set(k, matrix[i].get(k));
                        }
                    }
                }
            }
        }
    }
    private void transitiveReduction(BitSet[] pathMatrix){
        // transitively reduce
        for (int j = 0; j < pathMatrix.length; j++) {
            for (int i = 0; i < pathMatrix.length; i++) {
                if (pathMatrix[i].get(j)) {
                    for (int k = 0; k < pathMatrix.length; k++) {
                        if (pathMatrix[j].get(k)) {
                            pathMatrix[i].set(k, false);
                        }
                    }
                }
            }
        }
    }

    public void reduce(UnionRelationSet unionRelationSet, DcrModel.RELATION patternToReduce){
        setUp(unionRelationSet,patternToReduce);
        //intialize matrix with edges
        for (Triple<String, String, DcrModel.RELATION> relationPattern : allPattersWithPattern) {
            String src = relationPattern.getLeft();
            String tar = relationPattern.getMiddle();

            int i1 = activityList.indexOf(src);
            int i2 = activityList.indexOf(tar);
            this.originalMatrix[i1].set(i2);
        }

        final BitSet[] pathMatrix = originalMatrix;

        transformToPathMatrix(pathMatrix);

        // create reduced matrix from path matrix
        final BitSet[] transitivelyReducedMatrix = pathMatrix;

        transitiveReduction(transitivelyReducedMatrix);


        for (int i = 0; i < originalMatrix.length; i++) {
            for (int j = 0; j < originalMatrix.length; j++) {
                if (!transitivelyReducedMatrix[i].get(j)) {
                    String src = activityList.get(i);
                    String tar = activityList.get(j);
                    unionRelationSet.removeDCRRelation(Triple.of(src,tar, reducedPattern));

                }
            }
        }

        //save new set as
        /*for (Triple<String, String, DcrModel.RELATION> d : allPattersWithPattern ){
            System.out.println(d.toString());
        }*/
       


    }





}
