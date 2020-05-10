 create table defense_area (
        id bigint not null auto_increment,
        defense_name varchar(50) not null,
        PRIMARY KEY (id)
    )character set = utf8;
    ALTER TABLE defense_area add constraint UQ_DN unique (defense_name);

 create table hc_device (
        id bigint not null auto_increment,
        ip varchar(15) not null,
        port varchar(6) not null,
        account varchar(50) not null,
        password varchar(50) not null,
        defense_area_id bigint,
        PRIMARY KEY (id),
        FOREIGN KEY (defense_area_id) REFERENCES defense_area (id)
    )character set = utf8;
    ALTER TABLE hc_device add constraint UQ_ip unique (ip);


    create table user_info (
        uid bigint not null auto_increment,
        username varchar(50) not null,
        password varchar(100) not null,
        real_name varchar(50),
        mobile varchar(255),
        email varchar(255),
        generation_time datetime default CURRENT_TIMESTAMP,
        salt varchar(255) not null,
        state tinyint,
        PRIMARY KEY (UID)
    )character set = utf8;
    ALTER TABLE user_info add constraint UQ_USERNAME unique (username);

    create table role_info (
        id bigint not null auto_increment,
        role_name varchar(50) not null,
        description varchar(255),
        primary key (id)
    )character set = utf8;
    alter table role_info add constraint uq_rolename unique (role_name);

    create table privilege_info (
        id bigint not null auto_increment,
        privilege_name varchar(50) not null,
        description varchar(255),
        primary key (id)
    )character set = utf8;
    alter table privilege_info add constraint uq_privilegename unique (privilege_name);

    create table user_role_map(
      user_id bigint not null,
      role_id bigint not null
    );
    alter table user_role_map add constraint uq_user_role unique (user_id,role_id);
    alter table user_role_map add constraint fk_user_role_u foreign key (user_id)  references user_info (uid);
    ALTER TABLE user_role_map add constraint FK_user_role_R foreign key (role_id)  references role_info (id);



    create table role_privilege_map(
      role_id bigint not null,
      privilege_id bigint not null
    );
    alter table role_privilege_map add constraint uq_role_privilege unique (role_id,privilege_id);
    alter table role_privilege_map add constraint fk_role_privilege_r foreign key (role_id)  references role_info (id);
    ALTER TABLE role_privilege_map add constraint fk_role_privilege_p foreign key (privilege_id)  references privilege_info (id);


    create table operation_log (
        id bigint not null auto_increment,
        user varchar(50) not null,
        operation varchar(50) not null,
        generation_time datetime default CURRENT_TIMESTAMP,
        PRIMARY KEY (id)
    )character set = utf8;;

    create table alarm_log (
        id bigint not null auto_increment,
        device_ip varchar(50),
        state varchar(50),
        alarm_time datetime default CURRENT_TIMESTAMP,
        end_time datetime,
        PRIMARY KEY (id)
    )character set = utf8;