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

package net.sourceforge.doddle_owl.taskanalyzer;

import java.util.*;
import java.util.Map.*;

/**
 * @author Takeshi Morita
 */
public class Sentence {

    private List<Segment> segmentList;
    private Map<Segment, PrimitiveTask> segmentTaskDescriptionMap;
    private Map<String, Integer> compoundWordCountMap;
    private Map<String, Integer> compoundWordWithNokakuCountMap;
    private Map<Segment, Set<Segment>> segmentMap;

    public Sentence() {
        segmentList = new ArrayList<Segment>();
        segmentTaskDescriptionMap = new HashMap<Segment, PrimitiveTask>();
        compoundWordCountMap = new HashMap<String, Integer>();
        compoundWordWithNokakuCountMap = new HashMap<String, Integer>();
        segmentMap = new HashMap<Segment, Set<Segment>>();
    }

    public void addSegment(Segment seg) {
        segmentList.add(seg);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Segment seg : segmentList) {
            builder.append(seg);
        }
        return builder.toString();
    }

    public Collection<PrimitiveTask> getTaskDescriptionSet() {
        return segmentTaskDescriptionMap.values();
    }

    public List<Segment> getSegmentList() {
        return segmentList;
    }

    private void setDependToSegment() {
        for (Segment seg : segmentList) {
            if (0 < seg.getModificationRelationNum()) {
                Segment toSegment = segmentList.get(seg.getModificationRelationNum());
                seg.setDependToSegment(toSegment);
            } else {
                seg.setDependToSegment(new Segment(-1));
            }
        }
    }

    private void setDependFromSegment() {
        for (Segment seg : segmentList) {
            Segment toSegment = seg.getDependToSegment();
            toSegment.setDependFromSegment(seg);
        }
    }

    public void mergeSegments() {
        setDependToSegment();
        List<Segment> newSegmentList = new ArrayList<Segment>();
        for (int i = 1; i < segmentList.size(); i++) {
            Segment seg1 = segmentList.get(i - 1);
            Segment seg2 = segmentList.get(i);

            if (seg2.isIncludeSymbolParenthesis() && seg1.isNounPhrase()) {
                Segment newSegment = new Segment(seg2.getDependToSegment());
                newSegment.setModificationRelationNum(seg2.getModificationRelationNum());
                for (Morpheme m : seg1.getMorphemeList()) {
                    newSegment.addMorpheme(m);
                }
                for (Morpheme m : seg2.getMorphemeList()) {
                    newSegment.addMorpheme(m);
                }
                for (Segment seg : segmentList) {
                    if (seg.getDependToSegment().equals(seg1) || seg.getDependToSegment().equals(seg2)) {
                        seg.setDependToSegment(newSegment);
                    }
                }
                // System.out.println(newSegment+"--->"+newSegment.getTargetSegment());
                newSegmentList.add(newSegment);
                i++;
            } else {
                newSegmentList.add(seg1);
            }
        }
        if (0 < segmentList.size()) {
            newSegmentList.add(segmentList.get(segmentList.size() - 1));
            segmentList = newSegmentList;
        }

        setDependFromSegment(); // segmentListを更新してから??
        setSegmentMap();
        setCompoundWordCountMap();
        setCompoundWordWithNokakuCountMap();
        setTaskDescription();
    }

    private void setTaskDescription() {
        for (Segment seg : segmentList) {
            if (seg.isIncludingVerb() && !seg.isIncludingNoKaku() && !seg.isSubjectSegment() && !seg.isObjectSegment()) {
                segmentTaskDescriptionMap.put(seg, new PrimitiveTask(seg));
            }
        }
        for (Segment seg : segmentList) {
            Segment depFromSeg = seg.getDependFromSegment();
            Segment predicateSegment = seg.getDependToSegment();
            PrimitiveTask taskDescription = segmentTaskDescriptionMap.get(predicateSegment);
            if (seg.isSubjectSegment()) {
                if (taskDescription != null) {
                    if (depFromSeg != null && depFromSeg.isIncludingNoKaku()) {
                        taskDescription.addSubject(depFromSeg);
                    }
                    taskDescription.addSubject(seg);
                }
            } else if (seg.isObjectSegment()) {
                if (taskDescription != null) {
                    if (depFromSeg != null && depFromSeg.isIncludingNoKaku()) {
                        taskDescription.addObject(depFromSeg);
                    }
                    taskDescription.addObject(seg);
                }
            }
        }
    }

    private void setSegmentMap() {
        for (Segment seg : segmentList) {
            if (0 < seg.getModificationRelationNum()) {
                Segment targetSeg = seg.getDependToSegment();
                if (segmentMap.get(targetSeg) != null) {
                    Set<Segment> segmentSet = segmentMap.get(targetSeg);
                    segmentSet.add(seg);
                } else {
                    Set<Segment> segmentSet = new HashSet<Segment>();
                    segmentSet.add(seg);
                    segmentMap.put(targetSeg, segmentSet);
                }
            }
        }
    }

    private void setCompoundWordWithNokakuCountMap() {
        for (Entry<Segment, Set<Segment>> entry : segmentMap.entrySet()) {
            for (Segment seg : entry.getValue()) {
                if (seg.isIncludingNoKaku()) {
                    String compoundWordWithNokaku = seg.getRefinedPhrase() + entry.getKey().getNounPhrase();
                    if (compoundWordWithNokakuCountMap.get(compoundWordWithNokaku) != null) {
                        compoundWordWithNokakuCountMap.put(compoundWordWithNokaku, compoundWordWithNokakuCountMap
                                .get(compoundWordWithNokaku) + 1);
                    } else {
                        compoundWordWithNokakuCountMap.put(compoundWordWithNokaku, 1);
                    }
                }
            }
        }
    }

    public Map<Segment, Set<Segment>> getSegmentMap() {
        return segmentMap;
    }

    public Set<String> getCompoundWordSet() {
        return compoundWordCountMap.keySet();
    }

    public Map<String, Integer> getCompoundWordCountMap() {
        return compoundWordCountMap;
    }

    public Set<String> getCompoundWordWithNokakuSet() {
        return compoundWordWithNokakuCountMap.keySet();
    }

    public Map<String, Integer> getCompoundWordWithNokakuCountMap() {
        return compoundWordWithNokakuCountMap;
    }

    private void setCompoundWordCountMap() {
        for (Segment seg : segmentList) {
            String compoundWord = seg.getCompoundNounPhrase();
            if (compoundWord != null) {
                putCompoundWord(compoundWord);
                if (seg.isIncludeSymbolParenthesis()) {
                    String[] words = compoundWord.split("[（）()\\[\\]{}【】［］”“（）]");
                    for (int i = 0; i < words.length; i++) {
                        if (0 < words[i].length()) {
                            putCompoundWord(words[i]); // A(B)において，A,Bが複合語の時，それらを登録する
                        }
                    }
                }
            }
        }
    }

    private void putCompoundWord(String compoundWord) {
        if (compoundWordCountMap.get(compoundWord) != null) {
            compoundWordCountMap.put(compoundWord, compoundWordCountMap.get(compoundWord) + 1);
        } else {
            compoundWordCountMap.put(compoundWord, 1);
        }
    }
}
