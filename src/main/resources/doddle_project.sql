-- MySQL dump 10.11
--
-- Host: localhost    Database: doddle
-- ------------------------------------------------------
-- Server version	5.0.45-community-nt

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `apriori_result`
--

DROP TABLE IF EXISTS `apriori_result`;
CREATE TABLE `apriori_result` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Doc_ID` int(10) unsigned NOT NULL,
  `Term1` text NOT NULL,
  `Term2` text NOT NULL,
  `Value` double NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

--
-- Table structure for table `class_trimmed_result_analysis`
--

DROP TABLE IF EXISTS `class_trimmed_result_analysis`;
CREATE TABLE `class_trimmed_result_analysis` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Concept_List_ID` int(10) unsigned NOT NULL,
  `Target_Concept` text NOT NULL,
  `Target_Parent_Concept` text NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `concept_definition`
--

DROP TABLE IF EXISTS `concept_definition`;
CREATE TABLE `concept_definition` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `is_Meta_Property` tinyint(1) NOT NULL,
  `Term1` text NOT NULL,
  `Relation` text NOT NULL,
  `Term2` text NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `concept_definition_parameter`
--

DROP TABLE IF EXISTS `concept_definition_parameter`;
CREATE TABLE `concept_definition_parameter` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Minimum_Confidence` int(10) unsigned NOT NULL,
  `Minimum_Support` double NOT NULL,
  `Front_Scope` int(10) unsigned NOT NULL,
  `Behind_Scope` int(10) unsigned NOT NULL,
  `N_Gram` int(10) unsigned NOT NULL,
  `Gram_Count` int(10) unsigned NOT NULL,
  `Word_Space_Value` int(10) unsigned NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `construct_tree_option`
--

DROP TABLE IF EXISTS `construct_tree_option`;
CREATE TABLE `construct_tree_option` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `is_Tree_Construction` tinyint(1) NOT NULL,
  `is_Construction_With_Compound_Word_Tree` tinyint(1) NOT NULL,
  `is_Trimming_Internal_Node` tinyint(1) NOT NULL,
  `is_Add_Abstract_Concept_With_Compound_Word_Tree` tinyint(1) NOT NULL,
  `is_Trimming_Internal_Node_With_Compound_Word_Tree` tinyint(1) NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `doc_info`
--

DROP TABLE IF EXISTS `doc_info`;
CREATE TABLE `doc_info` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Doc_ID` int(10) unsigned NOT NULL,
  `Doc_Path` text NOT NULL,
  `Language` text NOT NULL,
  `Text` text NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `eval_concept_set`
--

DROP TABLE IF EXISTS `eval_concept_set`;
CREATE TABLE `eval_concept_set` (
  `Project_ID` int(10) unsigned NOT NULL,
  `Term_ID` int(10) unsigned NOT NULL,
  `Eval_Value` double NOT NULL,
  `Concept` text NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `general_ontology_info`
--

DROP TABLE IF EXISTS `general_ontology_info`;
CREATE TABLE `general_ontology_info` (
  `EDR_General` tinyint(1) NOT NULL,
  `EDR_Technical` tinyint(1) NOT NULL,
  `WordNet` tinyint(1) NOT NULL,
  `Project_ID` int(10) unsigned NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `input_concept_set`
--

DROP TABLE IF EXISTS `input_concept_set`;
CREATE TABLE `input_concept_set` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Input_Concept` text NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `input_term_concept_map`
--

DROP TABLE IF EXISTS `input_term_concept_map`;
CREATE TABLE `input_term_concept_map` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Input_Term` text NOT NULL,
  `Input_Concept` text NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `input_term_construct_tree_option`
--

DROP TABLE IF EXISTS `input_term_construct_tree_option`;
CREATE TABLE `input_term_construct_tree_option` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Input_Term` text NOT NULL,
  `Input_Concept` text NOT NULL,
  `Tree_Option` varchar(45) NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `input_term_set`
--

DROP TABLE IF EXISTS `input_term_set`;
CREATE TABLE `input_term_set` (
  `Project_ID` int(11) NOT NULL default '0',
  `Input_Term` text NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `jena_g1t0_reif`
--

DROP TABLE IF EXISTS `jena_g1t0_reif`;
CREATE TABLE `jena_g1t0_reif` (
  `Subj` varchar(100) character set utf8 collate utf8_bin default NULL,
  `Prop` varchar(100) character set utf8 collate utf8_bin default NULL,
  `Obj` varchar(100) character set utf8 collate utf8_bin default NULL,
  `GraphID` int(11) default NULL,
  `Stmt` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `HasType` char(1) NOT NULL,
  UNIQUE KEY `jena_g1t0_reifXSTMT` (`Stmt`,`HasType`),
  KEY `jena_g1t0_reifXSP` (`Subj`,`Prop`),
  KEY `jena_g1t0_reifXO` (`Obj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_g1t1_stmt`
--

DROP TABLE IF EXISTS `jena_g1t1_stmt`;
CREATE TABLE `jena_g1t1_stmt` (
  `Subj` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `Prop` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `Obj` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `GraphID` int(11) default NULL,
  KEY `jena_g1t1_stmtXSP` (`Subj`,`Prop`),
  KEY `jena_g1t1_stmtXO` (`Obj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_g2t0_reif`
--

DROP TABLE IF EXISTS `jena_g2t0_reif`;
CREATE TABLE `jena_g2t0_reif` (
  `Subj` varchar(100) character set utf8 collate utf8_bin default NULL,
  `Prop` varchar(100) character set utf8 collate utf8_bin default NULL,
  `Obj` varchar(100) character set utf8 collate utf8_bin default NULL,
  `GraphID` int(11) default NULL,
  `Stmt` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `HasType` char(1) NOT NULL,
  UNIQUE KEY `jena_g2t0_reifXSTMT` (`Stmt`,`HasType`),
  KEY `jena_g2t0_reifXSP` (`Subj`,`Prop`),
  KEY `jena_g2t0_reifXO` (`Obj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_g2t1_stmt`
--

DROP TABLE IF EXISTS `jena_g2t1_stmt`;
CREATE TABLE `jena_g2t1_stmt` (
  `Subj` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `Prop` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `Obj` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `GraphID` int(11) default NULL,
  KEY `jena_g2t1_stmtXSP` (`Subj`,`Prop`),
  KEY `jena_g2t1_stmtXO` (`Obj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_g3t0_reif`
--

DROP TABLE IF EXISTS `jena_g3t0_reif`;
CREATE TABLE `jena_g3t0_reif` (
  `Subj` varchar(100) character set utf8 collate utf8_bin default NULL,
  `Prop` varchar(100) character set utf8 collate utf8_bin default NULL,
  `Obj` varchar(100) character set utf8 collate utf8_bin default NULL,
  `GraphID` int(11) default NULL,
  `Stmt` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `HasType` char(1) NOT NULL,
  UNIQUE KEY `jena_g3t0_reifXSTMT` (`Stmt`,`HasType`),
  KEY `jena_g3t0_reifXSP` (`Subj`,`Prop`),
  KEY `jena_g3t0_reifXO` (`Obj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_g3t1_stmt`
--

DROP TABLE IF EXISTS `jena_g3t1_stmt`;
CREATE TABLE `jena_g3t1_stmt` (
  `Subj` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `Prop` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `Obj` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `GraphID` int(11) default NULL,
  KEY `jena_g3t1_stmtXSP` (`Subj`,`Prop`),
  KEY `jena_g3t1_stmtXO` (`Obj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_g4t0_reif`
--

DROP TABLE IF EXISTS `jena_g4t0_reif`;
CREATE TABLE `jena_g4t0_reif` (
  `Subj` varchar(100) character set utf8 collate utf8_bin default NULL,
  `Prop` varchar(100) character set utf8 collate utf8_bin default NULL,
  `Obj` varchar(100) character set utf8 collate utf8_bin default NULL,
  `GraphID` int(11) default NULL,
  `Stmt` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `HasType` char(1) NOT NULL,
  UNIQUE KEY `jena_g4t0_reifXSTMT` (`Stmt`,`HasType`),
  KEY `jena_g4t0_reifXSP` (`Subj`,`Prop`),
  KEY `jena_g4t0_reifXO` (`Obj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_g4t1_stmt`
--

DROP TABLE IF EXISTS `jena_g4t1_stmt`;
CREATE TABLE `jena_g4t1_stmt` (
  `Subj` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `Prop` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `Obj` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `GraphID` int(11) default NULL,
  KEY `jena_g4t1_stmtXSP` (`Subj`,`Prop`),
  KEY `jena_g4t1_stmtXO` (`Obj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_g5t0_reif`
--

DROP TABLE IF EXISTS `jena_g5t0_reif`;
CREATE TABLE `jena_g5t0_reif` (
  `Subj` varchar(100) character set utf8 collate utf8_bin default NULL,
  `Prop` varchar(100) character set utf8 collate utf8_bin default NULL,
  `Obj` varchar(100) character set utf8 collate utf8_bin default NULL,
  `GraphID` int(11) default NULL,
  `Stmt` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `HasType` char(1) NOT NULL,
  UNIQUE KEY `jena_g5t0_reifXSTMT` (`Stmt`,`HasType`),
  KEY `jena_g5t0_reifXSP` (`Subj`,`Prop`),
  KEY `jena_g5t0_reifXO` (`Obj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_g5t1_stmt`
--

DROP TABLE IF EXISTS `jena_g5t1_stmt`;
CREATE TABLE `jena_g5t1_stmt` (
  `Subj` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `Prop` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `Obj` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `GraphID` int(11) default NULL,
  KEY `jena_g5t1_stmtXSP` (`Subj`,`Prop`),
  KEY `jena_g5t1_stmtXO` (`Obj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_g6t0_reif`
--

DROP TABLE IF EXISTS `jena_g6t0_reif`;
CREATE TABLE `jena_g6t0_reif` (
  `Subj` varchar(100) character set utf8 collate utf8_bin default NULL,
  `Prop` varchar(100) character set utf8 collate utf8_bin default NULL,
  `Obj` varchar(100) character set utf8 collate utf8_bin default NULL,
  `GraphID` int(11) default NULL,
  `Stmt` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `HasType` char(1) NOT NULL,
  UNIQUE KEY `jena_g6t0_reifXSTMT` (`Stmt`,`HasType`),
  KEY `jena_g6t0_reifXSP` (`Subj`,`Prop`),
  KEY `jena_g6t0_reifXO` (`Obj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_g6t1_stmt`
--

DROP TABLE IF EXISTS `jena_g6t1_stmt`;
CREATE TABLE `jena_g6t1_stmt` (
  `Subj` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `Prop` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `Obj` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `GraphID` int(11) default NULL,
  KEY `jena_g6t1_stmtXSP` (`Subj`,`Prop`),
  KEY `jena_g6t1_stmtXO` (`Obj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_g7t0_reif`
--

DROP TABLE IF EXISTS `jena_g7t0_reif`;
CREATE TABLE `jena_g7t0_reif` (
  `Subj` varchar(100) character set utf8 collate utf8_bin default NULL,
  `Prop` varchar(100) character set utf8 collate utf8_bin default NULL,
  `Obj` varchar(100) character set utf8 collate utf8_bin default NULL,
  `GraphID` int(11) default NULL,
  `Stmt` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `HasType` char(1) NOT NULL,
  UNIQUE KEY `jena_g7t0_reifXSTMT` (`Stmt`,`HasType`),
  KEY `jena_g7t0_reifXSP` (`Subj`,`Prop`),
  KEY `jena_g7t0_reifXO` (`Obj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_g7t1_stmt`
--

DROP TABLE IF EXISTS `jena_g7t1_stmt`;
CREATE TABLE `jena_g7t1_stmt` (
  `Subj` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `Prop` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `Obj` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `GraphID` int(11) default NULL,
  KEY `jena_g7t1_stmtXSP` (`Subj`,`Prop`),
  KEY `jena_g7t1_stmtXO` (`Obj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_graph`
--

DROP TABLE IF EXISTS `jena_graph`;
CREATE TABLE `jena_graph` (
  `ID` int(11) NOT NULL auto_increment,
  `Name` tinyblob,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_long_lit`
--

DROP TABLE IF EXISTS `jena_long_lit`;
CREATE TABLE `jena_long_lit` (
  `ID` int(11) NOT NULL auto_increment,
  `Head` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `ChkSum` bigint(20) default NULL,
  `Tail` mediumblob,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `jena_XLIT` (`Head`,`ChkSum`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_long_uri`
--

DROP TABLE IF EXISTS `jena_long_uri`;
CREATE TABLE `jena_long_uri` (
  `ID` int(11) NOT NULL auto_increment,
  `Head` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `ChkSum` bigint(20) default NULL,
  `Tail` mediumblob,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `jena_XURI` (`Head`,`ChkSum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_prefix`
--

DROP TABLE IF EXISTS `jena_prefix`;
CREATE TABLE `jena_prefix` (
  `ID` int(11) NOT NULL auto_increment,
  `Head` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `ChkSum` bigint(20) default NULL,
  `Tail` mediumblob,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `jena_XBND` (`Head`,`ChkSum`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `jena_sys_stmt`
--

DROP TABLE IF EXISTS `jena_sys_stmt`;
CREATE TABLE `jena_sys_stmt` (
  `Subj` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `Prop` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `Obj` varchar(100) character set utf8 collate utf8_bin NOT NULL,
  `GraphID` int(11) default NULL,
  KEY `jena_XSP` (`Subj`,`Prop`),
  KEY `jena_XO` (`Obj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `project_info`
--

DROP TABLE IF EXISTS `project_info`;
CREATE TABLE `project_info` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Project_Name` text NOT NULL,
  `Author` text NOT NULL,
  `Creation_Date` datetime NOT NULL,
  `Modification_Date` datetime NOT NULL,
  `Available_General_Ontologies` text NOT NULL,
  `Input_Term_Count` int(10) unsigned NOT NULL,
  `Perfectly_Matched_Term_Count` int(10) unsigned NOT NULL,
  `System_Added_Perfectly_Matched_Term_Count` int(10) unsigned NOT NULL,
  `Partially_Matched_Term_Count` int(10) unsigned NOT NULL,
  `Matched_Term_Count` int(10) unsigned NOT NULL,
  `Undefined_Term_Count` int(10) unsigned NOT NULL,
  `Input_Concept_Count` int(10) unsigned NOT NULL,
  `Input_Noun_Concept_Count` int(10) unsigned NOT NULL,
  `Input_Verb_Concept_Count` int(10) unsigned NOT NULL,
  `Class_SIN_Count` int(10) unsigned NOT NULL,
  `Before_Trimming_Class_Count` int(10) unsigned NOT NULL,
  `Trimmed_Class_Count` int(10) unsigned NOT NULL,
  `After_Trimming_Class_Count` int(10) unsigned NOT NULL,
  `Property_SIN_Count` int(10) unsigned NOT NULL,
  `Before_Trimming_Property_Count` int(10) unsigned NOT NULL,
  `Trimmed_Property_Count` int(10) unsigned NOT NULL,
  `After_Trimming_Property_Count` int(10) unsigned NOT NULL,
  `Abstract_Internal_Class_Count` int(10) unsigned NOT NULL,
  `Average_Abstract_Sibling_Concept_Count_In_Classes` int(10) unsigned NOT NULL,
  `Abstract_Internal_Property_Count_Message` int(10) unsigned NOT NULL,
  `Average_Abstract_Sibling_Concept_Count_In_Properties` int(10) unsigned NOT NULL,
  `Class_From_Compound_Word_Count` int(10) unsigned NOT NULL,
  `Property_From_Compound_Word_Count` int(10) unsigned NOT NULL,
  `Total_Class_Count` int(10) unsigned NOT NULL,
  `Total_Property_Count` int(10) unsigned NOT NULL,
  `Average_Sibling_Classes` double NOT NULL,
  `Average_Sibling_Properties` double NOT NULL,
  `Base_URI` text NOT NULL,
  `Comment` text NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `property_trimmed_result_analysis`
--

DROP TABLE IF EXISTS `property_trimmed_result_analysis`;
CREATE TABLE `property_trimmed_result_analysis` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Concept_List_ID` int(10) unsigned NOT NULL,
  `Target_Concept` text NOT NULL,
  `Target_Parent_Concept` text NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `removed_term_info`
--

DROP TABLE IF EXISTS `removed_term_info`;
CREATE TABLE `removed_term_info` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Term` text NOT NULL,
  `POS_List_ID` int(10) unsigned NOT NULL,
  `TF` int(10) unsigned NOT NULL,
  `IDF` double NOT NULL,
  `TF_IDF` double NOT NULL,
  `Doc_List_ID` int(10) unsigned NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `removed_term_info_doc_list`
--

DROP TABLE IF EXISTS `removed_term_info_doc_list`;
CREATE TABLE `removed_term_info_doc_list` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Doc_List_ID` int(10) unsigned NOT NULL,
  `Doc` text NOT NULL,
  `TF` int(10) unsigned NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `removed_term_info_pos_list`
--

DROP TABLE IF EXISTS `removed_term_info_pos_list`;
CREATE TABLE `removed_term_info_pos_list` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `POS_List_ID` int(10) unsigned NOT NULL,
  `POS` text NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `term_eval_concept_set`
--

DROP TABLE IF EXISTS `term_eval_concept_set`;
CREATE TABLE `term_eval_concept_set` (
  `Project_ID` int(10) unsigned NOT NULL,
  `Term_ID` int(10) unsigned NOT NULL,
  `Term` text NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `term_info`
--

DROP TABLE IF EXISTS `term_info`;
CREATE TABLE `term_info` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Term` text NOT NULL,
  `POS_List_ID` int(10) unsigned NOT NULL,
  `TF` int(10) unsigned NOT NULL,
  `IDF` double NOT NULL,
  `TF_IDF` double NOT NULL,
  `Doc_List_ID` int(10) unsigned NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `term_info_doc_list`
--

DROP TABLE IF EXISTS `term_info_doc_list`;
CREATE TABLE `term_info_doc_list` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Doc_List_ID` int(10) unsigned NOT NULL,
  `Doc` text NOT NULL,
  `TF` int(10) unsigned NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `term_info_pos_list`
--

DROP TABLE IF EXISTS `term_info_pos_list`;
CREATE TABLE `term_info_pos_list` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `POS_List_ID` int(10) unsigned NOT NULL,
  `POS` text NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `trimmed_class_list`
--

DROP TABLE IF EXISTS `trimmed_class_list`;
CREATE TABLE `trimmed_class_list` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Concept_List_ID` int(10) unsigned NOT NULL,
  `Concept` text NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `trimmed_property_list`
--

DROP TABLE IF EXISTS `trimmed_property_list`;
CREATE TABLE `trimmed_property_list` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Concept_List_ID` int(10) unsigned NOT NULL,
  `Concept` text NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `undefined_term_set`
--

DROP TABLE IF EXISTS `undefined_term_set`;
CREATE TABLE `undefined_term_set` (
  `Project_ID` int(10) unsigned NOT NULL,
  `Term` text NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `wordspace_result`
--

DROP TABLE IF EXISTS `wordspace_result`;
CREATE TABLE `wordspace_result` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Doc_ID` int(10) unsigned NOT NULL,
  `Term1` text NOT NULL,
  `Term2` text NOT NULL,
  `Value` double NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

--
-- Table structure for table `wrong_pair`
--

DROP TABLE IF EXISTS `wrong_pair`;
CREATE TABLE `wrong_pair` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Term1` text NOT NULL,
  `Term2` text NOT NULL,
  KEY `Index_1` (`Project_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2007-09-28  9:22:50
