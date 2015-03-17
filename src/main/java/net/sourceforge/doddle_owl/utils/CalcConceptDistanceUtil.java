/*
 * Project Name: DODDLE-OWL (a Domain Ontology rapiD DeveLopment Environment - OWL extension)
 * Project Website: http://doddle-owl.sourceforge.net/
 * 
 * Copyright (C) 2004-2015 Yamaguchi Laboratory, Keio University. All rights reserved. 
 * 
 * This file is part of DODDLE-OWL.
 * 
 * DODDLE-OWL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * DODDLE-OWL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with DODDLE-OWL.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.sourceforge.doddle_owl.utils;

import java.io.*;
import java.util.*;

import net.sourceforge.doddle_owl.data.*;

/**
 * @author Takeshi Morita
 */
public class CalcConceptDistanceUtil {

    public static Set<List<String>[]> makeCombination(Set<List<String>> pathSet1, Set<List<String>> pathSet2) {
        Set<List<String>[]> combinationSet = new HashSet<List<String>[]>();
        for (List<String> path1 : pathSet1) {
            for (List<String> path2 : pathSet2) {
                combinationSet.add(new List[] { path1, path2});
            }
        }
        return combinationSet;
    }

    private static Set<List<String>> getPathToRootSet(Concept c) {
        Set<List<String>> pathSet = new HashSet<List<String>>();

        pathSet = OWLOntologyManager.getURIPathToRootSet(c.getURI());
        if (pathSet.size() != 0) { return pathSet; }
        if (c.getNameSpace().equals(DODDLEConstants.EDR_URI)) {
            pathSet = EDRTree.getEDRTree().getURIPathToRootSet(c.getLocalName());
        } else if (c.getNameSpace().equals(DODDLEConstants.EDRT_URI)) {
            pathSet = EDRTree.getEDRTTree().getURIPathToRootSet(c.getLocalName());
        } else if (c.getNameSpace().equals(DODDLEConstants.WN_URI)) {
            pathSet = WordNetDic.getURIPathToRootSet(new Long(c.getLocalName()));
        } else if (c.getNameSpace().equals(DODDLEConstants.JPN_WN_URI)) {
            pathSet = JPNWNTree.getJPNWNTree().getURIPathToRootSet(c.getLocalName());
        }
        return pathSet;
    }

    private static List<Integer> getDepthList(Set<List<String>> pathSet) {
        List<Integer> depthList = new ArrayList<Integer>();
        for (List<String> path : pathSet) {
            depthList.add(path.size());
        }
        return depthList;
    }

    private static Set<List<String>[]> getCombinationSet(Concept c1, Concept c2) {
        Set<List<String>> pathSet1 = new HashSet<List<String>>();
        Set<List<String>> pathSet2 = new HashSet<List<String>>();

        pathSet1 = OWLOntologyManager.getURIPathToRootSet(c1.getURI());
        pathSet2 = OWLOntologyManager.getURIPathToRootSet(c2.getURI());
        if (0 < pathSet1.size() && 0 < pathSet2.size()) { return makeCombination(pathSet1, pathSet2); }

        if (c1.getNameSpace().equals(DODDLEConstants.EDR_URI) && c2.getNameSpace().equals(DODDLEConstants.EDR_URI)) {
            pathSet1 = EDRTree.getEDRTree().getURIPathToRootSet(c1.getLocalName());
            pathSet2 = EDRTree.getEDRTree().getURIPathToRootSet(c2.getLocalName());
        } else if (c1.getNameSpace().equals(DODDLEConstants.EDRT_URI)
                && c2.getNameSpace().equals(DODDLEConstants.EDRT_URI)) {
            pathSet1 = EDRTree.getEDRTTree().getURIPathToRootSet(c1.getLocalName());
            pathSet2 = EDRTree.getEDRTTree().getURIPathToRootSet(c2.getLocalName());
        } else if (c1.getNameSpace().equals(DODDLEConstants.WN_URI) && c2.getNameSpace().equals(DODDLEConstants.WN_URI)) {
            pathSet1 = WordNetDic.getURIPathToRootSet(new Long(c1.getLocalName()));
            pathSet2 = WordNetDic.getURIPathToRootSet(new Long(c2.getLocalName()));
        } else if (c1.getNameSpace().equals(DODDLEConstants.JPN_WN_URI)
                && c2.getNameSpace().equals(DODDLEConstants.JPN_WN_URI)) {
            pathSet1 = JPNWNTree.getJPNWNTree().getURIPathToRootSet(c1.getLocalName());
            pathSet2 = JPNWNTree.getJPNWNTree().getURIPathToRootSet(c2.getLocalName());
        }
        return makeCombination(pathSet1, pathSet2);
    }

    private static List<ConceptDistanceModel> getConceptDistanceModelList(Concept concept1, Concept concept2) {
        Set<List<String>[]> combinationSet = getCombinationSet(concept1, concept2);
        List<ConceptDistanceModel> conceptDistanceModelList = new ArrayList<ConceptDistanceModel>();
        for (List<String>[] combination : combinationSet) {
            List<String> path1 = combination[0];
            List<String> path2 = combination[1];
            ConceptDistanceModel cdModel = new ConceptDistanceModel(concept1, concept2);
            conceptDistanceModelList.add(getConceptDistanceModel(path1, path2, cdModel));
        }
        Collections.sort(conceptDistanceModelList);
        return conceptDistanceModelList;
    }

    private static List<ConceptDistanceModel> getExtendConceptDistanceModelList(Concept concept1, Concept concept2) {
        Set<List<String>[]> combinationSet = getCombinationSet(concept1, concept2);

        List<ConceptDistanceModel> conceptDistanceModelList = new ArrayList<ConceptDistanceModel>();
        for (List<String>[] combination : combinationSet) {
            List<String> path1 = combination[0];
            List<String> path2 = combination[1];
            ConceptDistanceModel cdModel = new ConceptDistanceModel(concept1, concept2);
            calcConceptDistance(path2.get(path2.size() - 1), path1, cdModel);
            if (cdModel.getCommonAncestor() != null) {
                conceptDistanceModelList.add(cdModel);
            }
        }
        Collections.sort(conceptDistanceModelList);
        return conceptDistanceModelList;
    }

    private static void calcConceptDistance(String c, List<String> path, ConceptDistanceModel cdModel) {
        if (path.contains(c)) {
            for (int i = path.size() - 1, distance = 0; 0 <= i; i--, distance++) {
                if (path.get(i).equals(c)) {
                    if (c.equals(cdModel.getConcept1().getURI())) {
                        Concept commonAncestorConcept = cdModel.getConcept1();
                        cdModel.setCommonAncestor(commonAncestorConcept);
                        cdModel.setC1ToCommonAncestorDistance(0);
                        cdModel.setC2ToCommonAncestorDistance(distance);
                        List<Integer> depthList = getDepthList(getPathToRootSet(commonAncestorConcept));
                        cdModel.setCommonAncestorDepth(depthList);
                    } else {
                        Concept commonAncestorConcept = cdModel.getConcept2();
                        cdModel.setCommonAncestor(commonAncestorConcept);
                        cdModel.setC1ToCommonAncestorDistance(distance);
                        cdModel.setC2ToCommonAncestorDistance(0);
                        List<Integer> depthList = getDepthList(getPathToRootSet(commonAncestorConcept));
                        cdModel.setCommonAncestorDepth(depthList);
                    }
                    return;
                }
            }
        }
    }

    private static void calcConceptDistance2(List<String> path1, List<String> path2, ConceptDistanceModel cdModel) {
        for (int i = path1.size() - 1; 0 <= i; i--) {
            String c1 = path1.get(i);
            for (int j = path2.size() - 1; 0 <= j; j--) {
                String c2 = path2.get(j);
                if (c1.equals(c2)) {
                    int len1 = path1.size() - i - 1;
                    int len2 = path2.size() - j - 1;
                    Concept commonAncestorConcept = DODDLEDic.getConcept(c1);
                    cdModel.setCommonAncestor(commonAncestorConcept);
                    cdModel.setC1ToCommonAncestorDistance(len1);
                    cdModel.setC2ToCommonAncestorDistance(len2);
                    List<Integer> depthList = getDepthList(getPathToRootSet(commonAncestorConcept));
                    cdModel.setCommonAncestorDepth(depthList);
                    return;
                }
            }
        }
    }

    private static ConceptDistanceModel getConceptDistanceModel(List<String> path1, List<String> path2,
            ConceptDistanceModel cdModel) {
        calcConceptDistance(path2.get(path2.size() - 1), path1, cdModel);
        if (cdModel.getCommonAncestor() != null) { return cdModel; }
        calcConceptDistance(path1.get(path1.size() - 1), path2, cdModel);
        if (cdModel.getCommonAncestor() != null) { return cdModel; }
        calcConceptDistance2(path1, path2, cdModel);
        if (cdModel.getCommonAncestor() != null) { return cdModel; }
        cdModel.setC1ToCommonAncestorDistance(path1.size());
        cdModel.setC1ToCommonAncestorDistance(path2.size());
        Concept commonAncestorConcept = DODDLEDic.getConcept(path1.get(0));
        cdModel.setCommonAncestor(commonAncestorConcept);
        List<Integer> depthList = getDepthList(getPathToRootSet(commonAncestorConcept));
        cdModel.setCommonAncestorDepth(depthList);
        return cdModel;
    }

    public static int getShortestConceptDistance(Concept c1, Concept c2) {
        List<ConceptDistanceModel> conceptDistanceList = getConceptDistanceModelList(c1, c2);
        if (conceptDistanceList.size() == 0) { return 0; }
        return conceptDistanceList.get(0).getConceptDistance();
    }

    public static int getLongestConceptDistance(Concept c1, Concept c2) {
        List<ConceptDistanceModel> conceptDistanceList = getConceptDistanceModelList(c1, c2);
        if (conceptDistanceList.size() == 0) { return 0; }
        return conceptDistanceList.get(conceptDistanceList.size() - 1).getConceptDistance();
    }

    public static int getAverageConceptDistance(Concept c1, Concept c2) {
        List<ConceptDistanceModel> conceptDistanceList = getConceptDistanceModelList(c1, c2);
        int total = 0;
        for (ConceptDistanceModel cdModel : conceptDistanceList) {
            total += cdModel.getConceptDistance();
        }
        return total / conceptDistanceList.size();
    }

    public static boolean isShortestExtendMatching(Concept outputConcept, Concept inputConcept, int threshold) {
        List<ConceptDistanceModel> conceptDistanceList = getExtendConceptDistanceModelList(outputConcept, inputConcept);
        if (conceptDistanceList.size() == 0) { return false; }
        int distance = conceptDistanceList.get(0).getConceptDistance();
        return distance <= threshold;
    }

    public static boolean isLongestExtendMatching(Concept outputConcept, Concept inputConcept, int threshold) {
        List<ConceptDistanceModel> conceptDistanceList = getExtendConceptDistanceModelList(outputConcept, inputConcept);
        if (conceptDistanceList.size() == 0) { return false; }
        int distance = conceptDistanceList.get(conceptDistanceList.size() - 1).getConceptDistance();
        return distance <= threshold;
    }

    public static boolean isAverageExtendMatching(Concept outputConcept, Concept inputConcept, int threshold) {
        List<ConceptDistanceModel> conceptDistanceList = getExtendConceptDistanceModelList(outputConcept, inputConcept);
        if (conceptDistanceList.size() == 0) { return false; }
        int total = 0;
        for (ConceptDistanceModel cdModel : conceptDistanceList) {
            total += cdModel.getConceptDistance();
        }
        int distance = total / conceptDistanceList.size();
        return distance <= threshold;
    }

    public static void testEDRandWNConceptDistance() {
        EDRDic.initEDRDic();
        System.out.println(getShortestConceptDistance(EDRDic.getEDRConcept("3cf5e5"), EDRDic.getEDRConcept("30f6af")));
        System.out.println(getShortestConceptDistance(EDRDic.getEDRConcept("30f6af"), EDRDic.getEDRConcept("3cf5e5")));
        System.out.println(getShortestConceptDistance(EDRDic.getEDRConcept("1f585c"), EDRDic.getEDRConcept("3c90af")));
        System.out.println(getShortestConceptDistance(EDRDic.getEDRConcept("3cfd0d"), EDRDic.getEDRConcept("3cf5fb")));
        System.out.println(getShortestConceptDistance(EDRDic.getEDRConcept("3cf5fb"), EDRDic.getEDRConcept("3cfd0d")));
        System.out.println(getShortestConceptDistance(EDRDic.getEDRConcept("3cf5e5"), EDRDic.getEDRConcept("3cfd0d")));
        System.out.println(getShortestConceptDistance(EDRDic.getEDRConcept("3bdc67"), EDRDic.getEDRConcept("101b25")));
        System.out.println(getShortestConceptDistance(EDRDic.getEDRConcept("3bdc67"), EDRDic.getEDRConcept("3bc83c")));
        System.out.println(getLongestConceptDistance(EDRDic.getEDRConcept("3bdc67"), EDRDic.getEDRConcept("3bc83c")));
        System.out.println(getAverageConceptDistance(EDRDic.getEDRConcept("3bdc67"), EDRDic.getEDRConcept("3bc83c")));
        System.out.println(getAverageConceptDistance(WordNetDic.getInstance().getWNConcept("2001223"), WordNetDic
                .getInstance().getWNConcept("2037721")));
        System.out.println(getAverageConceptDistance(EDRDic.getEDRTConcept("2f16f0"), EDRDic.getEDRTConcept("2f14dd")));
    }

    public static void getOWLConceptDistance(String fname, String uri1, String uri2) {
        OWLOntologyManager.addRefOntology(new File(fname));
        Concept c1 = OWLOntologyManager.getConcept(uri1);
        Concept c2 = OWLOntologyManager.getConcept(uri2);

        ConceptDistanceModel resultModel = null;
        List<ConceptDistanceModel> cdModelList = getConceptDistanceModelList(c1, c2);
        for (ConceptDistanceModel cdModel : cdModelList) {
            if (resultModel == null) {
                resultModel = cdModel;
                continue;
            }
            if (resultModel.getShortestCommonAncestorDepth() < cdModel.getShortestCommonAncestorDepth()) {
                resultModel = cdModel;
            }
        }
        System.err.println(resultModel);
    }

    public static void getOWLLongestDepth(String fname) {
        OWLOntologyManager.addRefOntology(new File(fname));
        ReferenceOWLOntology refOnt = OWLOntologyManager.getRefOntology(new File(fname).getAbsolutePath());
        int depth = 0;
        Set<String> classSet = refOnt.getClassSet();
        for (String cls : classSet) {
            Set<List<Concept>> pathToRootSet = refOnt.getPathToRootSet(cls);
            for (List<Concept> path : pathToRootSet) {
                if (depth < path.size()) {
                    depth = path.size();
                    System.out.println(path);
                }
            }
        }
        System.out.println("depth: " + depth);
    }

    public static void main(String[] args) {
        if (args.length != 2) { return; }
        Translator.loadDODDLEComponentOntology(DODDLEConstants.LANG);
        String ontFileName = args[0];
        String inputFileName = args[1];

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName),
                    "UTF-8"));
            // while (reader.ready()) {
            // String line = reader.readLine();
            // String[] uris = line.split("\\s+");
            // getOWLConceptDistance(ontFileName, uris[0], uris[1]);
            // }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // testEDRandWNConceptDistance();
        getOWLLongestDepth(ontFileName);
    }
}
