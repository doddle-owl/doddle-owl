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

package jp.ac.keio.ae.comp.yamaguti.doddle.data;

import java.util.*;

import com.hp.hpl.jena.rdf.model.*;

import jp.ac.keio.ae.comp.yamaguti.doddle.*;
import jp.ac.keio.ae.comp.yamaguti.doddle.utils.*;

/**
 * @author takeshi morita
 */
public class DODDLEDic {

	private static boolean isEDREnable() {
		return DODDLE.getCurrentProject().getOntologySelectionPanel().isEDREnable();
	}

	private static boolean isEDRTEnable() {
		return DODDLE.getCurrentProject().getOntologySelectionPanel().isEDRTEnable();
	}

	private static boolean isWordNetEnable() {
		return DODDLE.getCurrentProject().getOntologySelectionPanel().isWordNetEnable();
	}

	private static boolean isJPNWordNetEnable() {
		return DODDLE.getCurrentProject().getOntologySelectionPanel().isJpnWordNetEnable();
	}

	private static boolean isJWOEnable() {
		return DODDLE.getCurrentProject().getOntologySelectionPanel().isJWOEnable();
	}

	public static Concept getConcept(String uri) {
		Resource res = ResourceFactory.createResource(uri);
		String id = Utils.getLocalName(res);
		String ns = Utils.getNameSpace(res);
		Concept c = OWLOntologyManager.getConcept(uri);
		
		if (!isJWOEnable() && ns.equals(DODDLEConstants.JWO_URI)) {
			return null;
		}
		if (c != null) {
			return c;
		}
		if (isEDREnable() && ns.equals(DODDLEConstants.EDR_URI)) {
			return EDRDic.getEDRConcept(id);
		} else if (isEDRTEnable() && ns.equals(DODDLEConstants.EDRT_URI)) {
			return EDRDic.getEDRTConcept(id);
		} else if (isWordNetEnable() && ns.equals(DODDLEConstants.WN_URI)) {
			return WordNetDic.getWNConcept(id);
		} else if (isJPNWordNetEnable() && ns.equals(DODDLEConstants.JPN_WN_URI)) {
			return JpnWordNetDic.getConcept(id);
		}
		return null;
	}

	public static Set<String> getURISet(String word) {
		Set<String> uriSet = new HashSet<String>();
		if (isEDREnable()) {
			Set<String> idSet = EDRDic.getEDRIDSet(word);
			for (String id : idSet) {
				uriSet.add(DODDLEConstants.EDR_URI + id);
			}
		}
		if (isEDRTEnable()) {
			Set<String> idSet = EDRDic.getEDRTIDSet(word);
			for (String id : idSet) {
				uriSet.add(DODDLEConstants.EDRT_URI + id);
			}
		}
		uriSet.addAll(WordNetDic.getURISet(word));
		uriSet.addAll(JpnWordNetDic.getSynsetSet(word));
		uriSet.addAll(OWLOntologyManager.getURISet(word));
		return uriSet;
	}
}
