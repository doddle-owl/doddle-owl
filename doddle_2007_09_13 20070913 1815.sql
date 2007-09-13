-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.0.45-community-nt


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema doddle
--

CREATE DATABASE IF NOT EXISTS doddle;
USE doddle;

--
-- Definition of table `apriori_result`
--

DROP TABLE IF EXISTS `apriori_result`;
CREATE TABLE `apriori_result` (
  `Project_ID` int(10) unsigned NOT NULL auto_increment,
  `Doc_ID` int(10) unsigned NOT NULL,
  `Term1` text NOT NULL,
  `Term2` text NOT NULL,
  `Apriori_Value` double NOT NULL,
  PRIMARY KEY  (`Project_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `apriori_result`
--

/*!40000 ALTER TABLE `apriori_result` DISABLE KEYS */;
/*!40000 ALTER TABLE `apriori_result` ENABLE KEYS */;


--
-- Definition of table `class_concept_list`
--

DROP TABLE IF EXISTS `class_concept_list`;
CREATE TABLE `class_concept_list` (
  `Project_ID` int(10) unsigned NOT NULL auto_increment,
  `Concept_List_ID` int(10) unsigned NOT NULL,
  `Concept` text NOT NULL,
  PRIMARY KEY  (`Project_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `class_concept_list`
--

/*!40000 ALTER TABLE `class_concept_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `class_concept_list` ENABLE KEYS */;


--
-- Definition of table `class_trimmed_result_analysis`
--

DROP TABLE IF EXISTS `class_trimmed_result_analysis`;
CREATE TABLE `class_trimmed_result_analysis` (
  `Project_ID` int(10) unsigned NOT NULL auto_increment,
  `Concept_List_ID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`Project_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `class_trimmed_result_analysis`
--

/*!40000 ALTER TABLE `class_trimmed_result_analysis` DISABLE KEYS */;
/*!40000 ALTER TABLE `class_trimmed_result_analysis` ENABLE KEYS */;


--
-- Definition of table `concept_definition`
--

DROP TABLE IF EXISTS `concept_definition`;
CREATE TABLE `concept_definition` (
  `Project_ID` int(10) unsigned NOT NULL auto_increment,
  `is_Meta_Property` tinyint(1) NOT NULL,
  `Term1` text NOT NULL,
  `Relation` text NOT NULL,
  `Term2` text NOT NULL,
  PRIMARY KEY  (`Project_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `concept_definition`
--

/*!40000 ALTER TABLE `concept_definition` DISABLE KEYS */;
/*!40000 ALTER TABLE `concept_definition` ENABLE KEYS */;


--
-- Definition of table `concept_definition_parameter`
--

DROP TABLE IF EXISTS `concept_definition_parameter`;
CREATE TABLE `concept_definition_parameter` (
  `Project_ID` int(10) unsigned NOT NULL auto_increment,
  `Minimum_Confidence` double NOT NULL,
  `Minimum_Support` double NOT NULL,
  `Front_Scope` int(10) unsigned NOT NULL,
  `Behind_Scope` int(10) unsigned NOT NULL,
  `N_Gram` int(10) unsigned NOT NULL,
  `Gram_Count` int(10) unsigned NOT NULL,
  `Word_Space_Value` double NOT NULL,
  PRIMARY KEY  (`Project_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `concept_definition_parameter`
--

/*!40000 ALTER TABLE `concept_definition_parameter` DISABLE KEYS */;
/*!40000 ALTER TABLE `concept_definition_parameter` ENABLE KEYS */;


--
-- Definition of table `construct_tree_option`
--

DROP TABLE IF EXISTS `construct_tree_option`;
CREATE TABLE `construct_tree_option` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `is_Tree_Construction` tinyint(1) NOT NULL,
  `is_Construction_With_Compound_Word_Tree` tinyint(1) NOT NULL,
  `is_Trimming_Internal_Node` tinyint(1) NOT NULL,
  `is_Add_Abstract_Concept_With_Compound_Word_Tree` tinyint(1) NOT NULL,
  `is_Trimming_Internal_Node_With_Compound_Word_Tree` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `construct_tree_option`
--

/*!40000 ALTER TABLE `construct_tree_option` DISABLE KEYS */;
INSERT INTO `construct_tree_option` (`Project_ID`,`is_Tree_Construction`,`is_Construction_With_Compound_Word_Tree`,`is_Trimming_Internal_Node`,`is_Add_Abstract_Concept_With_Compound_Word_Tree`,`is_Trimming_Internal_Node_With_Compound_Word_Tree`) VALUES 
 (1,1,1,1,1,0);
/*!40000 ALTER TABLE `construct_tree_option` ENABLE KEYS */;


--
-- Definition of table `doc_info`
--

DROP TABLE IF EXISTS `doc_info`;
CREATE TABLE `doc_info` (
  `Project_ID` int(10) unsigned NOT NULL auto_increment,
  `Doc_ID` int(10) unsigned NOT NULL,
  `Doc_Path` text NOT NULL,
  `Language` text NOT NULL,
  PRIMARY KEY  (`Project_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `doc_info`
--

/*!40000 ALTER TABLE `doc_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `doc_info` ENABLE KEYS */;


--
-- Definition of table `doddle_project`
--

DROP TABLE IF EXISTS `doddle_project`;
CREATE TABLE `doddle_project` (
  `Project_ID` int(10) unsigned NOT NULL auto_increment,
  `Project_Name` text NOT NULL,
  `Project_Author` text NOT NULL,
  `Project_Creation_Date` datetime NOT NULL,
  `Project_Modification_Date` datetime NOT NULL,
  PRIMARY KEY  (`Project_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `doddle_project`
--

/*!40000 ALTER TABLE `doddle_project` DISABLE KEYS */;
/*!40000 ALTER TABLE `doddle_project` ENABLE KEYS */;


--
-- Definition of table `general_ontology_info`
--

DROP TABLE IF EXISTS `general_ontology_info`;
CREATE TABLE `general_ontology_info` (
  `EDR_General` tinyint(1) NOT NULL,
  `EDR_Technical` tinyint(1) NOT NULL,
  `WordNet` tinyint(1) NOT NULL,
  `Project_ID` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `general_ontology_info`
--

/*!40000 ALTER TABLE `general_ontology_info` DISABLE KEYS */;
INSERT INTO `general_ontology_info` (`EDR_General`,`EDR_Technical`,`WordNet`,`Project_ID`) VALUES 
 (1,0,0,1);
/*!40000 ALTER TABLE `general_ontology_info` ENABLE KEYS */;


--
-- Definition of table `input_concept_set`
--

DROP TABLE IF EXISTS `input_concept_set`;
CREATE TABLE `input_concept_set` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Input_Concept` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `input_concept_set`
--

/*!40000 ALTER TABLE `input_concept_set` DISABLE KEYS */;
INSERT INTO `input_concept_set` (`Project_ID`,`Input_Concept`) VALUES 
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID1fb16b'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3d027f'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0e7fda'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0e328d'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f933c'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID1e8626'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3c19e0'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3c30ca'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3d0239'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3bf2e2'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID10b05c'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3c14a7'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f3c77'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID700ef4'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID10752b'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID4444f5'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0ef348'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0e8710'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cf772'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3bd8e7'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3bd183'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0eb50e'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID108272'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cf9dc'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID1f3a31'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID702d29'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID1e8c8c'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0fdb2a'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f5a5e'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f7b46'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID100c7a'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0ef08d'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cf2a9'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID201b7e'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID700a12'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID103dfd'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3c6a08'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0ffd77'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cf770'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID10a479'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cf5fb'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0e7747'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID1f92c7'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f514d'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID102f53'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0fc3e1'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID102e7f'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID1f3e05'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0ff2cb'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3c17fa'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID30f75e'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID30f74c'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cef6c'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID702e7e'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3be932'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3c69c7'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0ef511'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID201a62'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0e899c'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cf15e'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID704e99'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0fb469'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cf216'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f966e'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0fde0d'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3e510e'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3c1342'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID200c12'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f301c'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f1689'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cfdcb'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cf761'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3c2546'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f9516'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID1082ab'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID70008a'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cff8a'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0eb5f7'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID1024ba'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID1fae78'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID200d3b'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cf8d9'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3c4095'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3d04c7'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0e5231'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3d03f6'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f9515'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID10d587'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f58e4'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0fddc5'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID108b79'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID200d3c'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f4bf7'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0faa23'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cfd6c'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3ce62e'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID10d584'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID200bc6'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID1e8978'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0ed812'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cf335'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0aa678'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f0fe3'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID10cc89'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0aa0e7'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3d042e'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3c48fa'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f5b31'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0e14e0'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3c1589'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID201aec'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f8d30'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID10256e'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID1035b8'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cedc2'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID703b22'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f4b12'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cff6b'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0ebd0c'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f3f5d'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0e61d1'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3c17f9'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0ef51f'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f514f'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0a7b8b'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID700c89'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3c1105'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3ce7fa'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3ce800'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cf588'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID1f68eb'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f4e13'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID7024e8'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID200d3f'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f5f75'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cf20f'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cf511'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3bd3c2'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cb75e'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0ff488'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3cf54e'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0a958a'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3befd5'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0f934e'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID3d1717'),
 (1,'http://www2.nict.go.jp/kk/e416/EDR#ID0ead07');
/*!40000 ALTER TABLE `input_concept_set` ENABLE KEYS */;


--
-- Definition of table `input_term_concept_map`
--

DROP TABLE IF EXISTS `input_term_concept_map`;
CREATE TABLE `input_term_concept_map` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Input_Term` text NOT NULL,
  `Input_Concept` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `input_term_concept_map`
--

/*!40000 ALTER TABLE `input_term_concept_map` DISABLE KEYS */;
INSERT INTO `input_term_concept_map` (`Project_ID`,`Input_Term`,`Input_Concept`) VALUES 
 (1,'経路','http://www2.nict.go.jp/kk/e416/EDR#ID30f75e'),
 (1,'納品書発行','http://www2.nict.go.jp/kk/e416/EDR#ID3cf770'),
 (1,'経理部門','http://www2.nict.go.jp/kk/e416/EDR#ID3cf2a9'),
 (1,'類似品','http://www2.nict.go.jp/kk/e416/EDR#ID201a62'),
 (1,'コンピュータ在庫','http://www2.nict.go.jp/kk/e416/EDR#ID0f8d30'),
 (1,'倉庫番','http://www2.nict.go.jp/kk/e416/EDR#ID3d042e'),
 (1,'還元法','http://www2.nict.go.jp/kk/e416/EDR#ID0ead07'),
 (1,'コールセンター','http://www2.nict.go.jp/kk/e416/EDR#ID704e99'),
 (1,'資材購買部','http://www2.nict.go.jp/kk/e416/EDR#ID1082ab'),
 (1,'取引','http://www2.nict.go.jp/kk/e416/EDR#ID100c7a'),
 (1,'在庫圧縮','http://www2.nict.go.jp/kk/e416/EDR#ID0e328d'),
 (1,'受注管理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf216'),
 (1,'BTO','http://www2.nict.go.jp/kk/e416/EDR#ID70008a'),
 (1,'票','http://www2.nict.go.jp/kk/e416/EDR#ID3c6a08'),
 (1,'付加','http://www2.nict.go.jp/kk/e416/EDR#ID3cf20f'),
 (1,'消耗品','http://www2.nict.go.jp/kk/e416/EDR#ID3be932'),
 (1,'モーダルシフト','http://www2.nict.go.jp/kk/e416/EDR#ID0f4e13'),
 (1,'消耗財','http://www2.nict.go.jp/kk/e416/EDR#ID3d04c7'),
 (1,'発行','http://www2.nict.go.jp/kk/e416/EDR#ID3cf770'),
 (1,'メーカ','http://www2.nict.go.jp/kk/e416/EDR#ID10b05c'),
 (1,'得意先','http://www2.nict.go.jp/kk/e416/EDR#ID0f0fe3'),
 (1,'TMS','http://www2.nict.go.jp/kk/e416/EDR#ID700ef4'),
 (1,'主要材料','http://www2.nict.go.jp/kk/e416/EDR#ID0f3c77'),
 (1,'業者','http://www2.nict.go.jp/kk/e416/EDR#ID0ed812'),
 (1,'回収','http://www2.nict.go.jp/kk/e416/EDR#ID0e899c'),
 (1,'外注加工品','http://www2.nict.go.jp/kk/e416/EDR#ID200d3c'),
 (1,'EOS','http://www2.nict.go.jp/kk/e416/EDR#ID1f92c7'),
 (1,'決済','http://www2.nict.go.jp/kk/e416/EDR#ID0ef511'),
 (1,'設計部門','http://www2.nict.go.jp/kk/e416/EDR#ID3cf2a9'),
 (1,'要求納期','http://www2.nict.go.jp/kk/e416/EDR#ID102e7f'),
 (1,'貸借対照表','http://www2.nict.go.jp/kk/e416/EDR#ID3c48fa'),
 (1,'D','http://www2.nict.go.jp/kk/e416/EDR#ID0a7b8b'),
 (1,'訪問','http://www2.nict.go.jp/kk/e416/EDR#ID0e14e0'),
 (1,'企業','http://www2.nict.go.jp/kk/e416/EDR#ID0ebd0c'),
 (1,'発注登録','http://www2.nict.go.jp/kk/e416/EDR#ID3d027f'),
 (1,'卸売業','http://www2.nict.go.jp/kk/e416/EDR#ID3ce800'),
 (1,'完了','http://www2.nict.go.jp/kk/e416/EDR#ID3cedc2'),
 (1,'売価率','http://www2.nict.go.jp/kk/e416/EDR#ID3cf335'),
 (1,'ミス','http://www2.nict.go.jp/kk/e416/EDR#ID3cef6c'),
 (1,'決算','http://www2.nict.go.jp/kk/e416/EDR#ID0ef51f'),
 (1,'Q','http://www2.nict.go.jp/kk/e416/EDR#ID3e510e'),
 (1,'書','http://www2.nict.go.jp/kk/e416/EDR#ID0f5a5e'),
 (1,'振り分け','http://www2.nict.go.jp/kk/e416/EDR#ID10752b'),
 (1,'率','http://www2.nict.go.jp/kk/e416/EDR#ID3cf335'),
 (1,'店舗','http://www2.nict.go.jp/kk/e416/EDR#ID10a479'),
 (1,'庶務課','http://www2.nict.go.jp/kk/e416/EDR#ID0e8710'),
 (1,'仕込み在庫','http://www2.nict.go.jp/kk/e416/EDR#ID0f8d30'),
 (1,'保証','http://www2.nict.go.jp/kk/e416/EDR#ID3cf9dc'),
 (1,'運用付加','http://www2.nict.go.jp/kk/e416/EDR#ID3cf20f'),
 (1,'管理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf216'),
 (1,'損益計算書','http://www2.nict.go.jp/kk/e416/EDR#ID0fb469'),
 (1,'スポット購買','http://www2.nict.go.jp/kk/e416/EDR#ID3d1717'),
 (1,'平均法','http://www2.nict.go.jp/kk/e416/EDR#ID3d0239'),
 (1,'単純平均法','http://www2.nict.go.jp/kk/e416/EDR#ID3d0239'),
 (1,'適正在庫','http://www2.nict.go.jp/kk/e416/EDR#ID0f8d30'),
 (1,'生産','http://www2.nict.go.jp/kk/e416/EDR#ID0f934e'),
 (1,'棚卸業者','http://www2.nict.go.jp/kk/e416/EDR#ID0ed812'),
 (1,'棚','http://www2.nict.go.jp/kk/e416/EDR#ID0fc3e1'),
 (1,'取扱商品','http://www2.nict.go.jp/kk/e416/EDR#ID4444f5'),
 (1,'予算','http://www2.nict.go.jp/kk/e416/EDR#ID3bd8e7'),
 (1,'高額商品','http://www2.nict.go.jp/kk/e416/EDR#ID4444f5'),
 (1,'発注先','http://www2.nict.go.jp/kk/e416/EDR#ID0f301c'),
 (1,'棚卸差異','http://www2.nict.go.jp/kk/e416/EDR#ID3c69c7'),
 (1,'RM','http://www2.nict.go.jp/kk/e416/EDR#ID0aa678'),
 (1,'シフト','http://www2.nict.go.jp/kk/e416/EDR#ID0f4e13'),
 (1,'セット商品','http://www2.nict.go.jp/kk/e416/EDR#ID4444f5'),
 (1,'回答','http://www2.nict.go.jp/kk/e416/EDR#ID0e5231'),
 (1,'問い合わせ','http://www2.nict.go.jp/kk/e416/EDR#ID0f4b12'),
 (1,'担当者','http://www2.nict.go.jp/kk/e416/EDR#ID200bc6'),
 (1,'ハンディターミナル','http://www2.nict.go.jp/kk/e416/EDR#ID3c2546'),
 (1,'発注','http://www2.nict.go.jp/kk/e416/EDR#ID103dfd'),
 (1,'物流センター','http://www2.nict.go.jp/kk/e416/EDR#ID3c1342'),
 (1,'加工','http://www2.nict.go.jp/kk/e416/EDR#ID3c1589'),
 (1,'VAN会社','http://www2.nict.go.jp/kk/e416/EDR#ID30f74c'),
 (1,'定期訪問','http://www2.nict.go.jp/kk/e416/EDR#ID0e14e0'),
 (1,'在庫品','http://www2.nict.go.jp/kk/e416/EDR#ID0f8d30'),
 (1,'予定表','http://www2.nict.go.jp/kk/e416/EDR#ID10d587'),
 (1,'代替品','http://www2.nict.go.jp/kk/e416/EDR#ID200d3b'),
 (1,'受注機会損失','http://www2.nict.go.jp/kk/e416/EDR#ID3cff6b'),
 (1,'受注残管理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf216'),
 (1,'通信コスト','http://www2.nict.go.jp/kk/e416/EDR#ID0f1689'),
 (1,'出荷予定','http://www2.nict.go.jp/kk/e416/EDR#ID10d584'),
 (1,'原料在庫','http://www2.nict.go.jp/kk/e416/EDR#ID0f8d30'),
 (1,'受注商品','http://www2.nict.go.jp/kk/e416/EDR#ID4444f5'),
 (1,'納入','http://www2.nict.go.jp/kk/e416/EDR#ID0e7747'),
 (1,'覚書','http://www2.nict.go.jp/kk/e416/EDR#ID0e7fda'),
 (1,'トラック配送振り分け','http://www2.nict.go.jp/kk/e416/EDR#ID10752b'),
 (1,'納品業者','http://www2.nict.go.jp/kk/e416/EDR#ID0ed812'),
 (1,'備品','http://www2.nict.go.jp/kk/e416/EDR#ID3cf54e'),
 (1,'販売','http://www2.nict.go.jp/kk/e416/EDR#ID3cfd6c'),
 (1,'赤黒処理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf15e'),
 (1,'生産管理','http://www2.nict.go.jp/kk/e416/EDR#ID0f933c'),
 (1,'商談','http://www2.nict.go.jp/kk/e416/EDR#ID1fb16b'),
 (1,'調達先','http://www2.nict.go.jp/kk/e416/EDR#ID0f301c'),
 (1,'基本契約書','http://www2.nict.go.jp/kk/e416/EDR#ID0ef348'),
 (1,'受注完了','http://www2.nict.go.jp/kk/e416/EDR#ID3cedc2'),
 (1,'計算','http://www2.nict.go.jp/kk/e416/EDR#ID0ef08d'),
 (1,'入荷入力','http://www2.nict.go.jp/kk/e416/EDR#ID10256e'),
 (1,'法','http://www2.nict.go.jp/kk/e416/EDR#ID3d0239'),
 (1,'受注生産','http://www2.nict.go.jp/kk/e416/EDR#ID702d29'),
 (1,'入荷予定表','http://www2.nict.go.jp/kk/e416/EDR#ID10d587'),
 (1,'リスク','http://www2.nict.go.jp/kk/e416/EDR#ID3c19e0'),
 (1,'納品','http://www2.nict.go.jp/kk/e416/EDR#ID102f53'),
 (1,'部','http://www2.nict.go.jp/kk/e416/EDR#ID1082ab'),
 (1,'番','http://www2.nict.go.jp/kk/e416/EDR#ID3d042e'),
 (1,'有効在庫','http://www2.nict.go.jp/kk/e416/EDR#ID0f8d30'),
 (1,'技術者','http://www2.nict.go.jp/kk/e416/EDR#ID3bf2e2'),
 (1,'方式','http://www2.nict.go.jp/kk/e416/EDR#ID1e8626'),
 (1,'調整','http://www2.nict.go.jp/kk/e416/EDR#ID0fddc5'),
 (1,'予定','http://www2.nict.go.jp/kk/e416/EDR#ID10d584'),
 (1,'発注法','http://www2.nict.go.jp/kk/e416/EDR#ID3d0239'),
 (1,'定番商品','http://www2.nict.go.jp/kk/e416/EDR#ID1f68eb'),
 (1,'SFA','http://www2.nict.go.jp/kk/e416/EDR#ID700c89'),
 (1,'営業部門','http://www2.nict.go.jp/kk/e416/EDR#ID3cf2a9'),
 (1,'売価還元法','http://www2.nict.go.jp/kk/e416/EDR#ID0ead07'),
 (1,'SE','http://www2.nict.go.jp/kk/e416/EDR#ID3c30ca'),
 (1,'受注入力','http://www2.nict.go.jp/kk/e416/EDR#ID10256e'),
 (1,'在庫問い合わせ','http://www2.nict.go.jp/kk/e416/EDR#ID0f4b12'),
 (1,'経費','http://www2.nict.go.jp/kk/e416/EDR#ID3cf588'),
 (1,'保証金','http://www2.nict.go.jp/kk/e416/EDR#ID108b79'),
 (1,'納期','http://www2.nict.go.jp/kk/e416/EDR#ID102e7f'),
 (1,'共同配送','http://www2.nict.go.jp/kk/e416/EDR#ID1035b8'),
 (1,'流通経路','http://www2.nict.go.jp/kk/e416/EDR#ID30f75e'),
 (1,'外注課','http://www2.nict.go.jp/kk/e416/EDR#ID0e8710'),
 (1,'返品','http://www2.nict.go.jp/kk/e416/EDR#ID108272'),
 (1,'額','http://www2.nict.go.jp/kk/e416/EDR#ID0eb5f7'),
 (1,'部門','http://www2.nict.go.jp/kk/e416/EDR#ID3cf2a9'),
 (1,'審査部門','http://www2.nict.go.jp/kk/e416/EDR#ID3cf2a9'),
 (1,'受付','http://www2.nict.go.jp/kk/e416/EDR#ID3c17fa'),
 (1,'帳票','http://www2.nict.go.jp/kk/e416/EDR#ID703b22'),
 (1,'先','http://www2.nict.go.jp/kk/e416/EDR#ID0f301c'),
 (1,'トラック輸送','http://www2.nict.go.jp/kk/e416/EDR#ID10cc89'),
 (1,'購買','http://www2.nict.go.jp/kk/e416/EDR#ID3d1717'),
 (1,'ドッキング','http://www2.nict.go.jp/kk/e416/EDR#ID3befd5'),
 (1,'会社','http://www2.nict.go.jp/kk/e416/EDR#ID30f74c'),
 (1,'課','http://www2.nict.go.jp/kk/e416/EDR#ID0e8710'),
 (1,'伝票記載ミス','http://www2.nict.go.jp/kk/e416/EDR#ID3cef6c'),
 (1,'納期回答','http://www2.nict.go.jp/kk/e416/EDR#ID0e5231'),
 (1,'差異','http://www2.nict.go.jp/kk/e416/EDR#ID3c69c7'),
 (1,'損益計算','http://www2.nict.go.jp/kk/e416/EDR#ID0ef08d'),
 (1,'社印','http://www2.nict.go.jp/kk/e416/EDR#ID0f514d'),
 (1,'定量発注','http://www2.nict.go.jp/kk/e416/EDR#ID103dfd'),
 (1,'検品','http://www2.nict.go.jp/kk/e416/EDR#ID200c12'),
 (1,'製品','http://www2.nict.go.jp/kk/e416/EDR#ID0f966e'),
 (1,'出荷指示書','http://www2.nict.go.jp/kk/e416/EDR#ID0f5a5e'),
 (1,'処理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf15e'),
 (1,'在庫管理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf216'),
 (1,'場所','http://www2.nict.go.jp/kk/e416/EDR#ID3cf5fb'),
 (1,'調達','http://www2.nict.go.jp/kk/e416/EDR#ID0fde0d'),
 (1,'加工品','http://www2.nict.go.jp/kk/e416/EDR#ID200d3c'),
 (1,'販売管理','http://www2.nict.go.jp/kk/e416/EDR#ID3cb75e'),
 (1,'品','http://www2.nict.go.jp/kk/e416/EDR#ID0f4bf7'),
 (1,'仕入れ','http://www2.nict.go.jp/kk/e416/EDR#ID0f3f5d'),
 (1,'資材部門','http://www2.nict.go.jp/kk/e416/EDR#ID3cf2a9'),
 (1,'在庫リスク','http://www2.nict.go.jp/kk/e416/EDR#ID3c19e0'),
 (1,'発注依頼','http://www2.nict.go.jp/kk/e416/EDR#ID3ce62e'),
 (1,'資産','http://www2.nict.go.jp/kk/e416/EDR#ID3cf8d9'),
 (1,'POS','http://www2.nict.go.jp/kk/e416/EDR#ID0aa0e7'),
 (1,'注文書発行','http://www2.nict.go.jp/kk/e416/EDR#ID3cf770'),
 (1,'管理部','http://www2.nict.go.jp/kk/e416/EDR#ID201aec'),
 (1,'補助材料','http://www2.nict.go.jp/kk/e416/EDR#ID0f3c77'),
 (1,'見積依頼','http://www2.nict.go.jp/kk/e416/EDR#ID3ce62e'),
 (1,'依頼','http://www2.nict.go.jp/kk/e416/EDR#ID3ce62e'),
 (1,'外注','http://www2.nict.go.jp/kk/e416/EDR#ID0eb50e'),
 (1,'業','http://www2.nict.go.jp/kk/e416/EDR#ID3ce800'),
 (1,'MS','http://www2.nict.go.jp/kk/e416/EDR#ID201b7e'),
 (1,'VAN','http://www2.nict.go.jp/kk/e416/EDR#ID3bd3c2'),
 (1,'営業担当者','http://www2.nict.go.jp/kk/e416/EDR#ID200bc6'),
 (1,'伝票取り消し','http://www2.nict.go.jp/kk/e416/EDR#ID0ff2cb'),
 (1,'EDI','http://www2.nict.go.jp/kk/e416/EDR#ID702e7e'),
 (1,'在庫','http://www2.nict.go.jp/kk/e416/EDR#ID0f8d30'),
 (1,'注文','http://www2.nict.go.jp/kk/e416/EDR#ID3d03f6'),
 (1,'納期管理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf216'),
 (1,'売買','http://www2.nict.go.jp/kk/e416/EDR#ID0e61d1'),
 (1,'貯蔵品','http://www2.nict.go.jp/kk/e416/EDR#ID0f4bf7'),
 (1,'売上管理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf216'),
 (1,'指示書','http://www2.nict.go.jp/kk/e416/EDR#ID0f5a5e'),
 (1,'見積','http://www2.nict.go.jp/kk/e416/EDR#ID3bd8e7'),
 (1,'商品','http://www2.nict.go.jp/kk/e416/EDR#ID4444f5'),
 (1,'入荷','http://www2.nict.go.jp/kk/e416/EDR#ID1024ba'),
 (1,'受注','http://www2.nict.go.jp/kk/e416/EDR#ID0f7b46'),
 (1,'ターミナル','http://www2.nict.go.jp/kk/e416/EDR#ID3c2546'),
 (1,'ライン受注','http://www2.nict.go.jp/kk/e416/EDR#ID0f7b46'),
 (1,'会計','http://www2.nict.go.jp/kk/e416/EDR#ID1fae78'),
 (1,'印紙','http://www2.nict.go.jp/kk/e416/EDR#ID3c14a7'),
 (1,'3PL企業','http://www2.nict.go.jp/kk/e416/EDR#ID0ebd0c'),
 (1,'集中購買','http://www2.nict.go.jp/kk/e416/EDR#ID3d1717'),
 (1,'流行品','http://www2.nict.go.jp/kk/e416/EDR#ID0f4bf7'),
 (1,'売上伝票','http://www2.nict.go.jp/kk/e416/EDR#ID0ffd77'),
 (1,'調達方式','http://www2.nict.go.jp/kk/e416/EDR#ID1e8626'),
 (1,'指定伝票','http://www2.nict.go.jp/kk/e416/EDR#ID0ffd77'),
 (1,'注文商品','http://www2.nict.go.jp/kk/e416/EDR#ID4444f5'),
 (1,'実在庫','http://www2.nict.go.jp/kk/e416/EDR#ID0f8d30'),
 (1,'取り消し','http://www2.nict.go.jp/kk/e416/EDR#ID0ff2cb'),
 (1,'発注伝票','http://www2.nict.go.jp/kk/e416/EDR#ID0ffd77'),
 (1,'原価','http://www2.nict.go.jp/kk/e416/EDR#ID0f1689'),
 (1,'センター','http://www2.nict.go.jp/kk/e416/EDR#ID3c1342'),
 (1,'出庫','http://www2.nict.go.jp/kk/e416/EDR#ID0f58e4'),
 (1,'輸送','http://www2.nict.go.jp/kk/e416/EDR#ID10cc89'),
 (1,'財','http://www2.nict.go.jp/kk/e416/EDR#ID3d04c7'),
 (1,'JAN-POS','http://www2.nict.go.jp/kk/e416/EDR#ID0aa0e7'),
 (1,'発送','http://www2.nict.go.jp/kk/e416/EDR#ID3cf772'),
 (1,'不良品','http://www2.nict.go.jp/kk/e416/EDR#ID200d3f'),
 (1,'伝票','http://www2.nict.go.jp/kk/e416/EDR#ID0ffd77'),
 (1,'顧客','http://www2.nict.go.jp/kk/e416/EDR#ID0f0fe3'),
 (1,'営業管理部','http://www2.nict.go.jp/kk/e416/EDR#ID201aec'),
 (1,'期間','http://www2.nict.go.jp/kk/e416/EDR#ID3cfdcb'),
 (1,'倉庫','http://www2.nict.go.jp/kk/e416/EDR#ID0faa23'),
 (1,'向上','http://www2.nict.go.jp/kk/e416/EDR#ID3ce7fa'),
 (1,'POS導入店','http://www2.nict.go.jp/kk/e416/EDR#ID0f5b31'),
 (1,'損失','http://www2.nict.go.jp/kk/e416/EDR#ID3cff6b'),
 (1,'契約書','http://www2.nict.go.jp/kk/e416/EDR#ID0ef348'),
 (1,'理論在庫','http://www2.nict.go.jp/kk/e416/EDR#ID0f8d30'),
 (1,'出荷入力','http://www2.nict.go.jp/kk/e416/EDR#ID10256e'),
 (1,'品質管理','http://www2.nict.go.jp/kk/e416/EDR#ID3bd183'),
 (1,'取引先','http://www2.nict.go.jp/kk/e416/EDR#ID1f3a31'),
 (1,'発注書','http://www2.nict.go.jp/kk/e416/EDR#ID7024e8'),
 (1,'財務会計','http://www2.nict.go.jp/kk/e416/EDR#ID1fae78'),
 (1,'窓口','http://www2.nict.go.jp/kk/e416/EDR#ID3c17f9'),
 (1,'調達期間','http://www2.nict.go.jp/kk/e416/EDR#ID3cfdcb'),
 (1,'発注管理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf216'),
 (1,'売掛金','http://www2.nict.go.jp/kk/e416/EDR#ID1e8978'),
 (1,'責任者','http://www2.nict.go.jp/kk/e416/EDR#ID3c1105'),
 (1,'社員','http://www2.nict.go.jp/kk/e416/EDR#ID0f514f'),
 (1,'希望納期','http://www2.nict.go.jp/kk/e416/EDR#ID102e7f'),
 (1,'発注予定','http://www2.nict.go.jp/kk/e416/EDR#ID10d584'),
 (1,'注文書','http://www2.nict.go.jp/kk/e416/EDR#ID0fdb2a'),
 (1,'配送','http://www2.nict.go.jp/kk/e416/EDR#ID1035b8'),
 (1,'在庫費用率','http://www2.nict.go.jp/kk/e416/EDR#ID3cf335'),
 (1,'発注入力','http://www2.nict.go.jp/kk/e416/EDR#ID10256e'),
 (1,'製造業','http://www2.nict.go.jp/kk/e416/EDR#ID0f9515'),
 (1,'MRP','http://www2.nict.go.jp/kk/e416/EDR#ID0a958a'),
 (1,'CTO','http://www2.nict.go.jp/kk/e416/EDR#ID700a12'),
 (1,'資材','http://www2.nict.go.jp/kk/e416/EDR#ID3cf511'),
 (1,'クロスドッキング','http://www2.nict.go.jp/kk/e416/EDR#ID3befd5'),
 (1,'与信限度額','http://www2.nict.go.jp/kk/e416/EDR#ID0eb5f7'),
 (1,'費用率','http://www2.nict.go.jp/kk/e416/EDR#ID3cf335'),
 (1,'店','http://www2.nict.go.jp/kk/e416/EDR#ID0f5b31'),
 (1,'WEB-EDI','http://www2.nict.go.jp/kk/e416/EDR#ID702e7e'),
 (1,'売上入力','http://www2.nict.go.jp/kk/e416/EDR#ID10256e'),
 (1,'保管棚','http://www2.nict.go.jp/kk/e416/EDR#ID0fc3e1'),
 (1,'原価管理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf216'),
 (1,'小売店','http://www2.nict.go.jp/kk/e416/EDR#ID0f5f75'),
 (1,'購買部門','http://www2.nict.go.jp/kk/e416/EDR#ID3cf2a9'),
 (1,'平均調達期間','http://www2.nict.go.jp/kk/e416/EDR#ID3cfdcb'),
 (1,'仕掛品','http://www2.nict.go.jp/kk/e416/EDR#ID3c4095'),
 (1,'材料','http://www2.nict.go.jp/kk/e416/EDR#ID0f3c77'),
 (1,'手配','http://www2.nict.go.jp/kk/e416/EDR#ID0ff488'),
 (1,'見積書','http://www2.nict.go.jp/kk/e416/EDR#ID1e8c8c'),
 (1,'保管場所','http://www2.nict.go.jp/kk/e416/EDR#ID3cf5fb'),
 (1,'製造','http://www2.nict.go.jp/kk/e416/EDR#ID0f9516'),
 (1,'物流','http://www2.nict.go.jp/kk/e416/EDR#ID1f3e05'),
 (1,'新規顧客','http://www2.nict.go.jp/kk/e416/EDR#ID0f0fe3'),
 (1,'納品伝票','http://www2.nict.go.jp/kk/e416/EDR#ID0ffd77'),
 (1,'総務部門','http://www2.nict.go.jp/kk/e416/EDR#ID3cf2a9'),
 (1,'取引先登録','http://www2.nict.go.jp/kk/e416/EDR#ID3d027f'),
 (1,'督促表','http://www2.nict.go.jp/kk/e416/EDR#ID3cf761'),
 (1,'登録','http://www2.nict.go.jp/kk/e416/EDR#ID3d027f'),
 (1,'コスト','http://www2.nict.go.jp/kk/e416/EDR#ID0f1689'),
 (1,'棚卸記入票','http://www2.nict.go.jp/kk/e416/EDR#ID3c6a08'),
 (1,'売買契約書','http://www2.nict.go.jp/kk/e416/EDR#ID0ef348'),
 (1,'圧縮','http://www2.nict.go.jp/kk/e416/EDR#ID0e328d'),
 (1,'表','http://www2.nict.go.jp/kk/e416/EDR#ID3cf761'),
 (1,'在庫処理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf15e'),
 (1,'入力','http://www2.nict.go.jp/kk/e416/EDR#ID10256e'),
 (1,'後継品','http://www2.nict.go.jp/kk/e416/EDR#ID0f4bf7'),
 (1,'顧客満足度向上','http://www2.nict.go.jp/kk/e416/EDR#ID3ce7fa'),
 (1,'出荷','http://www2.nict.go.jp/kk/e416/EDR#ID3cff8a'),
 (1,'定期発注法','http://www2.nict.go.jp/kk/e416/EDR#ID3d0239');
/*!40000 ALTER TABLE `input_term_concept_map` ENABLE KEYS */;


--
-- Definition of table `input_term_construct_tree_option`
--

DROP TABLE IF EXISTS `input_term_construct_tree_option`;
CREATE TABLE `input_term_construct_tree_option` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Input_Term` text NOT NULL,
  `Input_Concept` text NOT NULL,
  `Tree_Option` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `input_term_construct_tree_option`
--

/*!40000 ALTER TABLE `input_term_construct_tree_option` DISABLE KEYS */;
INSERT INTO `input_term_construct_tree_option` (`Project_ID`,`Input_Term`,`Input_Concept`,`Tree_Option`) VALUES 
 (1,'棚卸差異','http://www2.nict.go.jp/kk/e416/EDR#ID3c69c7','SUB'),
 (1,'赤黒処理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf15e','SUB'),
 (1,'在庫処理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf15e','SUB'),
 (1,'発注登録','http://www2.nict.go.jp/kk/e416/EDR#ID3d027f','SUB'),
 (1,'設計部門','http://www2.nict.go.jp/kk/e416/EDR#ID3cf2a9','SUB'),
 (1,'スポット購買','http://www2.nict.go.jp/kk/e416/EDR#ID3d1717','SUB'),
 (1,'定期訪問','http://www2.nict.go.jp/kk/e416/EDR#ID0e14e0','SUB'),
 (1,'納期回答','http://www2.nict.go.jp/kk/e416/EDR#ID0e5231','SUB'),
 (1,'高額商品','http://www2.nict.go.jp/kk/e416/EDR#ID4444f5','SUB'),
 (1,'資材購買部','http://www2.nict.go.jp/kk/e416/EDR#ID1082ab','SUB'),
 (1,'受注機会損失','http://www2.nict.go.jp/kk/e416/EDR#ID3cff6b','SUB'),
 (1,'注文書発行','http://www2.nict.go.jp/kk/e416/EDR#ID3cf770','SUB'),
 (1,'外注課','http://www2.nict.go.jp/kk/e416/EDR#ID0e8710','SUB'),
 (1,'調達先','http://www2.nict.go.jp/kk/e416/EDR#ID0f301c','SUB'),
 (1,'セット商品','http://www2.nict.go.jp/kk/e416/EDR#ID4444f5','SUB'),
 (1,'受注残管理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf216','SUB'),
 (1,'原価管理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf216','SUB'),
 (1,'新規顧客','http://www2.nict.go.jp/kk/e416/EDR#ID0f0fe3','SUB'),
 (1,'WEB-EDI','http://www2.nict.go.jp/kk/e416/EDR#ID702e7e','SUB'),
 (1,'経理部門','http://www2.nict.go.jp/kk/e416/EDR#ID3cf2a9','SUB'),
 (1,'注文商品','http://www2.nict.go.jp/kk/e416/EDR#ID4444f5','SUB'),
 (1,'適正在庫','http://www2.nict.go.jp/kk/e416/EDR#ID0f8d30','SUB'),
 (1,'コンピュータ在庫','http://www2.nict.go.jp/kk/e416/EDR#ID0f8d30','SUB'),
 (1,'ハンディターミナル','http://www2.nict.go.jp/kk/e416/EDR#ID3c2546','SUB'),
 (1,'売価還元法','http://www2.nict.go.jp/kk/e416/EDR#ID0ead07','SUB'),
 (1,'売買契約書','http://www2.nict.go.jp/kk/e416/EDR#ID0ef348','SUB'),
 (1,'納品伝票','http://www2.nict.go.jp/kk/e416/EDR#ID0ffd77','SUB'),
 (1,'貯蔵品','http://www2.nict.go.jp/kk/e416/EDR#ID0f4bf7','SUB'),
 (1,'消耗財','http://www2.nict.go.jp/kk/e416/EDR#ID3d04c7','SUB'),
 (1,'納期管理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf216','SUB'),
 (1,'納品業者','http://www2.nict.go.jp/kk/e416/EDR#ID0ed812','SUB'),
 (1,'集中購買','http://www2.nict.go.jp/kk/e416/EDR#ID3d1717','SUB'),
 (1,'有効在庫','http://www2.nict.go.jp/kk/e416/EDR#ID0f8d30','SUB'),
 (1,'発注法','http://www2.nict.go.jp/kk/e416/EDR#ID3d0239','SUB'),
 (1,'主要材料','http://www2.nict.go.jp/kk/e416/EDR#ID0f3c77','SUB'),
 (1,'発注入力','http://www2.nict.go.jp/kk/e416/EDR#ID10256e','SUB'),
 (1,'見積依頼','http://www2.nict.go.jp/kk/e416/EDR#ID3ce62e','SUB'),
 (1,'棚卸記入票','http://www2.nict.go.jp/kk/e416/EDR#ID3c6a08','SUB'),
 (1,'発注管理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf216','SUB'),
 (1,'総務部門','http://www2.nict.go.jp/kk/e416/EDR#ID3cf2a9','SUB'),
 (1,'原料在庫','http://www2.nict.go.jp/kk/e416/EDR#ID0f8d30','SUB'),
 (1,'受注管理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf216','SUB'),
 (1,'調達期間','http://www2.nict.go.jp/kk/e416/EDR#ID3cfdcb','SUB'),
 (1,'損益計算','http://www2.nict.go.jp/kk/e416/EDR#ID0ef08d','SUB'),
 (1,'購買部門','http://www2.nict.go.jp/kk/e416/EDR#ID3cf2a9','SUB'),
 (1,'営業部門','http://www2.nict.go.jp/kk/e416/EDR#ID3cf2a9','SUB'),
 (1,'顧客満足度向上','http://www2.nict.go.jp/kk/e416/EDR#ID3ce7fa','SUB'),
 (1,'物流センター','http://www2.nict.go.jp/kk/e416/EDR#ID3c1342','SUB'),
 (1,'与信限度額','http://www2.nict.go.jp/kk/e416/EDR#ID0eb5f7','SUB'),
 (1,'共同配送','http://www2.nict.go.jp/kk/e416/EDR#ID1035b8','SUB'),
 (1,'棚卸業者','http://www2.nict.go.jp/kk/e416/EDR#ID0ed812','SUB'),
 (1,'出荷予定','http://www2.nict.go.jp/kk/e416/EDR#ID10d584','SUB'),
 (1,'モーダルシフト','http://www2.nict.go.jp/kk/e416/EDR#ID0f4e13','SUB'),
 (1,'卸売業','http://www2.nict.go.jp/kk/e416/EDR#ID3ce800','SUB'),
 (1,'外注加工品','http://www2.nict.go.jp/kk/e416/EDR#ID200d3c','SUB'),
 (1,'保管場所','http://www2.nict.go.jp/kk/e416/EDR#ID3cf5fb','SUB'),
 (1,'運用付加','http://www2.nict.go.jp/kk/e416/EDR#ID3cf20f','SUB'),
 (1,'VAN会社','http://www2.nict.go.jp/kk/e416/EDR#ID30f74c','SUB'),
 (1,'流通経路','http://www2.nict.go.jp/kk/e416/EDR#ID30f75e','SUB'),
 (1,'倉庫番','http://www2.nict.go.jp/kk/e416/EDR#ID3d042e','SUB'),
 (1,'理論在庫','http://www2.nict.go.jp/kk/e416/EDR#ID0f8d30','SUB'),
 (1,'流行品','http://www2.nict.go.jp/kk/e416/EDR#ID0f4bf7','SUB'),
 (1,'トラック配送振り分け','http://www2.nict.go.jp/kk/e416/EDR#ID10752b','SUB'),
 (1,'発注予定','http://www2.nict.go.jp/kk/e416/EDR#ID10d584','SUB'),
 (1,'クロスドッキング','http://www2.nict.go.jp/kk/e416/EDR#ID3befd5','SUB'),
 (1,'取引先登録','http://www2.nict.go.jp/kk/e416/EDR#ID3d027f','SUB'),
 (1,'ライン受注','http://www2.nict.go.jp/kk/e416/EDR#ID0f7b46','SUB'),
 (1,'納品書発行','http://www2.nict.go.jp/kk/e416/EDR#ID3cf770','SUB'),
 (1,'調達方式','http://www2.nict.go.jp/kk/e416/EDR#ID1e8626','SUB'),
 (1,'営業管理部','http://www2.nict.go.jp/kk/e416/EDR#ID201aec','SUB'),
 (1,'平均調達期間','http://www2.nict.go.jp/kk/e416/EDR#ID3cfdcb','SUB'),
 (1,'在庫費用率','http://www2.nict.go.jp/kk/e416/EDR#ID3cf335','SUB'),
 (1,'基本契約書','http://www2.nict.go.jp/kk/e416/EDR#ID0ef348','SUB'),
 (1,'発注先','http://www2.nict.go.jp/kk/e416/EDR#ID0f301c','SUB'),
 (1,'希望納期','http://www2.nict.go.jp/kk/e416/EDR#ID102e7f','SUB'),
 (1,'定量発注','http://www2.nict.go.jp/kk/e416/EDR#ID103dfd','SUB'),
 (1,'在庫問い合わせ','http://www2.nict.go.jp/kk/e416/EDR#ID0f4b12','SUB'),
 (1,'JAN-POS','http://www2.nict.go.jp/kk/e416/EDR#ID0aa0e7','SUB'),
 (1,'出荷指示書','http://www2.nict.go.jp/kk/e416/EDR#ID0f5a5e','SUB'),
 (1,'売価率','http://www2.nict.go.jp/kk/e416/EDR#ID3cf335','SUB'),
 (1,'仕込み在庫','http://www2.nict.go.jp/kk/e416/EDR#ID0f8d30','SUB'),
 (1,'3PL企業','http://www2.nict.go.jp/kk/e416/EDR#ID0ebd0c','SUB'),
 (1,'実在庫','http://www2.nict.go.jp/kk/e416/EDR#ID0f8d30','SUB'),
 (1,'発注依頼','http://www2.nict.go.jp/kk/e416/EDR#ID3ce62e','SUB'),
 (1,'受注商品','http://www2.nict.go.jp/kk/e416/EDR#ID4444f5','SUB'),
 (1,'庶務課','http://www2.nict.go.jp/kk/e416/EDR#ID0e8710','SUB'),
 (1,'指示書','http://www2.nict.go.jp/kk/e416/EDR#ID0f5a5e','SUB'),
 (1,'伝票記載ミス','http://www2.nict.go.jp/kk/e416/EDR#ID3cef6c','SUB'),
 (1,'POS導入店','http://www2.nict.go.jp/kk/e416/EDR#ID0f5b31','SUB'),
 (1,'伝票取り消し','http://www2.nict.go.jp/kk/e416/EDR#ID0ff2cb','SUB'),
 (1,'平均法','http://www2.nict.go.jp/kk/e416/EDR#ID3d0239','SUB'),
 (1,'売上入力','http://www2.nict.go.jp/kk/e416/EDR#ID10256e','SUB'),
 (1,'入荷予定表','http://www2.nict.go.jp/kk/e416/EDR#ID10d587','SUB'),
 (1,'審査部門','http://www2.nict.go.jp/kk/e416/EDR#ID3cf2a9','SUB'),
 (1,'売上管理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf216','SUB'),
 (1,'入荷入力','http://www2.nict.go.jp/kk/e416/EDR#ID10256e','SUB'),
 (1,'営業担当者','http://www2.nict.go.jp/kk/e416/EDR#ID200bc6','SUB'),
 (1,'要求納期','http://www2.nict.go.jp/kk/e416/EDR#ID102e7f','SUB'),
 (1,'発注伝票','http://www2.nict.go.jp/kk/e416/EDR#ID0ffd77','SUB'),
 (1,'保管棚','http://www2.nict.go.jp/kk/e416/EDR#ID0fc3e1','SUB'),
 (1,'費用率','http://www2.nict.go.jp/kk/e416/EDR#ID3cf335','SUB'),
 (1,'指定伝票','http://www2.nict.go.jp/kk/e416/EDR#ID0ffd77','SUB'),
 (1,'後継品','http://www2.nict.go.jp/kk/e416/EDR#ID0f4bf7','SUB'),
 (1,'財務会計','http://www2.nict.go.jp/kk/e416/EDR#ID1fae78','SUB'),
 (1,'出荷入力','http://www2.nict.go.jp/kk/e416/EDR#ID10256e','SUB'),
 (1,'売上伝票','http://www2.nict.go.jp/kk/e416/EDR#ID0ffd77','SUB'),
 (1,'在庫圧縮','http://www2.nict.go.jp/kk/e416/EDR#ID0e328d','SUB'),
 (1,'定期発注法','http://www2.nict.go.jp/kk/e416/EDR#ID3d0239','SUB'),
 (1,'資材部門','http://www2.nict.go.jp/kk/e416/EDR#ID3cf2a9','SUB'),
 (1,'取扱商品','http://www2.nict.go.jp/kk/e416/EDR#ID4444f5','SUB'),
 (1,'督促表','http://www2.nict.go.jp/kk/e416/EDR#ID3cf761','SUB'),
 (1,'受注完了','http://www2.nict.go.jp/kk/e416/EDR#ID3cedc2','SUB'),
 (1,'トラック輸送','http://www2.nict.go.jp/kk/e416/EDR#ID10cc89','SUB'),
 (1,'補助材料','http://www2.nict.go.jp/kk/e416/EDR#ID0f3c77','SUB'),
 (1,'通信コスト','http://www2.nict.go.jp/kk/e416/EDR#ID0f1689','SUB'),
 (1,'受注入力','http://www2.nict.go.jp/kk/e416/EDR#ID10256e','SUB'),
 (1,'在庫リスク','http://www2.nict.go.jp/kk/e416/EDR#ID3c19e0','SUB'),
 (1,'単純平均法','http://www2.nict.go.jp/kk/e416/EDR#ID3d0239','SUB'),
 (1,'在庫管理','http://www2.nict.go.jp/kk/e416/EDR#ID3cf216','SUB');
/*!40000 ALTER TABLE `input_term_construct_tree_option` ENABLE KEYS */;


--
-- Definition of table `input_term_set`
--

DROP TABLE IF EXISTS `input_term_set`;
CREATE TABLE `input_term_set` (
  `Project_ID` int(11) NOT NULL default '0',
  `Input_Term` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `input_term_set`
--

/*!40000 ALTER TABLE `input_term_set` DISABLE KEYS */;
INSERT INTO `input_term_set` (`Project_ID`,`Input_Term`) VALUES 
 (1,'部'),
 (1,'資材購買部'),
 (1,'番'),
 (1,'倉庫番'),
 (1,'D'),
 (1,'発注法'),
 (1,'平均法'),
 (1,'定期発注法'),
 (1,'単純平均法'),
 (1,'表'),
 (1,'督促表'),
 (1,'貯蔵品'),
 (1,'調達先'),
 (1,'発注先'),
 (1,'流行品'),
 (1,'業'),
 (1,'後継品'),
 (1,'完了'),
 (1,'品'),
 (1,'受注完了'),
 (1,'卸売業'),
 (1,'先'),
 (1,'財'),
 (1,'消耗財'),
 (1,'伝票記載ミス'),
 (1,'ミス'),
 (1,'SE'),
 (1,'場所'),
 (1,'保管場所'),
 (1,'補助材料'),
 (1,'物流センター'),
 (1,'棚'),
 (1,'材料'),
 (1,'店'),
 (1,'保管棚'),
 (1,'主要材料'),
 (1,'モーダルシフト'),
 (1,'センター'),
 (1,'シフト'),
 (1,'POS導入店'),
 (1,'決算'),
 (1,'振り分け'),
 (1,'指示書'),
 (1,'受付'),
 (1,'取引'),
 (1,'出荷指示書'),
 (1,'トラック配送振り分け'),
 (1,'額'),
 (1,'部門'),
 (1,'購買部門'),
 (1,'資産'),
 (1,'資材部門'),
 (1,'財務会計'),
 (1,'設計部門'),
 (1,'計算'),
 (1,'総務部門'),
 (1,'経理部門'),
 (1,'発注入力'),
 (1,'注文'),
 (1,'損益計算'),
 (1,'損失'),
 (1,'得意先'),
 (1,'審査部門'),
 (1,'売買'),
 (1,'売上入力'),
 (1,'営業部門'),
 (1,'受注機会損失'),
 (1,'受注入力'),
 (1,'出荷入力'),
 (1,'入荷入力'),
 (1,'入荷'),
 (1,'入力'),
 (1,'保証金'),
 (1,'会計'),
 (1,'予算'),
 (1,'与信限度額'),
 (1,'ハンディターミナル'),
 (1,'Q'),
 (1,'顧客'),
 (1,'運用付加'),
 (1,'通信コスト'),
 (1,'費用率'),
 (1,'覚書'),
 (1,'納期管理'),
 (1,'管理'),
 (1,'窓口'),
 (1,'発注管理'),
 (1,'発注予定'),
 (1,'率'),
 (1,'新規顧客'),
 (1,'売価率'),
 (1,'売上管理'),
 (1,'在庫費用率'),
 (1,'在庫管理'),
 (1,'受注管理'),
 (1,'受注残管理'),
 (1,'取り消し'),
 (1,'原価管理'),
 (1,'出荷予定'),
 (1,'倉庫'),
 (1,'保証'),
 (1,'伝票取り消し'),
 (1,'付加'),
 (1,'予定'),
 (1,'コスト'),
 (1,'CTO'),
 (1,'高額商品'),
 (1,'顧客満足度向上'),
 (1,'配送'),
 (1,'還元法'),
 (1,'返品'),
 (1,'赤黒処理'),
 (1,'調達期間'),
 (1,'調達'),
 (1,'課'),
 (1,'訪問'),
 (1,'見積依頼'),
 (1,'製造'),
 (1,'経路'),
 (1,'納期回答'),
 (1,'納品業者'),
 (1,'納品'),
 (1,'票'),
 (1,'社員'),
 (1,'社印'),
 (1,'発注依頼'),
 (1,'生産管理'),
 (1,'流通経路'),
 (1,'注文商品'),
 (1,'業者'),
 (1,'棚卸記入票'),
 (1,'棚卸業者'),
 (1,'棚卸差異'),
 (1,'手配'),
 (1,'庶務課'),
 (1,'店舗'),
 (1,'平均調達期間'),
 (1,'差異'),
 (1,'定期訪問'),
 (1,'外注課'),
 (1,'売価還元法'),
 (1,'在庫圧縮'),
 (1,'在庫問い合わせ'),
 (1,'在庫処理'),
 (1,'在庫リスク'),
 (1,'圧縮'),
 (1,'回答'),
 (1,'問い合わせ'),
 (1,'商品'),
 (1,'向上'),
 (1,'受注商品'),
 (1,'取扱商品'),
 (1,'原価'),
 (1,'印紙'),
 (1,'処理'),
 (1,'共同配送'),
 (1,'入荷予定表'),
 (1,'依頼'),
 (1,'会社'),
 (1,'企業'),
 (1,'仕入れ'),
 (1,'予定表'),
 (1,'リスク'),
 (1,'メーカ'),
 (1,'セット商品'),
 (1,'WEB-EDI'),
 (1,'VAN会社'),
 (1,'POS'),
 (1,'MS'),
 (1,'JAN-POS'),
 (1,'EDI'),
 (1,'3PL企業'),
 (1,'類似品'),
 (1,'集中購買'),
 (1,'適正在庫'),
 (1,'輸送'),
 (1,'購買'),
 (1,'資材'),
 (1,'貸借対照表'),
 (1,'責任者'),
 (1,'販売管理'),
 (1,'販売'),
 (1,'調達方式'),
 (1,'調整'),
 (1,'見積書'),
 (1,'見積'),
 (1,'要求納期'),
 (1,'製造業'),
 (1,'製品'),
 (1,'経費'),
 (1,'納期'),
 (1,'納品書発行'),
 (1,'納品伝票'),
 (1,'納入'),
 (1,'管理部'),
 (1,'登録'),
 (1,'発送'),
 (1,'発行'),
 (1,'発注登録'),
 (1,'発注書'),
 (1,'発注伝票'),
 (1,'発注'),
 (1,'生産'),
 (1,'理論在庫'),
 (1,'物流'),
 (1,'消耗品'),
 (1,'注文書発行'),
 (1,'注文書'),
 (1,'決済'),
 (1,'検品'),
 (1,'有効在庫'),
 (1,'方式'),
 (1,'損益計算書'),
 (1,'指定伝票'),
 (1,'担当者'),
 (1,'技術者'),
 (1,'帳票'),
 (1,'希望納期'),
 (1,'小売店'),
 (1,'実在庫'),
 (1,'定量発注'),
 (1,'定番商品'),
 (1,'契約書'),
 (1,'外注加工品'),
 (1,'外注'),
 (1,'売買契約書'),
 (1,'売掛金'),
 (1,'売上伝票'),
 (1,'基本契約書'),
 (1,'在庫品'),
 (1,'在庫'),
 (1,'回収'),
 (1,'営業管理部'),
 (1,'営業担当者'),
 (1,'商談'),
 (1,'品質管理'),
 (1,'受注生産'),
 (1,'受注'),
 (1,'取引先登録'),
 (1,'取引先'),
 (1,'原料在庫'),
 (1,'加工品'),
 (1,'加工'),
 (1,'出荷'),
 (1,'出庫'),
 (1,'備品'),
 (1,'伝票'),
 (1,'代替品'),
 (1,'仕込み在庫'),
 (1,'仕掛品'),
 (1,'不良品'),
 (1,'ライン受注'),
 (1,'ドッキング'),
 (1,'トラック輸送'),
 (1,'スポット購買'),
 (1,'コールセンター'),
 (1,'コンピュータ在庫'),
 (1,'クロスドッキング'),
 (1,'VAN'),
 (1,'TMS'),
 (1,'SFA'),
 (1,'RM'),
 (1,'MRP'),
 (1,'EOS'),
 (1,'BTO'),
 (1,'CRM'),
 (1,'EOQ'),
 (1,'KKD'),
 (1,'KU'),
 (1,'SKU'),
 (1,'WMS'),
 (1,'マスタ'),
 (1,'商品マスタ'),
 (1,'検収'),
 (1,'納品書');
/*!40000 ALTER TABLE `input_term_set` ENABLE KEYS */;


--
-- Definition of table `property_concept_list`
--

DROP TABLE IF EXISTS `property_concept_list`;
CREATE TABLE `property_concept_list` (
  `Project_ID` int(10) unsigned NOT NULL auto_increment,
  `Concept_List_ID` int(10) unsigned NOT NULL,
  `Concept` text NOT NULL,
  PRIMARY KEY  (`Project_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `property_concept_list`
--

/*!40000 ALTER TABLE `property_concept_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `property_concept_list` ENABLE KEYS */;


--
-- Definition of table `property_trimmed_result_analysis`
--

DROP TABLE IF EXISTS `property_trimmed_result_analysis`;
CREATE TABLE `property_trimmed_result_analysis` (
  `Project_ID` int(10) unsigned NOT NULL auto_increment,
  `Concept_List_ID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`Project_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `property_trimmed_result_analysis`
--

/*!40000 ALTER TABLE `property_trimmed_result_analysis` DISABLE KEYS */;
/*!40000 ALTER TABLE `property_trimmed_result_analysis` ENABLE KEYS */;


--
-- Definition of table `removed_word_info`
--

DROP TABLE IF EXISTS `removed_word_info`;
CREATE TABLE `removed_word_info` (
  `Project_ID` int(10) unsigned NOT NULL auto_increment,
  `Term` text NOT NULL,
  `POS_List_ID` int(10) unsigned NOT NULL,
  `TF` int(10) unsigned NOT NULL,
  `IDF` double NOT NULL,
  `TF-IDF` double NOT NULL,
  `Doc_List_ID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`Project_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `removed_word_info`
--

/*!40000 ALTER TABLE `removed_word_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `removed_word_info` ENABLE KEYS */;


--
-- Definition of table `removed_word_info_doc_list`
--

DROP TABLE IF EXISTS `removed_word_info_doc_list`;
CREATE TABLE `removed_word_info_doc_list` (
  `Project_ID` int(10) unsigned NOT NULL auto_increment,
  `Doc_List_ID` int(10) unsigned NOT NULL,
  `Doc` text NOT NULL,
  `TF` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`Project_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `removed_word_info_doc_list`
--

/*!40000 ALTER TABLE `removed_word_info_doc_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `removed_word_info_doc_list` ENABLE KEYS */;


--
-- Definition of table `removed_word_info_pos_list`
--

DROP TABLE IF EXISTS `removed_word_info_pos_list`;
CREATE TABLE `removed_word_info_pos_list` (
  `Project_ID` int(10) unsigned NOT NULL auto_increment,
  `POST_List_ID` int(10) unsigned NOT NULL,
  `POS` text NOT NULL,
  PRIMARY KEY  (`Project_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `removed_word_info_pos_list`
--

/*!40000 ALTER TABLE `removed_word_info_pos_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `removed_word_info_pos_list` ENABLE KEYS */;


--
-- Definition of table `word_info`
--

DROP TABLE IF EXISTS `word_info`;
CREATE TABLE `word_info` (
  `Project_ID` int(10) unsigned NOT NULL auto_increment,
  `Term` text NOT NULL,
  `POS_List_ID` int(10) unsigned NOT NULL,
  `TF` int(10) unsigned NOT NULL,
  `IDF` double NOT NULL,
  `TF-IDF` double NOT NULL,
  `Doc_List_ID` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`Project_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `word_info`
--

/*!40000 ALTER TABLE `word_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `word_info` ENABLE KEYS */;


--
-- Definition of table `word_info_doc_list`
--

DROP TABLE IF EXISTS `word_info_doc_list`;
CREATE TABLE `word_info_doc_list` (
  `Project_ID` int(10) unsigned NOT NULL auto_increment,
  `Doc_List_ID` int(10) unsigned NOT NULL,
  `Doc` text NOT NULL,
  `TF` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`Project_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `word_info_doc_list`
--

/*!40000 ALTER TABLE `word_info_doc_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `word_info_doc_list` ENABLE KEYS */;


--
-- Definition of table `word_info_pos_list`
--

DROP TABLE IF EXISTS `word_info_pos_list`;
CREATE TABLE `word_info_pos_list` (
  `Project_ID` int(10) unsigned NOT NULL auto_increment,
  `POS_List_ID` int(10) unsigned NOT NULL,
  `POST` text NOT NULL,
  PRIMARY KEY  (`Project_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `word_info_pos_list`
--

/*!40000 ALTER TABLE `word_info_pos_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `word_info_pos_list` ENABLE KEYS */;


--
-- Definition of table `wordspace_result`
--

DROP TABLE IF EXISTS `wordspace_result`;
CREATE TABLE `wordspace_result` (
  `Project_ID` int(10) unsigned NOT NULL auto_increment,
  `Doc_ID` int(10) unsigned NOT NULL,
  `Term1` text NOT NULL,
  `Term2` text NOT NULL,
  `WordSpace_Value` double NOT NULL,
  PRIMARY KEY  (`Project_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `wordspace_result`
--

/*!40000 ALTER TABLE `wordspace_result` DISABLE KEYS */;
/*!40000 ALTER TABLE `wordspace_result` ENABLE KEYS */;


--
-- Definition of table `wrong_pair`
--

DROP TABLE IF EXISTS `wrong_pair`;
CREATE TABLE `wrong_pair` (
  `Project_ID` int(10) unsigned NOT NULL auto_increment,
  `Term1` text NOT NULL,
  `Term2` text NOT NULL,
  PRIMARY KEY  (`Project_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `wrong_pair`
--

/*!40000 ALTER TABLE `wrong_pair` DISABLE KEYS */;
/*!40000 ALTER TABLE `wrong_pair` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
