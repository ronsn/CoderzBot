CREATE DATABASE `coderzservice` /*!40100 DEFAULT CHARACTER SET utf8 */

CREATE TABLE `grammar_log` (
  `rec_id` int(11) NOT NULL auto_increment,
  `timestamp` int(11) NOT NULL default '0',
  `user_nick` varchar(64) NOT NULL default '',
  `user_login` varchar(64) NOT NULL default '',
  `user_hostmask` varchar(64) NOT NULL default '',
  `solving_time` int(11) NOT NULL default '0',
  `solved_sentence` varchar(128) NOT NULL default '',
  PRIMARY KEY  (`rec_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8