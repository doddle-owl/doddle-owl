/*
 * @(#)  2006/09/28
 */

package jp.ac.keio.ae.comp.yamaguti.doddle.utils;

import java.util.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.*;

/**
 * @author takeshi morita
 */
public class CalcConceptDistanceUtil {

    public static Set<List[]> makeCombination(Set pathSet1, Set pathSet2) {
        Set<List[]> combinationSet = new HashSet<List[]>();
        for (Iterator i = pathSet1.iterator(); i.hasNext();) {
            List path1 = (List) i.next();
            for (Iterator j = pathSet2.iterator(); j.hasNext();) {
                List path2 = (List) j.next();
                combinationSet.add(new List[] { path1, path2});
            }
        }
        return combinationSet;
    }

    private static Set<List[]> getCombinationSet(Concept c1, Concept c2) {
        Set<List<Concept>> pathSet1 = new HashSet<List<Concept>>();
        Set<List<Concept>> pathSet2 = new HashSet<List<Concept>>();
        if (c1.getNameSpace().equals(DODDLE.EDR_URI) && c2.getNameSpace().equals(DODDLE.EDR_URI)) {
            pathSet1 = EDRTree.getEDRTree().getPathToRootSet(c1.getLocalName());
            pathSet2 = EDRTree.getEDRTree().getPathToRootSet(c2.getLocalName());
        } else if (c1.getNameSpace().equals(DODDLE.EDRT_URI) && c2.getNameSpace().equals(DODDLE.EDRT_URI)) {
            pathSet1 = EDRTree.getEDRTTree().getPathToRootSet(c1.getLocalName());
            pathSet2 = EDRTree.getEDRTTree().getPathToRootSet(c2.getLocalName());
        } else if (c1.getNameSpace().equals(DODDLE.WN_URI) && c2.getNameSpace().equals(DODDLE.WN_URI)) {
            pathSet1 = WordNetDic.getPathToRootSet(new Long(c1.getLocalName()));
            pathSet2 = WordNetDic.getPathToRootSet(new Long(c2.getLocalName()));
        }
        return makeCombination(pathSet1, pathSet2);
    }

    private static List getConceptDiff(Concept concept1, Concept concept2) {
        Set<List[]> combinationSet = getCombinationSet(concept1, concept2);
        List conceptDiffList = new ArrayList();
        for (List[] combination : combinationSet) {
            List path1 = combination[0];
            List path2 = combination[1];
            conceptDiffList.add(calcConceptDiff(path1, path2));
        }
        Collections.sort(conceptDiffList);
        return conceptDiffList;
    }

    private static List getExtendDiff(Concept concept1, Concept concept2) {
        Set combinationSet = getCombinationSet(concept1, concept2);

        List conceptDiffList = new ArrayList();
        for (Iterator i = combinationSet.iterator(); i.hasNext();) {
            List[] combination = (List[]) i.next();
            List path1 = combination[0];
            List path2 = combination[1];
            Integer distance = calcConceptDiff((Concept) path2.get(path2.size() - 1), path1);
            if (distance != null) {
                conceptDiffList.add(distance);
            }
        }
        Collections.sort(conceptDiffList);
        return conceptDiffList;
    }

    private static Integer calcConceptDiff(Concept c, List path) {
        if (path.contains(c)) {
            for (int i = path.size() - 1, diff = 0; 0 <= i; i--, diff++) {
                if (path.get(i).equals(c)) { return new Integer(diff); }
            }
        }
        return null;
    }

    private static Integer calcConceptDiff2(List path1, List path2) {
        for (int i = path1.size() - 1; 0 <= i; i--) {
            Concept c1 = (Concept) path1.get(i);
            for (int j = path2.size() - 1; 0 <= j; j--) {
                Concept c2 = (Concept) path2.get(j);
                if (c1.equals(c2)) {
                    int len1 = path1.size() - i - 1;
                    int len2 = path2.size() - j - 1;
                    return new Integer(len1 + len2);
                }
            }
        }
        return null;
    }

    private static Integer calcConceptDiff(List path1, List path2) {
        Integer diff = null;
        diff = calcConceptDiff((Concept) path2.get(path2.size() - 1), path1);
        if (diff != null) { return diff; }
        diff = calcConceptDiff((Concept) path1.get(path1.size() - 1), path2);
        if (diff != null) { return diff; }
        diff = calcConceptDiff2(path1, path2);
        if (diff != null) { return diff; }
        return new Integer(path1.size() + path2.size());
    }

    public static int getShortestConceptDiff(Concept c1, Concept c2) {
        List conceptDiffList = getConceptDiff(c1, c2);
        if (conceptDiffList.size() == 0) { return 0; }
        Integer longestConceptDiff = (Integer) conceptDiffList.get(0);
        return longestConceptDiff.intValue();
    }

    public static int getLongestConceptDiff(Concept c1, Concept c2) {
        List conceptDiffList = getConceptDiff(c1, c2);
        if (conceptDiffList.size() == 0) { return 0; }
        Integer longestConceptDiff = (Integer) conceptDiffList.get(conceptDiffList.size() - 1);
        return longestConceptDiff.intValue();
    }

    public static int getAverageConceptDiff(Concept c1, Concept c2) {
        List conceptDiffList = getConceptDiff(c1, c2);
        int total = 0;
        for (Iterator i = conceptDiffList.iterator(); i.hasNext();) {
            Integer length = (Integer) i.next();
            total += length.intValue();
        }
        return total / conceptDiffList.size();
    }

    public static boolean isShortestExtendMatching(Concept outputConcept, Concept inputConcept, int threshold) {
        List conceptDiffList = getExtendDiff(outputConcept, inputConcept);
        if (conceptDiffList.size() == 0) { return false; }
        Integer longestConceptDiff = (Integer) conceptDiffList.get(0);
        int distance = longestConceptDiff.intValue();
        return distance <= threshold;
    }

    public static boolean isLongestExtendMatching(Concept outputConcept, Concept inputConcept, int threshold) {
        List conceptDiffList = getExtendDiff(outputConcept, inputConcept);
        if (conceptDiffList.size() == 0) { return false; }
        Integer longestConceptDiff = (Integer) conceptDiffList.get(conceptDiffList.size() - 1);
        int distance = longestConceptDiff.intValue();
        return distance <= threshold;
    }

    public static boolean isAverageExtendMatching(Concept outputConcept, Concept inputConcept, int threshold) {
        List conceptDiffList = getExtendDiff(outputConcept, inputConcept);
        if (conceptDiffList.size() == 0) { return false; }
        int total = 0;
        for (Iterator i = conceptDiffList.iterator(); i.hasNext();) {
            Integer length = (Integer) i.next();
            total += length.intValue();
        }
        int distance = total / conceptDiffList.size();
        return distance <= threshold;
    }

    public static void testConceptDiff() {
        System.out.println(getShortestConceptDiff(EDRDic.getEDRConcept("3cf5e5"), EDRDic.getEDRConcept("30f6af")));
        System.out.println(getShortestConceptDiff(EDRDic.getEDRConcept("30f6af"), EDRDic.getEDRConcept("3cf5e5")));
        System.out.println(getShortestConceptDiff(EDRDic.getEDRConcept("1f585c"), EDRDic.getEDRConcept("3c90af")));
        System.out.println(getShortestConceptDiff(EDRDic.getEDRConcept("3cfd0d"), EDRDic.getEDRConcept("3cf5fb")));
        System.out.println(getShortestConceptDiff(EDRDic.getEDRConcept("3cf5fb"), EDRDic.getEDRConcept("3cfd0d")));
        System.out.println(getShortestConceptDiff(EDRDic.getEDRConcept("3cf5e5"), EDRDic.getEDRConcept("3cfd0d")));
        System.out.println(getShortestConceptDiff(EDRDic.getEDRConcept("3bdc67"), EDRDic.getEDRConcept("101b25")));
        System.out.println(getShortestConceptDiff(EDRDic.getEDRConcept("3bdc67"), EDRDic.getEDRConcept("3bc83c")));
        System.out.println(getLongestConceptDiff(EDRDic.getEDRConcept("3bdc67"), EDRDic.getEDRConcept("3bc83c")));
        System.out.println(getAverageConceptDiff(EDRDic.getEDRConcept("3bdc67"), EDRDic.getEDRConcept("3bc83c")));
        System.out.println(getAverageConceptDiff(WordNetDic.getInstance().getWNConcept("2001223"), WordNetDic
                .getInstance().getWNConcept("2037721")));
        System.out.println(getAverageConceptDiff(EDRDic.getEDRTConcept("2f16f0"), EDRDic.getEDRTConcept("2f14dd")));
    }

    public static void main(String[] args) {
        EDRDic.initEDRDic();
        testConceptDiff();
    }
}
