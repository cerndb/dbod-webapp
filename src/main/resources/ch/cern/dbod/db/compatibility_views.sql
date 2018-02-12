CREATE OR REPLACE VIEW public.dod_command_definition AS 
 SELECT command_definition.command_name,
    command_definition.type,
    command_definition.exec,
    command_definition.category
   FROM command_definition;
   
CREATE OR REPLACE VIEW public.dod_command_params AS 
 SELECT command_param.username,
    command_param.db_name,
    command_param.command_name,
    command_param.type,
    command_param.creation_date,
    command_param.name,
    command_param.value,
    command_param.category
   FROM command_param;

CREATE OR REPLACE VIEW public.dod_instance_changes AS 
 SELECT instance_change.username,
    instance_change.db_name,
    instance_change.attribute,
    instance_change.change_date,
    instance_change.requester,
    instance_change.old_value,
    instance_change.new_value
   FROM instance_change;
   
CREATE OR REPLACE VIEW public.dod_instances AS 
 SELECT i.owner AS username,
    i.name AS db_name,
    i.e_group,
    i.category,
    i.creation_date,
    i.expiry_date,
    t.type AS db_type,
    i.size AS db_size,
    i.no_connections,
    i.project,
    i.description,
    i.version,
    m.name AS master,
    s.name AS slave,
    h.name AS host,
    i.state,
        CASE
            WHEN i.status = 'ACTIVE'::instance_status THEN 1
            ELSE 0
        END AS status,
    i.id,
    api.get_instance_attribute('port'::character varying, i.id) AS port
   FROM instance i
     JOIN instance_type t ON i.type_id = t.id
     LEFT JOIN instance m ON i.master_id = m.id
     LEFT JOIN instance s ON i.slave_id = s.id
     JOIN host h ON i.host_id = h.id;
     
CREATE OR REPLACE VIEW public.dod_jobs AS 
 SELECT j.username,
    j.db_name,
    j.command_name,
    j.type,
    j.creation_date,
    j.completion_date,
    j.requester,
    j.admin_action,
    j.state,
    j.log,
    j.result,
    j.email_sent,
    j.category,
    j.id,
    j.instance_id
   FROM job j;
   
CREATE OR REPLACE VIEW public.dod_upgrades AS 
 SELECT upgrade.db_type,
    upgrade.category,
    upgrade.version_from,
    upgrade.version_to
   FROM upgrade;
   
CREATE OR REPLACE VIEW public.fim_data AS 
 SELECT db_on_demand.internal_id,
    db_on_demand.instance_name,
    db_on_demand.description,
    db_on_demand.owner_account_type,
    db_on_demand.owner_first_name,
    db_on_demand.owner_last_name,
    db_on_demand.owner_login,
    db_on_demand.owner_mail,
    db_on_demand.owner_phone1,
    db_on_demand.owner_phone2,
    db_on_demand.owner_portable_phone,
    db_on_demand.owner_department,
    db_on_demand.owner_group,
    db_on_demand.owner_section
   FROM fim.db_on_demand;


ALTER TABLE public.dod_command_definition
  OWNER TO dbod;

ALTER TABLE public.dod_command_params
  OWNER TO dbod;
  
ALTER TABLE public.dod_instance_changes
  OWNER TO dbod;
  
ALTER TABLE public.dod_instances
  OWNER TO dbod;
  
ALTER TABLE public.dod_jobs
  OWNER TO dbod;
  
ALTER TABLE public.dod_upgrades
  OWNER TO dbod;
  
ALTER TABLE public.fim_data
  OWNER TO dbod;
