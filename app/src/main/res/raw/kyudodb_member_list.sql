


drop table IF EXISTS member_list;
create TABLE member_list (
  memberID int NOT NULL,
  family_name varchar(45) NOT NULL,
  first_name varchar(45) NOT NULL,
  sex int NOT NULL,
  PRIMARY KEY (memberID)
);


INSERT INTO member_list VALUES (0,'Guest','member',2);
--INSERT INTO member_list VALUES (0,'Guest','me      mber',2),(201801,'笹倉','一希',0),(201802,'野々山','航士',0),(201803,'保科','洋輝',0),(201804,'松岡','良弥',0),(201805,'岡田','真侑',1),(201806,'田原','成美',1),(201807,'植田','夏菜',1),(201808,'柴田','美羽',1),(201809,'田門','紗佳',1);
--INSERT INTO "member_list" VALUES (201901,'池田','滉太郎',0);
--INSERT INTO "member_list" VALUES (201902,'冨田','直輝',0);
--INSERT INTO "member_list" VALUES (201903,'小嶌','蓮',0);
--INSERT INTO "member_list" VALUES (201904,'西山','海里',0);
--INSERT INTO "member_list" VALUES (201905,'山本','大凱',0);
--INSERT INTO "member_list" VALUES (201906,'山本','照天',0);
--INSERT INTO "member_list" VALUES (201907,'服部','慎太郎',0);
--INSERT INTO "member_list" VALUES (201908,'豐田','湧太',0);
--INSERT INTO "member_list" VALUES (201909,'中山','潤',0);
--INSERT INTO "member_list" VALUES (201910,'多嶋','ひな',1);
--INSERT INTO "member_list" VALUES (201911,'横山','琴弓',1);
--INSERT INTO "member_list" VALUES (201912,'足立','爽',1);
--INSERT INTO "member_list" VALUES (201913,'澤','実伶',1);
--INSERT INTO "member_list" VALUES (201914,'塩谷','碧彩',1);
--INSERT INTO "member_list" VALUES (201915,'山口','百葉',1);
--INSERT INTO "member_list" VALUES (201916,'髙谷','実莉',1);
--INSERT INTO "member_list" VALUES (201917,'平山','紗帆',1);
--INSERT INTO "member_list" VALUES (202001,'加藤','愛唯',1);
--INSERT INTO "member_list" VALUES (202002,'熊谷','葵',1);
--INSERT INTO "member_list" VALUES (202003,'吉田','祐花里',1);
--INSERT INTO "member_list" VALUES (202004,'星','風花',1);
--INSERT INTO "member_list" VALUES (202005,'徳田','朱香',1);
--INSERT INTO "member_list" VALUES (202006,'伊藤','杏',1);
--INSERT INTO "member_list" VALUES (202007,'長坂','ひな',1);
--INSERT INTO "member_list" VALUES (202008,'赤羽','佳菜',1);
--INSERT INTO "member_list" VALUES (202009,'溝口','ほのか',1);
--INSERT INTO "member_list" VALUES (202010,'酒井','吾桂之',0);
--INSERT INTO "member_list" VALUES (202011,'近藤','柊星',0);
--INSERT INTO "member_list" VALUES (202012,'佐々木','洋弥',0);
--INSERT INTO "member_list" VALUES (202013,'仲野','巧真',0);
--INSERT INTO "member_list" VALUES (202014,'松永','悠希',0);
--INSERT INTO "member_list" VALUES (202015,'服部','隆之介',0);
--INSERT INTO "member_list" VALUES (202016,'山下','悠輔',0);
--INSERT INTO "member_list" VALUES (202017,'秋山','修蔵',0);
--INSERT INTO "member_list" VALUES (202018,'古舘','由崇',0);