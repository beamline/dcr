package beamline.dcr.model.relations;

import beamline.dcr.annotations.ExposedDcrPattern;
import beamline.dcr.model.DcrModel;
import beamline.dcr.model.UnionRelationSet;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import java.util.Set;

@ExposedDcrPattern(
        name="Sequence",
        latticeLevel = 3
)
public class Sequence implements RelationPattern {

    private String type= "Sequence";

    @Override
    public void populateConstraint(UnionRelationSet unionRelationSet) {

        Set<Pair<String,String>> dcrImmConditions =
                unionRelationSet.getDcrRelationWithPattern(DcrModel.RELATION.IMMCONDITION);

        Set<Pair<String,String>> dcrImmResponse =
                unionRelationSet.getDcrRelationWithPattern(DcrModel.RELATION.IMMRESPONSE);

        for (Pair<String, String> immCondRelation : dcrImmConditions) {
            String src = immCondRelation.getLeft();
            String tar = immCondRelation.getRight();
            if (dcrImmResponse.contains(Pair.of(src,tar))){

                unionRelationSet.addDcrRelation(Triple.of(src, tar, DcrModel.RELATION.SEQUENCE));

                unionRelationSet.removeDCRRelation(Triple.of(src, tar, DcrModel.RELATION.IMMCONDITION));
                unionRelationSet.removeDCRRelation(Triple.of(src, tar, DcrModel.RELATION.IMMRESPONSE));
            }
        }
    }

    @Override
    public String getType() {
        return type;
    }

}