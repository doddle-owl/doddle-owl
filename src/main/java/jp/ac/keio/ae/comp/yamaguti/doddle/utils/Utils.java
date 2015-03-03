/*
 * Project Name: DODDLE (a Domain Ontology rapiD DeveLopment Environment)
 * Project Website: http://doddle-owl.sourceforge.net/
 *
 * Copyright (C) 2004-2008 Yamaguchi Laboratory, Keio University. All rights reserved.
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

package jp.ac.keio.ae.comp.yamaguti.doddle.utils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import jp.ac.keio.ae.comp.yamaguti.doddle.DODDLE;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.Concept;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.ConceptTreeNode;
import jp.ac.keio.ae.comp.yamaguti.doddle.data.DODDLEConstants;
import net.infonode.docking.RootWindow;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.PropertiesUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;
import net.java.sen.SenFactory;
import net.java.sen.StringTagger;
import net.java.sen.dictionary.Token;

import org.apache.commons.io.FileUtils;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * @author takeshi morita
 */
public class Utils {
	public static final String RESOURCE_DIR = "jp/ac/keio/ae/comp/yamaguti/doddle/resources/";
	public static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + "DODDLE-OWL"
			+ File.separator;
	private static final String JPWN_TEMP_DIR = TEMP_DIR + "jpwn_dict_1.1" + File.separator;

	public static ImageIcon getImageIcon(String icon) {
		return new ImageIcon(DODDLE.class.getClassLoader().getResource(RESOURCE_DIR + icon));
	}

	public static URL getURL(String icon) {
		return DODDLE.class.getClassLoader().getResource(RESOURCE_DIR + icon);
	}

	public static File getENWNFile() {
		File wnDir = new File(TEMP_DIR + DODDLEConstants.ENWN_HOME);
		if (wnDir.exists()) {
			System.out.println("exist: " + wnDir.getAbsolutePath());
			return wnDir;
		} else {
			wnDir.mkdir();
			String[] wnFiles = { "adj.exc", "adv.exc", "cntlist", "cntlist.rev", "data.adj",
					"data.noun", "data.verb", "frames.vrb", "index.adj", "index.adv", "index.noun",
					"index.sense", "index.verb", "lexnames", "log.grind.3.0", "noun.exc",
					"sentidx.vrb", "sents.vrb", "verb.exc", "verb.Framestext" };
			for (String wnf : wnFiles) {
				URL url = DODDLE.class.getClassLoader().getResource(
						RESOURCE_DIR + DODDLEConstants.ENWN_HOME + wnf);
				try {
					File f = new File(wnDir.getAbsolutePath() + File.separator + wnf);
					if (url != null) {
						FileUtils.copyURLToFile(url, f);
					}
					System.out.println("copy: " + f.getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.out.println("created: " + wnDir.getAbsolutePath());
			return wnDir;
		}
	}

	public static File getJPWNFile(String resName) {
		File dir = new File(JPWN_TEMP_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File file = new File(JPWN_TEMP_DIR + resName);
		if (file.exists()) {
			System.out.println("exist: " + file.getAbsolutePath());
			return file;
		} else {
			URL url = DODDLE.class.getClassLoader().getResource(
					RESOURCE_DIR + DODDLEConstants.JPWN_HOME + resName);
			try {
				FileUtils.copyURLToFile(url, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("created: " + file.getAbsolutePath());
			return file;
		}
	}

	public static RootWindow createDODDLERootWindow(ViewMap viewMap) {
		RootWindow rootWindow = DockingUtil.createRootWindow(viewMap, true);
		rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);
		RootWindowProperties properties = new RootWindowProperties();
		DockingWindowsTheme currentTheme = new ShapedGradientDockingTheme();
		properties.addSuperObject(currentTheme.getRootWindowProperties());
		RootWindowProperties titleBarStyleProperties = PropertiesUtil
				.createTitleBarStyleRootWindowProperties();
		properties.addSuperObject(titleBarStyleProperties);
		rootWindow.getRootWindowProperties().addSuperObject(properties);
		return rootWindow;
	}

	public static void addJaCompoundWord(List tokenList, List<String> inputWordList) {
		Set<String> compoundWordSet = new HashSet<String>();
		Map<String, List<String>> compoundWordElementListMap = new HashMap<String, List<String>>();
		for (String compoundWord : inputWordList) {
			try {
				StringTagger tagger = SenFactory.getStringTagger(null);
				List<Token> compoundWordTokenList = new ArrayList<Token>();
				tagger.analyze(compoundWord, compoundWordTokenList);
				if (compoundWordTokenList.size() == 1) {
					continue; // 複合ではない
				}
				compoundWordSet.add(compoundWord);
				List<String> compoundWordElementList = new ArrayList<String>();
				for (Token compoundWordToken : compoundWordTokenList) {
					String bf = compoundWordToken.getMorpheme().getBasicForm();
					if (bf.equals("*")) {
						bf = compoundWordToken.getSurface();
					}
					compoundWordElementList.add(bf);
				}
				compoundWordElementListMap.put(compoundWord, compoundWordElementList);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		for (String compoundWord : compoundWordSet) {
			List<String> compoundWordElementList = compoundWordElementListMap.get(compoundWord);
			Utils.addCompoundWord(compoundWord, compoundWordElementList, tokenList, compoundWordSet);
		}
	}

	public static void addEnCompoundWord(List<String> tokenList, List<String> inputWordList) {
		Set<String> compoundWordSet = new HashSet<String>();
		Map<String, List<String>> compoundWordElementListMap = new HashMap<String, List<String>>();
		for (String compoundWord : inputWordList) {
			List<String> compoundWordElementList = Arrays.asList(compoundWord.split("\\s+"));
			if (compoundWordElementList.size() == 1) {
				continue; // 複合語ではない
			}
			compoundWordSet.add(compoundWord);
			compoundWordElementListMap.put(compoundWord, compoundWordElementList);
		}
		for (String compoundWord : compoundWordSet) {
			List<String> compoundWordElementList = compoundWordElementListMap.get(compoundWord);
			Utils.addCompoundWord(compoundWord, compoundWordElementList, tokenList, compoundWordSet);
		}
	}

	private static void addCompoundWord(String compoundWord, List<String> compoundWordElementList,
			List<String> tokenList, Set compoundWordSet) {
		for (int i = 0; i < tokenList.size(); i++) {
			List<String> compoundWordSizeList = new ArrayList<String>();
			for (int j = 0; compoundWordSizeList.size() != compoundWordElementList.size(); j++) {
				if ((i + j) == tokenList.size()) {
					break;
				}
				String nw = tokenList.get(i + j);
				if (compoundWordSet.contains(nw)) {
					continue;
				}
				compoundWordSizeList.add(nw);
			}
			if (compoundWordElementList.size() == compoundWordSizeList.size()) {
				boolean isCompoundWordList = true;
				for (int j = 0; j < compoundWordElementList.size(); j++) {
					if (!compoundWordElementList.get(j).equals(compoundWordSizeList.get(j))) {
						isCompoundWordList = false;
						break;
					}
				}
				if (isCompoundWordList) {
					tokenList.add(i, compoundWord);
					i++;
				}
			}
		}
	}

	public static int getUserObjectNum(DefaultMutableTreeNode rootNode) {
		Set userObjectSet = new HashSet();
		userObjectSet.add(rootNode.getUserObject());
		getAllUserObject(rootNode, userObjectSet);
		return userObjectSet.size();
	}

	private static void getAllUserObject(TreeNode node, Set userObjectSet) {
		for (int i = 0; i < node.getChildCount(); i++) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) node.getChildAt(i);
			userObjectSet.add(childNode.getUserObject());
			getAllUserObject(childNode, userObjectSet);
		}
	}

	public static Set getAllConcept(TreeModel treeModel) {
		Set<Concept> conceptSet = new HashSet<Concept>();
		if (!(treeModel.getRoot() instanceof ConceptTreeNode)) {
			return conceptSet;
		}
		ConceptTreeNode rootNode = (ConceptTreeNode) treeModel.getRoot();
		conceptSet.add(rootNode.getConcept());
		getAllConcept(rootNode, conceptSet);
		return conceptSet;
	}

	private static void getAllConcept(TreeNode node, Set<Concept> conceptSet) {
		for (int i = 0; i < node.getChildCount(); i++) {
			ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
			conceptSet.add(childNode.getConcept());
			getAllConcept(childNode, conceptSet);
		}
	}

	public static double getChildCntAverage(TreeModel treeModel) {
		List<Integer> childNodeCntList = new ArrayList<Integer>();
		if (!(treeModel.getRoot() instanceof ConceptTreeNode)) {
			return 0;
		}
		ConceptTreeNode rootNode = (ConceptTreeNode) treeModel.getRoot();
		getChildCntAverage(rootNode, childNodeCntList);
		double totalChildNum = 0;
		for (int childNum : childNodeCntList) {
			totalChildNum += childNum;
		}
		if (childNodeCntList.size() == 0) {
			return 0;
		}
		return totalChildNum / childNodeCntList.size();
	}

	private static void getChildCntAverage(TreeNode node, List<Integer> childNodeCntList) {
		if (0 < node.getChildCount()) {
			childNodeCntList.add(node.getChildCount());
		}
		for (int i = 0; i < node.getChildCount(); i++) {
			ConceptTreeNode childNode = (ConceptTreeNode) node.getChildAt(i);
			getChildCntAverage(childNode, childNodeCntList);
		}
	}

	/**
	 * ResourceクラスのgetLocalNameメソッドは，ローカル名が数字からはじまる場合に名前空間の分割がうまくできないため，
	 * 独自に実装している．（不完全）
	 */
	public static String getLocalName(Resource res) {
		return res.getURI().replaceAll(getNameSpace(res), "");
	}

	public static String getLocalName(String uri) {
		Resource res = ResourceFactory.createResource(uri);
		return res.getURI().replaceAll(getNameSpace(res), "");
	}

	/**
	 * ResourceクラスのgetNameSpaceメソッドは，ローカル名が数字からはじまる場合に名前空間の分割がうまくできないため，
	 * 独自に実装している．（不完全）
	 */
	public static String getNameSpace(Resource res) {
		String ns = res.getNameSpace();
		if (ns == null) {
			return "";
		}
		if (ns.matches(".*#$") || ns.matches(".*/$")) {
			return ns;
		}
		String ns2 = ns.split("#\\d*[^#/]*$")[0];
		if (ns2 != null && !ns2.equals(ns)) {
			return ns2 + "#";
		}
		ns2 = ns.split("/\\d*[^#/]*$")[0];
		if (ns2 != null && !ns2.equals(ns)) {
			return ns2 + "/";
		}
		return "";
	}

	public static String getNameSpace(String uri) {
		return getNameSpace(ResourceFactory.createResource(uri));
	}

	public static JComponent createWestPanel(JComponent p) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(p, BorderLayout.WEST);
		return panel;
	}

	public static JComponent createEastPanel(JComponent p) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(p, BorderLayout.EAST);
		return panel;
	}

	public static JComponent createNorthPanel(JComponent p) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(p, BorderLayout.NORTH);
		return panel;
	}

	public static JComponent createSouthPanel(JComponent p) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(p, BorderLayout.SOUTH);
		return panel;
	}

	public static JComponent createTitledPanel(JComponent component, String title) {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.setBorder(BorderFactory.createTitledBorder(title));
		p.add(component, BorderLayout.CENTER);
		return p;
	}

	public static JComponent createTitledPanel(JComponent component, String title, int width,
			int height) {
		component.setPreferredSize(new Dimension(width, height));
		component.setMinimumSize(new Dimension(width, height));
		return createTitledPanel(component, title);
	}

	public static String getRDFType(Resource rdfType) {
		if (rdfType.getURI().equals(
				"http://daml.umbc.edu/ontologies/webofbelief/1.4/wob.owl#RDFXML")) {
			return "RDF/XML";
		} else if (rdfType.getURI().equals(
				"http://daml.umbc.edu/ontologies/webofbelief/1.4/wob.owl#N3")) {
			return "N3";
		} else if (rdfType.getURI().equals(
				"http://daml.umbc.edu/ontologies/webofbelief/1.4/wob.owl#NTriples")) {
			return "N-Triple";
		}
		return "RDF/XML";
	}

	public static Model getOntModel(InputStream inputStream, String fileType, String rdfType,
			String baseURI) {
		OntModel model = null;
		try {
			if (fileType.equals("owl") || fileType.equals("rdfs")) {
				model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
				model.read(inputStream, baseURI, rdfType);
			} else {
				model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
				model.read(inputStream, baseURI);
				if (0 < model.listImportedOntologyURIs().size()) {
					return model;
				}
			}
		} catch (Exception e) {
			System.out.println("RDF Parse Exception");
		}
		return model;
	}
}
