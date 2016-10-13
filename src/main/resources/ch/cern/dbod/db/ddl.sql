-- Generated by Oracle SQL Developer Data Modeler 4.1.3.901
--   at:        2016-03-15 11:48:00 CET
--   site:      Oracle Database 12c
--   type:      Oracle Database 12c




CREATE TABLE DBONDEMAND.COMMAND_DEFINITION
  (
    ID       NUMBER NOT NULL ,
    NAME     VARCHAR2 (64 BYTE) NOT NULL ,
    TYPE     VARCHAR2 (64 BYTE) NOT NULL ,
    CATEGORY VARCHAR2 (16 BYTE) NOT NULL ,
    EXEC     VARCHAR2 (2048 BYTE)
  ) ;
CREATE UNIQUE INDEX DBONDEMAND.COM_DEF_NAM_TYP_CAT_IDX ON DBONDEMAND.COMMAND_DEFINITION
  (
    NAME ASC , TYPE ASC , CATEGORY ASC
  )
  ;
CREATE UNIQUE INDEX DBONDEMAND.PK_COM_DEF ON DBONDEMAND.COMMAND_DEFINITION
  (
    ID ASC
  )
  ;
ALTER TABLE DBONDEMAND.COMMAND_DEFINITION ADD CONSTRAINT COMMAND_DEFINITION_PK PRIMARY KEY ( ID ) ;
ALTER TABLE DBONDEMAND.COMMAND_DEFINITION ADD CONSTRAINT COM_DEF_NAME_TYPE_CATEGORY_UN UNIQUE ( NAME , TYPE , CATEGORY ) ;


CREATE TABLE DBONDEMAND.COMMAND_PARAM
  (
    ID     NUMBER NOT NULL ,
    JOB_ID NUMBER NOT NULL ,
    NAME   VARCHAR2 (64 BYTE) NOT NULL ,
    VALUE CLOB
  ) ;
CREATE UNIQUE INDEX DBONDEMAND.PK_COMMAND_PARAMS ON DBONDEMAND.COMMAND_PARAM
  (
    ID ASC
  )
  ;
  CREATE INDEX DBONDEMAND.COMMAND_PARAM_JOB_IDX ON DBONDEMAND.COMMAND_PARAM
    (
      JOB_ID ASC
    ) ;
ALTER TABLE DBONDEMAND.COMMAND_PARAM ADD CONSTRAINT COMMAND_PARAM_PK PRIMARY KEY ( ID ) ;


CREATE TABLE DBONDEMAND.INSTANCE
  (
    ID            NUMBER NOT NULL ,
    USERNAME      VARCHAR2 (32 BYTE) NOT NULL ,
    DB_NAME       VARCHAR2 (128 BYTE) NOT NULL ,
    HOST          VARCHAR2 (128 BYTE) NOT NULL ,
    E_GROUP       VARCHAR2 (256 BYTE) ,
    CATEGORY      VARCHAR2 (32 BYTE) NOT NULL ,
    CREATION_DATE DATE NOT NULL ,
    EXPIRY_DATE   DATE ,
    DB_TYPE       VARCHAR2 (32 BYTE) NOT NULL ,
    VERSION       VARCHAR2 (128 BYTE) ,
    PROJECT       VARCHAR2 (128 BYTE) ,
    DESCRIPTION   VARCHAR2 (1024 BYTE) ,
    STATE         VARCHAR2 (32 BYTE) NOT NULL ,
    STATUS        CHAR (1 BYTE) NOT NULL ,
    MASTER        VARCHAR2 (32 BYTE) ,
    SLAVE         VARCHAR2 (32 BYTE)
  ) ;
CREATE UNIQUE INDEX DBONDEMAND.PK_INSTANCES ON DBONDEMAND.INSTANCE
  (
    USERNAME ASC , DB_NAME ASC
  )
  ;
CREATE UNIQUE INDEX DBONDEMAND.INSTANCE_DB_NAME_IDX ON DBONDEMAND.INSTANCE
  (
    DB_NAME ASC
  )
  ;
ALTER TABLE DBONDEMAND.INSTANCE ADD CONSTRAINT INSTANCE_PK PRIMARY KEY ( ID ) ;
ALTER TABLE DBONDEMAND.INSTANCE ADD CONSTRAINT INSTANCE_DB_NAME_UN UNIQUE ( DB_NAME ) ;


CREATE TABLE DBONDEMAND.INSTANCE_CHANGE
  (
    ID          NUMBER NOT NULL ,
    INSTANCE_ID NUMBER NOT NULL ,
    ATTRIBUTE   VARCHAR2 (32 BYTE) NOT NULL ,
    CHANGE_DATE DATE NOT NULL ,
    REQUESTER   VARCHAR2 (32 BYTE) NOT NULL ,
    OLD_VALUE   VARCHAR2 (1024 BYTE) ,
    NEW_VALUE   VARCHAR2 (1024 BYTE)
  ) ;
CREATE UNIQUE INDEX DBONDEMAND.PK_INSTANCE_CHANGE_ID ON DBONDEMAND.INSTANCE_CHANGE
  (
    ID ASC
  )
  ;
  CREATE INDEX DBONDEMAND.INS_CHA_INSTANCE_ID_IDX ON DBONDEMAND.INSTANCE_CHANGE
    (
      INSTANCE_ID ASC
    ) ;
ALTER TABLE DBONDEMAND.INSTANCE_CHANGE ADD CONSTRAINT INSTANCE_CHANGE_PK PRIMARY KEY ( ID ) ;


CREATE TABLE DBONDEMAND.JOB
  (
    ID              NUMBER NOT NULL ,
    NAME            NUMBER NOT NULL ,
    CREATION_DATE   DATE NOT NULL ,
    COMPLETION_DATE DATE ,
    REQUESTER       VARCHAR2 (32 BYTE) NOT NULL ,
    ADMIN_ACTION    NUMBER (*,0) NOT NULL ,
    STATE           VARCHAR2 (32 BYTE) NOT NULL ,
    LOG CLOB ,
    RESULT                    VARCHAR2 (2048 BYTE) ,
    EMAIL_SENT                DATE ,
    DOD_COMMAND_DEFINITION_ID NUMBER NOT NULL ,
    COMMAND_DEFINITION_ID     NUMBER NOT NULL ,
    INSTANCE_ID               NUMBER NOT NULL
  ) ;
CREATE UNIQUE INDEX DBONDEMAND.PK_JOBS ON DBONDEMAND.JOB
  (
    CREATION_DATE ASC
  )
  ;
  CREATE INDEX DBONDEMAND.JOB_INSTANCE_IDX ON DBONDEMAND.JOB
    (
      INSTANCE_ID ASC
    ) ;
ALTER TABLE DBONDEMAND.JOB ADD CONSTRAINT JOB_PK PRIMARY KEY ( ID ) ;


CREATE TABLE DBONDEMAND.STATS_MONTHLY_INSTANCES
  (
    MONTHLY_DATE      DATE NOT NULL ,
    NUM_INSTANCES     NUMBER DEFAULT 0 NOT NULL ,
    AGG_NUM_INSTANCES NUMBER DEFAULT 0 NOT NULL
  ) ;
CREATE UNIQUE INDEX DBONDEMAND.PK_STATS_MONTHLY_INSTANCES ON DBONDEMAND.STATS_MONTHLY_INSTANCES
  (
    MONTHLY_DATE ASC
  )
  ;
ALTER TABLE DBONDEMAND.STATS_MONTHLY_INSTANCES ADD CONSTRAINT STATS_MONTHLY_INSTANCES_PK PRIMARY KEY ( MONTHLY_DATE ) ;


CREATE TABLE DBONDEMAND.UPGRADE
  (
    DB_TYPE      VARCHAR2 (32 BYTE) NOT NULL ,
    CATEGORY     VARCHAR2 (32 BYTE) NOT NULL ,
    VERSION_FROM VARCHAR2 (128 BYTE) NOT NULL ,
    VERSION_TO   VARCHAR2 (128 BYTE) NOT NULL
  ) ;
CREATE UNIQUE INDEX DBONDEMAND.PK_DOD_UPGRADES ON DBONDEMAND.UPGRADE
  (
    DB_TYPE ASC , CATEGORY ASC , VERSION_FROM ASC
  )
  ;
ALTER TABLE DBONDEMAND.UPGRADE ADD CONSTRAINT UPGRADE_PK PRIMARY KEY ( DB_TYPE, CATEGORY, VERSION_FROM ) ;


ALTER TABLE DBONDEMAND.COMMAND_PARAM ADD CONSTRAINT COMMAND_PARAM_JOB_FK FOREIGN KEY ( JOB_ID ) REFERENCES DBONDEMAND.JOB ( ID ) ON
DELETE CASCADE ;

ALTER TABLE DBONDEMAND.INSTANCE_CHANGE ADD CONSTRAINT INSTANCE_CHANGE_INSTANCE_FK FOREIGN KEY ( INSTANCE_ID ) REFERENCES DBONDEMAND.INSTANCE ( ID ) ON
DELETE CASCADE ;

ALTER TABLE DBONDEMAND.JOB ADD CONSTRAINT JOB_COMMAND_DEFINITION_FK FOREIGN KEY ( COMMAND_DEFINITION_ID ) REFERENCES DBONDEMAND.COMMAND_DEFINITION ( ID ) ON
DELETE CASCADE ;

ALTER TABLE DBONDEMAND.JOB ADD CONSTRAINT JOB_INSTANCE_FK FOREIGN KEY ( INSTANCE_ID ) REFERENCES DBONDEMAND.INSTANCE ( ID ) ;


-- Oracle SQL Developer Data Modeler Summary Report: 
-- 
-- CREATE TABLE                             7
-- CREATE INDEX                            12
-- ALTER TABLE                             13
-- CREATE VIEW                              0
-- ALTER VIEW                               0
-- CREATE PACKAGE                           0
-- CREATE PACKAGE BODY                      0
-- CREATE PROCEDURE                         0
-- CREATE FUNCTION                          0
-- CREATE TRIGGER                           0
-- ALTER TRIGGER                            0
-- CREATE COLLECTION TYPE                   0
-- CREATE STRUCTURED TYPE                   0
-- CREATE STRUCTURED TYPE BODY              0
-- CREATE CLUSTER                           0
-- CREATE CONTEXT                           0
-- CREATE DATABASE                          0
-- CREATE DIMENSION                         0
-- CREATE DIRECTORY                         0
-- CREATE DISK GROUP                        0
-- CREATE ROLE                              0
-- CREATE ROLLBACK SEGMENT                  0
-- CREATE SEQUENCE                          0
-- CREATE MATERIALIZED VIEW                 0
-- CREATE SYNONYM                           0
-- CREATE TABLESPACE                        0
-- CREATE USER                              0
-- 
-- DROP TABLESPACE                          0
-- DROP DATABASE                            0
-- 
-- REDACTION POLICY                         0
-- TSDP POLICY                              0
-- 
-- ORDS DROP SCHEMA                         0
-- ORDS ENABLE SCHEMA                       0
-- ORDS ENABLE OBJECT                       0
-- 
-- ERRORS                                   0
-- WARNINGS                                 0
