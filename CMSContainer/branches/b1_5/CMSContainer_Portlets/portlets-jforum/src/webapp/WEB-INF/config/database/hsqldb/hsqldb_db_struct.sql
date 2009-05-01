--
-- Table structure for table 'jforum_banlist'
--
DROP TABLE IF EXISTS jforum_banlist;
CREATE TABLE jforum_banlist (
  banlist_id int GENERATED BY DEFAULT AS IDENTITY (start with 1),
  user_id int,
  banlist_ip varchar(8),
  banlist_email varchar(255),
  PRIMARY KEY  (banlist_id)
) ;

CREATE INDEX idx_banlist_user ON jforum_banlist(user_id);
CREATE INDEX idx_banlist_ip ON jforum_banlist(banlist_ip);
CREATE INDEX idx_banlist_email ON jforum_banlist(banlist_email);

--
-- Table structure for table 'jforum_categories'
--
DROP TABLE IF EXISTS jforum_categories;
CREATE TABLE jforum_categories (
  categories_id int GENERATED BY DEFAULT AS IDENTITY (start with 1),
  title varchar(100) default '' NOT NULL ,
  display_order int default 0 NOT NULL,
  moderated INTEGER DEFAULT 0,
  PRIMARY KEY  (categories_id)
) ;

--
-- Table structure for table 'jforum_config'
--
DROP TABLE IF EXISTS jforum_config;
CREATE TABLE jforum_config (
  config_name varchar(255) default '' NOT NULL,
  config_value varchar(255) default '' NOT NULL,
  config_id int GENERATED BY DEFAULT AS IDENTITY (start with 1),
  PRIMARY KEY(config_id)
) ;

--
-- Table structure for table 'jforum_forums'
--
DROP TABLE IF EXISTS jforum_forums;
CREATE TABLE jforum_forums (
  forum_id int GENERATED BY DEFAULT AS IDENTITY (start with 1),
  categories_id int default '1' NOT NULL,
  forum_name varchar(150) default '' NOT NULL,
  forum_desc varchar(255) default NULL,
  forum_order int default '1',
  forum_topics int default '0' NOT NULL,
  forum_last_post_id int default '0' NOT NULL,
  moderated int default '0',
  PRIMARY KEY  (forum_id)
) ;
CREATE INDEX idx_forums_categories_id ON jforum_forums(categories_id);

--
-- Table structure for table 'jforum_forums_watch'
--
DROP TABLE IF EXISTS jforum_forums_watch;
CREATE TABLE jforum_forums_watch (
  forum_id INT NOT NULL,
  user_id INT NOT NULL,
  is_read INT DEFAULT 1
);
CREATE INDEX idx_fw_forum ON jforum_forums_watch(forum_id);
CREATE INDEX idx_fw_user ON jforum_forums_watch(user_id);

--
-- Table structure for table 'jforum_groups'
--
DROP TABLE IF EXISTS jforum_groups;
CREATE TABLE jforum_groups (
  group_id int GENERATED BY DEFAULT AS IDENTITY (start with 1),
  group_name varchar(40) default '' NOT NULL,
  group_description varchar(255) default NULL,
  parent_id int default '0',
  PRIMARY KEY  (group_id)
) ;


DROP TABLE IF EXISTS jforum_user_groups;
CREATE TABLE jforum_user_groups (
	group_id INT NOT NULL,
	user_id INT NOT NULL
) ;
CREATE INDEX idx_ug_group ON jforum_user_groups(group_id);
CREATE INDEX idx_ug_user ON jforum_user_groups(user_id);

--
-- Table structure for table 'jforum_roles'
--
DROP TABLE IF EXISTS jforum_roles;
CREATE TABLE jforum_roles (
  role_id int GENERATED BY DEFAULT AS IDENTITY (start with 1),
  group_id int default '0',
  name varchar(255) NOT NULL,
  PRIMARY KEY (role_id)
) ;
CREATE INDEX idx_roles_group ON jforum_roles(group_id);
CREATE INDEX idx_roles_name ON jforum_roles(name);

--
-- Table structure for table 'jforum_role_values'
--
DROP TABLE IF EXISTS jforum_role_values;
CREATE TABLE jforum_role_values (
  role_id INT NOT NULL,
  role_value VARCHAR(255)
) ;
CREATE INDEX idx_rv_role ON jforum_role_values(role_id);

--
-- Table structure for table 'jforum_posts'
--
DROP TABLE IF EXISTS jforum_posts;
CREATE TABLE jforum_posts (
  post_id int GENERATED BY DEFAULT AS IDENTITY (start with 1),
  topic_id int default '0' NOT NULL,
  forum_id int default '0' NOT NULL,
  user_id int default '0' NOT NULL,
  post_time timestamp default NULL,
  poster_ip varchar(15) default NULL,
  enable_bbcode int default '1' NOT NULL,
  enable_html int default '1' NOT NULL,
  enable_smilies int default '1' NOT NULL,
  enable_sig int default '1' NOT NULL,
  post_edit_time timestamp default NULL,
  post_edit_count int default '0' NOT NULL,
  status int default '1',
  attach int default 0,
  need_moderate int default '0',
  PRIMARY KEY  (post_id)
) ;
CREATE INDEX idx_posts_user ON jforum_posts(user_id);
CREATE INDEX idx_posts_topic ON jforum_posts(topic_id);
CREATE INDEX idx_posts_moderate ON jforum_posts(forum_id);
CREATE INDEX idx_posts_time ON jforum_posts(post_time);
CREATE INDEX idx_posts_forum ON jforum_posts(need_moderate);

--
-- Table structure for table 'jforum_posts_text'
--
DROP TABLE IF EXISTS jforum_posts_text;
CREATE TABLE jforum_posts_text (
	post_id int NOT NULL PRIMARY KEY,
	post_text LONGVARCHAR,
	post_subject VARCHAR(100)
) ;

--
-- Table structure for table 'jforum_privmsgs'
--
DROP TABLE IF EXISTS jforum_privmsgs;
CREATE TABLE jforum_privmsgs (
  privmsgs_id int GENERATED BY DEFAULT AS IDENTITY (start with 1),
  privmsgs_type int default '0' NOT NULL,
  privmsgs_subject varchar(255) default '' NOT NULL,
  privmsgs_from_userid int default '0' NOT NULL,
  privmsgs_to_userid int default '0' NOT NULL,
  privmsgs_date timestamp default '0' NOT NULL,
  privmsgs_ip varchar(15) default '' NOT NULL,
  privmsgs_enable_bbcode int default '1' NOT NULL,
  privmsgs_enable_html int default '0' NOT NULL,
  privmsgs_enable_smilies int default '1' NOT NULL,
  privmsgs_attach_sig int default '1' NOT NULL,
  PRIMARY KEY  (privmsgs_id)
) ;

DROP TABLE IF EXISTS jforum_privmsgs_text;
CREATE TABLE jforum_privmsgs_text (
	privmsgs_id int NOT NULL,
	privmsgs_text LONGVARCHAR,
	PRIMARY KEY ( privmsgs_id )
) ;

--
-- Table structure for table 'jforum_ranks'
--

DROP TABLE IF EXISTS jforum_ranks;
CREATE TABLE jforum_ranks (
  rank_id int GENERATED BY DEFAULT AS IDENTITY (start with 1),
  rank_title varchar(50) default '' NOT NULL,
  rank_min int default '0' NOT NULL,
  rank_special int default NULL,
  rank_image varchar(255) default NULL,
  PRIMARY KEY  (rank_id)
) ;

--
-- Table structure for table 'jforum_sessions'
--

DROP TABLE IF EXISTS jforum_sessions;
CREATE TABLE jforum_sessions (
  session_id varchar(150) default '' NOT NULL,
  session_user_id int default '0' NOT NULL,
  session_start timestamp default CURRENT_TIMESTAMP NOT NULL,
  session_time int default '0' NOT NULL,
  session_ip varchar(8) default '' NOT NULL,
  session_page int default '0' NOT NULL,
  session_logged_int int default NULL
) ;

CREATE INDEX idx_sess_user ON jforum_sessions(session_user_id);

--
-- Table structure for table 'jforum_smilies'
--

DROP TABLE IF EXISTS jforum_smilies;
CREATE TABLE jforum_smilies (
  smilie_id int GENERATED BY DEFAULT AS IDENTITY (start with 1),
  code varchar(50)default '' NOT NULL ,
  url varchar(100) default NULL,
  disk_name varchar(255),
  PRIMARY KEY  (smilie_id)
) ;

--
-- Table structure for table 'jforum_themes'
--

DROP TABLE IF EXISTS jforum_themes;
CREATE TABLE jforum_themes (
  themes_id int GENERATED BY DEFAULT AS IDENTITY (start with 1),
  template_name varchar(30) default '' NOT NULL,
  style_name varchar(30) default '' NOT NULL,
  PRIMARY KEY  (themes_id)
) ;

--
-- Table structure for table 'jforum_topics'
--

DROP TABLE IF EXISTS jforum_topics;
CREATE TABLE jforum_topics (
  topic_id int GENERATED BY DEFAULT AS IDENTITY (start with 1),
  forum_id int default '0' NOT NULL,
  topic_title varchar(100) default '' NOT NULL,
  user_id int default '0' NOT NULL,
  topic_time timestamp default '0' NOT NULL,
  topic_views int default '1',
  topic_replies int default '0',
  topic_status int default '0',
  topic_vote_id int default '0',
  topic_type int default '0',
  topic_first_post_id int default '0',
  topic_last_post_id int default '0' NOT NULL,
  topic_moved_id int default 0,
  moderated int default '0',
  PRIMARY KEY  (topic_id)
) ;

CREATE INDEX idx_topics_forum ON jforum_topics(forum_id);
CREATE INDEX idx_topics_user ON jforum_topics(user_id);
CREATE INDEX idx_topics_fp ON jforum_topics(topic_first_post_id);
CREATE INDEX idx_topics_lp ON jforum_topics(topic_last_post_id);
CREATE INDEX idx_topics_time ON jforum_topics(topic_time);
CREATE INDEX idx_topics_type ON jforum_topics(topic_type);
CREATE INDEX idx_topics_moved ON jforum_topics(topic_moved_id);

--
-- Table structure for table 'jforum_topics_watch'
--
DROP TABLE IF EXISTS jforum_topics_watch;
CREATE TABLE jforum_topics_watch (
  topic_id int default '0' NOT NULL,
  user_id int default '0' NOT NULL,
  is_read int default '0' NOT NULL
) ;
CREATE INDEX idx_tw_topic ON jforum_topics_watch(topic_id);
CREATE INDEX idx_tw_user ON jforum_topics_watch(user_id);

--
-- Table structure for table 'jforum_users'
--
DROP TABLE IF EXISTS jforum_users;
CREATE TABLE jforum_users (
  user_id int GENERATED BY DEFAULT AS IDENTITY (start with 1),
  user_active int default 1,
  username varchar(50) default '' NOT NULL,
  user_password varchar(32) default '' NOT NULL,
  user_session_time int default '0' NOT NULL,
  user_session_page int default '0' NOT NULL,
  user_lastvisit timestamp default CURRENT_TIMESTAMP NOT NULL,
  user_regdate timestamp default CURRENT_TIMESTAMP NOT NULL,
  user_level int default NULL,
  user_posts int default '0' NOT NULL,
  user_timezone varchar(5) default '' NOT NULL,
  user_style int default NULL,
  user_lang varchar(255) default '' NOT NULL,
  user_dateformat varchar(20) default '%d/%M/%Y %H:%i' NOT NULL,
  user_new_privmsg int default '0' NOT NULL,
  user_unread_privmsg int default '0' NOT NULL,
  user_last_privmsg timestamp NULL,
  user_emailtime timestamp NULL,
  user_viewemail int default '0',
  user_attachsig int default '1',
  user_allowhtml int default '0',
  user_allowbbcode int default '1',
  user_allowsmilies int default '1',
  user_allowavatar int default '1',
  user_allow_pm int default '1',
  user_allow_viewonline int default '1',
  user_notify int default '1',
  user_notify_pm int default '1',
  user_popup_pm int default '1',
  rank_id int default '0',
  user_avatar varchar(100) default NULL,
  user_avatar_type int default '0' NOT NULL,
  user_email varchar(255) default '' NOT NULL,
  user_icq varchar(15) default NULL,
  user_website varchar(255) default NULL,
  user_from varchar(100) default NULL,
  user_sig longvarchar,
  user_sig_bbcode_uid varchar(10) default NULL,
  user_aim varchar(255) default NULL,
  user_yim varchar(255) default NULL,
  user_msnm varchar(255) default NULL,
  user_occ varchar(100) default NULL,
  user_interests varchar(255) default NULL,
  user_biography longvarchar default NULL,
  user_actkey varchar(32) default NULL,
  gender char(1) default NULL,
  themes_id int default NULL,
  deleted int default NULL,
  user_viewonline int default '1',
  security_hash varchar(32),
  user_karma DECIMAL,
  user_authhash VARCHAR(32),
  user_notify_always int DEFAULT 0,
  user_notify_text int DEFAULT 0,
  PRIMARY KEY  (user_id)
) ;

--
-- Table structure for table 'jforum_vote_desc'
--
DROP TABLE IF EXISTS jforum_vote_desc;
CREATE TABLE jforum_vote_desc (
  vote_id int GENERATED BY DEFAULT AS IDENTITY (start with 1),
  topic_id int default '0' NOT NULL,
  vote_text varchar(255) default '' NOT NULL,
  vote_start DATETIME NOT NULL,
  vote_length int default '0' NOT NULL,
  PRIMARY KEY  (vote_id)
) ;

CREATE INDEX idx_vd_topic ON jforum_vote_desc(topic_id);

--
-- Table structure for table 'jforum_vote_results'
--
DROP TABLE IF EXISTS jforum_vote_results;
CREATE TABLE jforum_vote_results (
  vote_id int default '0' NOT NULL,
  vote_option_id int default '0' NOT NULL,
  vote_option_text varchar(255) default '' NOT NULL,
  vote_result int default '0' NOT NULL
) ;

CREATE INDEX idx_vr_id ON jforum_vote_results(vote_id);

--
-- Table structure for table 'jforum_vote_voters'
--
DROP TABLE IF EXISTS jforum_vote_voters;
CREATE TABLE jforum_vote_voters (
  vote_id int default '0' NOT NULL,
  vote_user_id int default '0' NOT NULL,
  vote_user_ip varchar(15) default '' NOT NULL
) ;

CREATE INDEX idx_vv_id ON jforum_vote_voters(vote_id);
CREATE INDEX idx_vv_user ON jforum_vote_voters(vote_user_id);

--
-- Table structure for table 'jforum_words'
--
DROP TABLE IF EXISTS jforum_words;
CREATE TABLE jforum_words (
  word_id int GENERATED BY DEFAULT AS IDENTITY (start with 1),
  word varchar(100) default '' NOT NULL,
  replacement varchar(100) default '' NOT NULL,
  PRIMARY KEY  (word_id)
) ;

--
-- Table structure for table 'jforum_karma'
--
DROP TABLE IF EXISTS jforum_karma;
CREATE TABLE jforum_karma (
	karma_id INT GENERATED BY DEFAULT AS IDENTITY (start with 1),
	post_id INT NOT NULL,
	topic_id INT NOT NULL,
	post_user_id INT NOT NULL,
	from_user_id INT NOT NULL,
	points INT NOT NULL,
	rate_date TIMESTAMP DEFAULT NULL,
	PRIMARY KEY(karma_id)
);

CREATE INDEX idx_krm_post ON jforum_karma(post_id);
CREATE INDEX idx_krm_topic ON jforum_karma(topic_id);
CREATE INDEX idx_krm_user ON jforum_karma(post_user_id);
CREATE INDEX idx_krm_from ON jforum_karma(from_user_id);

--
-- Table structure for table 'jforum_bookmark'
--
DROP TABLE IF EXISTS jforum_bookmarks;
CREATE TABLE jforum_bookmarks (
	bookmark_id INT GENERATED BY DEFAULT AS IDENTITY (start with 1),
	user_id INT NOT NULL,
	relation_id INT NOT NULL,
	relation_type INT NOT NULL,
	public_visible INT DEFAULT 1,
	title varchar(255),
	description varchar(255),
	PRIMARY KEY(bookmark_id)
);

CREATE INDEX idx_bok_user ON jforum_bookmarks(user_id);
CREATE INDEX idx_bok_rel ON jforum_bookmarks(relation_id);

-- 
-- Table structure for table 'jforum_quota_limit'
--
DROP TABLE IF EXISTS jforum_quota_limit;
CREATE TABLE jforum_quota_limit (
	quota_limit_id INT GENERATED BY DEFAULT AS IDENTITY (start with 1),
	quota_desc VARCHAR(50) NOT NULL,
	quota_limit INT NOT NULL,
	quota_type INT DEFAULT 1,
	PRIMARY KEY(quota_limit_id)
);

--
-- Table structure for table 'jforum_extension_groups'
--
DROP TABLE IF EXISTS jforum_extension_groups;
CREATE TABLE jforum_extension_groups (
	extension_group_id INT GENERATED BY DEFAULT AS IDENTITY (start with 1),
	name VARCHAR(100) NOT NULL,
	allow INT DEFAULT 1, 
	upload_icon VARCHAR(100),
	download_mode INT DEFAULT 1,
	PRIMARY KEY(extension_group_id)
);

-- 
-- Table structure for table 'jforum_extensions'
--
DROP TABLE IF EXISTS jforum_extensions;
CREATE TABLE jforum_extensions (
	extension_id INT GENERATED BY DEFAULT AS IDENTITY (start with 1),
	extension_group_id INT NOT NULL,
	description VARCHAR(100),
	upload_icon VARCHAR(100),
	extension VARCHAR(10),
	allow INT DEFAULT 1,
	PRIMARY KEY(extension_id)
);

CREATE INDEX idx_ext_group ON jforum_extensions(extension_group_id);
CREATE INDEX idx_ext_ext ON jforum_extensions(extension);

--
-- Table structure for table 'jforum_attach'
--
DROP TABLE IF EXISTS jforum_attach;
CREATE TABLE jforum_attach (
	attach_id INT GENERATED BY DEFAULT AS IDENTITY (start with 1),
	post_id INT,
	privmsgs_id INT,
	user_id INT NOT NULL,
	PRIMARY KEY(attach_id)
);

CREATE INDEX idx_att_post ON jforum_attach(post_id);
CREATE INDEX idx_att_priv ON jforum_attach(privmsgs_id);
CREATE INDEX idx_att_user ON jforum_attach(user_id);

-- 
-- Table structure for table 'jforum_attach_desc'
--
DROP TABLE IF EXISTS jforum_attach_desc;
CREATE TABLE jforum_attach_desc (
	attach_desc_id INT GENERATED BY DEFAULT AS IDENTITY (start with 1),
	attach_id INT NOT NULL,
	physical_filename VARCHAR(255) NOT NULL,
	real_filename VARCHAR(255) NOT NULL,
	download_count INT,
	description VARCHAR(255),
	mimetype VARCHAR(50),
	filesize INT,
	upload_time DATETIME,
	thumb INT DEFAULT 0,
	extension_id INT,
	PRIMARY KEY(attach_desc_id)
);

CREATE INDEX idx_att_d_att ON jforum_attach_desc(attach_id);
CREATE INDEX idx_att_d_ext ON jforum_attach_desc(extension_id);

--
-- Table structure for table 'jforum_attach_quota'
--
DROP TABLE IF EXISTS jforum_attach_quota;
CREATE TABLE jforum_attach_quota (
	attach_quota_id INT GENERATED BY DEFAULT AS IDENTITY (start with 1),
	group_id INT NOT NULL,
	quota_limit_id INT NOT NULL,
	PRIMARY KEY(attach_quota_id)
);

CREATE INDEX idx_aq_group ON jforum_attach_quota(group_id);
CREATE INDEX idx_aq_ql ON jforum_attach_quota(quota_limit_id);

--
-- Table structure for table 'jforum_banner'
--
DROP TABLE IF EXISTS jforum_banner;
CREATE TABLE jforum_banner (
	banner_id INT GENERATED BY DEFAULT AS IDENTITY (start with 1),
	banner_name VARCHAR(90),
	banner_placement INT NOT NULL,
	banner_description VARCHAR(30),
	banner_clicks INT NOT NULL,
	banner_views INT NOT NULL,
	banner_url VARCHAR(90),
	banner_weight INT DEFAULT 50 NOT NULL,
	banner_active INT NOT NULL,
	banner_comment VARCHAR(50),
	banner_type INT NOT NULL,
	banner_width INT NOT NULL,
	banner_height INT NOT NULL,
	PRIMARY KEY(banner_id)
);

--
-- Table structure for table 'jforum_moderation_log'
-- 
DROP TABLE IF EXISTS jforum_moderation_log;
CREATE TABLE jforum_moderation_log (
	log_id INT GENERATED BY DEFAULT AS IDENTITY (start with 1),
	user_id INT NOT NULL,
	log_description LONGVARCHAR NOT NULL,
	log_original_message LONGVARCHAR,
	log_date TIMESTAMP NOT NULL,
	log_type INT DEFAULT 0,
	post_id INT,
	topic_id INT,
	post_user_id INT,
	PRIMARY KEY(log_id)
);

CREATE INDEX idx_ml_user ON jforum_moderation_log(user_id);
CREATE INDEX idx_ml_post_user ON jforum_moderation_log(post_user_id);
