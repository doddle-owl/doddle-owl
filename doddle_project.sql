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
  `Value` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `class_trimmed_result_analysis`
--

DROP TABLE IF EXISTS `class_trimmed_result_analysis`;
CREATE TABLE `class_trimmed_result_analysis` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Concept_List_ID` int(10) unsigned NOT NULL,
  `Target_Concept` text NOT NULL,
  `Target_Parent_Concept` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `concept_definition`
--

DROP TABLE IF EXISTS `concept_definition`;
CREATE TABLE `concept_definition` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `is_Meta_Property` tinyint(1) NOT NULL,
  `Term1` text NOT NULL,
  `Relation` text NOT NULL,
  `Term2` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `concept_definition_parameter`
--

DROP TABLE IF EXISTS `concept_definition_parameter`;
CREATE TABLE `concept_definition_parameter` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Minimum_Confidence` double NOT NULL,
  `Minimum_Support` double NOT NULL,
  `Front_Scope` int(10) unsigned NOT NULL,
  `Behind_Scope` int(10) unsigned NOT NULL,
  `N_Gram` int(10) unsigned NOT NULL,
  `Gram_Count` int(10) unsigned NOT NULL,
  `Word_Space_Value` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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
  `is_Trimming_Internal_Node_With_Compound_Word_Tree` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `doc_info`
--

DROP TABLE IF EXISTS `doc_info`;
CREATE TABLE `doc_info` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Doc_ID` int(10) unsigned NOT NULL,
  `Doc_Path` text NOT NULL,
  `Language` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `doddle_project`
--

DROP TABLE IF EXISTS `doddle_project`;
CREATE TABLE `doddle_project` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Project_Name` text NOT NULL,
  `Project_Author` text NOT NULL,
  `Project_Creation_Date` datetime NOT NULL,
  `Project_Modification_Date` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `eval_concept_set`
--

DROP TABLE IF EXISTS `eval_concept_set`;
CREATE TABLE `eval_concept_set` (
  `Project_ID` int(10) unsigned NOT NULL,
  `Term_ID` int(10) unsigned NOT NULL,
  `Eval_Value` double NOT NULL,
  `Concept` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `general_ontology_info`
--

DROP TABLE IF EXISTS `general_ontology_info`;
CREATE TABLE `general_ontology_info` (
  `EDR_General` tinyint(1) NOT NULL,
  `EDR_Technical` tinyint(1) NOT NULL,
  `WordNet` tinyint(1) NOT NULL,
  `Project_ID` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `input_concept_set`
--

DROP TABLE IF EXISTS `input_concept_set`;
CREATE TABLE `input_concept_set` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Input_Concept` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `input_term_concept_map`
--

DROP TABLE IF EXISTS `input_term_concept_map`;
CREATE TABLE `input_term_concept_map` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Input_Term` text NOT NULL,
  `Input_Concept` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `input_term_construct_tree_option`
--

DROP TABLE IF EXISTS `input_term_construct_tree_option`;
CREATE TABLE `input_term_construct_tree_option` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Input_Term` text NOT NULL,
  `Input_Concept` text NOT NULL,
  `Tree_Option` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `input_term_set`
--

DROP TABLE IF EXISTS `input_term_set`;
CREATE TABLE `input_term_set` (
  `Project_ID` int(11) NOT NULL default '0',
  `Input_Term` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `property_trimmed_result_analysis`
--

DROP TABLE IF EXISTS `property_trimmed_result_analysis`;
CREATE TABLE `property_trimmed_result_analysis` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Concept_List_ID` int(10) unsigned NOT NULL,
  `Target_Concept` text NOT NULL,
  `Target_Parent_Concept` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `removed_word_info`
--

DROP TABLE IF EXISTS `removed_word_info`;
CREATE TABLE `removed_word_info` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Term` text NOT NULL,
  `POS_List_ID` int(10) unsigned NOT NULL,
  `TF` int(10) unsigned NOT NULL,
  `IDF` double NOT NULL,
  `TF_IDF` double NOT NULL,
  `Doc_List_ID` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `removed_word_info_doc_list`
--

DROP TABLE IF EXISTS `removed_word_info_doc_list`;
CREATE TABLE `removed_word_info_doc_list` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Doc_List_ID` int(10) unsigned NOT NULL,
  `Doc` text NOT NULL,
  `TF` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `removed_word_info_pos_list`
--

DROP TABLE IF EXISTS `removed_word_info_pos_list`;
CREATE TABLE `removed_word_info_pos_list` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `POS_List_ID` int(10) unsigned NOT NULL,
  `POS` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `term_eval_concept_set`
--

DROP TABLE IF EXISTS `term_eval_concept_set`;
CREATE TABLE `term_eval_concept_set` (
  `Project_ID` int(10) unsigned NOT NULL,
  `Term_ID` int(10) unsigned NOT NULL,
  `Term` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `trimmed_class_list`
--

DROP TABLE IF EXISTS `trimmed_class_list`;
CREATE TABLE `trimmed_class_list` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Concept_List_ID` int(10) unsigned NOT NULL,
  `Concept` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `trimmed_property_list`
--

DROP TABLE IF EXISTS `trimmed_property_list`;
CREATE TABLE `trimmed_property_list` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Concept_List_ID` int(10) unsigned NOT NULL,
  `Concept` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `undefined_term_set`
--

DROP TABLE IF EXISTS `undefined_term_set`;
CREATE TABLE `undefined_term_set` (
  `Project_ID` int(10) unsigned NOT NULL,
  `Term` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `word_info`
--

DROP TABLE IF EXISTS `word_info`;
CREATE TABLE `word_info` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Term` text NOT NULL,
  `POS_List_ID` int(10) unsigned NOT NULL,
  `TF` int(10) unsigned NOT NULL,
  `IDF` double NOT NULL,
  `TF_IDF` double NOT NULL,
  `Doc_List_ID` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `word_info_doc_list`
--

DROP TABLE IF EXISTS `word_info_doc_list`;
CREATE TABLE `word_info_doc_list` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Doc_List_ID` int(10) unsigned NOT NULL,
  `Doc` text NOT NULL,
  `TF` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `word_info_pos_list`
--

DROP TABLE IF EXISTS `word_info_pos_list`;
CREATE TABLE `word_info_pos_list` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `POS_List_ID` int(10) unsigned NOT NULL,
  `POS` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `wordspace_result`
--

DROP TABLE IF EXISTS `wordspace_result`;
CREATE TABLE `wordspace_result` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Doc_ID` int(10) unsigned NOT NULL,
  `Term1` text NOT NULL,
  `Term2` text NOT NULL,
  `Value` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `wrong_pair`
--

DROP TABLE IF EXISTS `wrong_pair`;
CREATE TABLE `wrong_pair` (
  `Project_ID` int(10) unsigned NOT NULL default '0',
  `Term1` text NOT NULL,
  `Term2` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2007-09-16  4:55:55
