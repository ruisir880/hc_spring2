insert into user_info(username,password,real_name,mobile,salt) value ('admin','4da85196d96808f7d69e5ac6f5354cda','管理者','123456789','f319cd5b392d16c68e3abd8af3fde536');


insert into role_info(role_name,description) values('admin','管理员');
insert into role_info(role_name,description) values('normalUser','普通用户');


insert into privilege_info (privilege_name,description) values('system.management','系统设置');
insert into privilege_info (privilege_name,description) values('defence.management','防区设置');
insert into privilege_info (privilege_name,description) values('video.management','视频设置');
insert into privilege_info (privilege_name,description) values('alarm.log','报警日志');
insert into privilege_info (privilege_name,description) values('operation.log','操作日志');
insert into privilege_info (privilege_name,description) values('oneTouch','一键布撤防');

insert into defense_area (defense_name) values(1);
insert into defense_area (defense_name) values(2);
insert into defense_area (defense_name) values(3);
insert into defense_area (defense_name) values(4);

insert into user_role_map values(1,1);

insert into role_privilege_map(role_id,privilege_id) values(1,1);